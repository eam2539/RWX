package com.corrodinggames.rts.game.units.custom.logic.actions;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logic.CustomActionDef;
import com.corrodinggames.rts.game.units.custom.logic.LogicAction;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.a.a.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/a/a/l.class */
public class TagAction extends LogicAction {

    /* JADX INFO: renamed from: a */
    boolean resetToDefaultTags;

    /* JADX INFO: renamed from: b */
    AnimationSet temporarilyAddTags;

    /* JADX INFO: renamed from: c */
    AnimationSet temporarilyRemoveTags;

    /* JADX INFO: renamed from: d */
    AnimationSet addGlobalTeamTags;

    /* JADX INFO: renamed from: e */
    AnimationSet removeGlobalTeamTags;

    public static void a(CustomUnitConfig customUnitConfig, IniFile iniFile, String str, String str2, CustomActionDef customActionDef, String str3, boolean z) {
        boolean zBooleanValue = iniFile.getBoolean(str, str2 + "resetToDefaultTags", (Boolean) false).booleanValue();
        AnimationSet animationSet = iniFile.getAnimationSet(customUnitConfig, str, str2 + "temporarilyAddTags", (AnimationSet) null);
        AnimationSet animationSet2 = iniFile.getAnimationSet(customUnitConfig, str, str2 + "temporarilyRemoveTags", (AnimationSet) null);
        if (zBooleanValue || animationSet != null || animationSet2 != null) {
            TagAction tagAction = new TagAction();
            tagAction.resetToDefaultTags = zBooleanValue;
            tagAction.temporarilyAddTags = animationSet;
            tagAction.temporarilyRemoveTags = animationSet2;
            customActionDef.ac.add(tagAction);
        }
        AnimationSet animationSet3 = iniFile.getAnimationSet(customUnitConfig, str, str2 + "addGlobalTeamTags", (AnimationSet) null);
        AnimationSet animationSet4 = iniFile.getAnimationSet(customUnitConfig, str, str2 + "removeGlobalTeamTags", (AnimationSet) null);
        if (animationSet3 != null || animationSet4 != null) {
            TagAction tagAction2 = new TagAction();
            tagAction2.addGlobalTeamTags = animationSet3;
            tagAction2.removeGlobalTeamTags = animationSet4;
            customActionDef.ac.add(tagAction2);
        }
    }

    @Override // com.corrodinggames.rts.game.units.custom.logic.LogicAction
    /* JADX INFO: renamed from: a */
    public boolean doAction(CustomUnit customUnit, AbstractUnitAction abstractUnitAction, PointF pointF, BaseUnit baseUnit, int i) {
        if (this.resetToDefaultTags) {
            customUnit.j(false);
        }
        if (this.temporarilyRemoveTags != null) {
            customUnit.b(this.temporarilyRemoveTags);
        }
        if (this.temporarilyAddTags != null) {
            customUnit.a(this.temporarilyAddTags);
        }
        if (this.addGlobalTeamTags != null) {
            customUnit.team.b(this.addGlobalTeamTags);
        }
        if (this.removeGlobalTeamTags != null) {
            customUnit.team.c(this.removeGlobalTeamTags);
            return true;
        }
        return true;
    }
}
