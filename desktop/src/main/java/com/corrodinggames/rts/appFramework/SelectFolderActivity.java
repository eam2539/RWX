package com.corrodinggames.rts.appFramework;

import android.net.Uri;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.s */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/s.class */
public class SelectFolderActivity extends TaskQueueActivity {

    /* JADX INFO: renamed from: d */
    static boolean isShowing = false;

    /* JADX INFO: renamed from: c */
    boolean finishAfterSelect = true;

    /* JADX INFO: renamed from: e */
    int[] delayTimeouts = {100, 250, 500, 1000, 2000, 5000, 10000};

    /* JADX INFO: renamed from: f */
    ActivityObject activityObject = new ActivityObject() { // from class: com.corrodinggames.rts.appFramework.s.1
    };

    /* JADX INFO: renamed from: l */
    public void showFolderChooser() {
        AppFrameworkUtils.showSAFDirectoryChooser(this, 9, true, "Select a Rusted Warfare Folder to use", Uri.parse("content://com.android.externalstorage.documents/document/primary%3A" + "rustedWarfare".replace("//", "%2F")));
    }
}
