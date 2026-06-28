package com.corrodinggames.rts.gameFramework.network;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ay */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ay.class */
public class SplitPacketData extends PacketData {

    /* JADX INFO: renamed from: f */
    public PacketData originalPacket;

    /* JADX INFO: renamed from: g */
    public int steamChannelId;

    public SplitPacketData(int i, PacketData packetData) {
        super(175);
        this.steamChannelId = i;
        this.originalPacket = packetData;
    }
}
