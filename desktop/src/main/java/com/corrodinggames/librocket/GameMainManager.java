package com.corrodinggames.librocket;

import android.app.Activity;
import com.corrodinggames.librocket.scripts.Root;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.appFramework.GameView;
import com.corrodinggames.rts.appFramework.LevelSelectActivity;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.network.PasswordHandler;
import com.corrodinggames.rts.gameFramework.utility.BooleanHolder;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.librocket.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/a.class */
public abstract class GameMainManager {

    /* JADX INFO: renamed from: a */
    protected static GameMainManager instance;

    /* JADX INFO: renamed from: b */
    public LibRocketManager libRocketManager;

    /* JADX INFO: renamed from: c */
    public GameView gameView;
    boolean d = true;

    /* JADX INFO: renamed from: e */
    boolean isGamePaused = true;

    /* JADX INFO: renamed from: g */
    public abstract void applyResolution();

    /* JADX INFO: renamed from: h */
    public abstract void postUpdate();

    /* JADX INFO: renamed from: i */
    public abstract int getModifiers();

    /* JADX INFO: renamed from: d */
    public abstract void setMouseGrabbed(boolean z);

    /* JADX INFO: renamed from: a */
    public static GameMainManager getInstance() {
        return instance;
    }

    /* JADX INFO: renamed from: a */
    public void init(LibRocketManager libRocketManager, GameView gameView) {
        this.libRocketManager = libRocketManager;
        this.gameView = gameView;
    }

