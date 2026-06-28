package io.github.rwx.input;

/* Core pointer compatibility holder; real input is supplied by the Kool input bridge. */
public class MultiTouchController {
    public static final boolean isSupported = true;
    public static final boolean isMouseSupported = true;
    static final int[] pointerIndices = new int[10];

    static {
        for (int i = 0; i < pointerIndices.length; i++) {
            pointerIndices[i] = -1;
        }
    }
}
