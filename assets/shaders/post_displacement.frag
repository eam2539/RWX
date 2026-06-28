#version 130

precision mediump float;

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D screenBase;
uniform vec2 screenBaseSize;

uniform vec2 u_resolution;
uniform float u_offsetBy;

uniform float u_uiScaling;

void main() {
  vec2 usedScreenSize=(u_resolution.xy/screenBaseSize.xy);
  
  vec2 screenUV=gl_FragCoord.xy / (u_resolution * u_uiScaling);
  
  screenUV.y = 1.0 - screenUV.y;
  screenUV.xy = screenUV.xy * usedScreenSize;
  
  vec2 spriteUV=v_texCoords;
  vec4 spriteColor = texture2D(u_texture, spriteUV);
  
  vec2 screenOffset=u_offsetBy*(spriteColor.xy- (128.0/255.0) )*spriteColor.a*v_color.a;
  
  //Disable effect when offset is small. Allows some effects to overlap when aren't using a dedicated post processing layer
  //vec4 screenColor=(abs(screenOffset.x)<0.0002 && abs(screenOffset.y)<0.0002) ? vec4(0.0,0.0,0.0,0.0):vec4(1.0,1.0,1.0,1.0);
  
  vec4 screenColor=vec4(1,1,1,1);
  
  vec2 offsetScreenUV=screenUV;
  
  offsetScreenUV.xy += screenOffset;
  
  offsetScreenUV.xy = min(offsetScreenUV.xy, usedScreenSize.xy);
  offsetScreenUV.xy = max(offsetScreenUV.xy, vec2(0.0,0.0));
  
  vec4 newScreenPixel = texture2D(screenBase, offsetScreenUV );
  
  gl_FragColor = newScreenPixel * screenColor;
  
  
}
