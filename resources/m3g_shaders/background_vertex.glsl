attribute vec2 a_pos;
attribute vec2 a_normalizedUV;

//varying vec2 v_pos;
varying vec2 v_uv;

void main() {
    //v_pos = a_pos;
    v_uv = a_normalizedUV;

    gl_Position = vec4((a_pos*2.0)-1.0, 0.0, 1.0);
}
