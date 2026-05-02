package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamAuthTicket.class */
public class SteamAuthTicket extends SteamNativeHandle {
    static final long AuthTicketInvalid = 0;

    SteamAuthTicket(long j) {
        super(j);
    }

    public boolean isValid() {
        return this.handle != AuthTicketInvalid;
    }
}
