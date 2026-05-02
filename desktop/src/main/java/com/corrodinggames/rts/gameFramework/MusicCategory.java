package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.file.FileHelper;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.at */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/at.class */
enum MusicCategory {
    starting { // from class: com.corrodinggames.rts.gameFramework.at.1
        @Override // com.corrodinggames.rts.gameFramework.MusicCategory
        /* JADX INFO: renamed from: d */
        String getMusicPath() {
            return "music/starting";
        }
    },
    buildup { // from class: com.corrodinggames.rts.gameFramework.at.2
        @Override // com.corrodinggames.rts.gameFramework.MusicCategory
        /* JADX INFO: renamed from: d */
        String getMusicPath() {
            return "music/buildup";
        }
    },
    attacked { // from class: com.corrodinggames.rts.gameFramework.at.3
        @Override // com.corrodinggames.rts.gameFramework.MusicCategory
        /* JADX INFO: renamed from: d */
        String getMusicPath() {
            return "music/attacked";
        }
    };


    /* JADX INFO: renamed from: d */
    String[] trackPaths;

    /* JADX INFO: renamed from: d */
    abstract String getMusicPath();

    /* JADX INFO: renamed from: a */
    void load() {
        this.trackPaths = FileHelper.listFilesRecursive(getMusicPath(), false);
        if (this.trackPaths == null) {
            this.trackPaths = new String[0];
            GameEngine.reportProblem("Failed to open music folder: " + getMusicPath());
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        ArrayList arrayList = new ArrayList();
        for (String str : this.trackPaths) {
            String strMapPath = FileHelper.mapPath(str);
            if (MusicManager.loadMusic(getFullPath(strMapPath), true) != null) {
                GameEngine.log("Loaded track:" + strMapPath);
                arrayList.add(strMapPath);
            } else {
                GameEngine.updatePaintTextSizeIfNeeded("Skipping track:" + strMapPath);
            }
            gameEngine.loadLevel("music", false);
        }
        this.trackPaths = (String[]) arrayList.toArray(new String[0]);
    }

    /* JADX INFO: renamed from: b */
    String[] getTrackPaths() {
        return this.trackPaths;
    }

    /* JADX INFO: renamed from: c */
    static void loadAll() {
        starting.load();
        buildup.load();
        attacked.load();
    }

    /* JADX INFO: renamed from: a */
    String getFullPath(String str) {
        return getMusicPath() + "/" + str;
    }
}
