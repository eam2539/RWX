#version 150

precision mediump float;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec4 teamColor;

void main() {
  vec2 uv=v_texCoords;
  vec4 color = texture2D(u_texture, uv);
  
  float hueness=abs(color.r-color.g);
  hueness=max(hueness, abs(color.g-color.b));
  hueness=max(hueness, abs(color.b-color.r));
  
  if (hueness > (15.0/256.0) )
  {
    float lightness=min(min(color.r, color.g),color.b);
    
    color.r = lightness + teamColor.r*hueness;
    color.g = lightness + teamColor.g*hueness;
    color.b = lightness + teamColor.b*hueness;
  }
  
  gl_FragColor = color*v_color;
}
