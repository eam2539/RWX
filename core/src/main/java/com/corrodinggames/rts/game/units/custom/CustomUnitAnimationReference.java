package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/o.class */
public class CustomUnitAnimationReference {

    /* JADX INFO: renamed from: a */
    String animationName;

    /* JADX INFO: renamed from: b */
    AnimationConfig animationConfig;

    /* JADX INFO: renamed from: c */
    final /* synthetic */ CustomUnitConfig customUnitConfig;

    public CustomUnitAnimationReference(CustomUnitConfig customUnitConfig) {
        this.customUnitConfig = customUnitConfig;
    }

    public void a() {
        if (this.animationName != null && b() == null) {
            throw new RuntimeException("Failed to find animation:" + this.animationName);
        }
    }

    public AnimationConfig b() {
        if (this.animationName == null) {
            return null;
        }
        if (this.animationConfig != null) {
            return this.animationConfig;
        }
        for (AnimationConfig animationConfig : this.customUnitConfig.animations) {
            if (animationConfig.animationName.equalsIgnoreCase(this.animationName)) {
                this.animationConfig = animationConfig;
                return animationConfig;
            }
        }
        return null;
    }
}
