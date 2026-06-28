package org.lwjglx.opengl;

import org.lwjglx.LWJGLException;

public class Pbuffer {
    public static final int PBUFFER_SUPPORTED = 1;
    public static final int RENDER_TEXTURE_SUPPORTED = 2;
    public static final int FRONT_LEFT_BUFFER = 0;

    public Pbuffer(int width, int height, PixelFormat pixelFormat, RenderTexture renderTexture, Object sharedDrawable)
            throws LWJGLException {
        throw new LWJGLException("LWJGL2 Pbuffer is not supported by the embedded LWJGL3 backend");
    }

    public static int getCapabilities() {
        return 0;
    }

    public void makeCurrent() throws LWJGLException {
    }

    public void destroy() {
    }

    public boolean isBufferLost() {
        return false;
    }

    public void bindTexImage(int buffer) {
    }

    public void releaseTexImage(int buffer) {
    }
}
