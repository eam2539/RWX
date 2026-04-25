package android.net.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/* JADX INFO: renamed from: android.net.http.a */
/* JADX INFO: loaded from: game-lib.jar:android/net/http/a.class */
class CurlLogger implements HttpRequestInterceptor {

    /* JADX INFO: renamed from: a */
    final /* synthetic */ AndroidHttpClient this$0;

    CurlLogger(AndroidHttpClient androidHttpClient) {
        this.this$0 = androidHttpClient;
    }

    @Override // org.apache.http.HttpRequestInterceptor
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        LoggingConfiguration loggingConfiguration = this.this$0.f;
        if (loggingConfiguration != null && loggingConfiguration.isLoggable() && (httpRequest instanceof HttpUriRequest)) {
            loggingConfiguration.println(AndroidHttpClient.b((HttpUriRequest) httpRequest, false));
        }
    }
}
