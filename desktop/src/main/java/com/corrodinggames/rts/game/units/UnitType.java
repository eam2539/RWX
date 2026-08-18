package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.SelectUnitTypeAction;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.PlacementRules;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.as */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/as.class */
public interface UnitType {
    boolean isAvailableInDemo();

    boolean w();

    BaseUnit a();

    SelectUnitTypeAction d();

    int c();

    int b(int i);

    UnitPrice u();

    UnitPrice d(int i);

    UnitPrice B();

    Texture z();

    boolean y();

    float D();

    int g();

    boolean isBuildingUnit();

    boolean l();

    boolean k();

    boolean m();

    boolean n();

    UnitMovementType o();

    boolean p();

    PlacementRules q();

    /* JADX INFO: renamed from: e */
    String getUnitName();

    String f();

    /* JADX INFO: renamed from: i */
    String getUnitTypeDescriptionShort();

    void h();

    ArrayList<AbstractUnitAction> a(int i);

    String v();

    AnimationSet x();

    int a(BaseUnit baseUnit);
}
