package io.github.rwx.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameMode;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.statistics.AndroidMenu;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import io.github.rwx.mod.UiRegistry;

import java.io.IOException;

/* Core facade for legacy game callbacks while in-game menus are rendered by the game UI. */
public class InGameMenuController {
    private static final boolean DEBUG_SLICK_MENU = "1".equals(System.getenv("RWX_DEBUG_SLICK_MENU"));
    private boolean finishing;
    private InGameMenuCallbacks callbacks;

    public InGameMenuController() {
        this(InGameMenuCallbacks.CORE_UI_EVENT_QUEUE);
    }

    public InGameMenuController(InGameMenuCallbacks callbacks) {
        setCallbacks(callbacks);
    }

    public void setCallbacks(InGameMenuCallbacks callbacks) {
        this.callbacks = callbacks != null ? callbacks : InGameMenuCallbacks.CORE_UI_EVENT_QUEUE;
    }

    public void b() {
        GameEngine.log("InGameMenuController finish");
        this.finishing = true;
    }

    public boolean c() {
        return this.finishing;
    }

    public boolean a(AndroidMenu menu) {
        if (menu != null) {
            menu.clear();
            GameEngine gameEngine = GameEngine.getInstance();
            menu.add(0, 12, 0, Locale.get("menus.ingame.save"));
            if (gameEngine.isGameStarted && !GameEngine.isIOSVersion) {
                menu.add(0, 18, 0, Locale.get("menus.ingame.exportMap"));
            }
            if ((gameEngine.missionEngine != null && gameEngine.missionEngine.hasMapPortalMode())
                    || this.callbacks.shouldShowMapList()) {
                menu.add(0, 24, 0, "RWX Maps");
            }
            menu.add(0, 2, 0, Locale.get("menus.ingame.settings"));
            if (gameEngine.replayEngine != null && gameEngine.replayEngine.j()) {
                menu.add(0, 22, 0, Locale.get("menus.ingame.hideInterface"));
            }
            if (gameEngine.isNetworkConnected()) {
                menu.add(0, 13, 0, Locale.get("menus.ingame.chat"));
                menu.add(0, 14, 0, Locale.get("menus.ingame.players"));
                if (gameEngine.networkEngine.isServer && DisabledSteamEngine.a().e()) {
                    menu.add(0, 17, 0, Locale.get("menus.ingame.steam_reinvite"));
                }
                boolean defeated = gameEngine.playerTeam != null && gameEngine.playerTeam.isTeamWipedOut;
                if (!defeated && !gameEngine.hasWonGame) {
                    menu.add(0, 19, 0, Locale.get("menus.ingame.surrender"));
                }
                if (!gameEngine.networkEngine.isServer) {
                    menu.add(0, 10, 0, Locale.get("menus.ingame.disconnect"));
                } else {
                    menu.add(0, 10, 0, Locale.get("menus.ingame.exitGame"));
                }
            } else {
                if (gameEngine.missionEngine != null && gameEngine.missionEngine.h != null) {
                    menu.add(0, 11, 0, Locale.get("menus.ingame.briefing"));
                }
                menu.add(0, 15, 0, Locale.get("menus.ingame.exitGame"));
            }
            for (UiRegistry.InGameMenuItem item : UiRegistry.getInGameMenuItems()) {
                menu.add(0, item.getMenuId(), 0, item.getLabel());
            }
            if (gameEngine.settingsEngine.allowGameRecording) {
                if (!gameEngine.isGameRecording) {
                    menu.add(0, 9, 0, "Start Recording");
                } else {
                    menu.add(0, 9, 0, "Stop Recording");
                }
            }
        }
        return true;
    }

    public void selectMenuOption(int i) {
        selectMenuOption(i, GameEngine.getInstance());
    }

    public void selectMenuOption(int i, GameEngine gameEngine) {
        if (DEBUG_SLICK_MENU) {
            GameEngine.log("RWX_DEBUG_SLICK_MENU InGameMenuController.selectMenuOption id=" + i
                    + " engine=" + System.identityHashCode(gameEngine)
                    + " callbacks=" + this.callbacks.getClass().getName());
        }
        onSelectMenuOption(i, gameEngine);
    }

    public void onSelectMenuOption(int i) {
        onSelectMenuOption(i, GameEngine.getInstance());
    }

    public void onSelectMenuOption(int i, GameEngine gameEngine) {
        if (gameEngine == null) {
            GameEngine.log("InGameMenuController ignored menu option without game engine: " + i);
            return;
        }
        if (UiRegistry.handleInGameMenuSelection(i)) {
            return;
        }
        switch (i) {
            case 2:
                closeInGameMenu(gameEngine);
                this.callbacks.requestSettings();
                return;
            case 5:
                closeInGameMenu(gameEngine);
                gameEngine.stopGameThread();
                gameEngine.loadGame(true, GameMode.normal);
                gameEngine.startGameThread();
                return;
            case 9:
                gameEngine.isGameRecording = !gameEngine.isGameRecording;
                return;
            case 10:
                closeInGameMenu(gameEngine);
                this.callbacks.requestExit();
                return;
            case 11:
                if (gameEngine.missionEngine != null && gameEngine.missionEngine.h != null) {
                    gameEngine.showMessageBox("Briefing", gameEngine.missionEngine.h);
                }
                return;
            case 12:
                closeInGameMenu(gameEngine);
                if (DEBUG_SLICK_MENU) {
                    GameEngine.log("RWX_DEBUG_SLICK_MENU InGameMenuController.requestSave engine="
                            + System.identityHashCode(gameEngine)
                            + " draggingAfterClose=" + (gameEngine.gameUI != null && gameEngine.gameUI.isDraggingSelection));
                }
                this.callbacks.requestSave();
                return;
            case 18:
                closeInGameMenu(gameEngine);
                this.callbacks.requestExportMap();
                return;
            case 13:
            case 16:
                closeInGameMenu(gameEngine);
                this.callbacks.requestChat(i == 16);
                return;
            case 14:
                closeInGameMenu(gameEngine);
                this.callbacks.requestPlayerList();
                return;
            case 15:
                closeInGameMenu(gameEngine);
                b();
                this.callbacks.requestExit();
                return;
            case 17:
                closeInGameMenu(gameEngine);
                DisabledSteamEngine.a().g();
                return;
            case 19:
                closeInGameMenu(gameEngine);
                this.callbacks.requestSurrender();
                return;
            case 20:
                closeInGameMenu(gameEngine);
                b();
                this.callbacks.requestExit();
                return;
            case 21:
                closeInGameMenu(gameEngine);
                b();
                this.callbacks.requestReturnToBattleRoom();
                return;
            case 22:
                gameEngine.isMenuOpen = true;
                gameEngine.gameUI.isDraggingSelection = false;
                return;
            case 23:
                if (gameEngine.teamStats != null) {
                    gameEngine.teamStats.nextSort();
                }
                return;
            case 24:
                closeInGameMenu(gameEngine);
                this.callbacks.requestMapList();
                return;
            default:
                GameEngine.log("InGameMenuController ignored menu option: " + i);
        }
    }

    private static void closeInGameMenu(GameEngine gameEngine) {
        if (gameEngine != null && gameEngine.gameUI != null) {
            gameEngine.gameUI.isDraggingSelection = false;
        }
    }

    public void d(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.stopGameThread();
        try {
            gameEngine.gameSaver.saveGame(str, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            gameEngine.startGameThread();
        }
    }

    public void l() {
        GameEngine.log("Rate-game action requested; platform store UI is not available in core");
    }

    public void m() {
        this.callbacks.requestExit();
    }
}
