package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ax */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ax.class */
public class BlockOutputStream {

    /* JADX INFO: renamed from: a */
    public BufferedOutputStream bufferedStream;

    /* JADX INFO: renamed from: b */
    public String blockName;

    /* JADX INFO: renamed from: d */
    public PrintStream printStream;

    /* JADX INFO: renamed from: e */
    public boolean deferClose = false;

    /* JADX INFO: renamed from: c */
    public ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public void a() throws IOException {
        this.printStream.flush();
        if (this.bufferedStream != null) {
            this.bufferedStream.flush();
        }
    }

    public void b() {
        if (!this.deferClose) {
            this.printStream.close();
        } else {
            GameEngine.logWarningAndStack("TODO: Cannot yet close wrapped stream");
        }
    }

    public BlockOutputStream(boolean z) {
        OutputStream outputStream;
        if (z) {
            this.bufferedStream = new BufferedOutputStream(this.buffer);
            outputStream = this.bufferedStream;
        } else {
            outputStream = this.buffer;
        }
        this.printStream = new PrintStream(outputStream);
    }
}
