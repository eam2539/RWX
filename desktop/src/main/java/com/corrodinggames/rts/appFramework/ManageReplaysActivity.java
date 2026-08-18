package com.corrodinggames.rts.appFramework;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/j.class */
public class ManageReplaysActivity extends TaskQueueActivity {

    /* JADX INFO: renamed from: c */
    String[] replayFiles;

    @Override // android.app.Activity
    public void b() {
        super.b();
        AppFrameworkUtils.onActivitySetContentView((Activity) this, true);
    }

    /* JADX INFO: renamed from: l */
    public static String[] getReplayFiles() {
        String[] strArrListFilesRecursive = FileHelper.listFilesRecursive("/SD/rustedWarfare/saves/", false);
        if (strArrListFilesRecursive == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (String str : strArrListFilesRecursive) {
            if (!str.endsWith(".map") && !str.endsWith(".tmp")) {
                arrayList.add(str);
            }
        }
        Collections.sort(arrayList, new ReplayComparator());
        return (String[]) arrayList.toArray(new String[0]);
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        contextMenu.setHeaderTitle(((Button) view).getText());
        contextMenu.add(0, view.getId(), 0, "Share");
        contextMenu.add(1, view.getId(), 0, "Rename");
        contextMenu.add(2, view.getId(), 0, "Delete");
        if (this.replayFiles != null && this.replayFiles.length > 0) {
            MenuItem menuItemAdd = contextMenu.add(3, view.getId(), 0, "Storage: " + FileHelper.getStorageTypeForPath(this.replayFiles[view.getId()]));
            if (menuItemAdd != null) {
                menuItemAdd.setEnabled(false);
            }
        }
    }
}
