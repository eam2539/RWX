package com.corrodinggames.rts.gameFramework.file;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/h.class */
class CachedInputStream {

    /* JADX INFO: renamed from: a */
    public InputStream inputStream;

    public CachedInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /* JADX INFO: renamed from: a */
    public void close() {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
