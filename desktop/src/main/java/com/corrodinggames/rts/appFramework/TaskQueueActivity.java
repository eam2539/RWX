package com.corrodinggames.rts.appFramework;

import android.app.Activity;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/b.class */
public class TaskQueueActivity extends Activity {
    ArrayList b = new ArrayList();

    public void a(Runnable runnable) {
        synchronized (this.b) {
            this.b.add(runnable);
        }
    }
}
