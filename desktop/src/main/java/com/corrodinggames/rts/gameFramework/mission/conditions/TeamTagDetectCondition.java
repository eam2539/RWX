package com.corrodinggames.rts.gameFramework.mission.conditions;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.mission.MapTrigger;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/a/b.class */
public class TeamTagDetectCondition extends TriggerCondition {
    PlayerTeam a;
    AnimationTag b;

    public static TeamTagDetectCondition d(MapTrigger mapTrigger) throws MapLoadException {
        TeamTagDetectCondition teamTagDetectCondition = new TeamTagDetectCondition();
        teamTagDetectCondition.a = mapTrigger.a();
        if (teamTagDetectCondition.a == null) {
            throw new MapLoadException("teamTagDetect requires a team set");
        }
        String strB = mapTrigger.b("teamTag");
        if (strB != null && !strB.equals(VariableScope.nullOrMissingString)) {
            try {
                teamTagDetectCondition.b = AnimationTag.b(strB);
                return teamTagDetectCondition;
            } catch (ConfigParseException e) {
                throw new MapLoadException(e.getMessage());
            }
        }
        throw new MapLoadException("teamTagDetect requires a teamTag set");
    }

    @Override // com.corrodinggames.rts.gameFramework.mission.conditions.TriggerCondition
    public boolean b(MapTrigger mapTrigger) {
        if (AnimationTag.a(this.b, this.a.getTeamAnimationSet())) {
            return true;
        }
        return false;
    }
}
