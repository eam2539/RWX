package com.codedisaster.steamworks;

import java.nio.ByteBuffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworking.class */
public class SteamNetworking extends SteamInterface {
    private final int[] tmpIntResult;
    private final long[] tmpLongResult;

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworking$API.class */
    public enum API {
        Client,
        Server
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworking$P2PSend.class */
    public enum P2PSend {
        Unreliable,
        UnreliableNoDelay,
        Reliable,
        ReliableWithBuffering
    }

    private static native long createCallback(SteamNetworkingCallbackAdapter steamNetworkingCallbackAdapter, boolean z);

    private static native boolean sendP2PPacket(long j, long j2, ByteBuffer byteBuffer, int i, int i2, int i3, int i4);

    private static native boolean isP2PPacketAvailable(long j, int[] iArr, int i);

    private static native boolean readP2PPacket(long j, ByteBuffer byteBuffer, int i, int i2, int[] iArr, long[] jArr, int i3);

    private static native boolean acceptP2PSessionWithUser(long j, long j2);

    private static native boolean closeP2PSessionWithUser(long j, long j2);

    private static native boolean closeP2PChannelWithUser(long j, long j2, int i);

    private static native boolean getP2PSessionState(long j, long j2, P2PSessionState p2PSessionState);

    private static native boolean allowP2PPacketRelay(long j, boolean z);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworking$P2PSessionError.class */
    public enum P2PSessionError {
        None,
        NotRunningApp,
        NoRightsToApp,
        DestinationNotLoggedIn,
        Timeout;

        private static final P2PSessionError[] values = values();

        public static P2PSessionError byOrdinal(int i) {
            return values[i];
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamNetworking$P2PSessionState.class */
    public class P2PSessionState {
        byte connectionActive;
        byte connecting;
        byte sessionError;
        byte usingRelay;
        int bytesQueuedForSend;
        int packetsQueuedForSend;
        int remoteIP;
        short remotePort;

        public boolean isConnectionActive() {
            return this.connectionActive != 0;
        }

        public boolean isConnecting() {
            return this.connecting != 0;
        }

        public P2PSessionError getLastSessionError() {
            return P2PSessionError.byOrdinal(this.sessionError);
        }

        public boolean isUsingRelay() {
            return this.usingRelay != 0;
        }

        public int getBytesQueuedForSend() {
            return this.bytesQueuedForSend;
        }

        public int getPacketsQueuedForSend() {
            return this.packetsQueuedForSend;
        }

        public int getRemoteIP() {
            return this.remoteIP;
        }

        public short getRemotePort() {
            return this.remotePort;
        }
    }

    public SteamNetworking(SteamNetworkingCallback steamNetworkingCallback, API api) {
        super(api == API.Client ? SteamAPI.getSteamNetworkingPointer() : SteamGameServerAPI.getSteamGameServerNetworkingPointer(), createCallback(new SteamNetworkingCallbackAdapter(steamNetworkingCallback), api == API.Client));
        this.tmpIntResult = new int[1];
        this.tmpLongResult = new long[1];
    }

    public boolean sendP2PPacket(SteamID steamID, ByteBuffer byteBuffer, P2PSend p2PSend, int i) throws SteamException {
        int iPosition = byteBuffer.position();
        return sendP2PPacket(steamID, byteBuffer, iPosition, byteBuffer.limit() - iPosition, p2PSend, i);
    }

    public boolean sendP2PPacket(SteamID steamID, ByteBuffer byteBuffer, int i, int i2, P2PSend p2PSend, int i3) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        if (byteBuffer.capacity() < i + i2) {
            throw new SteamException("Buffer capacity exceeded!");
        }
        return sendP2PPacket(this.pointer, steamID.handle, byteBuffer, i, i2, p2PSend.ordinal(), i3);
    }

    public int isP2PPacketAvailable(int i) {
        if (isP2PPacketAvailable(this.pointer, this.tmpIntResult, i)) {
            return this.tmpIntResult[0];
        }
        return 0;
    }

    public int readP2PPacket(SteamID steamID, ByteBuffer byteBuffer, int i) throws SteamException {
        int iPosition = byteBuffer.position();
        return readP2PPacket(steamID, byteBuffer, iPosition, byteBuffer.limit() - iPosition, i);
    }

    public int readP2PPacket(SteamID steamID, ByteBuffer byteBuffer, int i, int i2, int i3) throws SteamException {
        if (!byteBuffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        if (byteBuffer.capacity() < i + i2) {
            throw new SteamException("Buffer capacity exceeded!");
        }
        if (readP2PPacket(this.pointer, byteBuffer, i, i2, this.tmpIntResult, this.tmpLongResult, i3)) {
            byteBuffer.position(i);
            byteBuffer.limit(i + this.tmpIntResult[0]);
            steamID.handle = this.tmpLongResult[0];
            return this.tmpIntResult[0];
        }
        return 0;
    }

    public boolean acceptP2PSessionWithUser(SteamID steamID) {
        return acceptP2PSessionWithUser(this.pointer, steamID.handle);
    }

    public boolean closeP2PSessionWithUser(SteamID steamID) {
        return closeP2PSessionWithUser(this.pointer, steamID.handle);
    }

    public boolean closeP2PChannelWithUser(SteamID steamID, int i) {
        return closeP2PChannelWithUser(this.pointer, steamID.handle, i);
    }

    public boolean getP2PSessionState(SteamID steamID, P2PSessionState p2PSessionState) {
        return getP2PSessionState(this.pointer, steamID.handle, p2PSessionState);
    }

    public boolean allowP2PPacketRelay(boolean z) {
        return allowP2PPacketRelay(this.pointer, z);
    }
}
