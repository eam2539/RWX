package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.java.audio.AudioRecorder;
import com.corrodinggames.rts.java.audio.util.AudioException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/JavaSoundAudioRecorder.class */
public class JavaSoundAudioRecorder implements AudioRecorder {
    private TargetDataLine line;
    private byte[] buffer = new byte[4096];

    public JavaSoundAudioRecorder(int i, boolean z) {
        try {
            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, i, 16, z ? 1 : 2, z ? 2 : 4, i, false);
            this.line = AudioSystem.getTargetDataLine(audioFormat);
            this.line.open(audioFormat, this.buffer.length);
            this.line.start();
        } catch (Exception e) {
            throw new AudioException("Error creating JavaSoundAudioRecorder.", e);
        }
    }

    @Override // com.corrodinggames.rts.java.audio.AudioRecorder
    public void read(short[] sArr, int i, int i2) {
        if (this.buffer.length < i2 * 2) {
            this.buffer = new byte[i2 * 2];
        }
        int i3 = i2 * 2;
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 == i3) {
                break;
            } else {
                i4 = i5 + this.line.read(this.buffer, i5, i3 - i5);
            }
        }
        int i6 = 0;
        int i7 = 0;
        while (i6 < i2 * 2) {
            sArr[i + i7] = (short) ((this.buffer[i6 + 1] << 8) | (this.buffer[i6] & 255));
            i6 += 2;
            i7++;
        }
    }

    @Override // com.corrodinggames.rts.java.audio.AudioRecorder
    public void dispose() {
        this.line.close();
    }
}
