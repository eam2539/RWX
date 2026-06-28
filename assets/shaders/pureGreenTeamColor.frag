#version 130

precision mediump float;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform vec4 teamColor;

void main() {
  vec2 uv=v_texCoords;
  
  vec4 color = texture2D(u_texture, uv);
  
  float threshold=0.04; //High threshold is used right now because of linear filtering when zoomed out. Would likely need a pre-pass before filtering to avoid.
  
  if (color.g>0.0 && abs(color.r-color.b)<=threshold) //TODO: test branching performance or make branchless
  {
    float lightness=color.r;
    float greenness=color.g-lightness; //Will be 0 for gray colors
    
    color.rgb = lightness + teamColor.rgb*greenness;
  }
  
  gl_FragColor = color*v_color;
}
