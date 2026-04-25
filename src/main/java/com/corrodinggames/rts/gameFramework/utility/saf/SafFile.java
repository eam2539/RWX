package com.corrodinggames.rts.gameFramework.utility.saf;

import android.content.Context;
import android.net.Uri;
import com.corrodinggames.rts.appFramework.AppFrameworkUtils;
import com.corrodinggames.rts.appFramework.common.SAFInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/a/c.class */
class SafFile {
    String a;
    Uri b;
    boolean c;
    HashMap d;
    HashMap e;
    boolean f;
    int g;
    final /* synthetic */ SafFileSystem h;

    public SafFile(SafFileSystem safFileSystem, String str, Uri uri, boolean z) {
        this.h = safFileSystem;
        this.a = str;
        this.b = uri;
        this.c = z;
    }

    public HashMap a() throws IOException {
        if (this.d == null || this.f || this.g != this.h.g) {
            synchronized (this) {
                if (this.d == null || this.f || this.g != this.h.g) {
                    a(AppFrameworkUtils.getContext());
                }
            }
        }
        return this.d;
    }

    public void a(Context context) throws IOException {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        if (this.c) {
            for (SAFInterface.FileRow fileRow : SafFileLoader.a.listWithDetails(context, this.b)) {
                Uri childUri = SafFileLoader.a.getChildUri(this.b, fileRow.id);
                String strReplace = fileRow.name;
                boolean z = fileRow.isDirectory;
                if (strReplace.contains("/")) {
                    SafFileLoader.h("Name contains symbols: " + strReplace);
                    strReplace = strReplace.replace("/", "_");
                }
                SafFile safFile = new SafFile(this.h, this.a + "/" + strReplace, childUri, z);
                map.put(strReplace, safFile);
                String lowerCase = strReplace.toLowerCase(Locale.ROOT);
                if (map2.get(lowerCase) == null) {
                    map2.put(lowerCase, safFile);
                }
            }
        }
        this.d = map;
        this.e = map2;
        this.f = false;
        this.g = this.h.g;
    }
}
