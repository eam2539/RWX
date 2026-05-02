package android.net.http;

import java.util.ArrayList;

/* JADX INFO: loaded from: game-lib.jar:android/net/http/Headers.class */
public final class Headers {
    private static final String[] f = {"transfer-encoding", "content-length", "content-type", "content-encoding", "connection", "location", "proxy-connection", "www-authenticate", "proxy-authenticate", "content-disposition", "accept-ranges", "expires", "cache-control", "last-modified", "etag", "set-cookie", "pragma", "refresh", "x-permitted-cross-domain-policies"};
    private ArrayList d = new ArrayList(2);
    private String[] e = new String[19];
    private ArrayList g = new ArrayList(4);
    private ArrayList h = new ArrayList(4);
    private long a = 0;
    private long b = -1;
    private int c = 0;

    /* JADX INFO: loaded from: game-lib.jar:android/net/http/Headers$HeaderCallback.class */
    public interface HeaderCallback {
    }
}
