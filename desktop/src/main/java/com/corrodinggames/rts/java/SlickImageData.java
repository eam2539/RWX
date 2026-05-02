package com.corrodinggames.rts.java;

import java.nio.ByteBuffer;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.MiscUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/t.class */
public class SlickImageData implements ImageData {
    int a;
    private int c;
    private int d;
    private int e;
    private int f;
    private byte[] g;
    final /* synthetic */ SlickTexture b;

    public SlickImageData(SlickTexture slickTexture, Image image) {
        this.b = slickTexture;
        Texture texture = image.getTexture();
        this.g = texture.getTextureData();
        this.a = texture.hasAlpha() ? 32 : 24;
        this.c = texture.getImageWidth();
        this.d = texture.getImageHeight();
        this.e = texture.getTextureWidth();
        this.f = texture.getTextureHeight();
    }

    public int getDepth() {
        return this.a;
    }

    public int getWidth() {
        return this.c;
    }

    public int getHeight() {
        return this.d;
    }

    public int getTexWidth() {
        return this.e;
    }

    public int getTexHeight() {
        return this.f;
    }

    public ByteBuffer getImageBufferData() {
        ByteBuffer byteBufferCreateByteBuffer = MiscUtils.createByteBuffer(this.g.length);
        byteBufferCreateByteBuffer.put(this.g);
        byteBufferCreateByteBuffer.flip();
        return byteBufferCreateByteBuffer;
    }

    public byte[] a() {
        return this.g;
    }
}
