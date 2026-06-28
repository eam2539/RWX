package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.TeamHistory;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.aa */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/aa.class */
public class TeamHistoryChart {
    public TeamHistory a;
    String b;
    int c;
    GamePaint[] d = new GamePaint[11];
    GamePaint[] e = new GamePaint[11];

    public GamePaint a(int i, boolean z) {
        int i2 = i / 25;
        if (i2 < 0) {
            i2 = 0;
        }
        if (i2 > 10) {
            i2 = 10;
        }
        if (z) {
            return this.e[i2];
        }
        return this.d[i2];
    }

    public TeamHistoryChart(TeamHistory teamHistory, String str, int i) {
        this.a = teamHistory;
        this.b = str;
        this.c = i;
        int i2 = 0;
        while (i2 < 11) {
            int i3 = i2 == 10 ? 255 : i2 * 25;
            this.d[i2] = new GamePaint();
            this.d[i2].a(2.0f);
            if (GameEngine.isIOSVersion) {
                this.d[i2].a(3.0f);
            }
            this.d[i2].a(KoolPaint.Cap.ROUND);
            this.d[i2].b(i);
            this.d[i2].c(i3);
            this.e[i2] = new GamePaint();
            this.e[i2].b(-13162713);
            this.e[i2].c(i3);
            this.e[i2].a(5.0f);
            this.e[i2].a(KoolPaint.Cap.ROUND);
            i2++;
        }
    }
}
