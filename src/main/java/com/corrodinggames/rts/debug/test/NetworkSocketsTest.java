package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/b.class */
public class NetworkSocketsTest extends Test {
    /* JADX INFO: renamed from: a */
    public void test() throws ConfigParseException {
        testSocketLoop();
    }

    /* JADX INFO: renamed from: b */
    public void testSocketLoop() throws ConfigParseException {
        GameEngine.isInSpace("networkSocks");
        GameEngine gameEngine = GameEngine.getInstance();
        for (int i = 0; i < 10000; i++) {
            gameEngine.networkEngine.startServerHosting(false);
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameEngine.networkEngine.disconnectNetworking("test");
        }
        GameEngine.isInSpace("done");
        try {
            Thread.sleep(100000L);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }
}
