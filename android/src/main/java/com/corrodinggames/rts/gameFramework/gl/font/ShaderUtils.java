package com.corrodinggames.rts.gameFramework.gl.font;

import android.opengl.GLES20;
import android.util.Log;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.f */
/* JADX INFO: loaded from: classes.dex */
public final class ShaderUtils {
    /* JADX WARN: Removed duplicated region for block: B:14:0x0044  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int a(int i, int i2, ShaderAttributeType[] shaderAttributeTypeArr) {
        int i3 = 0;
        int iGlCreateProgram = GLES20.glCreateProgram();
        if (iGlCreateProgram != 0) {
            GLES20.glAttachShader(iGlCreateProgram, i);
            GLES20.glAttachShader(iGlCreateProgram, i2);
            for (ShaderAttributeType shaderAttributeType : shaderAttributeTypeArr) {
                GLES20.glBindAttribLocation(iGlCreateProgram, shaderAttributeType.c, shaderAttributeType.d);
            }
            GLES20.glLinkProgram(iGlCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(iGlCreateProgram, 35714, iArr, 0);
            if (iArr[0] == 0) {
                Log.v("Utilities", GLES20.glGetProgramInfoLog(iGlCreateProgram));
                GLES20.glDeleteProgram(iGlCreateProgram);
            } else {
                i3 = iGlCreateProgram;
            }
        }
        if (i3 == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return i3;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0048  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int a(int i, String str) {
        int i2 = 0;
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (iGlCreateShader != 0) {
            GLES20.glShaderSource(iGlCreateShader, str);
            GLES20.glCompileShader(iGlCreateShader);
            int[] iArr = new int[1];
            GLES20.glGetShaderiv(iGlCreateShader, 35713, iArr, 0);
            if (iArr[0] == 0) {
                Log.v("Utilities", "Shader fail info: " + GLES20.glGetShaderInfoLog(iGlCreateShader));
                GLES20.glDeleteShader(iGlCreateShader);
            } else {
                i2 = iGlCreateShader;
            }
        }
        if (i2 == 0) {
            throw new RuntimeException("Error creating shader ".concat(String.valueOf(i)));
        }
        return i2;
    }
}
