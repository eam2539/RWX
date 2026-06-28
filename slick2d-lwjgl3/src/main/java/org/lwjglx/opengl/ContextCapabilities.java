package org.lwjglx.opengl;

public class ContextCapabilities {
    public final boolean GL_EXT_framebuffer_object;
    public final boolean GL_EXT_texture_mirror_clamp;
    public final boolean GL_EXT_secondary_color;

    ContextCapabilities(
            boolean framebufferObject,
            boolean textureMirrorClamp,
            boolean secondaryColor) {
        GL_EXT_framebuffer_object = framebufferObject;
        GL_EXT_texture_mirror_clamp = textureMirrorClamp;
        GL_EXT_secondary_color = secondaryColor;
    }
}
