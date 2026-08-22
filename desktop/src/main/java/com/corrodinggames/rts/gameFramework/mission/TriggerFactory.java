package com.corrodinggames.rts.gameFramework.mission;

import android.graphics.Paint;
import android.graphics.Typeface;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.MapObject;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.LocaleString;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.mission.conditions.TeamTagDetectCondition;
import com.corrodinggames.rts.gameFramework.mission.conditions.UnitCountCondition;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/c.class */
public class TriggerFactory {
    public static MapTrigger a(MissionEngine missionEngine, MapObject mapObject) throws MapLoadException {
        try {
            GameEngine gameEngine = GameEngine.getInstance();
            String str = mapObject.name;
            if (str == null) {
                str = "NULL";
            }
            String description = mapObject.getDescription("id");
            if (description != null && !description.equals(VariableScope.nullOrMissingString)) {
                str = description;
            }
            String strTrim = str.trim();
            String str2 = mapObject.type;
            if (str2 != null) {
                TriggerType triggerTypeA = TriggerType.a(str2);
                if (triggerTypeA == null) {
                    MissionEngine.c("Error: Unknown type:" + str2 + " found on " + strTrim);
                    return null;
                }
                MapTrigger mapTrigger = new MapTrigger();
                mapTrigger.mapObject = mapObject;
                mapTrigger.triggerType = triggerTypeA;
                mapTrigger.rawId = strTrim;
                int i = 0;
                Iterator it = missionEngine.J.iterator();
                while (it.hasNext()) {
                    if (((MapTrigger) it.next()).rawId.equalsIgnoreCase(mapTrigger.rawId)) {
                        i++;
                    }
                }
                mapTrigger.uniqueId = mapTrigger.rawId;
                if (i != 0) {
                    mapTrigger.uniqueId += "_" + i;
                }
                mapTrigger.name = mapObject.name;
                Integer numD = mapTrigger.d("team");
                if (numD != null) {
                    mapTrigger.team = PlayerTeam.k(numD.intValue());
                    if (mapTrigger.team == null) {
                        mapTrigger.g("Cannot find team:" + numD);
                        return null;
                    }
                }
                mapTrigger.r = mapTrigger.b("delay", mapTrigger.r);
                mapTrigger.repeatDelay = mapTrigger.b("repeatDelay", mapTrigger.repeatDelay);
                mapTrigger.o = mapTrigger.a("repeatCount", mapTrigger.o);
                mapTrigger.q = mapTrigger.b("resetActivationAfter", mapTrigger.q);
                mapTrigger.allToActivate = mapTrigger.a("allToActivate", false);
                mapTrigger.d.requireAll = mapTrigger.allToActivate;
                mapTrigger.s = mapTrigger.b("warmup", mapTrigger.s);
                mapTrigger.A = mapTrigger.a("globalMessage", (LocaleString) null);
                mapTrigger.textOffsetX = mapTrigger.a("textOffsetX", 0.0f);
                mapTrigger.textOffsetY = mapTrigger.a("textOffsetY", 0.0f);
                if (mapTrigger.triggerType == TriggerType.mapText || mapTrigger.triggerType == TriggerType.objective) {
                    mapTrigger.text = mapTrigger.a("text", (LocaleString) null);
                }
                if (mapTrigger.triggerType == TriggerType.mapText) {
                    missionEngine.hasMapText = true;
                    mapTrigger.B = new Paint();
                    mapTrigger.B.a(true);
                    mapTrigger.B.a(Paint.Align.CENTER);
                    mapTrigger.B.a(Typeface.a(Typeface.c, 1));
                    mapTrigger.B.b(mapTrigger.c("textColor", -1));
                    gameEngine.setScaledTextSize(mapTrigger.B, mapTrigger.a("textSize", 20));
                    if (mapTrigger.B.f() == 0) {
                        mapTrigger.g("Text has an alpha of 0");
                    }
                    String strB = mapTrigger.b("style");
                    if (strB != null && !strB.equals(VariableScope.nullOrMissingString)) {
                        if (strB.equalsIgnoreCase("arrow")) {
                            mapTrigger.C = true;
                        } else {
                            mapTrigger.g("Unknown style: " + strB);
                        }
                    }
                }
                if (mapTrigger.triggerType == TriggerType.event_unitAdd) {
                    try {
                        mapTrigger.v = UnitSpawner.a(mapTrigger.b("spawnUnits"), "<unitAdd>", "spawnUnits");
                        if (mapTrigger.a() == null) {
                            mapTrigger.g("No team set");
                        }
                    } catch (ConfigParseException e) {
                        MissionEngine.c(e.getMessage());
                        return null;
                    }
                }
                if (mapTrigger.triggerType == TriggerType.event_teamTags) {
                    mapTrigger.a("addTeamTags");
                    mapTrigger.a("removeTeamTags");
                }
                if (mapTrigger.triggerType == TriggerType.event_changeCredits) {
                    mapTrigger.a("add");
                    mapTrigger.a("set");
                }
                if (mapTrigger.triggerType == TriggerType.trigger_unitDetect) {
                    mapTrigger.a(UnitCountCondition.d(mapTrigger));
                }
                if (mapTrigger.triggerType == TriggerType.trigger_teamTagDetect) {
                    mapTrigger.a(TeamTagDetectCondition.d(mapTrigger));
                }
                mapTrigger.a("comment");
                mapTrigger.a("team");
                mapTrigger.a("globalMessage");
                mapTrigger.a("globalMessage_delayPerChar");
                mapTrigger.a("globalMessage_textColor");
                mapTrigger.a("debugMessage");
                mapTrigger.a("showOnMap");
                mapTrigger.a("text");
                mapTrigger.a("target");
                mapTrigger.a("onlyIfEmpty");
                if (mapTrigger.triggerType == TriggerType.event_move) {
                    mapTrigger.a("unload");
                }
                if (mapTrigger.triggerType == TriggerType.event_unitRemove) {
                    mapTrigger.a("onlyIfEmpty");
                }
                return mapTrigger;
            }
            MissionEngine.c("Error: no type field set for: " + strTrim);
            return null;
        } catch (RuntimeException e2) {
            throw new MapLoadException("Error while reading: " + mapObject.getTriggerTag(), e2);
        }
    }
}
