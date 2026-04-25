package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.Music;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.java.audio.util.AudioFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/m.class */
public class OpenALMusic extends Music {
    OpenALMusicFactory a;
    com.corrodinggames.rts.java.audio.Music c;

    public OpenALMusic(String str, OpenALMusicFactory openALMusicFactory) {
        super(str, openALMusicFactory);
        this.a = openALMusicFactory;
        synchronized (openALMusicFactory.f()) {
            this.a = openALMusicFactory;
            String strConvertAbstractPath = FileHelper.convertAbstractPath(str);
            if (strConvertAbstractPath.contains(".rwmod")) {
                this.c = openALMusicFactory.b.newMusic(new AudioFile(FileHelper.openFileByPath(str), strConvertAbstractPath));
            } else {
                this.c = openALMusicFactory.b.newMusic(new AudioFile(strConvertAbstractPath));
            }
        }
    }
}
