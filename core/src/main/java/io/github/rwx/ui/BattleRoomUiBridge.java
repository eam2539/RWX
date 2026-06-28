package io.github.rwx.ui;

import com.corrodinggames.rts.game.GameTeam;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameMode;
import com.corrodinggames.rts.gameFramework.network.GameModeType;
import com.corrodinggames.rts.gameFramework.network.PasswordHandler;

public class BattleRoomUiBridge {
    public static boolean startGamePending = false;

    public static GameTeam createSinglePlayerLocalTeam(String playerName) {
        PlayerTeam.resetTeamRegistry();
        GameTeam localPlayerTeam = new GameTeam(0);
        localPlayerTeam.teamName = playerName != null && !playerName.trim().isEmpty() ? playerName : "Player";
        localPlayerTeam.isTeamSpectator = false;
        localPlayerTeam.isTeamControlledByAI = false;
        localPlayerTeam.isTeamObserver = false;
        localPlayerTeam.isTeamReady = true;
        localPlayerTeam.teamPingTime = 0;
        return localPlayerTeam;
    }

    public static GameTeam prepareSinglePlayerLocalTeam(GameEngine gameEngine, String playerName) {
        GameTeam localPlayerTeam = createSinglePlayerLocalTeam(playerName);
        gameEngine.playerTeam = localPlayerTeam;
        if (gameEngine.networkEngine != null) {
            gameEngine.networkEngine.localPlayerTeam = localPlayerTeam;
        }
        return localPlayerTeam;
    }

    public static boolean isActivityOpen() {
        return false;
    }

    public static void refreshChatLog() {
        CoreUiEventQueue.requestBattleRoomRefresh();
    }

    public static void addMessageToChatLog(String str) {
        // Android bridge path: the pre-formatted line carries no team color, so use -1 (default tint).
        CoreUiEventQueue.appendBattleRoomChatMessage(str, -1);
    }

    public static void finishActivity(String str, String str2) {
        GameEngine.log("Battle room finish requested: " + str + " / " + str2);
        CoreUiEventQueue.requestBattleRoomClosed(str, str2);
    }

    public static void updateUI() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null) {
            CoreUiEventQueue.requestBattleRoomRefresh();
            return;
        }
        if (gameEngine.networkEngine != null) {
            gameEngine.networkEngine.updateTeamConnectionStatuses();
            gameEngine.networkEngine.callbacks.c();
        }
        if (GameEngine.isNonAndroidVersion) {
            return;
        }
        if (gameEngine.networkEngine != null && gameEngine.networkEngine.gameHasBeenStarted) {
            return;
        }
        CoreUiEventQueue.requestBattleRoomRefresh();
    }

    public static void startGame() {
        // startGamePending is managed by the Kotlin handler (BattleRoomLaunchController): it stays
        // set while the UI cannot act on the start event yet and is replayed each frame.
        CoreUiEventQueue.requestBattleRoomGameStarted();
    }

    public static void showTeamStats() {
        TeamBalanceStats teamStats = new TeamBalanceStats("Starting unit count");
        TeamBalanceStats teamStats2 = new TeamBalanceStats("Total unit HP");
        TeamBalanceStats teamStats3 = new TeamBalanceStats("Team Credits");
        for (PlayerTeam playerTeam : PlayerTeam.getTeams()) {
            int i = 0;
            int i2 = 0;
            BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
            int size = BaseUnit.bE.size();
            for (int i3 = 0; i3 < size; i3++) {
                BaseUnit baseUnit = baseUnitArrA[i3];
                if (baseUnit.team == playerTeam) {
                    i++;
                    i2 = (int) (i2 + baseUnit.currentHealth);
                }
            }
            if (i != 0) {
                teamStats.addValue(playerTeam, i);
                teamStats2.addValue(playerTeam, i2);
                teamStats3.addValue(playerTeam, (int) playerTeam.credits);
            }
        }
        if (!teamStats.checkForImbalance()) {
            teamStats2.checkForImbalance();
        }
        teamStats3.checkForImbalance();
    }

    public static void setupGame() {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.remoteMapStream = null;
        if (gameEngine.networkEngine.roomSettings.gameModeType == GameModeType.savedGame) {
            if (!gameEngine.networkEngine.isServer) {
                gameEngine.gameSaver.writeSaveToStream(gameEngine.networkEngine.receivedSaveGameStream, true, false, false);
                gameEngine.gameUI.messageManager.addMessage((String) null, "Note: Game was started from a saved game.");
            } else {
                gameEngine.gameSaver.performAutosave(gameEngine.networkEngine.roomSettings.mapPath, true);
            }
            showTeamStats();
            return;
        }
        if (gameEngine.networkEngine.roomSettings.gameModeType == GameModeType.customMap) {
            if (!gameEngine.networkEngine.isServer) {
                gameEngine.currentMapPath = VariableScope.nullOrMissingString;
                gameEngine.remoteMapStream = gameEngine.networkEngine.receivedCustomMapStream;
                gameEngine.loadGame(true, GameMode.normal);
                gameEngine.gameUI.messageManager.addMessage((String) null, "Note: Game was started from a custom map on server.");
            } else {
                gameEngine.currentMapPath = gameEngine.networkEngine.selectedMapPath;
                gameEngine.loadGame(true, GameMode.normal);
            }
            showTeamStats();
            return;
        }
        gameEngine.currentMapPath = gameEngine.networkEngine.selectedMapPath;
        gameEngine.loadGame(true, GameMode.normal);
    }

    public static void showPasswordDialog(PasswordHandler passwordHandler2) {
        String title = passwordHandler2.dialogTitle != null ? passwordHandler2.dialogTitle : "Password Required";
        String prompt = passwordHandler2.promptMessage != null ? passwordHandler2.promptMessage : "This server requires a password to join";
        GameEngine.log("Password prompt requested in Kool UI: " + title + " - " + prompt);
        CoreUiEventQueue.requestPasswordDialog(
                passwordHandler2,
                title,
                prompt,
                passwordHandler2.confirmButtonLabel,
                passwordHandler2.cancelButtonLabel
        );
    }
}
