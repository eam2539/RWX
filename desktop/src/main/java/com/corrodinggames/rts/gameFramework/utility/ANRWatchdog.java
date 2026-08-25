package com.corrodinggames.rts.gameFramework.utility;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/d.class */
public class ANRWatchdog extends Thread {
    private static final ANRCallback a = new ANRCallback() { // from class: com.corrodinggames.rts.gameFramework.utility.d.1
        @Override // com.corrodinggames.rts.gameFramework.utility.ANRCallback
        public void a(ANRException aNRException) {
            throw aNRException;
        }
    };
    private static final InterruptCallback b = new InterruptCallback() { // from class: com.corrodinggames.rts.gameFramework.utility.d.2
        @Override // com.corrodinggames.rts.gameFramework.utility.InterruptCallback
        public void a(InterruptedException interruptedException) {
            Log.c("ANRWatchdog", "Interrupted: " + interruptedException.getMessage());
        }
    };
    private final int intervalMs;
    private ANRCallback anrCallback;
    private final Handler e;
    private InterruptCallback interruptCallback;
    private String g;
    private boolean h;
    private boolean i;
    private volatile int tickCount;
    private final Runnable k;

    public ANRWatchdog() {
        this(5000);
    }

    public ANRWatchdog(int i) {
        this.anrCallback = a;
        this.interruptCallback = b;
        this.e = new Handler(Looper.b());
        this.g = VariableScope.nullOrMissingString;
        this.h = false;
        this.i = false;
        this.tickCount = 0;
        this.k = new Runnable() { // from class: com.corrodinggames.rts.gameFramework.utility.d.3
            @Override // java.lang.Runnable
            public void run() {
                ANRWatchdog.this.tickCount = (ANRWatchdog.this.tickCount + 1) % Integer.MAX_VALUE;
            }
        };
        this.intervalMs = i;
    }

    public ANRWatchdog a(ANRCallback aNRCallback) {
        if (aNRCallback == null) {
            this.anrCallback = a;
        } else {
            this.anrCallback = aNRCallback;
        }
        return this;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        ANRException aNRExceptionA;
        setName("|ANR-WatchDog|");
        int i = -1;
        while (!isInterrupted()) {
            int i2 = this.tickCount;
            this.e.a(this.k);
            try {
                Thread.sleep(this.intervalMs);
                if (this.tickCount == i2) {
                    if (!this.i && Debug.isDebuggerConnected()) {
                        if (this.tickCount != i) {
                            Log.c("ANRWatchdog", "An ANR was detected but ignored because the debugger is connected (you can prevent this with setIgnoreDebugger(true))");
                        }
                        i = this.tickCount;
                    } else {
                        if (this.g != null) {
                            aNRExceptionA = ANRException.a(this.g, this.h);
                        } else {
                            aNRExceptionA = ANRException.a();
                        }
                        this.anrCallback.a(aNRExceptionA);
                        return;
                    }
                }
            } catch (InterruptedException e) {
                this.interruptCallback.a(e);
                return;
            }
        }
    }
}
