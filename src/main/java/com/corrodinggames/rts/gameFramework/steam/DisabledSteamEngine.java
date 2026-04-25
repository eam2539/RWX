package com.corrodinggames.rts.gameFramework.steam;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.o.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/o/a.class */
public class DisabledSteamEngine {
    public static DisabledSteamEngine a = new DisabledSteamEngine();

    public static DisabledSteamEngine a() {
        return a;
    }

    public void b() {
        GameEngine.isInSpace("SteamEngine - blank init");
    }

    public void a(float f) {
    }

    public String c() {
        return null;
    }

    public void d() {
        GameEngine.isInSpace("SteamEngine - disableSteam - already disabled");
    }

    public boolean e() {
        return !f();
    }

    public boolean f() {
        return true;
    }

    public void g() {
        GameEngine.isInSpace("disabledSteam - showInviteDialog");
        GameEngine.getInstance().alert("steam API not connected");
    }

    public void h() {
        GameEngine.isInSpace("Steam: alertNotEnabled");
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null) {
            gameEngine.alert("steam API not connected");
        }
    }

    public void i() {
    }

    public void j() {
    }

    public void k() {
    }

    public void l() {
        GameEngine.isInSpace("disabledSteam - loadWorkshopMods");
    }

    public void m() {
        GameEngine.isInSpace("disabledSteam - showWorkshop");
    }

    public void a(ModInfo modInfo) {
        GameEngine.isInSpace("disabledSteam - showWorkshopMod");
    }

    public void b(ModInfo modInfo) {
        GameEngine.isInSpace("disabledSteam - publishWorkshopMod");
    }

    public void a(ModInfo modInfo, boolean z, String str) {
        GameEngine.isInSpace("disabledSteam - uploadWorkshopMod");
    }
}
