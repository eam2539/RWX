package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.stats.GameObjectComparator;
import com.corrodinggames.rts.gameFramework.stats.StatType;
import com.corrodinggames.rts.gameFramework.stats.TeamStats;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/k.class */
public class Leaderboard {

    /* JADX INFO: renamed from: a */
    GameUI gameUi;

    /* JADX INFO: renamed from: b */
    GameEngine gameEngine;

    /* JADX INFO: renamed from: c */
    Paint textPaint;

    /* JADX INFO: renamed from: d */
    Paint titlePaint;

    /* JADX INFO: renamed from: e */
    RectF backgroundRect = new RectF();

    Leaderboard(GameEngine gameEngine, GameUI gameUI) {
        this.gameUi = gameUI;
        this.gameEngine = gameEngine;
        initialize();
    }

    /* JADX INFO: renamed from: a */
    public void initialize() {
        this.titlePaint = new Paint();
        this.titlePaint.a(255, 255, 255, 255);
        this.titlePaint.a(true);
        this.titlePaint.c(true);
        this.titlePaint.a(Typeface.a(Typeface.c, 1));
        this.gameEngine.updatePaintTextSize(this.titlePaint, 16.0f);
        this.textPaint = new Paint();
        this.textPaint.a(255, 255, 255, 255);
        this.textPaint.a(true);
        this.textPaint.c(true);
        this.textPaint.a(Typeface.a(Typeface.c, 0));
        this.gameEngine.updatePaintTextSize(this.textPaint, 16.0f);
    }

    /* JADX INFO: renamed from: a */
    public int draw(float f, int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        TeamStats teamStats = gameEngine.teamStats;
        if (teamStats.getStatType() == StatType.none) {
            return i;
        }
        ArrayList<GameObjectComparator> comparators = teamStats.getComparators();
        ArrayList<LeaderboardRow> arrayList = new ArrayList();
        String str = Locale.get("gui.leaderboard.type." + teamStats.getStatType().name(), new Object[0]);
        arrayList.add(new LeaderboardRow(VariableScope.nullOrMissingString, this.textPaint, str, this.titlePaint));
        int i2 = i;
        int i3 = ((int) (gameEngine.screenWidth - gameEngine.sidebarWidth)) - 6;
        int i4 = (int) (20.0f * gameEngine.screenScale);
        for (GameObjectComparator gameObjectComparator : comparators) {
            LeaderboardRow leaderboardRow = new LeaderboardRow(teamStats.getFormattedValue(gameObjectComparator), this.titlePaint, " " + gameObjectComparator.b(), gameObjectComparator.a() ? this.titlePaint : this.textPaint);
            leaderboardRow.color = gameObjectComparator.c();
            leaderboardRow.teamId = gameObjectComparator.d();
            arrayList.add(leaderboardRow);
        }
        float fB = gameEngine.graphicsEngine2.b(str, this.textPaint);
        float f2 = 0.0f;
        for (LeaderboardRow leaderboardRow2 : arrayList) {
            if (leaderboardRow2.labelWidth > f2) {
                f2 = leaderboardRow2.labelWidth;
            }
        }
        for (LeaderboardRow leaderboardRow3 : arrayList) {
            if (Utility.abs(leaderboardRow3.labelWidth - f2) < 40.0f) {
                leaderboardRow3.labelWidth = f2;
            }
            float f3 = leaderboardRow3.labelWidth + leaderboardRow3.valueWidth;
            if (f3 > fB) {
                fB = f3;
            }
        }
        float fCeil = Utility.ceil(fB / 20.0f) * 20.0f;
        int i5 = i3 - ((int) (fCeil + 0.5f));
        this.backgroundRect.a = i5 - 6;
        this.backgroundRect.c = i5 + 6 + fCeil;
        this.backgroundRect.b = (i2 - 6) - i4;
        this.backgroundRect.d = i2 + 6 + ((arrayList.size() - 1) * i4);
        GamePaint gamePaint = new GamePaint();
        gamePaint.b(Color.a(100, 0, 0, 0));
        gamePaint.a(Paint.Style.FILL_AND_STROKE);
        gameEngine.graphicsEngine2.a(this.backgroundRect, gamePaint);
        for (int i6 = 0; i6 < arrayList.size(); i6++) {
            LeaderboardRow leaderboardRow4 = (LeaderboardRow) arrayList.get(i6);
            leaderboardRow4.labelPaint.b(leaderboardRow4.teamId);
            gameEngine.graphicsEngine2.a(leaderboardRow4.label, i5, i2, leaderboardRow4.labelPaint);
            leaderboardRow4.valuePaint.b(leaderboardRow4.color);
            gameEngine.graphicsEngine2.a(leaderboardRow4.value, i5 + leaderboardRow4.labelWidth, i2, leaderboardRow4.valuePaint);
            i2 += i4;
        }
        return i2 + i4;
    }
}
