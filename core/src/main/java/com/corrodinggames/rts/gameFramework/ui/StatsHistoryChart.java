package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.*;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.statistics.StatHistoryBuilder;
import com.corrodinggames.rts.gameFramework.statistics.ValueDisplayMode;
import com.corrodinggames.rts.gameFramework.stats.TeamStats;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolPaint;
import io.github.rwx.render.canvas.KoolTypeface;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.y */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/y.class */
public class StatsHistoryChart {
    private ArrayList<GameStatistic> e;
    private ArrayList<TeamHistoryChart> l;
    private StatHistoryBuilder[] m;
    TeamHistoryChart a;
    private long n;
    private Texture o;
    private Texture[] p;
    private Rect q;
    private Rect r;
    KoolPaint c;
    KoolPaint d;
    private StatsTab f = StatsTab.overallStats;
    private ValueDisplayMode g = ValueDisplayMode.absolute;
    private ArrayList h = new ArrayList();
    private StatHistoryBuilder[] i = new StatHistoryBuilder[StatisticType.values().length];
    private ArrayList j = new ArrayList();
    private StatHistoryBuilder[] k = new StatHistoryBuilder[StatisticType.values().length];
    private ArrayList<String> s = new ArrayList();
    private ArrayList t = new ArrayList();
    private int u = -1;
    private int v = -1;
    private int w = -1;
    Rect b = new Rect();

    public static StatsHistoryChart a() {
        return new StatsHistoryChart(GameEngine.getInstance().gameStatistics.getActiveTeamStatistics(), GameStatistic.getGameStatistics());
    }

