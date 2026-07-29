package com.corrodinggames.rts.gameFramework.utility;

import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.af */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/af.class */
public abstract class IFileLoader {
    /* JADX INFO: renamed from: a */
    public abstract boolean exists(String str);

    /* JADX INFO: renamed from: b */
    public abstract String[] listDir(String str);

    /* JADX INFO: renamed from: a */
    public abstract long getSize(String str, boolean z);

    /* JADX INFO: renamed from: b */
    public abstract AssetInputStream openAssetInputStream(String str, boolean z);

    /* JADX INFO: renamed from: c */
    public abstract OutputStream openOutputStream(String str, boolean z);

    /* JADX INFO: renamed from: a */
    public abstract boolean rename(String str, String str2);

    /* JADX INFO: renamed from: c */
    public abstract boolean delete(String str);

    /* JADX INFO: renamed from: d */
    public abstract boolean isDirectory(String str);

    /* JADX INFO: renamed from: e */
    public abstract boolean createDirectory(String str);

    /* JADX INFO: renamed from: a */
    public abstract void closeAll();

    /* JADX INFO: renamed from: f */
    public abstract String convertAbstractPathForDebug(String str);

    /* JADX INFO: renamed from: g */
    public abstract long getLastModified(String str);
}
