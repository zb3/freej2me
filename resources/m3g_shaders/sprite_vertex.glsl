attribute vec2 a_uv; // The texture coordinates passed as an attribute
attribute float a_vertexID; // Vertex ID passed as an attribute

uniform mat4 projectionMatrix; // Projection matrix (camera)
uniform mat4 modelViewMatrix; // Model-view matrix (sprite transformation)
uniform float scaleX; // Scale factor in X direction
uniform float scaleY; // Scale factor in Y direction

varying vec2 v_uv; // Texture coordinates to pass to fragment shader
varying float v_fogCoord;

uniform vec2 depthRange; // should it work here?

void main() {
    vec4 ecPosition = modelViewMatrix * vec4(0.0, 0.0, 0.0, 1.0);

    // Calculate the origin in clip space
    vec4 origin = projectionMatrix * ecPosition;

    // Adjust the position based on vertex ID and scaling
    vec2 offset = vec2(0.0, 0.0);
    if (a_vertexID == 0.0) {
        offset = vec2(scaleX / 2.0, scaleY / 2.0);
    } else if (a_vertexID == 1.0) {
        offset = vec2(-scaleX / 2.0, scaleY / 2.0);
    } else if (a_vertexID == 2.0) {
        offset = vec2(scaleX / 2.0, -scaleY / 2.0);
    } else if (a_vertexID == 3.0) {
        offset = vec2(-scaleX / 2.0, -scaleY / 2.0);
    }

    // Modify gl_Position using the offset, while accounting for w-component
    vec4 adjustedPosition = origin;
    adjustedPosition.x += offset.x * origin.w;
    adjustedPosition.y += offset.y * origin.w;

    gl_Position = adjustedPosition;

    // Pass the texture coordinates to the fragment shader
    v_uv = a_uv;

    // this is an approximation but it works like this everywhere
    v_fogCoord = abs(ecPosition.z);

    if (depthRange.x != 0.0 || depthRange.y != 1.0) {
        gl_Position.z = (depthRange.y-depthRange.x)*gl_Position.z + depthRange.x*gl_Position.w*2.0;
    }
}
