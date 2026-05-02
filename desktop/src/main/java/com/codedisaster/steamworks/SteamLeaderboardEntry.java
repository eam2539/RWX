package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamLeaderboardEntry.class */
public class SteamLeaderboardEntry {
    long steamIDUser;
    int globalRank;
    int score;
    int details;

    public SteamID getSteamIDUser() {
        return new SteamID(this.steamIDUser);
    }

    public int getGlobalRank() {
        return this.globalRank;
    }

    public int getScore() {
        return this.score;
    }

    public int getNumDetails() {
        return this.details;
    }
}
