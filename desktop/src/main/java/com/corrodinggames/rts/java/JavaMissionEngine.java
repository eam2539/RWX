package com.corrodinggames.rts.java;

import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.debug.DebugSocketServer;
import com.corrodinggames.rts.game.GameLogic;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.MissionEngine;
import org.lwjgl.Sys;

import java.awt.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/i.class */
public class JavaMissionEngine extends MissionEngine {
    Main a;

    public JavaMissionEngine(Main main) {
        this.a = main;
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public void a(final String str, int i) {
        GameEngine.log("slick queuing-alert:" + str);
        ScriptEngine.getInstance().addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.rts.java.i.1
            @Override // java.lang.Runnable
            public void run() {
                GameEngine.log("slick post-alert:" + str);
                JavaMissionEngine.this.a.p.showMessageBox(VariableScope.nullOrMissingString, str);
            }
        });
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public void a(final String str, final String str2) {
        GameEngine.log("slick queuing-messageBox:" + str2);
        ScriptEngine.getInstance().addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.rts.java.i.2
            @Override // java.lang.Runnable
            public void run() {
                GameEngine.log("slick messageBox:" + str2);
                JavaMissionEngine.this.a.p.showMessageBox(str, str2);
            }
        });
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public void a(String str, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        if ((gameEngine == null || !gameEngine.isGameMinimized) && this.a.j != null) {
            this.a.j.a(str, z);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public void d() {
        GameEngine.log("refreshModDisplay");
        ScriptEngine.getInstance().addScriptToQueue("mods.refreshModList()");
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public void a(Throwable th) {
        a(th, true);
    }

    public void a(Throwable th, boolean z) {
        try {
            GameEngine.log("----------- onGameCrash ----------");
            Toolkit.getDefaultToolkit();
            String str = GameEngine.getSimpleExceptionMessage(th) + "\nCheck logs for more details";
            GameEngine.log("Error message: " + str);
            if (DebugSocketServer.isEnabled()) {
                GameEngine.log("onGameCrash: Not showing popup message due to active debugSocket");
                System.exit(1);
                return;
            }
            if (th != null && (th instanceof OutOfMemoryError) && !GameLogic.isCheatingEnabled) {
                str = str + " (You are also using the 32 bit version, switching to the 64 bit version might help with out of memory)";
            }
            Sys.alert("Crash", str);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GameEngine.log("onGameCrash: end");
        } catch (Throwable th2) {
            GameEngine.log("exception showing message");
            th2.printStackTrace();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public boolean b() {
        if (!GameEngine.getInstance().isGameThreadRunning() && !this.a.p.isGuiVisible()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.MissionEngine
    public boolean c() {
        return DebugSocketServer.isEnabled();
    }
}
