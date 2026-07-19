package com.corrodinggames.rts.game.units.management;

import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.spatial.FilteredUnitCallback;
import com.corrodinggames.rts.gameFramework.GameEngine;

import com.corrodinggames.rts.gameFramework.utility.UnitList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.f.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/f/c.class */
public final class UnitSpatialIndex {
    int a;
    int b;
    float c;
    float d;
    public UnitStatisticsManager[][] e = null;
    AreaUnitCallback f = new AreaUnitCallback();
    RectUnitCallback g = new RectUnitCallback();
    MovingUnitCallback h = new MovingUnitCallback();
    final UnitList i = new UnitList();
    final UnitListIterator j = new UnitListIterator();
    final Rect k = new Rect();
    final int l = 32;
    int m;

    public void a(float f, float f2, float f3, OrderableUnit orderableUnit, float f4, FilteredUnitCallback filteredUnitCallback) {
        this.g.a(f - f3, f2 - f3, f + f3, f2 + f3);
        a(this.g.a, this.g, orderableUnit, f4, filteredUnitCallback);
    }

    public final UnitListIterator a(float f, float f2, float f3) {
        UnitList unitList = this.i;
        unitList.clear();
        a(f, f2, f3, unitList);
        this.j.a(unitList);
        return this.j;
    }

    public final void a(float f, float f2, float f3, UnitList unitList) {
        UnitStatisticsManager[][] unitStatisticsManagerArr = this.e;
        float f4 = f - f3;
        float f5 = f + f3;
        float f6 = f2 - f3;
        float f7 = f2 + f3;
        int iA = a(f4);
        int iA2 = a(f5);
        int iB = b(f6);
        int iB2 = b(f7);
        for (int i = iA; i <= iA2; i++) {
            for (int i2 = iB; i2 <= iB2; i2++) {
                com.corrodinggames.rts.game.units.management.UnitList unitList2 = unitStatisticsManagerArr[i][i2].a;
                BaseUnit[] baseUnitArrA = unitList2.a();
                int i3 = unitList2.b;
                for (int i4 = 0; i4 < i3; i4++) {
                    BaseUnit baseUnit = baseUnitArrA[i4];
                    float f8 = baseUnit.posX;
                    float f9 = baseUnit.posY;
                    if (f4 <= f8 && f8 <= f5 && f6 <= f9 && f9 <= f7) {
                        unitList.add(baseUnit);
                    }
                }
            }
        }
    }

    public UnitListIterator b(float f, float f2, float f3) {
        UnitList unitList = this.i;
        unitList.clear();
        b(f, f2, f3, unitList);
        this.j.a(unitList);
        return this.j;
    }

    public final void b(float f, float f2, float f3, UnitList unitList) {
        UnitStatisticsManager[][] unitStatisticsManagerArr = this.e;
        float f4 = f - f3;
        float f5 = f + f3;
        float f6 = f2 - f3;
        float f7 = f2 + f3;
        int iA = a(f4 - 50.0f);
        int iA2 = a(f5 + 50.0f);
        int iB = b(f6 - 50.0f);
        int iB2 = b(f7 + 50.0f);
        for (int i = iA; i <= iA2; i++) {
            for (int i2 = iB; i2 <= iB2; i2++) {
                com.corrodinggames.rts.game.units.management.UnitList unitList2 = unitStatisticsManagerArr[i][i2].a;
                BaseUnit[] baseUnitArrA = unitList2.a();
                int i3 = unitList2.b;
                for (int i4 = 0; i4 < i3; i4++) {
                    BaseUnit baseUnit = baseUnitArrA[i4];
                    float f8 = baseUnit.posX;
                    float f9 = baseUnit.posY;
                    float f10 = baseUnit.radius;
                    if (f4 - f10 <= f8 && f8 <= f5 + f10 && f6 - f10 <= f9 && f9 <= f7 + f10) {
                        unitList.b(baseUnit);
                    }
                }
            }
        }
    }

    public final void a(PlayerTeam playerTeam, float f, float f2, float f3, UnitList unitList) {
        UnitStatisticsManager[][] unitStatisticsManagerArr = this.e;
        float f4 = f - f3;
        float f5 = f + f3;
        float f6 = f2 - f3;
        float f7 = f2 + f3;
        int iA = a(f4 - 50.0f);
        int iA2 = a(f5 + 50.0f);
        int iB = b(f6 - 50.0f);
        int iB2 = b(f7 + 50.0f);
        int i = playerTeam.teamId;
        for (int i2 = iA; i2 <= iA2; i2++) {
            for (int i3 = iB; i3 <= iB2; i3++) {
                com.corrodinggames.rts.game.units.management.UnitList unitList2 = unitStatisticsManagerArr[i2][i3].b[i];
                BaseUnit[] baseUnitArrA = unitList2.a();
                int i4 = unitList2.b;
                for (int i5 = 0; i5 < i4; i5++) {
                    BaseUnit baseUnit = baseUnitArrA[i5];
                    float f8 = baseUnit.posX;
                    float f9 = baseUnit.posY;
                    float f10 = baseUnit.radius;
                    if (f4 - f10 <= f8 && f8 <= f5 + f10 && f6 - f10 <= f9 && f9 <= f7 + f10) {
                        unitList.b(baseUnit);
                    }
                }
            }
        }
    }

