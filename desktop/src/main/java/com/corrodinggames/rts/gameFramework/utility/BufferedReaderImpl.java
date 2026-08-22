package com.corrodinggames.rts.gameFramework.utility;

import java.io.IOException;
import java.io.Reader;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/l.class */
public class BufferedReaderImpl extends Reader {
    /* JADX INFO: renamed from: a */
    private Reader in;

    /* JADX INFO: renamed from: b */
    private char[] buffer;

    /* JADX INFO: renamed from: c */
    private int nextChar;

    /* JADX INFO: renamed from: d */
    private int nChars;

    /* JADX INFO: renamed from: e */
    private int markPos;

    /* JADX INFO: renamed from: f */
    private int readAheadLimit;

    public BufferedReaderImpl(Reader reader) {
        this(reader, 8192);
    }

    public BufferedReaderImpl(Reader reader, int i) {
        super(reader);
        this.markPos = -1;
        this.readAheadLimit = -1;
        if (i <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.in = reader;
        this.buffer = new char[i];
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        synchronized (this.lock) {
            if (!c()) {
                this.in.close();
                this.buffer = null;
            }
        }
    }

    private int b() throws IOException {
        if (this.markPos == -1 || this.nextChar - this.markPos >= this.readAheadLimit) {
            int i = this.in.read(this.buffer, 0, this.buffer.length);
            if (i > 0) {
                this.markPos = -1;
                this.nextChar = 0;
                this.nChars = i;
            }
            return i;
        }
        if (this.markPos == 0 && this.readAheadLimit > this.buffer.length) {
            int length = this.buffer.length * 2;
            if (length > this.readAheadLimit) {
                length = this.readAheadLimit;
            }
            char[] cArr = new char[length];
            System.arraycopy(this.buffer, 0, cArr, 0, this.buffer.length);
            this.buffer = cArr;
        } else if (this.markPos > 0) {
            System.arraycopy(this.buffer, this.markPos, this.buffer, 0, this.buffer.length - this.markPos);
            this.nextChar -= this.markPos;
            this.nChars -= this.markPos;
            this.markPos = 0;
        }
        int i2 = this.in.read(this.buffer, this.nextChar, this.buffer.length - this.nextChar);
        if (i2 != -1) {
            this.nChars += i2;
        }
        return i2;
    }

    private boolean c() {
        return this.buffer == null;
    }

    @Override // java.io.Reader
    public void mark(int i) throws IOException {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        synchronized (this.lock) {
            d();
            this.readAheadLimit = i;
            this.markPos = this.nextChar;
        }
    }

    private void d() throws IOException {
        if (c()) {
            throw new IOException("BufferedReader is closed");
        }
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        synchronized (this.lock) {
            d();
            if (this.nextChar < this.nChars || b() != -1) {
                char[] cArr = this.buffer;
                int i = this.nextChar;
                this.nextChar = i + 1;
                return cArr[i];
            }
            return -1;
        }
    }

    public static void a(int i, int i2, int i3) {
        if ((i2 | i3) < 0 || i2 > i || i - i2 < i3) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override // java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        int i3;
        synchronized (this.lock) {
            d();
            a(cArr.length, i, i2);
            int i4 = i2;
            while (true) {
                if (i4 <= 0) {
                    break;
                }
                int i5 = this.nChars - this.nextChar;
                if (i5 > 0) {
                    int i6 = Math.min(i5, i4);
                    System.arraycopy(this.buffer, this.nextChar, cArr, i, i6);
                    this.nextChar += i6;
                    i += i6;
                    i4 -= i6;
                }
                if (i4 == 0 || (i4 < i2 && !this.in.ready())) {
                    break;
                }
                if ((this.markPos == -1 || this.nextChar - this.markPos >= this.readAheadLimit) && i4 >= this.buffer.length) {
                    int i7 = this.in.read(cArr, i, i4);
                    if (i7 > 0) {
                        i4 -= i7;
                        this.markPos = -1;
                    }
                } else if (b() == -1) {
                    break;
                }
            }
            int i8 = i2 - i4;
            i3 = (i8 > 0 || i8 == i2) ? i8 : -1;
        }
        return i3;
    }

    public String a() throws IOException {
        synchronized (this.lock) {
            d();
            if (this.nextChar == this.nChars && b() == -1) {
                return null;
            }
            for (int i = this.nextChar; i < this.nChars; i++) {
                char c = this.buffer[i];
                if (c <= '\r') {
                    if (c == '\n') {
                        String str = new String(this.buffer, this.nextChar, i - this.nextChar);
                        this.nextChar = i + 1;
                        return str;
                    }
                    if (c == '\r') {
                        String str2 = new String(this.buffer, this.nextChar, i - this.nextChar);
                        this.nextChar = i + 1;
                        if ((this.nextChar < this.nChars || b() != -1) && this.buffer[this.nextChar] == '\n') {
                            this.nextChar++;
                        }
                        return str2;
                    }
                }
            }
            char c2 = 0;
            StringBuilder sb = new StringBuilder(80);
            sb.append(this.buffer, this.nextChar, this.nChars - this.nextChar);
            while (true) {
                this.nextChar = this.nChars;
                if (c2 == '\n') {
                    return sb.toString();
                }
                if (b() == -1) {
                    return (sb.length() > 0 || c2 != 0) ? sb.toString() : null;
                }
                for (int i2 = this.nextChar; i2 < this.nChars; i2++) {
                    char c3 = this.buffer[i2];
                    if (c2 == 0) {
                        if (c3 == '\n' || c3 == '\r') {
                            c2 = c3;
                        }
                    } else {
                        if (c2 == '\r' && c3 == '\n') {
                            if (i2 > this.nextChar) {
                                sb.append(this.buffer, this.nextChar, (i2 - this.nextChar) - 1);
                            }
                            this.nextChar = i2 + 1;
                            return sb.toString();
                        }
                        if (i2 > this.nextChar) {
                            sb.append(this.buffer, this.nextChar, (i2 - this.nextChar) - 1);
                        }
                        this.nextChar = i2;
                        return sb.toString();
                    }
                }
                if (c2 == 0) {
                    sb.append(this.buffer, this.nextChar, this.nChars - this.nextChar);
                } else {
                    sb.append(this.buffer, this.nextChar, (this.nChars - this.nextChar) - 1);
                }
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        boolean z;
        synchronized (this.lock) {
            d();
            z = this.nChars - this.nextChar > 0 || this.in.ready();
        }
        return z;
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        synchronized (this.lock) {
            d();
            if (this.markPos == -1) {
                throw new IOException("Invalid mark");
            }
            this.nextChar = this.markPos;
        }
    }

    @Override // java.io.Reader
    public long skip(long j) throws IOException {
        if (j < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + j);
        }
        synchronized (this.lock) {
            d();
            if (j < 1) {
                return 0L;
            }
            if (this.nChars - this.nextChar >= j) {
                this.nextChar = (int) (((long) this.nextChar) + j);
                return j;
            }
            long j2 = this.nChars - this.nextChar;
            this.nextChar = this.nChars;
            while (j2 < j) {
                if (b() == -1) {
                    return j2;
                }
                if (this.nChars - this.nextChar >= j - j2) {
                    this.nextChar = (int) (((long) this.nextChar) + (j - j2));
                    return j;
                }
                j2 += (long) (this.nChars - this.nextChar);
                this.nextChar = this.nChars;
            }
            return j;
        }
    }
}
