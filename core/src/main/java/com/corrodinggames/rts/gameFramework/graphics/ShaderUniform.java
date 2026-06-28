package com.corrodinggames.rts.gameFramework.graphics;

public class ShaderUniform {
    public String a;
    public int b = -1;
    public boolean c;
    public boolean d;
    public float[] e = new float[1];
    public Texture f;
    public boolean g;
    public ShaderUniformValueType h = ShaderUniformValueType.FLOAT;

    public void a(float f) {
        if (this.e.length != 1) {
            this.e = new float[1];
        }
        if (this.e[0] == f) {
            return;
        }
        this.e[0] = f;
        this.c = true;
    }

    public void a(float f, float f2) {
        if (this.e.length != 2) {
            this.e = new float[2];
        }
        if (this.e[0] == f && this.e[1] == f2) {
            return;
        }
        this.e[0] = f;
        this.e[1] = f2;
        this.c = true;
    }

    public void a(float f, float f2, float f3) {
        if (this.e.length != 3) {
            this.e = new float[3];
        }
        if (this.e[0] == f && this.e[1] == f2 && this.e[2] == f3) {
            return;
        }
        this.e[0] = f;
        this.e[1] = f2;
        this.e[2] = f3;
        this.c = true;
    }

    public void a(float f, float f2, float f3, float f4) {
        if (this.e.length != 4) {
            this.e = new float[4];
        }
        if (this.e[0] == f && this.e[1] == f2 && this.e[2] == f3 && this.e[3] == f4) {
            return;
        }
        this.e[0] = f;
        this.e[1] = f2;
        this.e[2] = f3;
        this.e[3] = f4;
        this.c = true;
    }

    public void a(Texture texture) {
        if (this.f != texture) {
            this.f = texture;
            this.c = true;
        }
    }

    public void b(Texture texture) {
        this.g = true;
        if (this.f != texture) {
            this.f = texture;
            this.c = true;
        }
    }

    public void a(float[] values, ShaderUniformValueType valueType) {
        if (!java.util.Arrays.equals(this.e, values) || this.h != valueType) {
            this.e = java.util.Arrays.copyOf(values, values.length);
            this.h = valueType;
            this.c = true;
        }
    }
}
