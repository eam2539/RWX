package org.lwjglx;

public final class PointerBuffer {
    private final org.lwjgl.PointerBuffer delegate;

    private PointerBuffer(org.lwjgl.PointerBuffer delegate) {
        this.delegate = delegate;
    }

    public static PointerBuffer allocateDirect(int capacity) {
        return new PointerBuffer(org.lwjgl.PointerBuffer.allocateDirect(capacity));
    }

    org.lwjgl.PointerBuffer delegate() {
        return delegate;
    }
}
