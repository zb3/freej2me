precision highp float;

const int MAX_TEXTURES = 2;

uniform sampler2D texture[MAX_TEXTURES];


uniform int usedTextures;



#define FOG_LINEAR 1
#define FOG_EXP 2
uniform int fogType;
uniform float fogStartOrDensity;
uniform float fogEnd;
uniform vec4 fogColor;

varying vec4 v_color;
varying vec4 v_backColor;
varying vec2 v_coords0;
varying vec2 v_coords1;


uniform vec4 blendColor[MAX_TEXTURES];

#define FUNC_ADD 224
#define FUNC_BLEND 225
#define FUNC_DECAL 226
#define FUNC_MODULATE 227
#define FUNC_REPLACE 228
uniform int blendMode[MAX_TEXTURES];

uniform bool hasColor[MAX_TEXTURES];
uniform bool hasAlpha[MAX_TEXTURES];


// for alpha test
uniform float minAlpha;

uniform bool isTwoSided;


varying float v_fogCoord;

void main() {
    vec4 color = v_color;

    if (isTwoSided && !gl_FrontFacing) {
        color = v_backColor;
    }


    vec2 v_coords[MAX_TEXTURES];
    v_coords[0] = v_coords0;
    v_coords[1] = v_coords1;

    for (int i=0; i<MAX_TEXTURES; i++) {
        if (i < usedTextures) {
            vec4 texColor = vec4(0.0);
            if (i == 0) {
                texColor += texture2D(texture[0], v_coords[0]);
            }
            else if (i == 1) {
                texColor += texture2D(texture[1], v_coords[1]);
            }

            if (blendMode[i] == FUNC_REPLACE) {
                if (hasColor[i]) {
                    color.rgb = texColor.rgb;
                }
                if (hasAlpha[i]) {
                    color.a = texColor.a;
                }
            } else if (blendMode[i] == FUNC_MODULATE) {
                if (hasColor[i]) {
                    color.rgb = color.rgb * texColor.rgb;
                }
                if (hasAlpha[i]) {
                    color.a = color.a * texColor.a;
                }
            } else if (blendMode[i] == FUNC_DECAL) {
                if (hasAlpha[i]) {
                    color.rgb = color.rgb*vec3(1.0 - texColor.a) + texColor.rgb * texColor.a;
                } else {
                    color.rgb = texColor.rgb;
                }
            } else if (blendMode[i] == FUNC_BLEND) {
                if (hasColor[i]) {
                    color.rgb = color.rgb * vec3(-texColor.rgb + 1.0) + blendColor[i].rgb * texColor.rgb;
                }
                if (hasAlpha[i]) {
                    color.a = color.a * texColor.a;
                }
            } else if (blendMode[i] == FUNC_ADD) {
                if (hasColor[i]) {
                    color.rgb = color.rgb + texColor.rgb;
                }
                if (hasAlpha[i]) {
                    color.a = color.a * texColor.a;
                }
            }

            color = clamp(color, 0.0, 1.0); // clamp here?
        }
    }

    if (color.a < minAlpha) {
        discard;
    }

    if (fogType > 0) {
        float ffact = 1.0;

        if (fogType == FOG_LINEAR) {
            ffact = clamp((fogEnd - v_fogCoord)/ (fogEnd - fogStartOrDensity), 0., 1.);
        } else {
            ffact = clamp(exp(-fogStartOrDensity * v_fogCoord), 0., 1.);
        }

        color.rgb = mix(fogColor.rgb, color.rgb, ffact);
    }

    gl_FragColor = color;
}
