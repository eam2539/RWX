package org.newdawn.slick.openal;

import java.io.IOException;
import java.io.InputStream;

/**
 * A sound as a MOD file - can only be played as music
 *
 * @author Kevin Glass
 */
public class MODSound extends AudioImpl {
    /**
     * The sound store this belongs to
     */
    private SoundStore store;

    /**
     * Create a mod sound to be played back
     *
     * @param store The store this sound belongs to
     * @param in    The input stream to read the data from
     * @throws IOException Indicates a failure to load a sound
     */
    public MODSound(SoundStore store, InputStream in) throws IOException {
        this.store = store;
        throw new IOException("MOD audio is not supported by the LWJGL3 Slick backend");
    }

    /**
     * @see org.newdawn.slick.openal.AudioImpl#playAsMusic(float, float, boolean)
     */
    public int playAsMusic(float pitch, float gain, boolean loop) {
        return -1;
    }

    /**
     * Poll the streaming on the MOD
     */
    public void poll() {
    }

    /**
     * @see org.newdawn.slick.openal.AudioImpl#playAsSoundEffect(float, float, boolean)
     */
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return -1;
    }

    /**
     * @see org.newdawn.slick.openal.AudioImpl#stop()
     */
    public void stop() {
        store.setMOD(null);
    }

    /**
     * @see org.newdawn.slick.openal.AudioImpl#getPosition()
     */
    public float getPosition() {
        throw new RuntimeException("Positioning on modules is not currently supported");
    }

    /**
     * @see org.newdawn.slick.openal.AudioImpl#setPosition(float)
     */
    public boolean setPosition(float position) {
        throw new RuntimeException("Positioning on modules is not currently supported");
    }
}
