package com.corrodinggames.rts.gameFramework;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a.class */
public class CollisionEngine {
    ArrayList<CommandGroup> n = new ArrayList();
    public CommandGroup a = a((byte) 1);
    public CommandGroup b = a((byte) 2);
    public CommandGroup c = a((byte) 3);
    public CommandGroup d = a((byte) 4);
    public CommandGroup e = a((byte) 10);
    public CommandGroup f = a((byte) 11);
    public CommandGroup g = a((byte) 13);
    public CommandGroup h = a((byte) 21);
    public CommandGroup i = a((byte) 35);
    public CommandGroup j = a((byte) 40);
    public CommandGroup k = a((byte) 45);
    public CommandGroup l = a((byte) 52);
    public CommandGroup m = a((byte) 60);

    public CollisionEngine() {
        CommandGroup commandGroup = this.a;
        commandGroup.a(commandGroup);
        commandGroup.a(a((byte) 3));
        commandGroup.a(a((byte) 4));
        commandGroup.a(a((byte) 10));
        commandGroup.a(a((byte) 11));
        commandGroup.a(a((byte) 13));
        commandGroup.a(a((byte) 21));
        this.k.a(a((byte) 52));
        CommandGroup commandGroup2 = this.m;
        commandGroup2.a(commandGroup2);
        commandGroup2.a(a((byte) 3));
        commandGroup2.a(a((byte) 4));
        commandGroup2.a(a((byte) 10));
        commandGroup2.a(a((byte) 11));
        commandGroup2.a(a((byte) 13));
        commandGroup2.a(a((byte) 21));
        CommandGroup commandGroupA = a((byte) 10);
        commandGroupA.a(a((byte) 3));
        commandGroupA.a(a((byte) 4));
        commandGroupA.a(a((byte) 10));
        commandGroupA.a(a((byte) 13));
        commandGroupA.a(a((byte) 40));
        CommandGroup commandGroupA2 = a((byte) 11);
        commandGroupA2.a(a((byte) 3));
        commandGroupA2.a(a((byte) 10));
        commandGroupA2.a(a((byte) 13));
        commandGroupA2.a(a((byte) 40));
        CommandGroup commandGroupA3 = a((byte) 3);
        commandGroupA3.a(a((byte) 3));
        commandGroupA3.a(a((byte) 4));
        commandGroupA3.a(a((byte) 10));
        commandGroupA3.a(a((byte) 13));
        CommandGroup commandGroupA4 = a((byte) 4);
        commandGroupA4.a(a((byte) 3));
        commandGroupA4.a(a((byte) 4));
        commandGroupA4.a(a((byte) 10));
        commandGroupA4.a(a((byte) 13));
        CommandGroup commandGroupA5 = a((byte) 13);
        commandGroupA5.a(a((byte) 3));
        commandGroupA5.a(a((byte) 4));
        commandGroupA5.a(a((byte) 10));
        commandGroupA5.a(a((byte) 13));
        CommandGroup commandGroupA6 = a((byte) 21);
        commandGroupA6.a(a((byte) 3));
        commandGroupA6.a(a((byte) 4));
        commandGroupA6.a(a((byte) 10));
        commandGroupA6.a(a((byte) 13));
        CommandGroup commandGroup3 = this.i;
        commandGroup3.a(a((byte) 10));
        commandGroup3.a(a((byte) 13));
    }

    public CommandGroup a(byte b) {
        for (CommandGroup commandGroup : this.n) {
            if (commandGroup.groupId == b) {
                return commandGroup;
            }
        }
        CommandGroup commandGroup2 = new CommandGroup();
        commandGroup2.groupId = b;
        this.n.add(commandGroup2);
        return commandGroup2;
    }
}
