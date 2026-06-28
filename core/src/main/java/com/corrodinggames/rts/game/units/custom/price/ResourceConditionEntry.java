package com.corrodinggames.rts.game.units.custom.price;

import com.corrodinggames.rts.game.units.custom.condition.resources.Resource;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.d.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/d/d.class */
class ResourceConditionEntry {
    public final Resource a;
    public double b;
    public LogicBoolean c;

    public ResourceConditionEntry(Resource resource, LogicBoolean logicBoolean) {
        this.a = resource;
        if (this.c instanceof LogicBoolean.StaticValueBoolean) {
            this.b = ((LogicBoolean.StaticValueBoolean) this.c).getStaticValue();
        } else {
            this.c = logicBoolean;
        }
    }
}
