package com.corrodinggames.rts.appFramework;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/q.class */
public class ReplayBrowserActivity extends TaskQueueActivity {

    /* JADX INFO: renamed from: c */
    String[] replayFiles;

    @Override // android.app.Activity
    public void b() {
        super.b();
        AppFrameworkUtils.onActivitySetContentView((Activity) this, true);
    }

    /* JADX INFO: renamed from: l */
    public static String[] getReplayFiles() {
        String[] strArrListFiles = FileHelper.listFiles("/SD/rustedWarfare/replays/");
        if (strArrListFiles == null) {
            GameEngine.isInSpace("failed to find replay folder");
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (String str : strArrListFiles) {
            if (!str.endsWith(".map")) {
                arrayList.add(str);
            }
        }
        Collections.sort(arrayList, new ReplayDateComparator());
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
            MenuItem menuItemAdd = contextMenu.add(3, view.getId(), 0, "Storage: " + FileHelper.getAbsolutePath(this.replayFiles[view.getId()]));
            if (menuItemAdd != null) {
                menuItemAdd.setEnabled(false);
            }
        }
    }
}
