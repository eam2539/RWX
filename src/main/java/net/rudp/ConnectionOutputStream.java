package net.rudp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

/* JADX INFO: renamed from: a.a.q */
/* JADX INFO: loaded from: game-lib.jar:a/a/q.class */
class ConnectionOutputStream extends OutputStream {

    /* JADX INFO: renamed from: a */
    protected ReliableSocket connection;

    /* JADX INFO: renamed from: b */
    protected byte[] writeBuffer;

    /* JADX INFO: renamed from: c */
    protected int bufferedBytes;

    public ConnectionOutputStream(ReliableSocket reliableSocket) throws SocketException {
        if (reliableSocket == null) {
            throw new NullPointerException("sock");
        }
        this.connection = reliableSocket;
        this.writeBuffer = new byte[this.connection.getSendBufferSize()];
        this.bufferedBytes = 0;
    }

    @Override // java.io.OutputStream
    public synchronized void write(int i) throws IOException {
        if (this.bufferedBytes >= this.writeBuffer.length) {
            flush();
        }
        byte[] bArr = this.writeBuffer;
        int i2 = this.bufferedBytes;
        this.bufferedBytes = i2 + 1;
        bArr[i2] = (byte) (i & 255);
    }

    @Override // java.io.OutputStream
    public synchronized void write(byte[] bArr) throws IOException {
        write(bArr, 0, bArr.length);
    }

    @Override // java.io.OutputStream
    public synchronized void write(byte[] bArr, int i, int i2) throws IOException {
        if (bArr == null) {
            throw new NullPointerException();
        }
        if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
            throw new IndexOutOfBoundsException();
        }
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 < i2) {
                int iMin = Math.min(this.writeBuffer.length, i2 - i4);
                if (iMin > this.writeBuffer.length - this.bufferedBytes) {
                    flush();
                }
                System.arraycopy(bArr, i + i4, this.writeBuffer, this.bufferedBytes, iMin);
                this.bufferedBytes += iMin;
                i3 = i4 + iMin;
            } else {
                return;
            }
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public synchronized void flush() throws IOException {
        if (this.bufferedBytes > 0) {
            this.connection.sendDatagramBytes(this.writeBuffer, 0, this.bufferedBytes);
            this.bufferedBytes = 0;
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        flush();
        this.connection.shutdownOutput();
    }
}
