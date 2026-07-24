package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitAnimationReference;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/d.class */
public class AnimationAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    boolean finishPlayingLastAnimation;

    /* JADX INFO: renamed from: b */
    boolean stopLastAnimation;

    /* JADX INFO: renamed from: c */
    CustomUnitAnimationReference playAnimation;

    /* JADX INFO: renamed from: d */
    CustomUnitAnimationReference playAnimationIfNotPlaying;

    /* JADX INFO: renamed from: e */
    boolean playAnimationLowPriority;

    /* JADX INFO: renamed from: f */
    int animationPriority = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: g */
    int animationDelay = Integer.MIN_VALUE;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) {
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "finishPlayingLastAnimation", (Boolean) false).booleanValue();
        boolean zBooleanValue2 = iniFile.getBoolean(str, str2 + "stopLastAnimation", (Boolean) false).booleanValue();
        CustomUnitAnimationReference customUnitAnimationReferenceLoadCore = customUnitConfig.loadCore(iniFile.getString(str, str2 + "playAnimation", (String) null), (CustomUnitAnimationReference) null);
        CustomUnitAnimationReference customUnitAnimationReferenceLoadCore2 = customUnitConfig.loadCore(iniFile.getString(str, str2 + "playAnimationIfNotPlaying", (String) null), (CustomUnitAnimationReference) null);
        if (customUnitAnimationReferenceLoadCore != null && customUnitAnimationReferenceLoadCore2 != null) {
            throw new RuntimeException("Cannot use playAnimation and playAnimationIfNotPlaying at same time");
        }
        if (zBooleanValue2 && zBooleanValue) {
            throw new RuntimeException("Cannot use stopLastAnimation and finishPlayingLastAnimation at same time");
        }
        if (customUnitAnimationReferenceLoadCore != null || customUnitAnimationReferenceLoadCore2 != null || zBooleanValue || zBooleanValue2) {
            AnimationAction animationAction = new AnimationAction();
            animationAction.finishPlayingLastAnimation = zBooleanValue;
            animationAction.stopLastAnimation = zBooleanValue2;
            animationAction.playAnimation = customUnitAnimationReferenceLoadCore;
            animationAction.playAnimationIfNotPlaying = customUnitAnimationReferenceLoadCore2;
            animationAction.playAnimationLowPriority = iniFile.getBoolean(str, str2 + "playAnimation_lowPriority", (Boolean) false).booleanValue();
            customActionDef.logicActions.add(animationAction);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        if (this.finishPlayingLastAnimation) {
            customUnit.animationController.b();
        }
        if (this.stopLastAnimation) {
            customUnit.animationController.a();
        }
        if (this.playAnimation != null) {
            int i2 = 15;
            if (this.playAnimationLowPriority) {
                i2 = 4;
            }
            customUnit.animationController.a(this.playAnimation.b(), i2, true);
        }
        if (this.playAnimationIfNotPlaying != null) {
            int i3 = 15;
            if (this.playAnimationLowPriority) {
                i3 = 4;
            }
            if (!customUnit.animationController.a(this.playAnimationIfNotPlaying.b())) {
                customUnit.animationController.a(this.playAnimationIfNotPlaying.b(), i3, true);
                return true;
            }
            return true;
        }
        return true;
    }
}
