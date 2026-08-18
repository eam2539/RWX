package com.corrodinggames.rts.appFramework;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.corrodinggames.rts.game.GameTeam;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameMode;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/i.class */
public class LevelSelectActivity extends TaskQueueActivity {

    /* JADX INFO: renamed from: c */
    boolean showMapDetails;

    /* JADX INFO: renamed from: d */
    String selectedMap;

    @Override // android.app.Activity
    public void b() {
        super.b();
        AppFrameworkUtils.onActivitySetContentView((Activity) this, true);
    }

    /* JADX INFO: renamed from: d */
    public static String getMapNameFromPath(String str) {
        if (str == null) {
            return null;
        }
        if (str.contains("/MOD|")) {
            return str.substring(str.indexOf("/MOD|"));
        }
        if (str.contains("/NEW_PATH|")) {
            return str.substring(str.indexOf("/NEW_PATH|"));
        }
        String[] strArrSplit = str.split("/");
        return strArrSplit[strArrSplit.length - 1];
    }

    /* JADX INFO: renamed from: a */
    public static boolean isDemoMap(String str, String str2) {
        Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(str);
        if (matcher.matches()) {
            if ((matcher.group(1).toLowerCase(Locale.ENGLISH) + "|").contains("demo|")) {
                return true;
            }
        }
        if (FileHelper.fileExists(str2.replace(".tmx", VariableScope.nullOrMissingString) + "_demo")) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: e */
    public static String getMapName(String str) {
        return AppFrameworkUtils.getMapName(str);
    }

    /* JADX INFO: renamed from: f */
    public static boolean isSkirmishMap(String str) {
        if (str.contains("skirmish/")) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: g */
    public static boolean isFromSdCard(String str) {
        if (str.contains("SD/")) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: a */
    public static void startNewGame(String str, boolean z, int i, int i2, boolean z2, boolean z3) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.gameUI.clearMessages();
        if (z || z3) {
            int i3 = 0;
            gameEngine.stopGameThread();
            synchronized (gameEngine) {
                gameEngine.remoteMapStream = null;
                gameEngine.currentMapPath = str;
                int i4 = PlayerTeam.TEAM_NEUTRAL - 1;
                int numberOfPlayersInMap = AppFrameworkUtils.getNumberOfPlayersInMap(str);
                GameEngine.log("Max teams on map: " + str + " = " + numberOfPlayersInMap);
                if (numberOfPlayersInMap > 0 && numberOfPlayersInMap - 1 < i4) {
                    i4 = numberOfPlayersInMap - 1;
                }
                PlayerTeam.resetTeamRegistry();
                gameEngine.playerTeam = new GameTeam(0);
                gameEngine.playerTeam.teamName = "Player";
                int i5 = 0;
                while (i5 <= 1) {
                    for (int i6 = 1; i6 <= i4; i6++) {
                        boolean z4 = i6 % 2 == 0 || i5 == 1;
                        if (i3 < i2 && z4 && PlayerTeam.k(i6) == null) {
                            AIController aIController = new AIController(i6);
                            aIController.teamName = "AI";
                            aIController.teamColorId = 0;
                            i3++;
                        }
                    }
                    i5++;
                }
                GameEngine.log("Allies: " + i3 + "/" + i2);
                int i7 = 0;
                int i8 = i - i2;
                int i9 = 0;
                while (i9 <= 1) {
                    for (int i10 = 1; i10 <= i4; i10++) {
                        boolean z5 = i10 % 2 == 1 || i9 == 1;
                        if (!z2) {
                            z5 = true;
                        }
                        if (i7 < i8 && z5 && PlayerTeam.k(i10) == null) {
                            AIController aIController2 = new AIController(i10);
                            aIController2.teamName = "AI";
                            i7++;
                            if (z2) {
                                aIController2.teamColorId = 1;
                            }
                        }
                    }
                    i9++;
                }
                gameEngine.networkEngine.updateAiTeamNames();
                if (!z3) {
                    gameEngine.loadGame(false, GameMode.normal);
                }
            }
            return;
        }
        gameEngine.stopGameThread();
        synchronized (gameEngine) {
            gameEngine.remoteMapStream = null;
            gameEngine.currentMapPath = str;
        }
        if (!z3) {
            gameEngine.loadGame(true, GameMode.normal);
        }
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ModInfo linkedModForFile;
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        View view2 = ((AdapterView.AdapterContextMenuInfo) contextMenuInfo).targetView;
        String str = (String) view2.getTag();
        GameEngine gameEngine = GameEngine.getInstance();
        String mapName = getMapName(str);
        if (str != null) {
            linkedModForFile = gameEngine.modManager.getLinkedModForFile(str);
        } else {
            linkedModForFile = null;
        }
        this.selectedMap = str;
        contextMenu.setHeaderTitle(mapName);
        MenuItem menuItemAdd = contextMenu.add(0, view2.getId(), 0, "Export");
        if (linkedModForFile != null) {
            menuItemAdd.setTitle("Export (Standalone maps only)");
            menuItemAdd.setEnabled(false);
        }
        MenuItem menuItemAdd2 = contextMenu.add(2, view2.getId(), 0, "Delete");
        if (linkedModForFile != null) {
            menuItemAdd2.setTitle("Delete (Standalone maps only)");
            menuItemAdd2.setEnabled(false);
        }
        if (linkedModForFile != null) {
            contextMenu.add(4, view2.getId(), 0, "From Mod: " + linkedModForFile.getPaddedTitle()).setEnabled(false);
        }
        if (linkedModForFile == null && this.showMapDetails) {
            MenuItem menuItemAdd3 = contextMenu.add(3, view.getId(), 0, "Storage: " + FileHelper.getStorageTypeForPath(str));
            if (menuItemAdd3 != null) {
                menuItemAdd3.setEnabled(false);
            }
        }
    }
}
