package com.corrodinggames.rts.gameFramework.mission;

import android.graphics.PointF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.gameFramework.Command;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.mission.conditions.TriggerCondition;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.ui.Message;
import com.corrodinggames.rts.gameFramework.ui.MinimapEffectType;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/d.class */
public class TriggerExecutor {
    public static void a(final MissionEngine f, final MapTrigger a) throws MapLoadException {
        final GameEngine instance = GameEngine.getInstance();
        boolean b = false;
        if (!a.j) {
            b = true;
        }
        f.a(a);
        a.i = true;
        a.j = true;
        a.k = instance.gameTimeMillis;
        boolean b2 = false;
        if (a.A != null) {
            final Message addMessage = instance.gameUI.messageManager.addMessage(null, a.A.resolveText());
            if (addMessage != null) {
                final String s = "globalMessage_delayPerChar";
                final String b3 = a.b(s);
                if (b3 != null) {
                    if (b3.equals("slow")) {
                        addMessage.delayPerCharMs = 18;
                    }
                    else {
                        final int n = a.b(s, -1);
                        if (n != -1) {
                            addMessage.delayPerCharMs = n;
                        }
                    }
                }
                final int n = a.c("globalMessage_textColor", -1);
                if (n != -1) {
                    addMessage.textColor = n;
                }
            }
            b2 = true;
        }
        final String b4 = a.b("debugMessage");
        if (b4 != null) {
            a.h("Debug: " + b4);
            if (instance.isGameStarted && instance.isDebugTempMode) {
                NetworkEngine.a((String)null, "Debug: " + b4);
            }
            b2 = true;
        }
        if (a.a("showOnMap", false)) {
            instance.minimap.addEffect(a.b(), a.c(), MinimapEffectType.message);
            b2 = true;
        }
        if (a.f.size > 0) {
            final Iterator iterator = a.f.iterator();
            while (iterator.hasNext()) {
                if (((TriggerCondition)iterator.next()).c(a)) {
                    b2 = true;
                }
            }
        }
        if (a.g == TriggerType.objective) {
            if (b) {
                a.h("objective met");
            }
            b2 = true;
        }
        if (a.g == TriggerType.trigger_basic) {
            b2 = true;
        }
        if (a.g == TriggerType.trigger_unitDetect) {
            b2 = true;
        }
        if (a.g == TriggerType.trigger_teamTagDetect) {
            b2 = true;
        }
        if (a.g == TriggerType.mapText) {
            b2 = true;
        }
        if (a.g == TriggerType.moveCamera) {
            b2 = true;
            final float n2 = (float)a.b();
            final float n3 = (float)a.c();
            instance.centerViewpoint(n2, n3);
        }
        if (a.g == TriggerType.event_unitAdd) {
            final float n2 = (float)a.b();
            final float n3 = (float)a.c();
            final float float3 = 0.0f;
            final float float4 = 0.0f;
            final PlayerTeam a2 = a.a();
            final BaseUnit am = null;
            final boolean boolean6 = false;
            final FastArrayList m = null;
            final boolean boolean7 = false;
            if (a2 == null) {
                a.g("No team set, cannot spawn");
            }
            else if (a.v != null) {
                a.v.a(n2, n3, float3, float4, a2, boolean6, am, m, boolean7);
            }
            else {
                a.g("No valid unit list to spawn");
            }
            b2 = true;
        }
        if (a.g == TriggerType.event_changeCredits) {
            final PlayerTeam a3 = a.a();
            if (a3 == null) {
                a.g("Team not set for changeCredits");
                return;
            }
            final Integer d = a.d("set");
            if (d != null) {
                a3.credits = d;
            }
            final Integer d2 = a.d("add");
            if (d2 != null) {
                a3.d((float)d2);
            }
            b2 = true;
        }
        else if (a.g == TriggerType.event_teamTags) {
            final PlayerTeam a4 = a.a();
            if (a4 == null) {
                a.g("Team not set for event_teamTags");
                return;
            }
            final String a5 = a.a("addTeamTags", (String)null);
            if (a5 != null) {
                a4.b(AnimationTag.a(a5));
            }
            final String a6 = a.a("removeTeamTags", (String)null);
            if (a6 != null) {
                a4.c(AnimationTag.a(a6));
            }
            b2 = true;
        }
        else {
            if (a.g != TriggerType.event_move) {
                if (a.g == TriggerType.event_unitRemove) {
                    final FastArrayList<BaseUnit> list = new FastArrayList();
                    for (final BaseUnit object : BaseUnit.bE) {
                        if (object instanceof OrderableUnit && a.a(object) && a.b(object)) {
                            list.add(object);
                        }
                    }
                    if (list.size() > 0) {
                        for (final BaseUnit baseUnit : list) {
                            baseUnit.removeFromGame();
                            if (baseUnit instanceof OrderableUnit && baseUnit.bI()) {
                                instance.pathfindingEngine.a((OrderableUnit)baseUnit);
                            }
                        }
                    }
                    b2 = true;
                }
                if (!b2) {
                    a.h("Trigger activated with no effect");
                }
                return;
            }
            final String b5 = a.b("target");
            if (b5 == null) {
                MissionEngine.i("Move trigger has no target id:" + a.a);
                return;
            }
            final PointF f2 = f.f(b5);
            if (f2 == null) {
                MissionEngine.i("Move trigger: Cannot find target for:" + a.a + " target:" + b5);
                return;
            }
            final PlayerTeam a7 = a.a();
            if (a7 == null) {
                MissionEngine.i("Team not set map trigger:" + a.a);
                return;
            }
            int n4 = 0;
            final Command commandForTeam = instance.commandController.createCommandForTeam(a7);
            for (final BaseUnit baseUnit2 : BaseUnit.bE) {
                if (baseUnit2.team == a7 && baseUnit2 instanceof OrderableUnit && a.a(baseUnit2) && a.b(baseUnit2)) {
                    commandForTeam.setTargetUnit((OrderableUnit)baseUnit2);
                    ++n4;
                }
            }
            commandForTeam.setMoveTarget(f2.x, f2.y);
            if (b) {
                f.b("firstActivation: move at:" + instance.gameTimeMillis + " for teamId:" + a7.teamId + " to targetId:" + b5 + " (#units:" + n4 + ")");
            }
            if (a.b("unload") != null) {
                for (final BaseUnit baseUnit3 : BaseUnit.bE) {
                    if (baseUnit3.team == a7 && baseUnit3 instanceof OrderableUnit && a.a(baseUnit3) && a.b(baseUnit3) && baseUnit3.canTransportUnits()) {
                        final OrderableUnit targetUnit = (OrderableUnit)baseUnit3;
                        final Command commandForTeam2 = instance.commandController.createCommandForTeam(a7);
                        commandForTeam2.isQueued = true;
                        commandForTeam2.setTargetUnit(targetUnit);
                        commandForTeam2.setActionId(targetUnit.getUnloadActionId());
                    }
                }
            }
            b2 = true;
        }
    }
}
