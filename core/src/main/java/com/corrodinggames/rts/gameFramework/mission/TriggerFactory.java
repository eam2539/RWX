package com.corrodinggames.rts.gameFramework.mission;

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
import io.github.rwx.render.canvas.KoolPaint;
import io.github.rwx.render.canvas.KoolTypeface;

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
                mapTrigger.t = mapObject;
                mapTrigger.g = triggerTypeA;
                mapTrigger.b = strTrim;
                int i = 0;
                Iterator it = missionEngine.J.iterator();
                while (it.hasNext()) {
                    if (((MapTrigger) it.next()).b.equalsIgnoreCase(mapTrigger.b)) {
                        i++;
                    }
                }
                mapTrigger.c = mapTrigger.b;
                if (i != 0) {
                    mapTrigger.c += "_" + i;
                }
                mapTrigger.a = mapObject.name;
                Integer numD = mapTrigger.d("team");
                if (numD != null) {
                    mapTrigger.y = PlayerTeam.k(numD.intValue());
                    if (mapTrigger.y == null) {
                        mapTrigger.g("Cannot find team:" + numD);
                        return null;
                    }
                }
                mapTrigger.r = mapTrigger.b("delay", mapTrigger.r);
                mapTrigger.p = mapTrigger.b("repeatDelay", mapTrigger.p);
                mapTrigger.o = mapTrigger.a("repeatCount", mapTrigger.o);
                mapTrigger.q = mapTrigger.b("resetActivationAfter", mapTrigger.q);
                mapTrigger.h = mapTrigger.a("allToActivate", false);
                mapTrigger.d.b = mapTrigger.h;
                mapTrigger.s = mapTrigger.b("warmup", mapTrigger.s);
                mapTrigger.A = mapTrigger.a("globalMessage", (LocaleString) null);
                mapTrigger.w = mapTrigger.a("textOffsetX", 0.0f);
                mapTrigger.x = mapTrigger.a("textOffsetY", 0.0f);
                if (mapTrigger.g == TriggerType.mapText || mapTrigger.g == TriggerType.objective) {
                    mapTrigger.z = mapTrigger.a("text", (LocaleString) null);
                }
                if (mapTrigger.g == TriggerType.mapText) {
                    missionEngine.i = true;
                    mapTrigger.B = new KoolPaint();
                    mapTrigger.B.a(true);
                    mapTrigger.B.a(KoolPaint.Align.CENTER);
                    mapTrigger.B.a(KoolTypeface.a(KoolTypeface.c, 1));
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
                if (mapTrigger.g == TriggerType.event_unitAdd) {
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
                if (mapTrigger.g == TriggerType.event_teamTags) {
                    mapTrigger.a("addTeamTags");
                    mapTrigger.a("removeTeamTags");
                }
                if (mapTrigger.g == TriggerType.event_changeCredits) {
                    mapTrigger.a("add");
                    mapTrigger.a("set");
                }
                if (mapTrigger.g == TriggerType.trigger_unitDetect) {
                    mapTrigger.a(UnitCountCondition.d(mapTrigger));
                }
                if (mapTrigger.g == TriggerType.trigger_teamTagDetect) {
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
                if (mapTrigger.g == TriggerType.event_move) {
                    mapTrigger.a("unload");
                }
                if (mapTrigger.g == TriggerType.event_unitRemove) {
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