    public void a(RectF rectF, UnitCallback unitCallback, OrderableUnit orderableUnit, float f, FilteredUnitCallback filteredUnitCallback) {
        UnitStatisticsManager[][] unitStatisticsManagerArr = this.e;
        int iA = a(rectF.a);
        int iA2 = a(rectF.c);
        int iB = b(rectF.b);
        int iB2 = b(rectF.d);
        PlayerTeam playerTeamK = null;
        int iExcludeTeam = filteredUnitCallback.excludeTeam(orderableUnit);
        if (iExcludeTeam != -2 && iExcludeTeam != -3) {
            playerTeamK = PlayerTeam.k(iExcludeTeam);
        }
        PlayerTeam playerTeamOnlyEnemiesOfTeam = filteredUnitCallback.onlyEnemiesOfTeam(orderableUnit);
        PlayerTeam playerTeamOnlyTeam = filteredUnitCallback.onlyTeam(orderableUnit);
        filteredUnitCallback.setup(orderableUnit, f);
        if (playerTeamOnlyEnemiesOfTeam == null && playerTeamOnlyTeam == null) {
            for (int i = iA; i <= iA2; i++) {
                for (int i2 = iB; i2 <= iB2; i2++) {
                    com.corrodinggames.rts.game.units.management.UnitList unitList = unitStatisticsManagerArr[i][i2].a;
                    BaseUnit[] baseUnitArrA = unitList.a();
                    int i3 = unitList.b;
                    for (int i4 = 0; i4 < i3; i4++) {
                        BaseUnit baseUnit = baseUnitArrA[i4];
                        if ((playerTeamK == null || baseUnit.team != playerTeamK) && unitCallback.a(baseUnit)) {
                            filteredUnitCallback.callback(orderableUnit, f, baseUnit);
                        }
                    }
                }
            }
            return;
        }
        if (playerTeamOnlyTeam != null) {
            int i5 = playerTeamOnlyTeam.teamId;
            if (i5 == -1) {
                for (int i6 = iA; i6 <= iA2; i6++) {
                    for (int i7 = iB; i7 <= iB2; i7++) {
                        com.corrodinggames.rts.game.units.management.UnitList unitList2 = unitStatisticsManagerArr[i6][i7].d;
                        if (unitList2.b > 0) {
                            BaseUnit[] baseUnitArrA2 = unitList2.a();
                            int i8 = unitList2.b;
                            for (int i9 = 0; i9 < i8; i9++) {
                                BaseUnit baseUnit2 = baseUnitArrA2[i9];
                                if (unitCallback.a(baseUnit2)) {
                                    filteredUnitCallback.callback(orderableUnit, f, baseUnit2);
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (i5 == -2) {
                for (int i10 = iA; i10 <= iA2; i10++) {
                    for (int i11 = iB; i11 <= iB2; i11++) {
                        com.corrodinggames.rts.game.units.management.UnitList unitList3 = unitStatisticsManagerArr[i10][i11].c;
                        if (unitList3.b > 0) {
                            BaseUnit[] baseUnitArrA3 = unitList3.a();
                            int i12 = unitList3.b;
                            for (int i13 = 0; i13 < i12; i13++) {
                                BaseUnit baseUnit3 = baseUnitArrA3[i13];
                                if (unitCallback.a(baseUnit3)) {
                                    filteredUnitCallback.callback(orderableUnit, f, baseUnit3);
                                }
                            }
                        }
                    }
                }
                return;
            }
            for (int i14 = iA; i14 <= iA2; i14++) {
                for (int i15 = iB; i15 <= iB2; i15++) {
                    com.corrodinggames.rts.game.units.management.UnitList unitList4 = unitStatisticsManagerArr[i14][i15].b[i5];
                    if (unitList4.b > 0) {
                        BaseUnit[] baseUnitArrA4 = unitList4.a();
                        int i16 = unitList4.b;
                        for (int i17 = 0; i17 < i16; i17++) {
                            BaseUnit baseUnit4 = baseUnitArrA4[i17];
                            if (unitCallback.a(baseUnit4)) {
                                filteredUnitCallback.callback(orderableUnit, f, baseUnit4);
                            }
                        }
                    }
                }
            }
            return;
        }
        if (playerTeamOnlyEnemiesOfTeam != PlayerTeam.TEAM_UNKNOWN) {
            for (int i18 = iA; i18 <= iA2; i18++) {
                for (int i19 = iB; i19 <= iB2; i19++) {
                    com.corrodinggames.rts.game.units.management.UnitList unitList5 = unitStatisticsManagerArr[i18][i19].c;
                    if (unitList5.b > 0) {
                        BaseUnit[] baseUnitArrA5 = unitList5.a();
                        int i20 = unitList5.b;
                        for (int i21 = 0; i21 < i20; i21++) {
                            BaseUnit baseUnit5 = baseUnitArrA5[i21];
                            if (unitCallback.a(baseUnit5)) {
                                filteredUnitCallback.callback(orderableUnit, f, baseUnit5);
                            }
                        }
                    }
                }
            }
        }
        int i22 = this.m;
        for (int i23 = 0; i23 <= i22; i23++) {
            PlayerTeam playerTeamK2 = PlayerTeam.k(i23);
            if (playerTeamK2 != null && playerTeamOnlyEnemiesOfTeam != playerTeamK2 && playerTeamOnlyEnemiesOfTeam.c(playerTeamK2)) {
                for (int i24 = iA; i24 <= iA2; i24++) {
                    for (int i25 = iB; i25 <= iB2; i25++) {
                        com.corrodinggames.rts.game.units.management.UnitList unitList6 = unitStatisticsManagerArr[i24][i25].b[i23];
                        int i26 = unitList6.b;
                        if (i26 > 0) {
                            BaseUnit[] baseUnitArrA6 = unitList6.a();
                            for (int i27 = 0; i27 < i26; i27++) {
                                BaseUnit baseUnit6 = baseUnitArrA6[i27];
                                if (unitCallback.a(baseUnit6)) {
                                    filteredUnitCallback.callback(orderableUnit, f, baseUnit6);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public final int a(float f) {
        int i = (int) (f * this.c);
        if (i < 0) {
            i = 0;
        }
        if (i >= 32) {
            i = 31;
        }
        return i;
    }

    public final int b(float f) {
        int i = (int) (f * this.d);
        if (i < 0) {
            i = 0;
        }
        if (i >= 32) {
            i = 31;
        }
        return i;
    }

    public void a() {
        float f = this.c;
        float f2 = this.d;
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i = 0; i < size; i++) {
            BaseUnit baseUnit = baseUnitArrA[i];
            if (baseUnit.isDead || ((int) (baseUnit.posX * f)) != baseUnit.unitAnimationFrame || ((int) (baseUnit.posY * f2)) != baseUnit.unitAnimationSpeed || baseUnit.team == null || baseUnit.unitAnimationType != baseUnit.team.teamId) {
                a(baseUnit);
            }
        }
    }

    public void a(BaseUnit baseUnit) {
        if (this.e == null) {
            if (GameEngine.getInstance().currentTick != 0) {
                GameEngine.logColored("updateUnitGeoIndex: areaList not active");
            }
            baseUnit.unitAnimationFrame = -1;
            baseUnit.unitAnimationSpeed = -1;
            return;
        }
        if (baseUnit.isDead) {
            if (baseUnit.unitAnimationFrame != -1 && baseUnit.unitAnimationSpeed != -1) {
                this.e[baseUnit.unitAnimationFrame][baseUnit.unitAnimationSpeed].b(baseUnit);
                baseUnit.unitAnimationFrame = -1;
                baseUnit.unitAnimationSpeed = -1;
                return;
            }
            return;
        }
        int iA = a(baseUnit.posX);
        int iB = b(baseUnit.posY);
        int i = -2;
        if (baseUnit.team != null) {
            i = baseUnit.team.teamId;
        }
        if (baseUnit.unitAnimationFrame == iA && baseUnit.unitAnimationSpeed == iB && baseUnit.unitAnimationType == i) {
            return;
        }
        if (baseUnit.unitAnimationFrame != -1 && baseUnit.unitAnimationSpeed != -1) {
            this.e[baseUnit.unitAnimationFrame][baseUnit.unitAnimationSpeed].b(baseUnit);
        }
        baseUnit.unitAnimationFrame = iA;
        baseUnit.unitAnimationSpeed = iB;
        baseUnit.unitAnimationType = i;
        if (i > this.m && this.m < PlayerTeam.TEAM_NEUTRAL) {
            this.m = i;
        }
        this.e[baseUnit.unitAnimationFrame][baseUnit.unitAnimationSpeed].a(baseUnit);
    }

    public void a(TileMap tileMap) {
        this.e = new UnitStatisticsManager[32][32];
        this.m = 0;
        for (int i = 0; i < 32; i++) {
            for (int i2 = 0; i2 < 32; i2++) {
                this.e[i][i2] = new UnitStatisticsManager();
            }
        }
        this.a = (tileMap.tileCountX * tileMap.tileWorldSizeX) / 32;
        this.b = (tileMap.tileCountY * tileMap.tileWorldSizeY) / 32;
        this.c = 1.0f / this.a;
        this.d = 1.0f / this.b;
    }

    public void b() {
        this.e = (UnitStatisticsManager[][]) null;
    }

    public void c(float f) {
    }
}
