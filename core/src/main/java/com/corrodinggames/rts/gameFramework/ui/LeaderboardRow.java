package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/l.class */
public class LeaderboardRow {

    /* JADX INFO: renamed from: a */
    KoolPaint labelPaint;

    /* JADX INFO: renamed from: b */
    String label;

    /* JADX INFO: renamed from: d */
    float labelWidth;

    /* JADX INFO: renamed from: e */
    KoolPaint valuePaint;

    /* JADX INFO: renamed from: f */
    String value;

    /* JADX INFO: renamed from: h */
    float valueWidth;

    /* JADX INFO: renamed from: c */
    int teamId = -1;

    /* JADX INFO: renamed from: g */
    int color = -1;

    public LeaderboardRow(String str, KoolPaint paint, String str2, KoolPaint paint2) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.labelPaint = paint;
        this.label = str;
        this.labelWidth = gameEngine.renderGraphicsEngine.b(str, paint);
        this.valuePaint = paint2;
        this.value = str2;
        this.valueWidth = gameEngine.renderGraphicsEngine.b(str2, paint2);
    }
}
