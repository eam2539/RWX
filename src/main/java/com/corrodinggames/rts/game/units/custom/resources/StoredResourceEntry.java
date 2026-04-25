package com.corrodinggames.rts.game.units.custom.resources;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.e.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/e/e.class */
public final class StoredResourceEntry {
    public final Resource a;
    public double b;

    public StoredResourceEntry(Resource resource) {
        this.a = resource;
    }

    public StoredResourceEntry(Resource resource, double d) {
        this.a = resource;
        this.b = d;
    }
}
