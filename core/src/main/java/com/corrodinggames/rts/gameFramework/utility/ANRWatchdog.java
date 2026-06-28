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
    private ANRCallback c;
    private InterruptCallback d;
    private final int f;
    private String g;
    private boolean h;
    private boolean i;
    private volatile int j;

    public ANRWatchdog() {
        this(5000);
    }

    public ANRWatchdog(int i) {
        this.c = a;
        this.d = b;
        this.g = VariableScope.nullOrMissingString;
        this.h = false;
        this.i = false;
        this.j = 0;
        this.f = i;
    }

    public ANRWatchdog a(ANRCallback aNRCallback) {
        if (aNRCallback == null) {
            this.c = a;
        } else {
            this.c = aNRCallback;
        }
        return this;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        setName("|ANR-WatchDog|");
        while (!isInterrupted()) {
            try {
                Thread.sleep(this.f);
                this.j = (this.j + 1) % Integer.MAX_VALUE;
            } catch (InterruptedException e) {
                this.d.a(e);
                return;
            }
        }
    }
}
