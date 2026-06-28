package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/b.class */
public class ChatMessage {
    int a;
    String b;
    String c;
    int d;
    long e;
    final /* synthetic */ ChatLog f;

    ChatMessage(ChatLog chatLog, int i, String str, String str2, NetworkConnection networkConnection) {
        this.f = chatLog;
        this.a = i;
        this.b = str;
        this.c = str2;
        if (networkConnection != null) {
            this.d = networkConnection.connectionId;
        }
        this.e = System.nanoTime();
    }

    public String a() {
        String str;
        if (this.b != null) {
            str = this.b + ": " + this.c;
        } else {
            str = this.c;
        }
        return str;
    }

    public int getTeamColorIndex() {
        return this.a;
    }

    public String getDisplayText() {
        return a();
    }

    public String b() {
        String str = VariableScope.nullOrMissingString;
        if (this.b != null) {
            int i = -1;
            if (this.a != -1) {
                i = PlayerTeam.i(this.a);
            }
            str = "<strong> <font color='" + Utility.toHexString(i) + "'>" + this.f.a(this.b) + ": </font></strong>";
        }
        boolean z = true;
        for (String str2 : this.c.split("\n")) {
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
