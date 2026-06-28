package org.lwjglx.openal;

import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjglx.LWJGLException;

import java.nio.IntBuffer;

public final class AL {
    private static ALCdevice alcDevice;
    private static ALCcontext alcContext;
    private static boolean created;

    private AL() {
    }

    public static void create() throws LWJGLException {
        create(null, 44100, 60, false);
    }

    public static void create(String deviceArguments, int contextFrequency, int contextRefresh, boolean contextSynchronized)
            throws LWJGLException {
        if (created) {
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer attribs = stack.mallocInt(7);
            attribs.put(org.lwjgl.openal.ALC10.ALC_FREQUENCY).put(contextFrequency);
            attribs.put(org.lwjgl.openal.ALC10.ALC_REFRESH).put(contextRefresh);
            attribs.put(org.lwjgl.openal.ALC10.ALC_SYNC)
                    .put(contextSynchronized ? org.lwjgl.openal.ALC10.ALC_TRUE : org.lwjgl.openal.ALC10.ALC_FALSE);
            attribs.put(0).flip();

            long device = org.lwjgl.openal.ALC10.alcOpenDevice((String) null);
            if (device == 0L) {
                throw new LWJGLException("Could not open ALC device");
            }
            ALCCapabilities deviceCaps = org.lwjgl.openal.ALC.createCapabilities(device);
            long context = org.lwjgl.openal.ALC10.alcCreateContext(device, attribs);
            if (context == 0L) {
                org.lwjgl.openal.ALC10.alcCloseDevice(device);
                throw new LWJGLException("Could not create ALC context");
            }
            org.lwjgl.openal.ALC10.alcMakeContextCurrent(context);
            org.lwjgl.openal.AL.createCapabilities(deviceCaps);
            alcDevice = new ALCdevice(device);
            alcContext = new ALCcontext(context);
            created = true;
        }
    }

    public static boolean isCreated() {
        return created;
    }

    public static void destroy() {
        if (alcContext != null) {
            org.lwjgl.openal.ALC10.alcDestroyContext(alcContext.context);
        }
        if (alcDevice != null) {
            org.lwjgl.openal.ALC10.alcCloseDevice(alcDevice.device);
        }
        alcContext = null;
        alcDevice = null;
        created = false;
    }

    public static ALCcontext getContext() {
        return alcContext;
    }

    public static ALCdevice getDevice() {
        return alcDevice;
    }
}
