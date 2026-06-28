package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.render.canvas.KoolPaint;
import io.github.rwx.render.canvas.KoolTypeface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ap.class */
public class WarLogDisplay {
    private GameEngine a;
    private KoolPaint b;
    private ArrayList<WarLogEntry> c = new ArrayList();

    public WarLogDisplay(GameEngine gameEngine) {
        this.a = gameEngine;
        a();
    }

    public void a() {
        this.b = new KoolPaint();
        this.b.a(255, 255, 255, 255);
        this.b.a(true);
        this.b.c(true);
        this.b.a(KoolTypeface.a(KoolTypeface.c, 1));
        this.a.updatePaintTextSize(this.b, 14.0f);
    }

    public synchronized void b() {
        this.c.clear();
    }

    public synchronized void a(BaseUnit baseUnit) {
        UnitCreatedLogEntry unitCreatedLogEntry = new UnitCreatedLogEntry(baseUnit.posX, baseUnit.posY, baseUnit.r());
        unitCreatedLogEntry.c = GameEngine.getCurrentTimeMillis();
        a(unitCreatedLogEntry);
    }

    public synchronized void b(BaseUnit baseUnit) {
        UnitUpgradedLogEntry unitUpgradedLogEntry = new UnitUpgradedLogEntry(baseUnit.posX, baseUnit.posY, baseUnit.r());
        unitUpgradedLogEntry.c = GameEngine.getCurrentTimeMillis();
        a(unitUpgradedLogEntry);
    }

    public synchronized void c(BaseUnit baseUnit) {
        UnitDamagedLogEntry unitDamagedLogEntry = new UnitDamagedLogEntry(baseUnit.posX, baseUnit.posY, baseUnit.bI());
        unitDamagedLogEntry.c = GameEngine.getCurrentTimeMillis();
        a(unitDamagedLogEntry);
    }

    public synchronized void a(String str) {
        StringLogEntry stringLogEntry = new StringLogEntry(str);
        stringLogEntry.c = GameEngine.getCurrentTimeMillis();
        a(stringLogEntry);
    }

    public synchronized void a(String str, int i) {
        StringLogEntry stringLogEntry = new StringLogEntry(str);
        stringLogEntry.c = GameEngine.getCurrentTimeMillis();
        stringLogEntry.d = i;
        stringLogEntry.i = true;
        a(stringLogEntry);
    }

    private void a(WarLogEntry warLogEntry) {
        boolean z = false;
        Iterator it = this.c.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            WarLogEntry warLogEntry2 = (WarLogEntry) it.next();
            if (warLogEntry2.a(warLogEntry)) {
                warLogEntry2.b(warLogEntry);
                z = true;
                break;
            }
        }
        if (z) {
            Collections.sort(this.c);
        } else {
            this.c.add(0, warLogEntry);
        }
    }

    public synchronized void a(float f) {
        c();
        GameEngine gameEngine = GameEngine.getInstance();
        int i = (int) (gameEngine.screenHeight - (130.0f * gameEngine.screenScale));
        int i2 = (int) (20.0f * gameEngine.screenScale);
        for (WarLogEntry warLogEntry : this.c) {
            String strA = warLogEntry.a();
            if (gameEngine.settingsEngine.showWarLogOnScreen || warLogEntry.i) {
                if (warLogEntry.c + warLogEntry.d >= System.currentTimeMillis()) {
                    if (warLogEntry.h) {
                        this.b.a(255, 160, 160, 160);
                    } else {
                        this.b.a(255, 255, 255, 255);
                    }
                    gameEngine.renderGraphicsEngine.a(strA, 20, i, this.b);
                    i -= i2;
                } else {
                    return;
                }
            }
        }
    }

    public synchronized void c() {
        Iterator it = this.c.iterator();
        while (it.hasNext()) {
            if (((WarLogEntry) it.next()).c + 20000 < System.currentTimeMillis()) {
                it.remove();
            }
        }
    }

    public synchronized void d() {
        if (this.c.isEmpty()) {
            return;
        }
        for (WarLogEntry warLogEntry : this.c) {
            if (!warLogEntry.h) {
                warLogEntry.h = true;
                this.a.centerViewpoint(warLogEntry.e, warLogEntry.f);
                return;
            }
        }
    }
}
