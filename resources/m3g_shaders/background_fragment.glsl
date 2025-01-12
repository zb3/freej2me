precision highp float;

uniform sampler2D texture;

uniform int repeatX;
uniform int repeatY;

varying vec2 v_uv; // normalized in texture coords, might be negative

void main() {
    if (repeatX == 0 && (v_uv.x < 0.0 || v_uv.x > 1.0)) {
        discard;
    }

    if (repeatY == 0 && (v_uv.y < 0.0 || v_uv.y > 1.0)) {
        discard;
    }

    // Wrap the UV coordinates using fract to simulate the modulo operation
    vec2 wrapped_uv = vec2(fract(1.0 + fract(v_uv.x)), fract(1.0 + fract(v_uv.y)));

    vec4 color = texture2D(texture, wrapped_uv);

    gl_FragColor = color;
}
