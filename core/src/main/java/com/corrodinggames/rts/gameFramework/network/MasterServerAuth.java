package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;

import java.util.List;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.aq */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/aq.class */
public class MasterServerAuth {

    /* JADX INFO: renamed from: a */
    public static MasterServerAuth instance = new MasterServerAuth();

    /* JADX INFO: renamed from: b */
    public static int saltA = 2;

    /* JADX INFO: renamed from: c */
    static int saltB = 3;

    /* JADX INFO: renamed from: d */
    static int saltC = 2;

    /* JADX INFO: renamed from: e */
    static int saltD = 3;

    /* JADX INFO: renamed from: f */
    public static int authTokenLength = 4;

    /* JADX INFO: renamed from: g */
    static String secretA = "tx";

    /* JADX INFO: renamed from: h */
    static String secretB = "_";

    /* JADX INFO: renamed from: i */
    public static int minClientVersion = 55;

    /* JADX INFO: renamed from: j */
    public static int minServerVersion = 66;

    /* JADX INFO: renamed from: k */
    public static int handshakeTimeoutSec = 100;

    /* JADX INFO: renamed from: l */
    public static boolean debugEnabled = true;

    /* JADX INFO: renamed from: a */
    public static float lerp(float f, float f2, float f3) {
        return f + ((f2 - f) * f3);
    }

    /* JADX INFO: renamed from: a */
    public void addAuthParams(String str, List list) {
        long currentTimeMillis = GameEngine.getCurrentTimeMillis();
        MasterServerClient.addParam(list, secretB + "1", VariableScope.nullOrMissingString + currentTimeMillis);
        MasterServerClient.addParam(list, secretA + "2", Utility.sha256Fingerprint("_" + str + (saltA + saltB)));
        MasterServerClient.addParam(list, secretA + "3", Utility.sha256Fingerprint("_" + str + (((long) (saltA + saltB)) + currentTimeMillis)));
    }

    /* JADX INFO: renamed from: b */
    public void addTokenHashParam(String str, List list) {
        MasterServerClient.addParam(list, secretA + "3", Utility.sha256Fingerprint("-" + str + (saltC + saltD) + authTokenLength));
    }

    /* JADX INFO: renamed from: c */
    public void addOptionalTokenHashParam(String str, List list) {
        if (authTokenLength > 1000) {
            MasterServerClient.addParam(list, secretA + "4", Utility.sha256Fingerprint("+" + str + (saltC + saltD) + authTokenLength));
        }
    }

    /* JADX INFO: renamed from: a */
    public static void applyHandshakeTimeoutFlag(NetworkConnection networkConnection) {
        if (networkConnection.noExtraChecks) {
            GameEngine.getCurrentTimeMillis();
            if (GameEngine.getInstance().currentTick > -5) {
                networkConnection.handshakeTimeoutReached = Utility.distanceSq(0.0f, 0.0f, (float) handshakeTimeoutSec, 0.0f) > 10.0f;
            }
        }
    }
}
