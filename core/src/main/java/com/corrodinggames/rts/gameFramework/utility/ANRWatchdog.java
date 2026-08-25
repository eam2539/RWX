package com.corrodinggames.rts.gameFramework.utility;

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
    /* JADX INFO: renamed from: c */
    private ANRCallback anrCallback;

    /* JADX INFO: renamed from: d */
    private InterruptCallback interruptCallback;

    /* JADX INFO: renamed from: f */
    private final int intervalMs;

    private String g;
    private boolean h;
    private boolean i;

    /* JADX INFO: renamed from: j */
    private volatile int tickCount;

    public ANRWatchdog() {
        this(5000);
    }

    public ANRWatchdog(int i) {
        this.anrCallback = a;
        this.interruptCallback = b;
        this.g = VariableScope.nullOrMissingString;
        this.h = false;
        this.i = false;
        this.tickCount = 0;
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
        setName("|ANR-WatchDog|");
        while (!isInterrupted()) {
            try {
                Thread.sleep(this.intervalMs);
                this.tickCount = (this.tickCount + 1) % Integer.MAX_VALUE;
            } catch (InterruptedException e) {
                this.interruptCallback.a(e);
                return;
            }
        }
    }
}
