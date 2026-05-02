package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.at */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/at.class */
public class StreamBlock {

    /* JADX INFO: renamed from: a */
    public GZIPOutputStream gzipOutput;

    /* JADX INFO: renamed from: b */
    public BufferedOutputStream bufferedOutput;

    /* JADX INFO: renamed from: c */
    public String blockName;

    /* JADX INFO: renamed from: e */
    public DataOutputStream dataOutput;

    /* JADX INFO: renamed from: f */
    public boolean deferClose = false;

    /* JADX INFO: renamed from: d */
    public ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    /* JADX INFO: renamed from: a */
    public void flush() throws IOException {
        this.dataOutput.flush();
        if (this.bufferedOutput != null) {
            this.bufferedOutput.flush();
        }
        if (this.gzipOutput != null) {
            this.gzipOutput.finish();
        }
    }

    /* JADX INFO: renamed from: b */
    public void close() throws IOException {
        if (!this.deferClose) {
            this.dataOutput.close();
        } else {
            GameEngine.logWarningAndStack("TODO: Cannot yet close wrapped stream");
        }
    }

    public StreamBlock(boolean z) throws IOException {
        OutputStream outputStream;
        if (z) {
            this.gzipOutput = new GZIPOutputStream(this.byteBuffer);
            this.bufferedOutput = new BufferedOutputStream(this.gzipOutput);
            outputStream = this.bufferedOutput;
        } else {
            outputStream = this.byteBuffer;
        }
        this.dataOutput = new DataOutputStream(outputStream);
    }
}
