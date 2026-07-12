package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/l.class */
public class LeaderboardRow {

    /* JADX INFO: renamed from: a */
    Paint labelPaint;

    /* JADX INFO: renamed from: b */
    String label;

    /* JADX INFO: renamed from: d */
    float labelWidth;

    /* JADX INFO: renamed from: e */
    Paint valuePaint;

    /* JADX INFO: renamed from: f */
    String value;

    /* JADX INFO: renamed from: h */
    float valueWidth;

    /* JADX INFO: renamed from: c */
    int teamId = -1;

    /* JADX INFO: renamed from: g */
    int color = -1;

    public LeaderboardRow(String str, Paint paint, String str2, Paint paint2) {
        GameEngine gameEngine = GameEngine.getInstance();
        this.labelPaint = paint;
        this.label = str;
        this.labelWidth = gameEngine.renderGraphicsEngine.b(str, paint);
        this.valuePaint = paint2;
        this.value = str2;
        this.valueWidth = gameEngine.renderGraphicsEngine.b(str2, paint2);
    }
}
