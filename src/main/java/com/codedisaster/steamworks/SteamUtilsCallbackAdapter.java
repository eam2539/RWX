package com.codedisaster.steamworks;

/* JADX INFO: loaded from: game-lib.jar:com/codedisaster/steamworks/SteamUtilsCallbackAdapter.class */
class SteamUtilsCallbackAdapter extends SteamCallbackAdapter {
    private SteamAPIWarningMessageHook messageHook;

    SteamUtilsCallbackAdapter(SteamUtilsCallback steamUtilsCallback) {
        super(steamUtilsCallback);
    }

    void setWarningMessageHook(SteamAPIWarningMessageHook steamAPIWarningMessageHook) {
        this.messageHook = steamAPIWarningMessageHook;
    }

    void onWarningMessage(int i, String str) {
        if (this.messageHook != null) {
            this.messageHook.onWarningMessage(i, str);
        }
    }

    void onSteamShutdown() {
        ((SteamUtilsCallback) this.callback).onSteamShutdown();
    }
}
