package android.os;

import android.util.Printer;

/* JADX INFO: loaded from: game-lib.jar:android/os/Looper.class */
public final class Looper {
    static final ThreadLocal a = new ThreadLocal();
    private static Looper d;
    final MessageQueue b;
    final Thread c = Thread.currentThread();
    private Printer e;

    private static void a(boolean z) {
        if (a.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        a.set(new Looper(z));
    }

    public static void a() {
        a(false);
        synchronized (Looper.class) {
            if (d != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            d = d();
        }
    }

    public static Looper b() {
        Looper looper;
        synchronized (Looper.class) {
            looper = d;
        }
        return looper;
    }

    public static void c() {
        Looper looperD = d();
        if (looperD == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        MessageQueue messageQueue = looperD.b;
        while (true) {
            Message messageA = messageQueue.a();
            if (messageA == null) {
                return;
            }
            Printer printer = looperD.e;
            if (printer != null) {
                printer.println(">>>>> Dispatching to " + messageA.j + " " + messageA.k + ": " + messageA.a);
            }
            messageA.j.b(messageA);
            if (printer != null) {
                printer.println("<<<<< Finished to " + messageA.j + " " + messageA.k);
            }
            messageA.c();
        }
    }

    public static Looper d() {
        return (Looper) a.get();
    }

    private Looper(boolean z) {
        this.b = new MessageQueue(z);
    }

    public Thread e() {
        return this.c;
    }

    public String toString() {
        return "Looper (" + this.c.getName() + ", tid " + this.c.getId() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
