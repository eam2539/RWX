#version 130

precision mediump float;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
  vec2 uv=v_texCoords;
  gl_FragColor = texture2D(u_texture, uv)*v_color;
}
