package com.corrodinggames.rts.gameFramework.android.graphics;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.b */
/* JADX INFO: loaded from: classes.dex */
public class ColorShader implements ShaderInterface {
    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ShaderInterface
    public String a() {
        return "uniform mat4 uMatrix;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * uMatrix * pos;\n}\n";
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ShaderInterface
    public String b() {
        return "precision mediump float;\nuniform vec4 uColor;\nvoid main() {\n  gl_FragColor = uColor;\n}\n";
    }

    @Override // com.corrodinggames.rts.gameFramework.android.graphics.ShaderInterface
    public void a(int i, GraphicsContext graphicsContext) {
    }
}
