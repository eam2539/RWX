package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.HttpClientManager;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/k.class */
public class JavaHttpClientManager extends HttpClientManager {
    @Override // com.corrodinggames.rts.gameFramework.network.HttpClientManager
    /* JADX INFO: renamed from: a */
    public HttpClient getNewHttpClient(int i, boolean z) {
        RequestConfig.Builder connectionRequestTimeout = RequestConfig.custom().setConnectTimeout(i).setConnectionRequestTimeout(i);
        HttpClientBuilder httpClientBuilderCreate = HttpClientBuilder.create();
        httpClientBuilderCreate.setDefaultRequestConfig(connectionRequestTimeout.build());
        return httpClientBuilderCreate.build();
    }

    @Override // com.corrodinggames.rts.gameFramework.network.HttpClientManager
    /* JADX INFO: renamed from: a */
    public void closeIfAndroidClient(HttpClient httpClient) {
        if (httpClient instanceof CloseableHttpClient) {
            try {
                ((CloseableHttpClient) httpClient).close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        GameEngine.log("closeHttpClient: Didn't close");
    }
}
