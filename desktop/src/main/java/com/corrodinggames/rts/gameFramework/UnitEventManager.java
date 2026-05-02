package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bl */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bl.class */
public class UnitEventManager {
    FastArrayList a = new FastArrayList();

    public void a(BaseUnit baseUnit, BaseUnit baseUnit2) {
        if (this.a.size > 0) {
            Iterator it = this.a.iterator();
            while (it.hasNext()) {
                ((UnitEventListener) it.next()).a(baseUnit, baseUnit2, null);
            }
        }
    }
}
