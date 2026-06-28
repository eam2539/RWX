#version 130

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;
    v_color = gl_Color;
    v_texCoords = vec2(gl_MultiTexCoord0);
}
