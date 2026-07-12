package com.corrodinggames.rts.game.units.buildings;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/w.class */
public class WallVertical extends FactoryWithQueue {

    /* JADX INFO: renamed from: a */
    static Texture baseTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture[] teamTextures = new Texture[10];

    /* JADX INFO: renamed from: c */
    static Texture deadTexture = null;

    public static void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        baseTexture = gameEngine.renderGraphicsEngine.a(R.drawable.wall_v);
        deadTexture = gameEngine.renderGraphicsEngine.a(R.drawable.wall_v);
        teamTextures = PlayerTeam.getTeamColorTextures(baseTexture);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return deadTexture;
        }
        if (this.team == null) {
            return teamTextures[teamTextures.length - 1];
        }
        return teamTextures[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
    }

    public WallVertical(boolean z) {
        super(z);
        b(baseTexture);
        this.radius = 15.0f;
        this.displayRadius = this.radius;
        this.maxHealth = 700.0f;
        this.currentHealth = this.maxHealth;
        super.baseTexture = baseTexture;
        this.buildingTargetRect.a(0, 0, 1, 0);
        this.buildingVelocityRect.a(0, 0, 1, 0);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: K, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.wall_v;
    }
}
