package com.corrodinggames.rts.gameFramework.gl;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/a.class */
public class ShaderProgram implements IShaderProgram {
    @Override // com.corrodinggames.rts.gameFramework.gl.IShaderProgram
    public String a() {
        return "uniform mat4 uMatrix;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * uMatrix * pos;\n}\n";
    }

    @Override // com.corrodinggames.rts.gameFramework.gl.IShaderProgram
    public String b() {
        return "precision mediump float;\nuniform vec4 uColor;\nvoid main() {\n  gl_FragColor = uColor;\n}\n";
    }
}
