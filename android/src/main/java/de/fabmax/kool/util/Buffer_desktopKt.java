package de.fabmax.kool.util;

/**
 * Bridges bytecode compiled from the JVM core module to Kool's Android buffer implementation.
 */
public final class Buffer_desktopKt {
    private Buffer_desktopKt() {
    }

    public static Uint8Buffer Uint8Buffer$default(
            int capacity,
            boolean isAutoLimit,
            int mask,
            Object marker
    ) {
        boolean resolvedAutoLimit = (mask & 0x2) != 0 ? false : isAutoLimit;
        return Buffer_androidKt.Uint8Buffer(capacity, resolvedAutoLimit);
    }
}
