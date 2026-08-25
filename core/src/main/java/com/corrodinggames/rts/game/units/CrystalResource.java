package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.units.buildings.BaseBuilding;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolBlendColorFilter;
import io.github.rwx.render.canvas.KoolCanvasBlendMode;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e.class */
public class CrystalResource extends BaseBuilding {

    /* JADX INFO: renamed from: b */
    float animationTimer;
    /* JADX INFO: renamed from: a */
    static Texture texture = null;
    static KoolBlendColorFilter c = new KoolBlendColorFilter(KoolArgbColor.a(200, 200, 200), KoolCanvasBlendMode.Multiply);

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.crystalResource;
    }

    public static void a_() {
        texture = GameEngine.getInstance().renderGraphicsEngine.a(R.drawable.crystal);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        return texture;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
    }

    public CrystalResource(boolean z) {
        super(z);
        this.baseTexture = texture;
        b(texture);
        this.radius = 11.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 600.0f;
        this.currentHealth = this.maxHealth;
        S(1);
        this.buildingTargetRect.a(0, -1, 0, 0);
        this.buildingVelocityRect.a(this.buildingTargetRect);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding
    /* JADX INFO: renamed from: f */
    public KoolPaint getBuildingPaint() {
        return super.getBuildingPaint();
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        this.animationTimer += 0.01f * f;
        if (this.animationTimer > 1.0f) {
            this.animationTimer -= 1.0f;
            if (this.animationTimer > 1.0f) {
                this.animationTimer = 0.0f;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: g */
    public float getResourceRate() {
        return 0.02f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType getMovementType() {
        return UnitMovementType.NONE;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.BaseBuilding, com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: s_ */
    public boolean isVisibleOnScreen() {
        GameEngine gameEngine = GameEngine.getInstance();
        du.a(getVisibilityBounds());
        return RectF.a(gameEngine.visibleScreenRect, du);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void n() {
        super.n();
        this.animationTimer = ((this.posY * 5.0f) + (this.posX * 3.0f)) % 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean o() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean p() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean q() {
        return true;
    }
}
