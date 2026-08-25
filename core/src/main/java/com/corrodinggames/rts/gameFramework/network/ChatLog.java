package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.Utility;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/a.class */
public class ChatLog {
    private ConcurrentLinkedQueue<ChatMessage> messages = new ConcurrentLinkedQueue();

    public String escapeHtml(String str) {
        return Utility.escapeHtml(str);
    }

    public void addMessage(int i, String str, String str2, NetworkConnection networkConnection) {
        this.messages.add(new ChatMessage(this, i, str, str2.trim(), networkConnection));
        if (this.messages.size() > 45) {
            this.messages.poll();
        }
    }

    public int countMessagesFrom(NetworkConnection networkConnection, int i) {
        if (networkConnection == null) {
            return 0;
        }
        int i2 = networkConnection.connectionId;
        int i3 = 0;
        for (ChatMessage chatMessage : this.messages) {
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

    public String getAllMessagesText() {
        String str = VariableScope.nullOrMissingString;
        Iterator it = this.messages.iterator();
        while (it.hasNext()) {
            str = str + ((ChatMessage) it.next()).getPlainText() + "\n";
        }
        return str;
    }

    public ConcurrentLinkedQueue getMessages() {
        return this.messages;
    }

    public String formatMessagesAsHtml(boolean z) {
        String str = VariableScope.nullOrMissingString;
        if (!z) {
            Iterator it = this.messages.iterator();
            while (it.hasNext()) {
                str = str + ((ChatMessage) it.next()).getHtmlText() + "<br/>\n";
            }
        } else {
            Iterator it2 = this.messages.iterator();
            while (it2.hasNext()) {
                str = ((ChatMessage) it2.next()).getHtmlText() + "<br/>\n" + str;
            }
        }
        return "<pre>" + str + "</pre>";
    }

    public String b(boolean z) {
        return formatMessagesAsHtml(z);
    }

    public void clearMessages() {
        this.messages.clear();
    }
}