    /* JADX INFO: renamed from: b */
    public void showMainMenu() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null) {
            gameEngine.colorizeLogMessage((Activity) null, this.gameView, true);
        } else {
            GameEngine.log("showMainMenu: game is null");
        }
        this.libRocketManager.setDocument("mainMenu.rml");
    }

    /* JADX INFO: renamed from: c */
    public void setGamePaused2() {
    }

    /* JADX INFO: renamed from: d */
    public void showSettings() {
        this.libRocketManager.setDocument("settings.rml");
    }

    /* JADX INFO: renamed from: e */
    public void showLeaderboard() {
        this.libRocketManager.setDocument("leaderboard.rml");
    }

    /* JADX INFO: renamed from: a */
    public synchronized void setGamePaused(boolean z) {
        this.isGamePaused = z;
        resumeGame();
    }

    /* JADX INFO: renamed from: f */
    public synchronized void resumeGame() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine == null) {
            GameEngine.logColored("Main::resumeGame(): game==null");
        } else {
            gameEngine.isGameResumed = true;
            gameEngine.colorizeLogMessage((Activity) null, this.gameView, this.isGamePaused);
        }
    }

    /* JADX INFO: renamed from: b */
    public synchronized void endGame(boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!z) {
            GameEngine.log("endGame: queDisconnectAndWait");
            gameEngine.networkEngine.u();
        } else {
            GameEngine.log("endGame: network disconnect");
            gameEngine.networkEngine.disconnectNetworking("shutdownServer");
        }
    }

    /* JADX INFO: renamed from: a */
    public synchronized void loadGame(String str) {
        endGame(true);
        GameEngine.getInstance();
        this.isGamePaused = false;
        LevelSelectActivity.startNewGame(str, false, 8, 0, true, false);
        resumeGame();
    }

    /* JADX INFO: renamed from: c */
    public void showAbout(boolean z) {
        this.isGamePaused = z;
    }

    /* JADX INFO: renamed from: j */
    public boolean isUIReady() {
        if (this.libRocketManager != null && !this.libRocketManager.isGuiVisible()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: a */
    public void onKeyDown(int i, char c) {
        int iInitializeGdxToAndroidMapping = SlickToAndroidKeycodes.convertSlickToAndroidKeyCode(i);
        if (isUIReady()) {
            Integer numInitializeAndroidToGdxMapping = SlickToAndroidKeycodes.convertSlickToGdxKeyCode(i);
            Object activeDocumentMetadata = this.libRocketManager.getActiveDocumentMetadata("event_onkeydown");
            if (activeDocumentMetadata != null) {
                ScriptEngine.getInstance().addScriptToQueue(activeDocumentMetadata + "(" + iInitializeGdxToAndroidMapping + ");");
                if (this.libRocketManager.getCurrentPopup() != null) {
                    return;
                }
            }
            if (c > 255) {
                ScriptEngine.getInstance().addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.librocket.a.1
                    @Override // java.lang.Runnable
                    public void run() {
                        GameMainManager.this.libRocketManager.scriptEngine.getRoot().event_unicodeEntered();
                    }
                });
            }
            if (i == 30 && getModifiers() == 1) {
                this.libRocketManager.processKeyDown(93, 0);
                this.libRocketManager.processKeyUp(93, 0);
                this.libRocketManager.processKeyDown(91, 2);
                this.libRocketManager.processKeyUp(91, 2);
                return;
            }
            if (i == 46 && getModifiers() == 1) {
                this.libRocketManager.processKeyDown(14, 1);
                return;
            }
            if (i == 47 && getModifiers() == 1) {
                this.libRocketManager.processKeyDown(33, 1);
                return;
            }
            if (numInitializeAndroidToGdxMapping != null) {
                this.libRocketManager.processKeyDown(numInitializeAndroidToGdxMapping.intValue(), getModifiers());
            } else if (c != 0) {
                if (Character.isISOControl(c)) {
                    if (c == '\b') {
                        GameEngine.log("backspace char pressed");
                        this.libRocketManager.processKeyDown(69, 0);
                        this.libRocketManager.processKeyUp(69, 0);
                    } else {
                        GameEngine.log("keyPressed skipping isISOControl:" + i + " c:" + ((int) c) + " c_print:" + c);
                    }
                } else {
                    this.libRocketManager.processTextInputChar(c);
                }
            }
            if (i == 28 || i == 156) {
                ScriptEngine.getInstance().addScriptToQueue("onEnter();");
            } else if (c == '\r') {
                GameEngine.log("keyPressed: new line entered");
                ScriptEngine.getInstance().addScriptToQueue("onEnter();");
            }
            if (i == 1) {
                ScriptEngine.getInstance().addScriptToQueue("onEscape();");
                return;
            }
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null) {
            gameEngine.setKeyState(iInitializeGdxToAndroidMapping, true);
            if (i == 1) {
            }
        }
    }

    /* JADX INFO: renamed from: k */
    public LinkedList getDebugInfo() {
        return null;
    }

    /* JADX INFO: renamed from: b */
    public boolean openURL(String str) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    GameEngine.log("Opening link from desktop api");
                    Desktop.getDesktop().browse(new URI(str));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e2) {
                    e2.printStackTrace();
                }
            } else {
                String lowerCase = System.getProperty("os.name").toLowerCase();
                String[] strArr = null;
                if (lowerCase.contains("win")) {
                    strArr = new String[]{"rundll32", "url.dll,FileProtocolHandler", str};
                }
                if (lowerCase.contains("mac")) {
                    strArr = new String[]{"open", str};
                }
                if (lowerCase.contains("nix") || lowerCase.contains("nux")) {
                    strArr = new String[]{"xdg-open", str};
                }
                if (strArr != null) {
                    try {
                        Runtime.getRuntime().exec(strArr);
                        return true;
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            return false;
        } catch (RuntimeException e4) {
            e4.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: renamed from: l */
    public void onUpdate() {
    }

    /* JADX INFO: renamed from: m */
    public void onRender() {
    }

    /* JADX INFO: renamed from: n */
    public void onResize() {
    }

    /* JADX INFO: renamed from: o */
    public void closeBattleroomIfOpen() {
        ScriptEngine scriptEngine;
        if (!GameEngine.getInstance().networkEngine.gameHasBeenStarted && (scriptEngine = ScriptEngine.getInstance()) != null) {
            scriptEngine.addScriptToQueue("mp.closeBattleroomIfOpen()");
        }
    }

    /* JADX INFO: renamed from: a */
    public void showPasswordPrompt(PasswordHandler passwordHandler) {
        GameEngine.getInstance();
        GameEngine.log("[relay-debug] GameMainManager.showPasswordPrompt prompt=" + passwordHandler.promptMessage + " title=" + passwordHandler.dialogTitle + " relayQuestion=" + passwordHandler.isRequesting);
        ScriptEngine scriptEngine = ScriptEngine.getInstance();
        if (scriptEngine != null) {
            GameEngine.log("[relay-debug] GameMainManager.showPasswordPrompt queueing script runnable");
            scriptEngine.addRunnableToQueue(new AnonymousClass2(scriptEngine, passwordHandler));
        } else {
            GameEngine.log("[relay-debug] GameMainManager.showPasswordPrompt scriptEngine is null");
        }
    }

    /* JADX INFO: renamed from: com.corrodinggames.librocket.a$2, reason: invalid class name */
    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/a$2.class */
    class AnonymousClass2 implements Runnable {
        final BooleanHolder a = new BooleanHolder(false);
        final /* synthetic */ ScriptEngine b;
        final /* synthetic */ PasswordHandler c;

        AnonymousClass2(ScriptEngine scriptEngine, PasswordHandler passwordHandler) {
            this.b = scriptEngine;
            this.c = passwordHandler;
        }

        @Override // java.lang.Runnable
        public void run() {
            GameEngine.log("[relay-debug] GameMainManager password runnable running");
            final Root root = this.b.getRoot();
            ButtonAction buttonAction = new ButtonAction(this.c.confirmButtonLabel != null ? this.c.confirmButtonLabel : "Join", new Runnable() { // from class: com.corrodinggames.librocket.a.2.1
                @Override // java.lang.Runnable
                public void run() {
                    if (AnonymousClass2.this.a.a) {
                        GameEngine.logColored("AskPasswordCallBack already called");
                        return;
                    }
                    AnonymousClass2.this.a.a = true;
                    String popupText = root.getPopupText();
                    root.closeAlertOnly();
                    AnonymousClass2.this.c.submitPassword(popupText);
                }
            });
            buttonAction.closesDialog = true;
            Runnable runnable = new Runnable() { // from class: com.corrodinggames.librocket.a.2.2
                @Override // java.lang.Runnable
                public void run() {
                    if (AnonymousClass2.this.a.a) {
                        GameEngine.logColored("AskPasswordCallBack already called");
                        return;
                    }
                    AnonymousClass2.this.a.a = true;
                    root.closeAlertOnly();
                    AnonymousClass2.this.c.cancelPasswordEntry();
                }
            };
            ButtonAction buttonAction2 = new ButtonAction(this.c.cancelButtonLabel != null ? this.c.cancelButtonLabel : "Close", runnable);
            String str = "Password Required";
            String strConvertInlineBlocks = "This server requires a password to join";
            if (this.c.promptMessage != null) {
                str = "Server Question";
                strConvertInlineBlocks = Locale.convertInlineBlocks(this.c.promptMessage);
            }
            if (this.c.dialogTitle != null) {
                str = this.c.dialogTitle;
            }
            DialogData dialogData = new DialogData();
            dialogData.title = str;
            dialogData.message = strConvertInlineBlocks;
            dialogData.textInputValue = VariableScope.nullOrMissingString;
            dialogData.button1 = buttonAction2;
            dialogData.button2 = buttonAction;
            dialogData.showBackButton = false;
            dialogData.onClose = runnable;
            GameEngine.log("[relay-debug] GameMainManager creating password dialog title=" + str + " message=" + strConvertInlineBlocks);
            GameMainManager.this.libRocketManager.createAndShowDialog(dialogData);
        }
    }
}
