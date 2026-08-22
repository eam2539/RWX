package com.corrodinggames.rts.gameFramework.network;

import android.text.Html;
import android.text.Spanned;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.Utility;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/a.class */
public class ChatLog {
    private ConcurrentLinkedQueue<ChatMessage> a = new ConcurrentLinkedQueue();

    public String a(String str) {
        return Utility.escapeHtml(str);
    }

    public void a(int i, String str, String str2, NetworkConnection networkConnection) {
        this.a.add(new ChatMessage(this, i, str, str2.trim(), networkConnection));
        if (this.a.size() > 45) {
            this.a.poll();
        }
    }

    public int a(NetworkConnection networkConnection, int i) {
        if (networkConnection == null) {
            return 0;
        }
        int i2 = networkConnection.connectionId;
        int i3 = 0;
        for (ChatMessage chatMessage : this.a) {
            if (chatMessage.connectionId == i2 && Utility.elapsedMilliseconds(chatMessage.timestampNano, System.nanoTime()) < i && !chatMessage.message.startsWith("-i ") && !chatMessage.message.startsWith("-qc ")) {
                i3++;
                if (chatMessage.message != null) {
                    if (Utility.countOccurrences(chatMessage.message, '\n') >= 3) {
                        i3 += 2;
                    }
                    if (Utility.countOccurrences(chatMessage.message, '\n') >= 6) {
                        i3 += 2;
                    }
                    if (Utility.countOccurrences(chatMessage.message, '\n') >= 9) {
                        i3 += 2;
                    }
                }
            }
        }
        return i3;
    }

    public String a() {
        String str = VariableScope.nullOrMissingString;
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            str = str + ((ChatMessage) it.next()).a() + "\n";
        }
        return str;
    }

    public ConcurrentLinkedQueue b() {
        return this.a;
    }

    public String a(boolean z) {
        String str = VariableScope.nullOrMissingString;
        if (!z) {
            Iterator it = this.a.iterator();
            while (it.hasNext()) {
                str = str + ((ChatMessage) it.next()).b() + "<br/>\n";
            }
        } else {
            Iterator it2 = this.a.iterator();
            while (it2.hasNext()) {
                str = ((ChatMessage) it2.next()).b() + "<br/>\n" + str;
            }
        }
        return "<pre>" + str + "</pre>";
    }

    public Spanned b(boolean z) {
        return Html.fromHtml(a(z));
    }

    public void c() {
        this.a.clear();
    }
}
