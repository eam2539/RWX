package com.corrodinggames.rts.appFramework;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameMode;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.GameModeType;
import com.corrodinggames.rts.gameFramework.network.PasswordHandler;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/n.class */
public class MultiplayerBattleroomActivity extends TaskQueueActivity {

    /* JADX INFO: renamed from: c */
    public static MultiplayerBattleroomActivity instance;

    /* JADX INFO: renamed from: d */
    boolean onCreateFinished;

    /* JADX INFO: renamed from: e */
     Handler uiHandler;

    /* JADX INFO: renamed from: f */
    public static boolean startGamePending = false;

    /* JADX INFO: renamed from: g */
    public boolean isActivityOpen;

    /* JADX INFO: renamed from: h */
    TextView chatLog;

    /* JADX INFO: renamed from: k */
    private Handler chatLogUpdateHandler;

    /* JADX INFO: renamed from: l */
    private Runnable updateUIRunnable;

    /* JADX INFO: renamed from: m */
    private Runnable startGameRunnable;

    /* JADX INFO: renamed from: i */
    static PasswordHandler passwordHandler;

    /* JADX INFO: renamed from: j */
    static AlertDialog passwordDialog;

    /* JADX INFO: renamed from: l */
    public static boolean isActivityOpen() {
        if (instance == null) {
            return false;
        }
        return instance.isActivityOpen;
    }

    /* JADX INFO: renamed from: m */
    public static void refreshChatLog() {
        if (instance != null) {
            // from class: com.corrodinggames.rts.appFramework.n.1
// java.lang.Runnable
            instance.uiHandler.a(() -> instance.refreshChatLogInternal());
        }
    }

    /* JADX INFO: renamed from: d */
    public static void addMessageToChatLog(String str) {
        MultiplayerBattleroomActivity multiplayerBattleroomActivity = instance;
        if (multiplayerBattleroomActivity == null) {
            return;
        }
        Message messageA = multiplayerBattleroomActivity.chatLogUpdateHandler.a();
        messageA.d().putString("text", str);
        multiplayerBattleroomActivity.chatLogUpdateHandler.c(messageA);
    }

    /* JADX INFO: renamed from: n */
    void refreshChatLogInternal() {
        if (!this.onCreateFinished) {
            GameEngine.logColored("addMessageToChatLogInternal: !onCreateFinished");
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        Spanned spannedB = gameEngine.networkEngine.chatLog.b(true);
        if (this.chatLog == null) {
            throw new RuntimeException("chatLog==null");
        }
        if (spannedB == null) {
            throw new RuntimeException("chatLogHTML==null");
        }
        try {
            this.chatLog.clearFocus();
            this.chatLog.setTextKeepState(spannedB);
        } catch (NullPointerException e) {
            GameEngine.log("chatLog.setText error", (Throwable) e);
            gameEngine.alert("chatLog.setText error", 1);
        }
    }

    /* JADX INFO: renamed from: a */
    public static void finishActivity(String str, final String str2) {
        if (instance != null) {
            instance.uiHandler.a(new Runnable() { // from class: com.corrodinggames.rts.appFramework.n.2
                @Override // java.lang.Runnable
                public void run() {
                    instance.b();
                    if (str2 != null) {
                    }
                }
            });
        }
    }

    /* JADX INFO: renamed from: o */
    public static void updateUI() {
        GameEngine gameEngine = GameEngine.getInstance();
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
        if (instance != null) {
            instance.uiHandler.a(instance.updateUIRunnable);
        } else {
            GameEngine.logColored("MultiplayerBattleroomActivity:updateUI() lastLoaded==null");
        }
    }

    /* JADX INFO: renamed from: p */
    public static void startGame() {
        if (instance != null) {
            instance.uiHandler.a(instance.startGameRunnable);
            startGamePending = false;
        } else {
            GameEngine.logColored("MultiplayerBattleroomActivity:startGame() lastLoaded==null");
            GameEngine.printStackTrace();
            startGamePending = true;
        }
    }

    /* JADX INFO: renamed from: q */
    public static void showTeamStats() {
        TeamStats teamStats = new TeamStats("Starting unit count");
        TeamStats teamStats2 = new TeamStats("Total unit HP");
        TeamStats teamStats3 = new TeamStats("Team Credits");
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

    /* JADX INFO: renamed from: r */
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

    /* JADX INFO: renamed from: a */
    public static void showPasswordDialog(final PasswordHandler passwordHandler2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameEngine.getInstance().clearGameState());
        String str = "Password Required";
        String strConvertInlineBlocks = "This server requires a password to join";
        if (passwordHandler2.promptMessage != null) {
            str = "Server Question";
            strConvertInlineBlocks = Locale.convertInlineBlocks(passwordHandler2.promptMessage);
        }
        if (passwordHandler2.dialogTitle != null) {
            str = passwordHandler2.dialogTitle;
        }
        builder.setTitle(str);
        builder.setMessage(strConvertInlineBlocks);
        final EditText editText = new EditText(builder.getContext());
        builder.setView(editText);
        if (passwordHandler2.promptMessage != null) {
            editText.setHint("Enter text...");
        } else {
            editText.setHint("Enter password...");
        }
        builder.setPositiveButton(passwordHandler2.confirmButtonLabel != null ? passwordHandler2.confirmButtonLabel : "Submit", new DialogInterface.OnClickListener() { // from class: com.corrodinggames.rts.appFramework.n.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                passwordHandler2.submitPassword(editText.getText().toString());
                MultiplayerBattleroomActivity.passwordHandler = null;
                MultiplayerBattleroomActivity.passwordDialog = null;
            }
        });
        builder.setNegativeButton(passwordHandler2.cancelButtonLabel != null ? passwordHandler2.cancelButtonLabel : "Disconnect", new DialogInterface.OnClickListener() { // from class: com.corrodinggames.rts.appFramework.n.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                passwordHandler2.cancelPasswordEntry();
                MultiplayerBattleroomActivity.passwordHandler = null;
                MultiplayerBattleroomActivity.passwordDialog = null;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.corrodinggames.rts.appFramework.n.5
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                passwordHandler2.cancelPasswordEntry();
                MultiplayerBattleroomActivity.passwordHandler = null;
                MultiplayerBattleroomActivity.passwordDialog = null;
            }
        });
        AlertDialog alertDialog = passwordDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        AlertDialog alertDialogShow = builder.show();
        passwordHandler = passwordHandler2;
        passwordDialog = alertDialogShow;
        editText.requestFocus();
    }
}
