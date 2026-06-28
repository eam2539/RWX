#version 130

precision mediump float;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec4 teamColor;
uniform float teamColorAmount;

void main() {
  vec2 uv=v_texCoords;
  vec4 color = texture2D(u_texture, uv);
  
  color.r += teamColor.r*teamColorAmount;
  color.g += teamColor.g*teamColorAmount;
  color.b += teamColor.b*teamColorAmount;
  
  gl_FragColor = color*v_color;
}
