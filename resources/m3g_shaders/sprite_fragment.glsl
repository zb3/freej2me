precision highp float;

uniform sampler2D texture; // Texture sampler
uniform float alphaFactor; // Alpha blending factor

#define FOG_LINEAR 1
#define FOG_EXP 2
uniform int fogType;
uniform float fogStartOrDensity;
uniform float fogEnd;
uniform vec4 fogColor;

varying vec2 v_uv; // Interpolated texture coordinates
varying float v_fogCoord;

uniform float minAlpha;

void main() {
    vec4 color = texture2D(texture, v_uv);
    color.a *= alphaFactor; // Apply the alpha factor

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
