package org.lwjglx;

import java.nio.Buffer;

public final class MemoryUtil {
    private MemoryUtil() {
    }

    public static long getAddress(Buffer buffer) {
        return org.lwjgl.system.MemoryUtil.memAddress(buffer);
    }

    public static long getAddressSafe(Buffer buffer) {
        return buffer == null ? 0L : getAddress(buffer);
    }
}
