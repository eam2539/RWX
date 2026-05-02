package com.corrodinggames.rts.gameFramework.gl;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/d.class */
public class TextureShader implements ITextureFilter {
    public String a() {
        return "#version 100;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nattribute vec2 aTextureCoordinate;\nattribute vec4 aColor;\nvarying vec4 v_color;\nvarying vec2 v_texCoords;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * pos;\n  v_texCoords = aTextureCoordinate;\n  v_color = aColor;\n}\n";
    }
}
