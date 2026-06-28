package com.corrodinggames.rts.gameFramework.android.graphics;

import android.opengl.GLES20;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.l */
/* JADX INFO: loaded from: classes.dex */
public final class CircleShader extends ColorShader {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    float f570a;

    @Override
    // com.corrodinggames.rts.gameFramework.android.graphics.ColorShader, com.corrodinggames.rts.gameFramework.android.graphics.ShaderInterface
    public final String a() {
        return "uniform mat4 uMatrix;\nuniform mat4 uProjection;\nattribute vec2 aPosition;\nvarying vec2 vDrawRegionCoord;\nvoid main() {\n  vec4 pos = vec4(aPosition, 0.0, 1.0);\n  gl_Position = uProjection * uMatrix * pos;\n  vDrawRegionCoord = pos.xy;\n}\n";
    }

    @Override
    // com.corrodinggames.rts.gameFramework.android.graphics.ColorShader, com.corrodinggames.rts.gameFramework.android.graphics.ShaderInterface
    public final String b() {
        return "precision mediump float;\nvarying vec2 vDrawRegionCoord;\nuniform vec4 uColor;\nuniform float lineWidth;\nvoid main() {\n  float dx = vDrawRegionCoord.x - 0.5;\n  float dy = vDrawRegionCoord.y - 0.5;\n  float powVal = dx*dx + dy*dy; \n  float subRadius = 0.5 - lineWidth; \n  if(powVal >= subRadius * subRadius && powVal <= 0.5 * 0.5) {\n    gl_FragColor = uColor;\n  } else {\n    gl_FragColor = vec4(0, 0, 0, 0);\n  }\n \n}\n";
    }

    @Override
    // com.corrodinggames.rts.gameFramework.android.graphics.ColorShader, com.corrodinggames.rts.gameFramework.android.graphics.ShaderInterface
    public final void a(int i, GraphicsContext graphicsContext) {
        super.a(i, graphicsContext);
        GLES20.glUniform1f(GLES20.glGetUniformLocation(i, "lineWidth"), this.f570a);
    }
}
