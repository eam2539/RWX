package com.corrodinggames.rts.gameFramework.utility;

public final class Debug {
    private Debug() {
    }

    public static long getNativeHeapSize() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getNativeHeapAllocatedSize() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static long getNativeHeapFreeSize() {
        return Runtime.getRuntime().freeMemory();
    }

    public static void startMethodTracing(String traceName, int bufferSize) {
    }

    public static void stopMethodTracing() {
    }
}
