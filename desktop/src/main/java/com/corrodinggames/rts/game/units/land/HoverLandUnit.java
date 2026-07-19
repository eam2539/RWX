package com.corrodinggames.rts.game.units.land;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/h.class */
public abstract class HoverLandUnit extends LandUnit {
    float l;
    public static Texture m = null;
    public static Texture[] n = new Texture[10];

    public HoverLandUnit(boolean z) {
        super(z);
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        if (getUnitAICombatTarget()) {
            return LandUnit.landUnitIconTexturesExp[this.team.getTeamColorIndex()];
        }
        return n[this.team.getTeamColorIndex()];
    }

    public static void K() {
        m = GameEngine.getInstance().renderGraphicsEngine.a(R.drawable.unit_icon_hover);
        n = PlayerTeam.getTeamColorTextures(m);
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType h() {
        return UnitMovementType.HOVER;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (isAlive() && !this.isDead && isMoving()) {
            if (this.rotation > 0.0f) {
                this.l += f;
            }
            if (this.l > 10.0f) {
                this.l = 0.0f;
                if (isBuilding()) {
                    Effect effectCreateEffectInternal = GameEngine.getInstance().effectManager.createEffectInternal(this.posX + (Utility.fastCos(this.rotationSpeed) * 4.0f), this.posY + (Utility.fastSin(this.rotationSpeed) * 4.0f), 0.0f, EffectType.custom, false, EffectQuality.low);
                    if (effectCreateEffectInternal != null) {
                        effectCreateEffectInternal.aq = 0;
                        effectCreateEffectInternal.ap = 13;
                        effectCreateEffectInternal.ar = (short) 1;
                        effectCreateEffectInternal.r = true;
                        effectCreateEffectInternal.E = 0.8f;
                        effectCreateEffectInternal.W = 80.0f;
                        effectCreateEffectInternal.V = 80.0f;
                        effectCreateEffectInternal.P = (-Utility.fastCos(this.rotationSpeed)) * 0.1f;
                        effectCreateEffectInternal.Q = (-Utility.fastSin(this.rotationSpeed)) * 0.1f;
                        effectCreateEffectInternal.Y = Utility.randomFloatInRange(-180.0f, 180.0f);
                    }
                }
            }
        }
    }
}
