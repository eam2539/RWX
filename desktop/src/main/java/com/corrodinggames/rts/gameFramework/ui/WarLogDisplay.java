package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;
import android.graphics.Typeface;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ap.class */
public class WarLogDisplay {
    private GameEngine gameEngine;
    private Paint paint;
    private ArrayList<WarLogEntry> entries = new ArrayList();

    public WarLogDisplay(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        a();
    }

    public void a() {
        this.paint = new Paint();
        this.paint.a(255, 255, 255, 255);
        this.paint.a(true);
        this.paint.c(true);
        this.paint.a(Typeface.a(Typeface.c, 1));
        this.gameEngine.updatePaintTextSize(this.paint, 14.0f);
    }

    public synchronized void b() {
        this.entries.clear();
    }

    public synchronized void a(BaseUnit baseUnit) {
        UnitCreatedLogEntry unitCreatedLogEntry = new UnitCreatedLogEntry(baseUnit.posX, baseUnit.posY, baseUnit.r());
        unitCreatedLogEntry.timestamp = GameEngine.getCurrentTimeMillis();
        a(unitCreatedLogEntry);
    }

    public synchronized void b(BaseUnit baseUnit) {
        UnitUpgradedLogEntry unitUpgradedLogEntry = new UnitUpgradedLogEntry(baseUnit.posX, baseUnit.posY, baseUnit.r());
        unitUpgradedLogEntry.timestamp = GameEngine.getCurrentTimeMillis();
        a(unitUpgradedLogEntry);
    }

    public synchronized void c(BaseUnit baseUnit) {
        UnitDamagedLogEntry unitDamagedLogEntry = new UnitDamagedLogEntry(baseUnit.posX, baseUnit.posY, baseUnit.bI());
        unitDamagedLogEntry.timestamp = GameEngine.getCurrentTimeMillis();
        a(unitDamagedLogEntry);
    }

    public synchronized void a(String str) {
        StringLogEntry stringLogEntry = new StringLogEntry(str);
        stringLogEntry.timestamp = GameEngine.getCurrentTimeMillis();
        a(stringLogEntry);
    }

    public synchronized void a(String str, int i) {
        StringLogEntry stringLogEntry = new StringLogEntry(str);
        stringLogEntry.timestamp = GameEngine.getCurrentTimeMillis();
        stringLogEntry.durationMs = i;
        stringLogEntry.alwaysShow = true;
        a(stringLogEntry);
    }

    private void a(WarLogEntry warLogEntry) {
        boolean z = false;
        Iterator it = this.entries.iterator();
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
            Collections.sort(this.entries);
        } else {
            this.entries.add(0, warLogEntry);
        }
    }

    public synchronized void a(float f) {
        c();
        GameEngine gameEngine = GameEngine.getInstance();
        int i = (int) (gameEngine.screenHeight - (130.0f * gameEngine.screenScale));
        int i2 = (int) (20.0f * gameEngine.screenScale);
        for (WarLogEntry warLogEntry : this.entries) {
            String strA = warLogEntry.a();
            if (gameEngine.settingsEngine.showWarLogOnScreen || warLogEntry.alwaysShow) {
                if (warLogEntry.timestamp + warLogEntry.durationMs >= System.currentTimeMillis()) {
                    if (warLogEntry.hasBeenShown) {
                        this.paint.a(255, 160, 160, 160);
                    } else {
                        this.paint.a(255, 255, 255, 255);
                    }
                    gameEngine.renderGraphicsEngine.a(strA, 20, i, this.paint);
                    i -= i2;
                } else {
                    return;
                }
            }
        }
    }

    public synchronized void c() {
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            if (((WarLogEntry) it.next()).timestamp + 20000 < System.currentTimeMillis()) {
                it.remove();
            }
        }
    }

    public synchronized void d() {
        if (this.entries.isEmpty()) {
            return;
        }
        for (WarLogEntry warLogEntry : this.entries) {
            if (!warLogEntry.hasBeenShown) {
                warLogEntry.hasBeenShown = true;
                this.gameEngine.centerViewpoint(warLogEntry.x, warLogEntry.y);
                return;
            }
        }
    }
}
