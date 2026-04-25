package com.corrodinggames.rts.gameFramework.gl.font;

import android.opengl.GLES20;
import android.util.Log;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/a/f.class */
public class ShaderUtils {
    public static int a(int i, int i2, ShaderAttributeType[] shaderAttributeTypeArr) {
        int iGlCreateProgram = GLES20.glCreateProgram();
        if (iGlCreateProgram != 0) {
            GLES20.glAttachShader(iGlCreateProgram, i);
            GLES20.glAttachShader(iGlCreateProgram, i2);
            for (ShaderAttributeType shaderAttributeType : shaderAttributeTypeArr) {
                GLES20.glBindAttribLocation(iGlCreateProgram, shaderAttributeType.a(), shaderAttributeType.b());
            }
            GLES20.glLinkProgram(iGlCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(iGlCreateProgram, 35714, iArr, 0);
            if (iArr[0] == 0) {
                Log.a("Utilities", GLES20.glGetProgramInfoLog(iGlCreateProgram));
                GLES20.glDeleteProgram(iGlCreateProgram);
                iGlCreateProgram = 0;
            }
        }
        if (iGlCreateProgram == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return iGlCreateProgram;
    }

    public static int a(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (iGlCreateShader != 0) {
            GLES20.glShaderSource(iGlCreateShader, str);
            GLES20.glCompileShader(iGlCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(iGlCreateShader, 35713, iArr, 0);
            if (iArr[0] == 0) {
                Log.a("Utilities", "Shader fail info: " + GLES20.glGetShaderInfoLog(iGlCreateShader));
                GLES20.glDeleteShader(iGlCreateShader);
                iGlCreateShader = 0;
            }
        }
        if (iGlCreateShader == 0) {
            throw new RuntimeException("Error creating shader " + i);
        }
        return iGlCreateShader;
    }
}
