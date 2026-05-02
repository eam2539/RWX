package com.corrodinggames.rts.gameFramework.network;

import java.io.*;
import java.util.zip.GZIPInputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/l.class */
public class BlockInput {

    /* JADX INFO: renamed from: a */
    public String blockName;

    /* JADX INFO: renamed from: b */
    public ByteArrayInputStream byteInput;

    /* JADX INFO: renamed from: c */
    public DataInputStream dataInput;

    public BlockInput(byte[] bArr, boolean z, boolean z2) throws IOException {
        InputStream bufferedInputStream;
        this.byteInput = new ByteArrayInputStream(bArr);
        if (z) {
            bufferedInputStream = new BufferedInputStream(new GZIPInputStream(this.byteInput));
        } else {
            bufferedInputStream = this.byteInput;
        }
        this.dataInput = new DataInputStream(bufferedInputStream);
    }
}
