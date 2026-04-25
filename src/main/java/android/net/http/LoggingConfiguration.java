package android.net.http;

import android.util.Log;

/* JADX INFO: renamed from: android.net.http.b */
/* JADX INFO: loaded from: game-lib.jar:android/net/http/b.class */
class LoggingConfiguration {

    /* JADX INFO: renamed from: a */
    private  String tag;

    /* JADX INFO: renamed from: b */
    private  int level;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public boolean isLoggable() {
        return Log.isLoggable(this.tag, this.level);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public void println(String str) {
        Log.a(this.level, this.tag, str);
    }
}
