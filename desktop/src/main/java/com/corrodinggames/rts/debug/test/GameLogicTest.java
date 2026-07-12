package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/a.class */
public class GameLogicTest extends Test {
    /* JADX INFO: renamed from: a */
    public void testTiming() {
        GameEngine.log("== Testing GameLogic ==");
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.gameTimeMillis = 1000;
        Assert.assertTrue(GameViewUtils.b(1000, 5));
        Assert.assertTrue(GameViewUtils.b(1000, 1100));
        Assert.assertTrue(GameViewUtils.b(900, 200));
        Assert.assertFalse(GameViewUtils.b(-9999, 200));
        Assert.assertFalse(GameViewUtils.b(1100, 200));
        Assert.assertFalse(GameViewUtils.b(700, 200));
        gameEngine.gameTimeMillis = 1000;
        Assert.assertTrue(GameViewUtils.a(500, 300));
        Assert.assertFalse(GameViewUtils.a(900, 300));
    }
}
