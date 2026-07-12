package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.appFramework.AppFrameworkUtils;
import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/j.class */
public class AssetInputStream extends InputStream {

    /* JADX INFO: renamed from: a */
    InputStream inputStream;

    /* JADX INFO: renamed from: b */
    String path;

    /* JADX INFO: renamed from: c */
    String assetPath;

    /* JADX INFO: renamed from: d */
    boolean closed;

    /* JADX INFO: renamed from: e */
    String buildVersion;

    /* JADX INFO: renamed from: a */
    public boolean isDirect() {
        if (this.inputStream instanceof FileInputStream) {
            return true;
        }
        if (!GameEngine.isPC() && this.assetPath != null) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: b */
    public FileDescriptor getFileDescriptor() throws IOException {
        if (this.inputStream instanceof FileInputStream) {
            return ((FileInputStream) this.inputStream).getFD();
        }
        if (!GameEngine.isPC() && this.assetPath != null) {
            return AppFrameworkUtils.getContext().d().b(this.assetPath).getFileDescriptor();
        }
        throw new RuntimeException("AssetInputStream: unexpected stream for: " + this.path);
    }

    private AssetInputStream() {
    }

    public AssetInputStream(InputStream inputStream, String str, String str2) throws FileNotFoundException {
        if (inputStream == null) {
            throw new FileNotFoundException();
        }
        this.inputStream = inputStream;
        this.path = str;
        this.assetPath = str2;
        this.buildVersion = GameEngine.getStackTrace();
    }

    public AssetInputStream(FileInputStream fileInputStream, String str) throws FileNotFoundException {
        if (fileInputStream == null) {
            throw new FileNotFoundException();
        }
        this.inputStream = fileInputStream;
        this.path = str;
        this.buildVersion = GameEngine.getStackTrace();
    }

    public AssetInputStream(InputStream inputStream, String str) throws FileNotFoundException {
        if (inputStream == null) {
            throw new FileNotFoundException();
        }
        this.inputStream = inputStream;
        this.path = str;
        this.buildVersion = GameEngine.getStackTrace();
    }

    /* JADX INFO: renamed from: c */
    public long lastModified() {
        if (!GameEngine.isPC()) {
            return -1L;
        }
        if (this.path == null) {
            return -2L;
        }
        return new File(this.path).lastModified();
    }

    /* JADX INFO: renamed from: d */
    public String getPath() {
        return this.path;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.inputStream.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.closed = true;
        this.inputStream.close();
    }

    protected void finalize() {
        if (!this.closed) {
            GameEngine.logColored("AssetInputStream was finalized with being closed");
            GameEngine.logColored(this.buildVersion);
        }
    }

    public boolean equals(Object obj) {
        return this.inputStream.equals(obj);
    }

    public int hashCode() {
        return this.inputStream.hashCode();
    }

    @Override // java.io.InputStream
    public void mark(int i) {
        this.inputStream.mark(i);
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        return this.inputStream.read(bArr, i, i2);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return this.inputStream.read(bArr);
    }

    @Override // java.io.InputStream
    public void reset() throws IOException {
        this.inputStream.reset();
    }

    @Override // java.io.InputStream
    public long skip(long j) throws IOException {
        return this.inputStream.skip(j);
    }

    public String toString() {
        return this.inputStream.toString();
    }
}
