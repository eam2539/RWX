package org.lwjglx;

import org.lwjgl.system.MemoryUtil;

import java.nio.*;

public final class BufferCasts {
    private BufferCasts() {
    }

    public static ByteBuffer toByteBuffer(CharBuffer buffer) {
        return MemoryUtil.memByteBuffer(buffer);
    }

    public static ByteBuffer toByteBuffer(ShortBuffer buffer) {
        return MemoryUtil.memByteBuffer(buffer);
    }

    public static ByteBuffer toByteBuffer(IntBuffer buffer) {
        return MemoryUtil.memByteBuffer(buffer);
    }

    public static ByteBuffer toByteBuffer(LongBuffer buffer) {
        return MemoryUtil.memByteBuffer(buffer);
    }

    public static ByteBuffer toByteBuffer(FloatBuffer buffer) {
        return MemoryUtil.memByteBuffer(buffer);
    }

    public static ByteBuffer toByteBuffer(DoubleBuffer buffer) {
        return MemoryUtil.memByteBuffer(buffer);
    }

    public static void updateBuffer(CharBuffer destination, ByteBuffer source) {
    }

    public static void updateBuffer(ShortBuffer destination, ByteBuffer source) {
    }

    public static void updateBuffer(IntBuffer destination, ByteBuffer source) {
    }

    public static void updateBuffer(LongBuffer destination, ByteBuffer source) {
    }

    public static void updateBuffer(FloatBuffer destination, ByteBuffer source) {
    }

    public static void updateBuffer(DoubleBuffer destination, ByteBuffer source) {
    }
}
