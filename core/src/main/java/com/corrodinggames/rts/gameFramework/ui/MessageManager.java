package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.render.canvas.KoolPaint;
import io.github.rwx.render.canvas.KoolTypeface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/m.class */
public class MessageManager {

    /* JADX INFO: renamed from: a */
    public static int MAX_MESSAGES = 95;

    /* JADX INFO: renamed from: b */
    GameUI gameUi;

    /* JADX INFO: renamed from: c */
    GameEngine gameEngine;

    /* JADX INFO: renamed from: d */
    KoolPaint textPaint;

    /* JADX INFO: renamed from: e */
    boolean hasShownMuteWarning;

    /* JADX INFO: renamed from: f */
    ArrayList messages = new ArrayList();

    MessageManager(GameEngine gameEngine, GameUI gameUI) {
        this.gameUi = gameUI;
        this.gameEngine = gameEngine;
        initialize();
    }

    /* JADX INFO: renamed from: a */
    public void initialize() {
        this.textPaint = new KoolPaint();
        this.textPaint.a(255, 255, 255, 255);
        this.textPaint.a(true);
        this.textPaint.c(true);
        this.textPaint.a(KoolTypeface.a(KoolTypeface.c, 1));
        this.gameEngine.updatePaintTextSize(this.textPaint, 16.0f);
    }

    /* JADX INFO: renamed from: b */
    public synchronized void clear() {
        this.hasShownMuteWarning = false;
        this.messages.clear();
    }

    /* JADX INFO: renamed from: c */
    public synchronized void pruneMessages() {
        while (this.messages.size() > MAX_MESSAGES) {
            this.messages.remove(0);
        }
    }

    /* JADX INFO: renamed from: a */
    public synchronized Message addMessage(String str, String str2) {
        GameEngine gameEngine = GameEngine.getInstance();
        Message message = new Message();
        message.author = str;
        message.text = str2;
        message.timestamp = System.currentTimeMillis();
        message.formattedTimestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        if (str != null && !str.equals(VariableScope.nullOrMissingString) && !gameEngine.settingsEngine.showPlayerChatInGame) {
            if (!this.hasShownMuteWarning) {
                this.hasShownMuteWarning = true;
                addMessage((String) null, "[WARNING: A player send a chat message, but you have chat muted in your settings]");
            }
            return message;
        }
        pruneMessages();
        this.messages.add(message);
        return message;
    }

    /* JADX INFO: renamed from: a */
    public synchronized int draw(float f, int i) {
        String strSubstring;
        GameEngine gameEngine = GameEngine.getInstance();
        pruneMessages();
        int i2 = (int) (20.0f * gameEngine.screenScale);
        boolean messageDisplayPersistent = gameEngine.isMessageDisplayPersistent();
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            Message message = (Message) this.messages.get(size);
            if (message.shouldDisplay()) {
                if (message.author == null || message.author.equals(VariableScope.nullOrMissingString)) {
                    strSubstring = message.text;
                } else {
                    strSubstring = message.author + ": " + message.text;
                }
                if (messageDisplayPersistent) {
                    strSubstring = message.formattedTimestamp + ": " + strSubstring;
                }
                if (message.delayPerCharMs > 0) {
                    int timeDifference = message.getTimeDifference() / message.delayPerCharMs;
                    if (timeDifference < 0) {
                        timeDifference = 0;
                    }
                    if (timeDifference < strSubstring.length()) {
                        strSubstring = strSubstring.substring(0, timeDifference);
                    }
                }
                this.textPaint.b(message.textColor);
                gameEngine.renderGraphicsEngine.a(strSubstring, 20, i, this.textPaint);
                i += i2;
            }
        }
        return i;
    }
}
