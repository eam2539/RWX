package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import com.corrodinggames.rts.gameFramework.utility.Build;

import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/v.class */
class ErrorReportSender implements Runnable {

    /* JADX INFO: renamed from: a */
    String message;

    /* JADX INFO: renamed from: b */
    String stacktrace;

    ErrorReportSender() {
    }

    @Override // java.lang.Runnable
    public void run() {
        long currentTimeMillis = GameEngine.getCurrentTimeMillis();
        GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("SendErrorReport", "Starting");
        try {
            try {
                try {
                    ArrayList arrayList = new ArrayList(2);
                    MasterServerClient.addParam(arrayList, "action", "error_report");
                    MasterServerClient.addParam(arrayList, "game_version", Integer.toString(gameEngine.getVersionCode(false)));
                    MasterServerClient.addParam(arrayList, "game_version_internal", Integer.toString(gameEngine.getVersionCode(true)));
                    MasterServerClient.addParam(arrayList, "game_version_string", gameEngine.getVersionNameWithSuffix());
                    MasterServerClient.addParam(arrayList, "package_name", gameEngine.getPackageName());
                    MasterServerClient.addParam(arrayList, "installation_source", gameEngine.getInstallerPackageName());
                    String str = VariableScope.nullOrMissingString + Build.VERSION.SDK_INT;
                    if (GameEngine.isPC()) {
                        str = "s:0;";
                        if (DisabledSteamEngine.a().e()) {
                            str = "s:1;";
                        }
                    }
                    if (GameEngine.isPC() || GameEngine.isIOSVersion) {
                        MasterServerClient.addParam(arrayList, "system_version", System.getProperty("os.name") + " - " + System.getProperty("os.version"));
                    }
                    MasterServerClient.addParam(arrayList, "sdk_version", str);
                    MasterServerClient.addParam(arrayList, "device_model", gameEngine.getPlatformName());
                    MasterServerClient.addParam(arrayList, "build_version", gameEngine.getBuildVersion());
                    MasterServerClient.addParam(arrayList, "release_version", Utility.booleanToString(GameEngine.isGameBeta));
                    MasterServerClient.addParam(arrayList, "dedicated_server", Utility.booleanToString(GameEngine.isNonAndroidVersion));
                    String str2 = gameEngine.networkEngine != null ? gameEngine.networkEngine.sessionToken : "NA";
                    MasterServerClient.addParam(arrayList, "private_token", str2);
                    MasterServerClient.addParam(arrayList, "private_token_2", Utility.md5Hex(Utility.md5Hex(str2)));
                    MasterServerClient.addParam(arrayList, "message", this.message);
                    MasterServerClient.addParam(arrayList, "stacktrace", this.stacktrace);
                    GameEngine.log("SendErrorReport", "making request");
                    String line = MasterServerClient.requestMasterServerResponse(arrayList).readLine();
                    if (line == null || !line.contains("CORRODINGGAMES")) {
                        GameEngine.log("StartCreateOnMasterServer", "Error bad header returned from the master server: " + line);
                        GameEngine.log("SendErrorReport", "took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
                    } else {
                        GameEngine.log("SendErrorReport", "Send trace successfully");
                        GameEngine.log("SendErrorReport", "took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameEngine.log("SendErrorReport", "took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                GameEngine.log("SendErrorReport", "took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
            }
        } catch (Throwable th) {
            GameEngine.log("SendErrorReport", "took: " + ((GameEngine.getCurrentTimeMillis() - currentTimeMillis) / 1000000.0f) + " seconds");
            throw th;
        }
    }
}
