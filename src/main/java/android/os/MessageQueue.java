package android.os;

import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: loaded from: game-lib.jar:android/os/MessageQueue.class */
public final class MessageQueue {
    private final boolean c;
    Message a;
    private IdleHandler[] f;
    private boolean g;
    private boolean h;
    static Object b = new Object();
    private final ArrayList e = new ArrayList();
    private long d = b();

    /* JADX INFO: loaded from: game-lib.jar:android/os/MessageQueue$IdleHandler.class */
    public interface IdleHandler {
        boolean a();
    }

    private long b() {
        return 100L;
    }

    private void a(long j) {
    }

    private void a(long j, int i) {
        synchronized (b) {
            try {
                if (i < 0) {
                    b.wait();
                } else {
                    b.wait(i);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void b(long j) {
        synchronized (b) {
            b.notifyAll();
        }
    }

    MessageQueue(boolean z) {
        this.c = z;
    }

    protected void finalize() throws Throwable {
        try {
            c();
        } finally {
            super.finalize();
        }
    }

    private void c() {
        if (this.d != 0) {
            a(this.d);
            this.d = 0L;
        }
    }

    android.os.Message a() {
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 != 0) {
                a(this.d, i2);
            }
            IdleHandler[] idleHandlerArr = null;
            synchronized (this) {
                long jA = SystemClock.a();
                Message message = this.a;
                Message message2 = null;
                if (message != null && message.j == null) {
                    do {
                        message2 = message;
                        message = message.l;
                    } while (message != null && !message.e());
                }
                if (message != null) {
                    if (jA < message.h) {
                        long j = message.h - jA;
                        i2 = j > 2147483647L ? Integer.MAX_VALUE : (int) j;
                    } else {
                        this.h = false;
                        if (message2 != null) {
                            message2.l = message.l;
                        } else {
                            this.a = message.l;
                        }
                        message.l = null;
                        return message;
                    }
                } else {
                    i2 = -1;
                }
                if (this.g) {
                    c();
                    return null;
                }
                if (i < 0 && (this.a == null || jA < this.a.h)) {
                    i = this.e.size();
                }
                if (i <= 0) {
                    this.h = true;
                } else {
                    if (this.f == null || this.f.length < i) {
                        this.f = new IdleHandler[i];
                    }
                    this.e.toArray(this.f);
                    idleHandlerArr = this.f;
                }
            }
            if (idleHandlerArr != null) {
                for (int i3 = 0; i3 < i; i3++) {
                    IdleHandler idleHandler = idleHandlerArr[i3];
                    idleHandlerArr[i3] = null;
                    boolean z = false;
                    try {
                        z = idleHandler.a();
                    } catch (Throwable th) {
                        Log.c("MessageQueue", "IdleHandler threw exception", th);
                    }
                    if (!z) {
                        synchronized (this) {
                            this.e.remove(idleHandler);
                        }
                    }
                }
                i = 0;
                i2 = 0;
            } else {
                i = -1;
            }
        }
    }

    boolean a(Message message, long j) {
        Message message2;
        if (message.j == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (message.f()) {
            throw new IllegalStateException(message + " This message is already in use.");
        }
        synchronized (this) {
            if (this.g) {
                IllegalStateException illegalStateException = new IllegalStateException(message.j + " sending message to a Handler on a dead thread");
                Log.a("MessageQueue", illegalStateException.getMessage(), illegalStateException);
                message.b();
                return false;
            }
            message.g();
            message.h = j;
            Message message3 = this.a;
            if (message3 == null || j == 0 || j < message3.h) {
                message.l = message3;
                this.a = message;
                boolean z = this.h;
            } else {
                boolean z2 = this.h && message3.j == null && message.e();
                while (true) {
                    message2 = message3;
                    message3 = message3.l;
                    if (message3 == null || j < message3.h) {
                        break;
                    }
                    if (z2 && message3.e()) {
                        z2 = false;
                    }
                }
                message.l = message3;
                message2.l = message;
            }
            b(this.d);
            return true;
        }
    }
}
