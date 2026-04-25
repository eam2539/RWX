package com.corrodinggames.rts.gameFramework.network;

import android.net.http.AndroidHttpClient;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.r */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/r.class */
public class HttpClientManager {

    /* JADX INFO: renamed from: a */
    ReentrantLock clientLock = new ReentrantLock();

    /* JADX INFO: renamed from: a */
    public HttpClient getAndroidHttpClient() {
        return getNewHttpClient(30000, false);
    }

    /* JADX INFO: renamed from: b */
    public HttpClient getDefaultHttpClient() {
        return getNewHttpClient(30000, true);
    }

    /* JADX INFO: renamed from: a */
    public HttpClient getNewHttpClient(int i, boolean z) {
        HttpClient defaultHttpClient;
        boolean zTryLock = false;
        try {
            zTryLock = this.clientLock.tryLock(300L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!zTryLock) {
            try {
                GameEngine.log("LoadFromMasterServer", "Could not get getNewHttpClient lock! another thread maybe stuck in getNewHttpClient!");
            } catch (Throwable th) {
                if (zTryLock) {
                    this.clientLock.unlock();
                }
                throw th;
            }
        }
        if (!z) {
            defaultHttpClient = AndroidHttpClient.a((String) null);
        } else {
            defaultHttpClient = new DefaultHttpClient();
        }
        HttpParams params = defaultHttpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, i);
        HttpConnectionParams.setSoTimeout(params, 20000);
        if (zTryLock) {
            this.clientLock.unlock();
        }
        return defaultHttpClient;
    }

    /* JADX INFO: renamed from: a */
    public void closeIfAndroidClient(HttpClient httpClient) {
        if (httpClient instanceof AndroidHttpClient) {
            ((AndroidHttpClient) httpClient).a();
        }
    }
}
