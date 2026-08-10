package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.java.audio.util.AudioException;
import com.corrodinggames.rts.java.audio.util.AudioFile;
import com.corrodinggames.rts.java.audio.util.StreamUtils;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Wav.class */
public class Wav {

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Wav$Music.class */
    public static class Music extends OpenALMusic {
        private WavInputStream input;

        public Music(OpenALAudio openALAudio, AudioFile audioFile) {
            super(openALAudio, audioFile);
            this.input = new WavInputStream(audioFile);
            if (openALAudio.noDevice) {
                return;
            }
            setup(this.input.channels, this.input.sampleRate);
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public int read(byte[] bArr) {
            if (this.input == null) {
                this.input = new WavInputStream(this.file);
                setup(this.input.channels, this.input.sampleRate);
            }
            try {
                return this.input.read(bArr);
            } catch (IOException e) {
                throw new AudioException("Error reading WAV file: " + this.file, e);
            }
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public void reset() {
            StreamUtils.a(this.input);
            this.input = null;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Wav$Sound.class */
    public static class Sound extends OpenALSound {
        public Sound(OpenALAudio openALAudio, AudioFile audioFile) {
            super(openALAudio);
            if (openALAudio.noDevice) {
                return;
            }
            WavInputStream wavInputStream = null;
            try {
                try {
                    wavInputStream = new WavInputStream(audioFile);
                    setup(StreamUtils.a(wavInputStream, wavInputStream.dataRemaining), wavInputStream.channels, wavInputStream.sampleRate);
                    StreamUtils.a(wavInputStream);
                } catch (IOException e) {
                    throw new AudioException("Error reading WAV file: " + audioFile, e);
                }
            } catch (Throwable th) {
                StreamUtils.a(wavInputStream);
                throw th;
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Wav$WavInputStream.class */
    public static class WavInputStream extends FilterInputStream {
        public int channels;
        public int sampleRate;
        public int dataRemaining;

        public WavInputStream(AudioFile audioFile) {
            super(audioFile.a());
            String str;
            try {
                if (read() != 82 || read() != 73 || read() != 70 || read() != 70) {
                    throw new AudioException("RIFF header not found: " + audioFile);
                }
                skipFully(4);
                if (read() != 87 || read() != 65 || read() != 86 || read() != 69) {
                    throw new AudioException("Invalid wave file header: " + audioFile);
                }
                int iSeekToChunk = seekToChunk('f', 'm', 't', ' ');
                int i = (read() & 255) | ((read() & 255) << 8);
                if (i != 1) {
                    switch (i) {
                        case 2:
                            str = "ADPCM";
                            break;
                        case 3:
                            str = "IEEE float";
                            break;
                        case 6:
                            str = "8-bit ITU-T G.711 A-law";
                            break;
                        case 7:
                            str = "8-bit ITU-T G.711 u-law";
                            break;
                        case 65534:
                            str = "Extensible";
                            break;
                        default:
                            str = "Unknown";
                            break;
                    }
                    throw new AudioException("WAV files must be PCM, unsupported format: " + str + " (" + i + ")");
                }
                this.channels = (read() & 255) | ((read() & 255) << 8);
                if (this.channels != 1 && this.channels != 2) {
                    throw new AudioException("WAV files must have 1 or 2 channels: " + this.channels);
                }
                this.sampleRate = (read() & 255) | ((read() & 255) << 8) | ((read() & 255) << 16) | ((read() & 255) << 24);
                skipFully(6);
                int i2 = (read() & 255) | ((read() & 255) << 8);
                if (i2 != 16) {
                    throw new AudioException("WAV files must have 16 bits per sample: " + i2);
                }
                skipFully(iSeekToChunk - 16);
                this.dataRemaining = seekToChunk('d', 'a', 't', 'a');
            } catch (Throwable th) {
                StreamUtils.a(this);
                throw new AudioException("Error reading WAV file: " + audioFile, th);
            }
        }

        private int seekToChunk(char c, char c2, char c3, char c4) throws IOException {
            while (true) {
                boolean z = (read() == c) & (read() == c2) & (read() == c3) & (read() == c4);
                int i = (read() & 255) | ((read() & 255) << 8) | ((read() & 255) << 16) | ((read() & 255) << 24);
                if (i == -1) {
                    throw new IOException("Chunk not found: " + c + c2 + c3 + c4);
                }
                if (z) {
                    return i;
                }
                skipFully(i);
            }
        }

        private void skipFully(int i) throws IOException {
            while (i > 0) {
                long jSkip = this.in.skip(i);
                if (jSkip <= 0) {
                    throw new EOFException("Unable to skip.");
                }
                i = (int) (((long) i) - jSkip);
            }
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bArr) throws IOException {
            int iMin;
            if (this.dataRemaining == 0 || (iMin = Math.min(super.read(bArr), this.dataRemaining)) == -1) {
                return -1;
            }
            this.dataRemaining -= iMin;
            return iMin;
        }
    }
}
