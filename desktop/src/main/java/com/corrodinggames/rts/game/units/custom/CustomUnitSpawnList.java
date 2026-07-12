package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.effects.Effect;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/z.class */
public class CustomUnitSpawnList {

    /* JADX INFO: renamed from: a */
    public String rawSpawnListString;

    /* JADX INFO: renamed from: b */
    public EffectTemplate[] spawnItems;

    /* JADX INFO: renamed from: c */
    final /* synthetic */ CustomUnitConfig customUnitConfig;

    public boolean a() {
        return (this.spawnItems == null || this.spawnItems.length == 0) ? false : true;
    }

    public boolean b() {
        return this.spawnItems != null && (this.spawnItems.length != 0 || this.spawnItems == CustomUnitConfig.noneSpawnItems);
    }

    CustomUnitSpawnList(CustomUnitConfig customUnitConfig, String str) {
        this.customUnitConfig = customUnitConfig;
        this.rawSpawnListString = str;
        customUnitConfig.spawnLists.add(this);
    }

    public void c() throws ConfigParseException {
        if (this.rawSpawnListString == null || this.rawSpawnListString.equals(VariableScope.nullOrMissingString)) {
            this.spawnItems = CustomUnitConfig.emptySpawnItems;
            return;
        }
        if (this.rawSpawnListString.equalsIgnoreCase("NONE")) {
            this.spawnItems = CustomUnitConfig.noneSpawnItems;
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (String str : this.rawSpawnListString.split(",")) {
            String[] strArrSplit = str.trim().split("\\*");
            String str2 = strArrSplit[0];
            int i = 1;
            if (strArrSplit.length >= 2) {
                i = Integer.parseInt(strArrSplit[1]);
            }
            EffectTemplate effectTemplateResolveEffect = this.customUnitConfig.resolveEffect(str2);
            for (int i2 = 0; i2 < i; i2++) {
                arrayList.add(effectTemplateResolveEffect);
            }
        }
        this.spawnItems = (EffectTemplate[]) arrayList.toArray(CustomUnitConfig.emptySpawnItems);
    }

    public Effect a(float f, float f2, float f3, float f4, GameObject gameObject) {
        return a(f, f2, f3, f4, gameObject, 0, (short) 0);
    }

    public Effect a(float f, float f2, float f3, float f4, GameObject gameObject, int i, short s) {
        Effect effect = null;
        for (EffectTemplate effectTemplate : this.spawnItems) {
            Effect effectA = effectTemplate.a(f, f2, f3, f4, gameObject, i, s);
            if (effectA != null && effect == null) {
                effect = effectA;
            }
        }
        return effect;
    }
}
