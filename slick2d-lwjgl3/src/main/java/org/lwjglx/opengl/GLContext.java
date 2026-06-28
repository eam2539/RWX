package org.lwjglx.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public final class GLContext {
    private static final ContextCapabilities UNSUPPORTED = new ContextCapabilities(false, false, false);

    private GLContext() {
    }

    public static ContextCapabilities getCapabilities() {
        try {
            GLCapabilities capabilities = GL.getCapabilities();
            return new ContextCapabilities(
                    capabilities.GL_EXT_framebuffer_object,
                    capabilities.GL_EXT_texture_mirror_clamp,
                    capabilities.GL_EXT_secondary_color);
        } catch (IllegalStateException ignored) {
            return UNSUPPORTED;
        }
    }
}
