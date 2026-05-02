package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamAuth;
import java.nio.Buffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamGameServer.class */
public class SteamGameServer extends SteamInterface {
    private static native long createCallback(SteamGameServerCallbackAdapter steamGameServerCallbackAdapter);

    private static native void setProduct(long j, String str);

    private static native void setGameDescription(long j, String str);

    private static native void setModDir(long j, String str);

    private static native void setDedicatedServer(long j, boolean z);

    private static native void logOn(long j, String str);

    private static native void logOnAnonymous(long j);

    private static native void logOff(long j);

    private static native boolean isLoggedOn(long j);

    private static native boolean isSecure(long j);

    private static native long getSteamID(long j);

    private static native boolean wasRestartRequested(long j);

    private static native void setMaxPlayerCount(long j, int i);

    private static native void setBotPlayerCount(long j, int i);

    private static native void setServerName(long j, String str);

    private static native void setMapName(long j, String str);

    private static native void setPasswordProtected(long j, boolean z);

    private static native void setSpectatorPort(long j, short s);

    private static native void setSpectatorServerName(long j, String str);

    private static native void clearAllKeyValues(long j);

    private static native void setKeyValue(long j, String str, String str2);

    private static native void setGameTags(long j, String str);

    private static native void setGameData(long j, String str);

    private static native void setRegion(long j, String str);

    private static native boolean sendUserConnectAndAuthenticate(long j, int i, Buffer buffer, int i2, long[] jArr);

    private static native long createUnauthenticatedUserConnection(long j);

    private static native void sendUserDisconnect(long j, long j2);

    private static native boolean updateUserData(long j, long j2, String str, int i);

    private static native int getAuthSessionTicket(long j, Buffer buffer, int i, int[] iArr);

    private static native int beginAuthSession(long j, Buffer buffer, int i, long j2);

    private static native void endAuthSession(long j, long j2);

    private static native void cancelAuthTicket(long j, int i);

    private static native int userHasLicenseForApp(long j, long j2, int i);

    private static native boolean requestUserGroupStatus(long j, long j2, long j3);

    private static native int getPublicIP(long j);

    private static native boolean handleIncomingPacket(long j, Buffer buffer, int i, int i2, short s);

    private static native int getNextOutgoingPacket(long j, Buffer buffer, int i, int[] iArr, short[] sArr);

    private static native void enableHeartbeats(long j, boolean z);

    private static native void setHeartbeatInterval(long j, int i);

    private static native void forceHeartbeat(long j);

    private static native long associateWithClan(long j, long j2);

    private static native long computeNewPlayerCompatibility(long j, long j2);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamGameServer$DenyReason.class */
    public enum DenyReason {
        Invalid,
        InvalidVersion,
        Generic,
        NotLoggedOn,
        NoLicense,
        Cheater,
        LoggedInElseWhere,
        UnknownText,
        IncompatibleAnticheat,
        MemoryCorruption,
        IncompatibleSoftware,
        SteamConnectionLost,
        SteamConnectionError,
        SteamResponseTimedOut,
        SteamValidationStalled,
        SteamOwnerLeftGuestUser;

        private static final DenyReason[] values = values();

        static DenyReason byOrdinal(int i) {
            return values[i];
        }
    }

    public SteamGameServer(SteamGameServerCallback steamGameServerCallback) {
        super(SteamGameServerAPI.getSteamGameServerPointer(), createCallback(new SteamGameServerCallbackAdapter(steamGameServerCallback)));
    }

    public void setProduct(String str) {
        setProduct(this.pointer, str);
    }

    public void setGameDescription(String str) {
        setGameDescription(this.pointer, str);
    }

    public void setModDir(String str) {
        setModDir(this.pointer, str);
    }

    public void setDedicatedServer(boolean z) {
        setDedicatedServer(this.pointer, z);
    }

    public void logOn(String str) {
        logOn(this.pointer, str);
    }

    public void logOnAnonymous() {
        logOnAnonymous(this.pointer);
    }

    public void logOff() {
        logOff(this.pointer);
    }

    public boolean isLoggedOn() {
        return isLoggedOn(this.pointer);
    }

    public boolean isSecure() {
        return isSecure(this.pointer);
    }

    public SteamID getSteamID() {
        return new SteamID(getSteamID(this.pointer));
    }

    public boolean wasRestartRequested() {
        return wasRestartRequested(this.pointer);
    }

