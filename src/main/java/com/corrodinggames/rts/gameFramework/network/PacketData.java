package com.corrodinggames.rts.gameFramework.network;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.au */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/au.class */
public class PacketData {

    /* JADX INFO: renamed from: b */
    public int packetType;

    /* JADX INFO: renamed from: c */
    public byte[] bytes;

    /* JADX INFO: renamed from: e */
    public boolean isUrgent;

    /* JADX INFO: renamed from: a */
    public NetworkConnection connection = null;

    /* JADX INFO: renamed from: d */
    public int delayMillis = -1;

    public PacketData(int i) {
        this.packetType = i;
    }
}
