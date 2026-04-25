package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUniverse.class */
public enum SteamUniverse {
    Invalid(0),
    Public(1),
    Beta(2),
    Internal(3),
    Dev(4);

    private final int value;
    private static final SteamUniverse[] values = values();

    SteamUniverse(int i) {
        this.value = i;
    }

    static SteamUniverse byValue(int i) {
        for (SteamUniverse steamUniverse : values) {
            if (steamUniverse.value == i) {
                return steamUniverse;
            }
        }
        return Invalid;
    }
}
