package com.codedisaster.steamworks;

import com.codedisaster.steamworks.SteamAuth;
import java.nio.Buffer;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUser.class */
public class SteamUser extends SteamInterface {
    private static native long createCallback(SteamUserCallbackAdapter steamUserCallbackAdapter);

    private static native long getSteamID(long j);

    private static native int getAuthSessionTicket(long j, Buffer buffer, int i, int[] iArr);

    private static native int beginAuthSession(long j, Buffer buffer, int i, long j2);

    private static native void endAuthSession(long j, long j2);

    private static native void cancelAuthTicket(long j, int i);

    private static native int userHasLicenseForApp(long j, long j2, int i);

    @Override // com.codedisaster.steamworks.SteamInterface
    public /* bridge */ /* synthetic */ void dispose() {
        super.dispose();
    }

    public SteamUser(SteamUserCallback steamUserCallback) {
        super(SteamAPI.getSteamUserPointer(), createCallback(new SteamUserCallbackAdapter(steamUserCallback)));
    }

    public SteamID getSteamID() {
        return new SteamID(getSteamID(this.pointer));
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
}
