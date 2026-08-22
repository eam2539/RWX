package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/b.class */
public class ChatMessage {

    /* JADX INFO: renamed from: a */
    int teamColorIndex;

    /* JADX INFO: renamed from: b */
    String senderName;

    /* JADX INFO: renamed from: c */
    String message;

    /* JADX INFO: renamed from: d */
    int connectionId;

    /* JADX INFO: renamed from: e */
    long timestampNano;

    final /* synthetic */ ChatLog f;

    ChatMessage(ChatLog chatLog, int i, String str, String str2, NetworkConnection networkConnection) {
        this.f = chatLog;
        this.teamColorIndex = i;
        this.senderName = str;
        this.message = str2;
        if (networkConnection != null) {
            this.connectionId = networkConnection.connectionId;
        }
        this.timestampNano = System.nanoTime();
    }

    public String a() {
        String str;
        if (this.senderName != null) {
            str = this.senderName + ": " + this.message;
        } else {
            str = this.message;
        }
        return str;
    }

    public int getTeamColorIndex() {
        return this.teamColorIndex;
    }

    public String getDisplayText() {
        return a();
    }

    public String b() {
        String str = VariableScope.nullOrMissingString;
        if (this.senderName != null) {
            int i = -1;
            if (this.teamColorIndex != -1) {
                i = PlayerTeam.i(this.teamColorIndex);
            }
            str = "<strong> <font color='" + Utility.toHexString(i) + "'>" + this.f.a(this.senderName) + ": </font></strong>";
        }
        boolean z = true;
        for (String str2 : this.message.split("\n")) {
            if (!str2.trim().equals(VariableScope.nullOrMissingString)) {
                if (z) {
                    z = false;
                } else {
                    str = str + "<br/>";
                }
                str = str + this.f.a(str2);
            }
        }
        return str;
    }
}
