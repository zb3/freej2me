const int MAX_TEXTURES = 2;

uniform int usedTextures;

attribute vec3 vPos;
attribute vec3 vPosLight;
attribute vec4 vPosSb;

attribute vec4 vColor;
attribute vec3 vNormal;

// sync with MAX_TEXTURES
attribute vec2 vCoords0;
attribute vec2 vCoords1;

uniform mat4 vCoordsMat[MAX_TEXTURES];


uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

varying vec4 v_color;
varying vec4 v_backColor;

// sync with MAX_TEXTURES
varying vec2 v_coords0;
varying vec2 v_coords1;

varying float v_fogCoord;


uniform bool trackVertexColors;

uniform bool useLighting;

uniform vec4 mAmbient;
uniform vec4 mDiffuse;
uniform vec4 mSpecular;
uniform vec4 mEmissive;
uniform float mShininess;

const int MAX_LIGHTS = 8;

uniform int usedLights;


uniform vec4 lAmbient[MAX_LIGHTS];
uniform vec4 lDiffuse[MAX_LIGHTS];
uniform vec4 lSpecular[MAX_LIGHTS];
uniform vec4 lPosition[MAX_LIGHTS];
uniform mat4 lMatrix[MAX_LIGHTS];
uniform vec3 lDirection[MAX_LIGHTS];
uniform float lShininess[MAX_LIGHTS];
uniform float lSpotCutoffCos[MAX_LIGHTS];
uniform float lConstAtt[MAX_LIGHTS];
uniform float lLinAtt[MAX_LIGHTS];
uniform float lQuadAtt[MAX_LIGHTS];

uniform bool isLocalViewer;
uniform bool isTwoSided;

uniform vec2 depthRange;

uniform bool isFlatShaded;


vec4 lightColor(int i, in vec3 ecPosition3, in vec3 normal, in vec4 ambient, in vec4 diffuse) {
    // no MVM here, lMatrix incorporates inverse camera, but not model
    vec4 lightPosition = lMatrix[i] * lPosition[i];

    if (lightPosition.w != 0.0 && lightPosition.w != 1.0) {
        lightPosition.xyz /= lightPosition.w;
    }

    vec3 VP = vec3(lightPosition) - ecPosition3;
    float d = length(VP);

    vec3 PV = -vec3(lightPosition);
    float att = 1.0;

    if (lightPosition.w != 0.0) {
        att = 1.0 / (lConstAtt[i] +
            lLinAtt[i] * d + lQuadAtt[i] * d * d);

        PV += ecPosition3;
    } else {
        VP += ecPosition3;
    }

    PV = normalize(PV);

    float spot = 1.0;

    if (lSpotCutoffCos[i] != -1.0) {
        return vec4(1.0, 0.0, 0.0, 1.0);
        float sdot = max(0.0, dot(PV, normalize(mat3(lMatrix[i]) * lDirection[i])));

        if (sdot >= lSpotCutoffCos[i]) {
            spot = pow(sdot, lShininess[i]);
        } else {
            spot = 0.0;
        }
    }

    vec4 light = vec4(0.0);

    if (att != 0.0 && spot != 0.0) {
        light = ambient * lAmbient[i];

        VP = normalize(VP);

        float nDotVP = max(0.0, dot(normal, VP));

        light += nDotVP * diffuse * lDiffuse[i];

        if (nDotVP != 0.0) {
            vec3 eye = vec3 (0.0, 0.0, 1.0);
            if (isLocalViewer) {
                eye = -normalize(ecPosition3);
            }

            float nDotHV = max(0.0, dot(normal, normalize(VP + eye)));

            light += pow(nDotHV, mShininess) * mSpecular * lSpecular[i];
        }

        light *= att*spot;

    }

    return light;
}

vec4 applyLights(in vec3 ecPosition3, in vec3 normal, in vec4 ambient, in vec4 diffuse) {
    vec4 color = mEmissive;

    for (int i=0; i<MAX_LIGHTS; i++) {
        if (i < usedLights) {
            color += lightColor(i, ecPosition3, normal, ambient, diffuse);
        }
    }

    color = clamp(color, 0.0, 1.0);

    return vec4(color.xyz, diffuse.w);
}

void main() {
    vec4 ecPosition = modelViewMatrix * vec4(vPos.xyz*vPosSb.w + vPosSb.xyz, 1.0);

    vec4 color;
    vec4 backColor;

    if (useLighting) {
        vec4 ambient = mAmbient;
        vec4 diffuse = mDiffuse;
        if (trackVertexColors) {
            ambient = vColor;
            diffuse = vColor;
        }

        vec3 ecPosition3;

        if (isFlatShaded) {
            vec4 tmp = modelViewMatrix * vec4(vPosLight.xyz*vPosSb.w + vPosSb.xyz, 1.0);
            ecPosition3 = (vec3 (tmp)) / tmp.w;
        } else {
            ecPosition3 = (vec3 (ecPosition)) / ecPosition.w;
        }

        // uniform scaling - we can just cast the matrix
        // this "asserts" projection parts of mvm are 0 + uniform scaling
        vec3 normal3 = mat3(modelViewMatrix) * vNormal;

        // since we're not using GL_NORMALIZE here we normalize manually
        normal3 = normalize(normal3);

        color = applyLights(ecPosition3, normal3, ambient, diffuse);
        if (isTwoSided) {
            // at this point we don't know whether the face is front or back faced
            // so we need to compute both versions and then pick the correct one
            // in the fragment shader using gl_FrontFacing
            backColor = applyLights(ecPosition3, -normal3, ambient, diffuse);
        }


    } else {
        color = vColor;
        backColor = vColor;
    }

    v_color = color;
    v_backColor = backColor;

    vec4 tvcoord0 = vCoordsMat[0] * vec4(vCoords0, 0, 1);
    vec4 tvcoord1 = vCoordsMat[1] * vec4(vCoords1, 0, 1);
    v_coords0 = tvcoord0.xy / tvcoord0.w;
    v_coords1 = tvcoord1.xy / tvcoord1.w;

    // this is an approximation but it works like this everywhere
    v_fogCoord = abs(ecPosition.z);

    gl_Position = projectionMatrix * ecPosition;

    if (depthRange.x != 0.0 || depthRange.y != 1.0) {
        gl_Position.z = (depthRange.y-depthRange.x)*(gl_Position.z+gl_Position.w) + gl_Position.w*(2.0*depthRange.x - 1.0);
    }
}
