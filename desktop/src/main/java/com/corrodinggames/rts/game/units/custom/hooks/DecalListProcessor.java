package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfigProcessor;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/g.class */
public class DecalListProcessor extends CustomUnitConfigProcessor {
    String a;
    FastArrayList b = new FastArrayList();

    public DecalListProcessor(String str) {
        this.a = str;
    }

    @Override // com.corrodinggames.rts.game.units.custom.CustomUnitConfigProcessor
    public void a(CustomUnitConfig customUnitConfig) throws ConfigParseException {
        if (this.a != null) {
            for (String str : this.a.split(",")) {
                String strTrim = str.trim();
                DecalDefinition decalDefinitionC = CustomUnitDecalRenderer.c(customUnitConfig, strTrim);
                if (decalDefinitionC == null) {
                    throw new ConfigParseException("Failed to find decal: " + strTrim);
                }
                this.b.add(decalDefinitionC);
            }
            this.a = null;
        }
    }

    public void a(CustomUnit customUnit, float f, float f2) {
        CustomUnitDecalRenderer.i.a(f, f2);
        CustomUnitDecalRenderer.a(customUnit, 0.0f, DecalLayer.inactive, this.b, CustomUnitDecalRenderer.i);
    }
}
