package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.java.audio.util.AudioFile;
import com.corrodinggames.rts.java.audio.util.StreamUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Ogg.class */
public class Ogg {

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Ogg$Music.class */
    public static class Music extends OpenALMusic {
        private OggInputStream input;
        private OggInputStream previousInput;

        public Music(OpenALAudio openALAudio, AudioFile audioFile) {
            super(openALAudio, audioFile);
            if (openALAudio.noDevice) {
                return;
            }
            this.input = new OggInputStream(audioFile.a());
            setup(this.input.getChannels(), this.input.getSampleRate());
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public int read(byte[] bArr) {
            if (this.input == null) {
                this.input = new OggInputStream(this.file.a(), this.previousInput);
                setup(this.input.getChannels(), this.input.getSampleRate());
                this.previousInput = null;
            }
            return this.input.read(bArr);
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public void reset() {
            StreamUtils.a(this.input);
            this.previousInput = null;
            this.input = null;
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        protected void loop() {
            StreamUtils.a(this.input);
            this.previousInput = this.input;
            this.input = null;
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Ogg$MusicWithThreadedLoader.class */
    public static class MusicWithThreadedLoader extends OpenALMusic {
        private OggInputStream input;
        private OggInputStream previousInput;
        ThreadedWrappingAudioInputStream backgroundInput;

        public MusicWithThreadedLoader(OpenALAudio openALAudio, AudioFile audioFile) {
            super(openALAudio, audioFile);
            if (openALAudio.noDevice) {
                return;
            }
            this.input = new OggInputStream(audioFile.a());
            setup(this.input.getChannels(), this.input.getSampleRate());
            this.backgroundInput = new ThreadedWrappingAudioInputStream(this.input);
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public int read(byte[] bArr) {
            if (this.input == null) {
                this.input = new OggInputStream(this.file.a(), this.previousInput);
                setup(this.input.getChannels(), this.input.getSampleRate());
                this.previousInput = null;
                this.backgroundInput = new ThreadedWrappingAudioInputStream(this.input);
            }
            long jA = PerformanceProfiler.a();
            int i = this.backgroundInput.read(bArr);
            double dA = PerformanceProfiler.a(jA);
            if (dA > 0.5d) {
                GameEngine.isInSpace("ogg read took:" + PerformanceProfiler.a(dA));
            }
            return i;
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public void backgroundUpdate() {
            try {
                if (this.input != null && this.backgroundInput != null) {
                    this.backgroundInput.backgroundFillBuffer();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        public void reset() {
            StreamUtils.a(this.input);
            this.previousInput = null;
            this.input = null;
            this.backgroundInput.close();
        }

        @Override // com.corrodinggames.rts.java.audio.lwjgl.OpenALMusic
        protected void loop() {
            StreamUtils.a(this.input);
            this.previousInput = this.input;
            this.input = null;
            this.backgroundInput.close();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/Ogg$Sound.class */
    public static class Sound extends OpenALSound {
        public Sound(OpenALAudio openALAudio, AudioFile audioFile) {
            super(openALAudio);
            int i;
            if (openALAudio.noDevice) {
                return;
            }
            OggInputStream oggInputStream = null;
            try {
                oggInputStream = new OggInputStream(audioFile.a());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
                byte[] bArr = new byte[2048];
                while (!oggInputStream.atEnd() && (i = oggInputStream.read(bArr)) != -1) {
                    byteArrayOutputStream.write(bArr, 0, i);
                }
                setup(byteArrayOutputStream.toByteArray(), oggInputStream.getChannels(), oggInputStream.getSampleRate());
                StreamUtils.a(oggInputStream);
            } catch (Throwable th) {
                StreamUtils.a(oggInputStream);
                throw th;
            }
        }
    }
}
