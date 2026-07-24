package com.corrodinggames.rts.gameFramework.statistics;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.DummyUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.condition.StoredResources;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/x.class */
public class PlaceholderUnit extends DummyUnit {
    StoredResources a;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.fogRevealer;
    }

    public PlaceholderUnit() {
        super(true);
        this.a = new StoredResources();
        this.team = PlayerTeam.TEAM_ALL;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        GameEngine.logErrorColored("PlaceholderUnit was updated");
        removeFromGame();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean t() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.DummyUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean u() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: df */
    public StoredResources getCustomResources() {
        return this.a;
    }

    public void a(StoredResources storedResources) {
        this.a = storedResources;
    }
}