    public void setMaxPlayerCount(int i) {
        setMaxPlayerCount(this.pointer, i);
    }

    public void setBotPlayerCount(int i) {
        setBotPlayerCount(this.pointer, i);
    }

    public void setServerName(String str) {
        setServerName(this.pointer, str);
    }

    public void setMapName(String str) {
        setMapName(this.pointer, str);
    }

    public void setPasswordProtected(boolean z) {
        setPasswordProtected(this.pointer, z);
    }

    public void setSpectatorPort(short s) {
        setSpectatorPort(this.pointer, s);
    }

    public void setSpectatorServerName(String str) {
        setSpectatorServerName(this.pointer, str);
    }

    public void clearAllKeyValues() {
        clearAllKeyValues(this.pointer);
    }

    public void setKeyValue(String str, String str2) {
        setKeyValue(this.pointer, str, str2);
    }

    public void setGameTags(String str) {
        setGameTags(this.pointer, str);
    }

    public void setGameData(String str) {
        setGameData(this.pointer, str);
    }

    public void setRegion(String str) {
        setRegion(this.pointer, str);
    }

    public SteamID sendUserConnectAndAuthenticate(int i, Buffer buffer, int i2, SteamID steamID) {
        long[] jArr = new long[1];
        if (sendUserConnectAndAuthenticate(this.pointer, i, buffer, i2, jArr)) {
            return new SteamID(jArr[0]);
        }
        return null;
    }

    public SteamID createUnauthenticatedUserConnection() {
        return new SteamID(createUnauthenticatedUserConnection(this.pointer));
    }

    public void sendUserDisconnect(SteamID steamID) {
        sendUserDisconnect(this.pointer, steamID.handle);
    }

    public boolean updateUserData(SteamID steamID, String str, int i) {
        return updateUserData(this.pointer, steamID.handle, str, i);
    }

    public SteamAuthTicket getAuthSessionTicket(Buffer buffer, int[] iArr) throws SteamException {
        if (!buffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        int authSessionTicket = getAuthSessionTicket(this.pointer, buffer, buffer.capacity(), iArr);
        if (authSessionTicket != 0) {
            buffer.limit(iArr[0]);
        }
        return new SteamAuthTicket(authSessionTicket);
    }

    public SteamAuth.BeginAuthSessionResult beginAuthSession(Buffer buffer, SteamID steamID) throws SteamException {
        if (!buffer.isDirect()) {
            throw new SteamException("Direct buffer required!");
        }
        return SteamAuth.BeginAuthSessionResult.byOrdinal(beginAuthSession(this.pointer, buffer, buffer.limit(), steamID.handle));
    }

    public void endAuthSession(SteamID steamID) {
        endAuthSession(this.pointer, steamID.handle);
    }

    public void cancelAuthTicket(SteamAuthTicket steamAuthTicket) {
        cancelAuthTicket(this.pointer, (int) steamAuthTicket.handle);
    }

    public SteamAuth.UserHasLicenseForAppResult userHasLicenseForApp(SteamID steamID, int i) {
        return SteamAuth.UserHasLicenseForAppResult.byOrdinal(userHasLicenseForApp(this.pointer, steamID.handle, i));
    }

    public boolean requestUserGroupStatus(SteamID steamID, SteamID steamID2) {
        return requestUserGroupStatus(this.pointer, steamID.handle, steamID2.handle);
    }

    public int getPublicIP() {
        return getPublicIP(this.pointer);
    }

    public boolean handleIncomingPacket(Buffer buffer, int i, short s) {
        return handleIncomingPacket(this.pointer, buffer, buffer.limit(), i, s);
    }

    public int getNextOutgoingPacket(Buffer buffer, int[] iArr, short[] sArr) {
        return getNextOutgoingPacket(this.pointer, buffer, buffer.capacity(), iArr, sArr);
    }

    public void enableHeartbeats(boolean z) {
        enableHeartbeats(this.pointer, z);
    }

    public void setHeartbeatInterval(int i) {
        setHeartbeatInterval(this.pointer, i);
    }

    public void forceHeartbeat() {
        forceHeartbeat(this.pointer);
    }

    public SteamAPICall associateWithClan(SteamID steamID) {
        return new SteamAPICall(associateWithClan(this.pointer, steamID.handle));
    }

    public SteamAPICall computeNewPlayerCompatibility(SteamID steamID) {
        return new SteamAPICall(computeNewPlayerCompatibility(this.pointer, steamID.handle));
    }
}
