package net.rudp;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

/* JADX INFO: renamed from: a.a.o */
/* JADX INFO: loaded from: game-lib.jar:a/a/o.class */
class ConnectionInputStream extends InputStream {

    /* JADX INFO: renamed from: a */
    protected ReliableSocket connection;

    /* JADX INFO: renamed from: b */
    protected byte[] readBuffer;

    /* JADX INFO: renamed from: c */
    protected int readOffset;

    /* JADX INFO: renamed from: d */
    protected int bufferedBytes;

    public ConnectionInputStream(ReliableSocket reliableSocket) throws SocketException {
        if (reliableSocket == null) {
            throw new NullPointerException("sock");
        }
        this.connection = reliableSocket;
        this.readBuffer = new byte[this.connection.getReceiveBufferSize()];
        this.bufferedBytes = 0;
        this.readOffset = 0;
    }

    @Override // java.io.InputStream
    public synchronized int read() throws IOException {
        if (fillReceiveBuffer() < 0) {
            return -1;
        }
        byte[] bArr = this.readBuffer;
        int i = this.readOffset;
        this.readOffset = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public synchronized int read(byte[] bArr, int i, int i2) throws IOException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
            throw new IndexOutOfBoundsException();
        }
        if (fillReceiveBuffer() < 0) {
            return -1;
        }
        int iMin = Math.min(available(), i2);
        System.arraycopy(this.readBuffer, this.readOffset, bArr, i, iMin);
        this.readOffset += iMin;
        return iMin;
    }

    @Override // java.io.InputStream
    public synchronized int available() {
        return this.bufferedBytes - this.readOffset;
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws SocketException {
        this.connection.shutdownInput();
    }

    /* JADX INFO: renamed from: a */
    private int fillReceiveBuffer() throws IOException {
        if (available() == 0) {
            this.bufferedBytes = this.connection.receiveDatagramBytes(this.readBuffer, 0, this.readBuffer.length);
            this.readOffset = 0;
        }
        return this.bufferedBytes;
    }
}