    private StatsHistoryChart(ArrayList arrayList, ArrayList arrayList2) {
        this.e = arrayList2;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            StatisticsData statisticsData = (StatisticsData) it.next();
            PlayerTeam playerTeamK = PlayerTeam.k(statisticsData.l.b());
            this.h.add(new TeamHistoryChart(statisticsData.l, playerTeamK.teamName, playerTeamK.getTeamColorArgb()));
        }
        for (Integer num : PlayerTeam.getTeamColorIds()) {
            ArrayList arrayList3 = new ArrayList();
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                StatisticsData statisticsData2 = (StatisticsData) it2.next();
                if (PlayerTeam.k(statisticsData2.l.b()).teamColorId == num.intValue()) {
                    arrayList3.add(statisticsData2);
                }
            }
            if (!arrayList3.isEmpty()) {
                this.j.add(new TeamHistoryChart(new TeamStatistics(arrayList3).l, "Team " + PlayerTeam.getTeamSlotLabel(num.intValue()), PlayerTeam.i(num.intValue())));
            }
        }
        for (StatisticType statisticType : StatisticType.values()) {
            this.i[statisticType.ordinal()] = new StatHistoryBuilder(statisticType, this.h);
            this.k[statisticType.ordinal()] = new StatHistoryBuilder(statisticType, this.j);
        }
        this.l = this.h;
        this.m = this.i;
        b();
    }

    public void b() {
        this.f = StatsTab.overallStats;
        GameEngine gameEngine = GameEngine.getInstance();
        this.c = new KoolPaint();
        this.c.a(true);
        this.c.a(KoolPaint.Align.LEFT);
        this.c.a(255, 0, 255, 0);
        gameEngine.setScaledTextSize(this.c, 16.0f);
        this.d = new KoolPaint();
        this.d.a(true);
        this.d.a(KoolPaint.Align.RIGHT);
        this.d.a(255, 0, 255, 0);
        gameEngine.setScaledTextSize(this.d, 16.0f);
        c();
    }

    private void c() {
        GameEngine gameEngine = GameEngine.getInstance();
        this.p = new Texture[StatsTab.values().length + 2];
        this.p[0] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_button_info);
        this.p[1] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_button_income);
        this.p[2] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_button_armyvalue);
        this.p[3] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_button_buildingvalue);
        this.p[4] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_button_totalvalue);
        this.p[5] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_toggle_relative);
        this.p[6] = gameEngine.renderGraphicsEngine.a(R.drawable.stats_toggle_teams);
        this.r = new Rect(0, 0, this.p[0].m(), this.p[0].l());
    }

    public void a(Rect rect, Rect rect2, float f, boolean z, boolean z2) {
        GameEngine gameEngine = GameEngine.getInstance();
        GameUI gameUI = gameEngine.gameUI;
        boolean z3 = true;
        if (z2) {
            int length = StatsTab.values().length;
            int screenPixels = gameEngine.toScreenPixels(30);
            int i = screenPixels * 2;
            int screenPixels2 = gameEngine.toScreenPixels(20);
            int i2 = (rect2.d - screenPixels) - screenPixels2;
            int i3 = gameUI.c ? length + 2 : length - 1;
            int i4 = (int) ((gameEngine.currentScreenWidthPixels / 2.0f) - (((i * i3) + (screenPixels2 * (i3 - 1))) / 2));
            KoolPaint paint = new KoolPaint();
            KoolPaint paint2 = new KoolPaint();
            paint2.a(100, 255, 255, 255);
            for (int i5 = 0; i5 < length; i5++) {
                StatsTab statsTab = StatsTab.values()[i5];
                if (gameUI.c || statsTab != StatsTab.overallStats) {
                    if (gameUI.a(i4, i2, i, screenPixels, IconGroup.none, false)) {
                        if (this.f != statsTab) {
                            this.f = statsTab;
                            this.n = System.currentTimeMillis();
                            this.u = -1;
                            this.v = -1;
                            this.w = -1;
                        }
                        if (this.f != StatsTab.overallStats) {
                            gameUI.c = true;
                        }
                    }
                    this.b.a(i4, i2, i4 + i, i2 + screenPixels);
                    gameEngine.renderGraphicsEngine.a(gameEngine.gameUI.uiTexture1, this.r, this.b, paint);
                    KoolPaint paint3 = paint2;
                    if (!gameUI.c || this.f == statsTab) {
                        paint3 = paint;
                    }
                    gameEngine.renderGraphicsEngine.a(this.p[i5], this.r, this.b, paint3);
                    i4 += screenPixels2 + i;
                }
            }
            int i6 = i4 + screenPixels2;
            if (gameUI.c) {
                boolean z4 = this.g != ValueDisplayMode.absolute;
                if (gameUI.a(i6, i2, i, screenPixels, IconGroup.none, false)) {
                    this.g = !z4 ? ValueDisplayMode.relative : ValueDisplayMode.absolute;
                    this.n = System.currentTimeMillis();
                }
                this.b.a(i6, i2, i6 + i, i2 + screenPixels);
                KoolPaint paint4 = paint;
                if (this.f == StatsTab.overallStats) {
                    paint4 = paint2;
                }
                gameEngine.renderGraphicsEngine.a(gameEngine.gameUI.uiTexture1, this.r, this.b, paint4);
                KoolPaint paint5 = paint;
                if (!z4 || this.f == StatsTab.overallStats) {
                    paint5 = paint2;
                }
                gameEngine.renderGraphicsEngine.a(this.p[5], this.r, this.b, paint5);
                int i7 = i6 + screenPixels2 + i;
                boolean z5 = this.l == this.j;
                if (gameUI.a(i7, i2, i, screenPixels, IconGroup.none, false)) {
                    if (!z5) {
                        this.l = this.j;
                        this.m = this.k;
                    } else {
                        this.l = this.h;
                        this.m = this.i;
                    }
                    this.n = System.currentTimeMillis();
                }
                this.b.a(i7, i2, i7 + i, i2 + screenPixels);
                KoolPaint paint6 = paint;
                if (this.f == StatsTab.overallStats) {
                    paint6 = paint2;
                }
                gameEngine.renderGraphicsEngine.a(gameEngine.gameUI.uiTexture1, this.r, this.b, paint6);
                KoolPaint paint7 = paint;
                if (!z5 || this.f == StatsTab.overallStats) {
                    paint7 = paint2;
                }
                gameEngine.renderGraphicsEngine.a(this.p[6], this.r, this.b, paint7);
                int i8 = i7 + screenPixels2 + i;
            }
            if (this.f == StatsTab.overallStats) {
                z3 = true;
            } else {
                z3 = false;
                rect.d = i2 - gameEngine.toScreenPixels(10);
                if (z) {
                    a(this.f.a(), this.g, rect);
                }
            }
        }
        if (z3) {
            a(rect, f);
        }
    }

    private void a(Rect rect, float f) {
        String str;
        GameEngine gameEngine = GameEngine.getInstance();
        float f2 = 1.5f;
        int screenPixels = rect.b + gameEngine.toScreenPixels(25);
        int iD = rect.d();
        this.c.a("123|", 0, "123|".length(), this.b);
        float fC = this.b.c() + 6;
        for (GameStatistic gameStatistic : this.e) {
            if (gameStatistic.revealProgress != 1.0f && f2 > 0.0f) {
                gameStatistic.revealProgress = Utility.distanceSq(gameStatistic.revealProgress, 1.0f, 0.01f * f2 * f);
                f2 -= 1.0f - gameStatistic.revealProgress;
            }
            float fClampTo255 = Utility.clampTo255(gameStatistic.revealProgress, 0.0f, 1.0f);
            if (gameStatistic.stringValue != null) {
                str = gameStatistic.stringValue;
            } else {
                str = VariableScope.nullOrMissingString + ((int) (gameStatistic.numericValue * fClampTo255));
                if (fClampTo255 <= 0.0f) {
                    str = " ";
                }
            }
            String str2 = gameStatistic.name;
            float fClampTo2552 = Utility.clampTo255(gameStatistic.revealProgress * 2.2f, 0.0f, 1.0f);
            int length = 0;
            if (fClampTo2552 > 0.0f) {
                length = (int) (str2.length() * fClampTo2552);
            }
            int iDistance = Utility.distance(length, 0, str2.length());
            String str3 = VariableScope.nullOrMissingString;
            if (iDistance > 0 && iDistance < str2.length() - 1) {
                str3 = "_";
            }
            gameEngine.renderGraphicsEngine.a(str2.substring(0, iDistance) + str3 + Utility.removeEnd(" ", (str2.length() + str3.length()) - iDistance), iD - (8.0f * this.c.k()), screenPixels, this.c);
            gameEngine.renderGraphicsEngine.a(str, iD + (8.0f * this.c.k()), screenPixels, this.d);
            screenPixels = (int) (screenPixels + fC);
        }
    }

    private void a(StatisticType statisticType, ValueDisplayMode valueDisplayMode, Rect rect) {
        a(GameEngine.getInstance().renderGraphicsEngine, statisticType, valueDisplayMode, rect);
    }


    private void a(GraphicsEngine y, StatisticType bj, ValueDisplayMode z, Rect rect) {
        GameEngine var5 = GameEngine.getInstance();
        GameUI var6 = var5.gameUI;
        StatHistoryBuilder var7 = this.m[bj.ordinal()];
        float var8 = (float) (System.currentTimeMillis() - this.n) / 250.0F;
        KoolPaint var9 = new KoolPaint();
        var9.a(255, 0, 255, 0);
        var9.a(true);
        var9.c(true);
        var9.a(KoolTypeface.a(KoolTypeface.c, 0));
        var5.setScaledTextSize(var9, 14.0F);
        KoolPaint var10 = new KoolPaint(var9);
        var10.a(KoolPaint.Align.CENTER);
        var5.setScaledTextSize(var10, 14.0F);
        KoolPaint var11 = new KoolPaint();
        var11.a(2.0F);
        if (GameEngine.isIOSVersion) {
            var11.a(3.0F);
        }

        var11.a(KoolPaint.Cap.ROUND);
        Rect var12 = new Rect();
        KoolPaint var14 = var6.buildingPreviewInvalidPaint;
        String var15 = Locale.get("gui.leaderboard.type." + bj.name());
        var14.a(var15, 0, var15.length(), this.b);
        y.a(var15, (float) rect.d(), (float) (rect.b + this.b.c()), var14);
        var12.b = rect.b + this.b.c() + 3;
        var12.d = rect.d - this.b.c() - 3;
        int var37 = Math.max(1, var7.b - var7.c);
        float var38 = (float) var12.c() / var37;
        String var16 = Utility.formatDuration(0L);
        int var13 = y.b(var16, var10);
        y.a(var16, (float) (rect.a + var13 / 2), (float) rect.d, var10);
        var12.a = rect.a + var13 / 2;
        String var17 = "123|";
        var9.a(var17, 0, var17.length(), this.b);
        int var18 = this.b.c();
        if (z == ValueDisplayMode.absolute) {
            String var19 = TeamStats.formatValue(var7.a.a(), var7.b);
            String var20 = TeamStats.formatValue(var7.a.a(), var7.c);
            var13 = Math.max(y.b(var19, var9), y.b(var20, var9));
            var12.c = rect.c - var13 - 2;
            int var21 = var18 / 2;
            y.b(var12, var6.minimapBorderPaint);
            var11.b(-13619152);

            for (int var22 = 0; var22 <= 4; var22++) {
                float var23 = var7.c + (float) var37 * var22 / 4.0F;
                float var24 = var12.d - (var23 - var7.c) * var38;
                String var25 = TeamStats.formatValue(var7.a.a(), (int) var23);
                y.a(var25, (float) (var12.c + 2), var24 + var21, var9);
                if (var22 > 0 && var22 < 4) {
                    y.a(var12.a, var24, var12.c, var24, var11);
                }
            }
        } else {
            var12.c = rect.c - var5.toScreenPixels(10);
        }

        String var39 = Utility.formatDuration((long) (var7.d / 1000));
        var13 = y.b(var39, var10);
        y.a(var39, (float) var12.c, (float) rect.d, var10);
        float var40 = (float) var12.b() / var7.d;
        if (z == ValueDisplayMode.absolute) {
            label170:
            for (int var41 = 0; var41 <= 2; var41++) {
                Iterator var44 = this.l.iterator();

                while (true) {
                    boolean var26;
                    TeamHistoryChart var47;
                    IntLookupTable var50;
                    short var54;
                    while (true) {
                        if (!var44.hasNext()) {
                            continue label170;
                        }

                        var47 = (TeamHistoryChart) var44.next();
                        var50 = var47.a.a(bj);
                        var26 = var41 == 0;
                        if (!var26) {
                            var54 = 220;
                            if (this.a != null) {
                                if (var47 == this.a) {
                                    var54 = 255;
                                } else {
                                    var54 = 50;
                                }
                            }
                            break;
                        }

                        if (var47.c == -16777216) {
                            var54 = 255;
                            if (this.a != null) {
                                if (var47 == this.a) {
                                    var54 = 255;
                                } else {
                                    var54 = 50;
                                }
                            }
                            break;
                        }
                    }

                    if (var41 == 2 ? var47 == this.a : var41 != 1 || var47 != this.a) {
                        Point2i var27 = (Point2i) var50.get(0);
                        float var28 = var12.a;
                        float var29 = var12.d - var38 * (var27.y - var7.c);

                        for (int var30 = 1; var30 < var50.size(); var30++) {
                            var27 = (Point2i) var50.get(var30);
                            float var31 = var12.a + var40 * var27.x;
                            float var32 = var12.d - var38 * (var27.y - var7.c);
                            int var33 = (int) (var54 * Math.min(1.0F, Math.max(0.0F, var8 - (float) var27.x / var7.d)));
                            GamePaint var34 = var47.a(var33, var26);
                            y.a(var28, var29, var31, var29, var34);
                            y.a(var31, var29, var31, var32, var34);
                            var28 = var31;
                            var29 = var32;
                        }
                    }
                }
            }
        } else {
            ArrayList var42 = var7.e;
            StatHistory var45 = (StatHistory) var42.get(0);

            for (int var48 = 1; var48 < var42.size(); var48++) {
                StatHistory var51 = (StatHistory) var42.get(var48);
                float var55 = var12.a + var40 * var45.a;
                float var60 = var12.a + var40 * var51.a;
                float var66 = var12.d;

                for (int var70 = 0; var70 < this.l.size(); var70++) {
                    float var73 = var45.a(var70);
                    float var76 = var66 - var12.c() * var73;
                    if (var73 > 0.0F) {
                        TeamHistoryChart var79 = this.l.get(var70);
                        float var82 = Math.min(1.0F, Math.max(0.0F, var8 - (float) var45.a / var7.d));
                        GamePaint var83 = var79.a((int) (var82 * 255.0F), false);
                        this.b.a((int) var55, (int) (var76 + 0.5F), (int) var60, (int) (var66 + 0.5F));
                        if (this.o != null) {
                            y.a(this.o, this.q, this.b, var83);
                        } else {
                            y.b(this.b, var83);
                        }
                    }

                    var66 = var76;
                }

                var45 = var51;
            }
        }

        if (var12.b((int) var6.selectionBoxStartX, (int) var6.selectionBoxStartY)) {
            var6.a(var12.a, var12.b, var12.b(), var12.c());
            var11.b(-1);
            y.a(var6.selectionBoxStartX, var12.b, var6.selectionBoxStartX, var12.d, var11);
            int var43 = (int) var6.selectionBoxStartX;
            int var46 = (int) var6.selectionBoxStartY;
            int var49 = (int) ((var6.selectionBoxStartX - var12.a) / var40);
            if (this.v != var43 || this.w != var46) {
                this.v = var43;
                this.w = var46;
                this.u = var49;
                this.s.clear();
                this.t.clear();
                this.s.add(Utility.formatDuration((long) (this.u / 1000)));
                this.t.add(-1);
                TeamHistoryChart var52 = null;
                if (z == ValueDisplayMode.absolute) {
                    float var56 = 30.0F;

                    for (TeamHistoryChart var67 : this.l) {
                        TeamHistory var71 = var67.a;
                        int var74 = var71.a(bj, this.u);
                        float var77 = var12.d - var38 * (var74 - var7.c);
                        float var80 = Utility.abs(var77 - var6.selectionBoxStartY);
                        if (var80 < var56) {
                            var56 = var80;
                            var52 = var67;
                        }
                    }
                }

                this.a = var52;

                for (TeamHistoryChart var62 : this.l) {
                    TeamHistory var68 = var62.a;
                    int var72 = var68.a(bj, this.u);
                    String var75 = TeamStats.formatValue(var7.a.a(), var72) + " " + var62.b;
                    this.s.add(var75);
                    int var78 = var62.c;
                    if (this.a != null && this.a != var62) {
                        byte var81 = 60;
                        var78 = KoolArgbColor.a(var81, KoolArgbColor.b(var78), KoolArgbColor.c(var78), KoolArgbColor.d(var78));
                    }

                    this.t.add(var78);
                }
            }

            this.b.a = var12.a + var5.toScreenPixels(5);
            this.b.b = var12.b + var5.toScreenPixels(5);
            this.b.d = this.b.b + var5.toScreenPixels(5) + var18 * this.s.size();
            String var53 = "";

            for (String var63 : this.s) {
                if (var53.length() < var63.length()) {
                    var53 = var63;
                }
            }

            int var59 = y.b(var53, var9);
            this.b.c = this.b.a + var5.toScreenPixels(10) + var59;
            y.b(this.b, var6.minimapPaint);
            int var64 = this.b.b + var18 + 3;

            for (int var69 = 0; var69 < this.s.size(); var69++) {
                var9.b((Integer) this.t.get(var69));
                y.a((String) this.s.get(var69), (float) (this.b.a + 3), (float) var64, var9);
                var64 += var18;
            }
        } else {
            this.a = null;
        }
    }
}
