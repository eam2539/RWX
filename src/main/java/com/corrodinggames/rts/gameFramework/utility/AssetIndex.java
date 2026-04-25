package com.corrodinggames.rts.gameFramework.utility;

import android.content.Context;
import android.content.res.AssetManager;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/i.class */
public final class AssetIndex {

    /* JADX INFO: renamed from: c */
    private ArrayList assetList;
    public Context a;

    /* JADX INFO: renamed from: b */
    boolean firstLoad = true;

    public AssetIndex(Context context) {
        this.a = context;
        loadIndex();
    }

    /* JADX INFO: renamed from: a */
    public void loadIndex() {
        new Thread() { // from class: com.corrodinggames.rts.gameFramework.utility.i.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                AssetIndex.this.buildIndex();
            }
        }.start();
    }

    /* JADX INFO: renamed from: b */
    public void buildIndex() {
        synchronized (this) {
            if (this.assetList != null) {
                return;
            }
            AssetManager assetManagerD = this.a.d();
            ArrayList arrayList = new ArrayList();
            try {
                GameEngine.isInSpace("------- createIndex -------");
                arrayList.addAll(findIndexFiles(assetManagerD, VariableScope.nullOrMissingString, 1));
                this.assetList = arrayList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public ArrayList findIndexFiles(AssetManager assetManager, String str, int i) {
        ArrayList arrayList = new ArrayList();
        String[] strArrC = assetManager.c(str);
        if (str.length() > 0) {
            str = str + "/";
        }
        if (i > 140) {
            throw new RuntimeException("dirLevel>140 for: " + str);
        }
        GameEngine.isInSpace("c:" + str);
        for (String str2 : strArrC) {
            String str3 = str + str2;
            boolean z = false;
            if (!str2.contains(".")) {
                z = true;
            }
            if (!str2.equals(".") && !str2.equals("..") && !str2.equals(VariableScope.nullOrMissingString)) {
                arrayList.add(str3);
                if (z) {
                    arrayList.addAll(findIndexFiles(assetManager, str3, i + 1));
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: renamed from: c */
    public ArrayList<String> getAssetList() {
        if (this.assetList != null) {
            if (this.firstLoad) {
                GameEngine.isInSpace("assetIndex: getFile was not blocked on load");
                this.firstLoad = false;
            }
            return this.assetList;
        }
        GameEngine.getCurrentTimeMillis();
        buildIndex();
        if (this.firstLoad) {
            GameEngine.isInSpace("assetIndex: getFile is BLOCKED on load");
            this.firstLoad = false;
        }
        return this.assetList;
    }

    /* JADX INFO: renamed from: a */
    public boolean exists(String str) {
        if (str.endsWith(File.separator)) {
            str = str.substring(0, str.length() - 1);
        }
        String strReplace = str.replace("//", "/");
        Iterator it = getAssetList().iterator();
        while (it.hasNext()) {
            if (((String) it.next()).equals(strReplace)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: renamed from: b */
    public String[] listDir(String str) {
        ArrayList arrayList = new ArrayList();
        String strSubstring = str;
        if (strSubstring.endsWith(File.separator)) {
            strSubstring = strSubstring.substring(0, strSubstring.length() - 1);
        }
        int i = 0;
        for (String str2 : getAssetList()) {
            if (str2.startsWith(strSubstring)) {
                String strSubstring2 = str2.substring(strSubstring.length());
                if (strSubstring2.length() != 0 && strSubstring2.charAt(0) == File.separatorChar && strSubstring2.indexOf(File.separator, 1) == -1) {
                    i++;
                    arrayList.add(str2.substring((strSubstring + "/").length()));
                }
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }
}
