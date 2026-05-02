package android.content.res;

import android.os.ParcelFileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/* JADX INFO: loaded from: game-lib.jar:android/content/res/AssetManager.class */
public final class AssetManager {
    private static final Object b = new Object();
    static AssetManager a = null;
    private final long[] c = new long[2];
    private int d = 1;
    private boolean e = true;

    private final native ParcelFileDescriptor openAssetFd(String str, long[] jArr);

    /* JADX INFO: Access modifiers changed from: private */
    public final native void destroyAsset(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native int readAssetChar(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final native int readAsset(int i, byte[] bArr, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long seekAsset(int i, long j, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public final native long getAssetRemainingLength(int i);

    public final InputStream a(String str) throws FileNotFoundException {
        return a(str, 2);
    }

    public final InputStream a(String str, int i) throws FileNotFoundException {
        FileInputStream fileInputStream;
        synchronized (this) {
            if (!this.e) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            fileInputStream = new FileInputStream("assets/" + str);
        }
        return fileInputStream;
    }

    public final AssetFileDescriptor b(String str) throws FileNotFoundException {
        synchronized (this) {
            if (!this.e) {
                throw new RuntimeException("Assetmanager has been closed");
            }
            ParcelFileDescriptor parcelFileDescriptorOpenAssetFd = openAssetFd(str, this.c);
            if (parcelFileDescriptorOpenAssetFd != null) {
                return new AssetFileDescriptor(parcelFileDescriptorOpenAssetFd, this.c[0], this.c[1]);
            }
            throw new FileNotFoundException("Asset file: " + str);
        }
    }

    public final String[] c(String str) {
        return new String[0];
    }

    protected void finalize() throws Throwable {
        try {
            a();
        } finally {
            super.finalize();
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:android/content/res/AssetManager$AssetInputStream.class */
    public final class AssetInputStream extends InputStream {
        private int b;
        private long c;
        private long d;
         /* synthetic */ AssetManager a;

        @Override // java.io.InputStream
        public final int read() {
            return this.a.readAssetChar(this.b);
        }

        @Override // java.io.InputStream
        public final boolean markSupported() {
            return true;
        }

        @Override // java.io.InputStream
        public final int available() {
            long assetRemainingLength = this.a.getAssetRemainingLength(this.b);
            if (assetRemainingLength > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            return (int) assetRemainingLength;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public final void close() {
            synchronized (this.a) {
                if (this.b != 0) {
                    this.a.destroyAsset(this.b);
                    this.b = 0;
                    this.a.a(hashCode());
                }
            }
        }

        @Override // java.io.InputStream
        public final void mark(int i) {
            this.d = this.a.seekAsset(this.b, 0L, 0);
        }

        @Override // java.io.InputStream
        public final void reset() {
            this.a.seekAsset(this.b, this.d, -1);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr) {
            return this.a.readAsset(this.b, bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public final int read(byte[] bArr, int i, int i2) {
            return this.a.readAsset(this.b, bArr, i, i2);
        }

        @Override // java.io.InputStream
        public final long skip(long j) {
            long jSeekAsset = this.a.seekAsset(this.b, 0L, 0);
            if (jSeekAsset + j > this.c) {
                j = this.c - jSeekAsset;
            }
            if (j > 0) {
                this.a.seekAsset(this.b, j, 0);
            }
            return j;
        }

        protected void finalize() {
            close();
        }
    }

    private final void a() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void a(int i) {
        this.d--;
        if (this.d == 0) {
            a();
        }
    }
}
