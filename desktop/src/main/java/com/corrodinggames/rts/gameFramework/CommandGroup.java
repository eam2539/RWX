package com.corrodinggames.rts.gameFramework;

import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b.class */
public class CommandGroup {

    /* JADX INFO: renamed from: a */
    public byte groupId;
    HashMap b = new HashMap();

    public void a(CommandGroup commandGroup) {
        this.b.put(Byte.valueOf(commandGroup.groupId), commandGroup);
    }
}
