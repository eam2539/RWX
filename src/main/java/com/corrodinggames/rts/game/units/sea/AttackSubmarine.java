package com.corrodinggames.rts.game.units.sea;

import android.graphics.Color;
import android.graphics.PointF;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.actions.NoneAction;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.h.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/h/e.class */
public class AttackSubmarine extends WaterUnit {
    boolean a;
    boolean b;
    float c;
    static Texture d = null;
    static Texture e = null;
    static Texture f = null;
    public static Texture g = null;
    public static Texture[] h = new Texture[10];
    static Texture[] i = new Texture[10];
    public static final AbstractUnitAction j = new NoneAction(151) { // from class: com.corrodinggames.rts.game.units.h.e.1
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String isLocked() {
            return "-Surface unit. Allows it to fire missiles";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getCostForUnit() {
            return "Surface";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            return !((AttackSubmarine) baseUnit).a;
        }
    };
    public static final AbstractUnitAction k = new NoneAction(152) { // from class: com.corrodinggames.rts.game.units.h.e.2
        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public String isLocked() {
            return "-Dive unit underwater. Evades most attacks";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: b */
        public String getCostForUnit() {
            return "Dive";
        }

        @Override // com.corrodinggames.rts.game.units.actions.AbstractUnitAction
        /* JADX INFO: renamed from: a */
        public boolean drawTooltip(BaseUnit baseUnit, boolean z) {
            return ((AttackSubmarine) baseUnit).a;
        }
    };
    static ArrayList l = new ArrayList();

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.a);
        gameOutputStream.writeFloat(this.c);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.a = gameInputStream.readBoolean();
        this.b = !Q();
        if (gameInputStream.getProtocolVersion() >= 21) {
            this.c = gameInputStream.readFloat();
        }
        L();
        super.a(gameInputStream);
    }

    static {
        l.add(j);
        l.add(k);
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return h[this.team.getTeamColorIndex()];
    }

    public static void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        e = gameEngine.graphicsEngine2.a(R.drawable.attack_submarine);
        f = attackUnit(e, e.m(), e.l());
        d = gameEngine.graphicsEngine2.a(R.drawable.attack_submarine_dead);
        g = gameEngine.graphicsEngine2.a(R.drawable.unit_icon_water);
        h = PlayerTeam.getUnitCountByType(g);
        i = PlayerTeam.getUnitCountByType(e);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ >= 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return d;
        }
        return i[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return f;
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType h() {
        return UnitMovementType.WATER;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: f, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.attackSubmarine;
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit
    /* JADX INFO: renamed from: K */
    public boolean shouldCreateSmokeEffect() {
        if (!Q()) {
            return true;
        }
        return false;
    }

    public AttackSubmarine(boolean z) {
        super(z);
        this.a = false;
        this.b = true;
        this.c = 0.0f;
        b(e);
        this.speed = 15.0f;
        this.maxSpeed = this.speed - 2.0f;
        this.maxHealth = 260.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = e;
    }

    public void L() {
        if (!this.b) {
            S(1);
        } else {
            S(2);
        }
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit
    /* JADX INFO: renamed from: s */
    public void adjustZPosition(float f2) {
        float f3;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.a) {
            f3 = 1.0f;
        } else {
            f3 = -8.0f;
        }
        if (Utility.abs(this.posZ - f3) > 2.0f) {
            this.c = Utility.distanceSq(this.c, 0.08f, 0.003f * f2);
        } else {
            this.c = Utility.distanceSq(this.c, 0.02f, 0.003f * f2);
        }
        this.posZ = Utility.distanceSq(this.posZ, f3, this.c * f2);
        boolean z = false;
        if (this.b && Q()) {
            this.b = false;
            L();
            z = true;
        }
        if (!this.b && !Q()) {
            this.b = true;
            L();
            z = true;
        }
        if (z) {
            gameEngine.effectManager.createDirectedExplosion(this.posX, this.posY, 0.0f, 0, 0.0f, 0.0f, this.rotationSpeed);
        }
    }

    @Override // com.corrodinggames.rts.game.units.sea.WaterUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        super.update(f2);
        if (!isAlive() || this.isDestroyed) {
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        if (!Q()) {
            return 250.0f;
        }
        return 180.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i2) {
        return 170.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i2) {
        return 10.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        if (!Q()) {
            return 0.8f;
        }
        return 0.45f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.06f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i2) {
        return 2.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i2) {
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.018f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i2) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean Q() {
        return this.posZ < -1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean ah() {
        if (!Q()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ae */
    public boolean canAttackSubmergedUnits() {
        if (!Q()) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        if (!Q()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: ag */
    public boolean canAttackSurfaceUnits() {
        if (!Q()) {
            return true;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i2) {
        return 42.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i2) {
        if (!Q()) {
            PointF pointFE = E(i2);
            Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y, this.posZ, i2);
            PointF pointFK = getShadowTexture(i2);
            projectileA.K = pointFK.x;
            projectileA.L = pointFK.y;
            projectileA.ar = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50);
            projectileA.U = 42.0f;
            projectileA.l = baseUnit;
            projectileA.h = 190.0f;
            projectileA.t = 2.0f;
            projectileA.aH = true;
            projectileA.aM = true;
            projectileA.aQ = true;
            GameEngine gameEngine = GameEngine.getInstance();
            gameEngine.soundEngine.playSound(SoundEngine.missileFireSound, 0.8f, this.posX, this.posY);
            gameEngine.effectManager.createLightEffect(this.posX, this.posY, this.posZ, -1118720);
            return;
        }
        PointF pointFE2 = E(i2);
        Projectile projectileA2 = Projectile.a(this, pointFE2.x, pointFE2.y, this.posZ, i2);
        PointF pointFK2 = getShadowTexture(i2);
        projectileA2.K = pointFK2.x;
        projectileA2.L = pointFK2.y;
        projectileA2.ar = Color.a(255, 30, 30, 150);
        projectileA2.x = 1.0f;
        projectileA2.U = 42.0f;
        projectileA2.l = baseUnit;
        projectileA2.h = 250.0f;
        projectileA2.t = 1.3f;
        projectileA2.aH = false;
        projectileA2.aM = true;
        projectileA2.aQ = true;
        GameEngine.getInstance();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = d;
        S(0);
        this.isAttacking = false;
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: a */
    public void performUnitAction(AbstractUnitAction abstractUnitAction, boolean z) {
        if (abstractUnitAction == j) {
            this.a = true;
        }
        if (abstractUnitAction == k) {
            this.a = false;
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: N */
    public ArrayList getAvailableActions() {
        return l;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f2) {
        super.e(f2);
        GameViewUtils.a(this, m());
    }
}
