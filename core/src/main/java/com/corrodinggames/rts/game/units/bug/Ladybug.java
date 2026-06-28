package com.corrodinggames.rts.game.units.bug;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.ScorchMark;
import com.corrodinggames.rts.game.units.AttackMode;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.land.LandUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.effects.EffectQuality;
import com.corrodinggames.rts.gameFramework.effects.EffectType;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.c.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/c/a.class */
public class Ladybug extends LandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture[] d = new Texture[10];
    int e;
    float f;
    Rect g;
    Rect h;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.ladybug;
    }

    public static void f() {
        b = GameEngine.getInstance().renderGraphicsEngine.a(R.drawable.ladybug);
        d = PlayerTeam.getTeamColorTextures(b);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return d[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.effectManager.createEffectInternal(this.posX, this.posY, this.posZ, EffectType.blood, false, EffectQuality.high) != null) {
        }
        gameEngine.soundEngine.playSound(SoundEngine.bugDieSound, 0.8f, this.posX, this.posY);
        ScorchMark.a(this, 1);
        return false;
    }

    public Ladybug(boolean z) {
        super(z);
        this.e = 0;
        this.f = 0.0f;
        this.g = new Rect();
        this.h = new Rect();
        T(17);
        U(26);
        this.radius = 5.0f;
        this.displayRadius = this.radius + 3.0f;
        this.maxHealth = 130.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
        this.attackMode = AttackMode.outOfRange;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        int i = this.e * this.es;
        this.g.a(i, 0, i + this.es, 0 + this.et);
        return this.g;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bP() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bO() {
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isMoving) {
            if (this.e == 0) {
                this.e = 1;
            } else {
                this.e = 0;
            }
        }
        if (this.f != 0.0f) {
            this.f = Utility.moveTowardsZero(this.f, f);
            this.e = 2;
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        Projectile.a((BaseUnit) this, baseUnit, 14.0f, (Projectile) null, false);
        this.f = 4.0f;
        PointF pointFE = E(i);
        GameEngine.getInstance().soundEngine.playSound(SoundEngine.bugAttackSound, 0.3f, pointFE.x, pointFE.y);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 43.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 17.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 1.7f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 5.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 99.0f;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        return super.c(f);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.07f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.12f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 7.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean E() {
        return true;
    }
}
