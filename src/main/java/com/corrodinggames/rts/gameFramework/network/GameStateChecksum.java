package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitCommand;
import com.corrodinggames.rts.game.units.buildings.CommandCenter;
import com.corrodinggames.rts.gameFramework.GameObject;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.ak */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/ak.class */
public class GameStateChecksum {

    /* JADX INFO: renamed from: a */
    public long totalChecksum;

    /* JADX INFO: renamed from: b */
    public ArrayList<ChecksumField> fields = new ArrayList();

    /* JADX INFO: renamed from: c */
    public ChecksumField unitPositionChecksum = new ChecksumField(this, "Unit Pos");

    /* JADX INFO: renamed from: d */
    public ChecksumField unitDirectionChecksum = new ChecksumField(this, "Unit Dir", false);

    /* JADX INFO: renamed from: e */
    public ChecksumField unitHealthChecksum = new ChecksumField(this, "Unit Hp");

    /* JADX INFO: renamed from: f */
    public ChecksumField unitIdChecksum = new ChecksumField(this, "Unit Id");

    /* JADX INFO: renamed from: g */
    public ChecksumField waypointChecksum = new ChecksumField(this, "Waypoints");

    /* JADX INFO: renamed from: h */
    public ChecksumField waypointPositionChecksum = new ChecksumField(this, "Waypoints Pos");

    /* JADX INFO: renamed from: i */
    public ChecksumField teamCreditsChecksum = new ChecksumField(this, "Team Credits");

    /* JADX INFO: renamed from: j */
    public ChecksumField unitPathsChecksum = new ChecksumField(this, "UnitPaths");

    /* JADX INFO: renamed from: k */
    public ChecksumField unitCountChecksum = new ChecksumField(this, "Unit Count");

    /* JADX INFO: renamed from: l */
    public ChecksumField teamInfoChecksum = new ChecksumField(this, "Team Info", false);

    /* JADX INFO: renamed from: m */
    public ChecksumField team1CreditsChecksum = new ChecksumField(this, "Team 1 Credits", false);

    /* JADX INFO: renamed from: n */
    public ChecksumField team2CreditsChecksum = new ChecksumField(this, "Team 2 Credits", false);

    /* JADX INFO: renamed from: o */
    public ChecksumField team3CreditsChecksum = new ChecksumField(this, "Team 3 Credits", false);

    /* JADX INFO: renamed from: p */
    public ChecksumField commandCenterAnimationTimerChecksum = new ChecksumField(this, "Command center2", false);

    /* JADX INFO: renamed from: q */
    public ChecksumField commandCenterAnimationCounterChecksum = new ChecksumField(this, "Command center3", false);

    /* JADX INFO: renamed from: a */
    public void resetFields() {
        Iterator it = this.fields.iterator();
        while (it.hasNext()) {
            ((ChecksumField) it.next()).value = 0L;
        }
    }

    /* JADX INFO: renamed from: b */
    public void computeChecksums() {
        this.totalChecksum = 0L;
        resetFields();
        for (GameObject gameObject : GameObject.fastGameObjectList) {
            if (gameObject instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) gameObject;
                this.totalChecksum = (long) (this.totalChecksum + (orderableUnit.posX * 1000.0f));
                this.totalChecksum = (long) (this.totalChecksum + (orderableUnit.posY * 1000.0f));
                this.totalChecksum = (long) (this.totalChecksum + (orderableUnit.currentHealth * 1.0f));
                this.totalChecksum += orderableUnit.objectId;
                this.unitPositionChecksum.value += (long) Float.floatToRawIntBits(orderableUnit.posX);
                this.unitPositionChecksum.value += (long) Float.floatToRawIntBits(orderableUnit.posY);
                this.unitDirectionChecksum.value += (long) Float.floatToRawIntBits(orderableUnit.rotationSpeed);
                this.unitHealthChecksum.value = (long) (this.unitHealthChecksum.value + orderableUnit.currentHealth);
                this.unitIdChecksum.value += orderableUnit.objectId;
                if (gameObject instanceof CommandCenter) {
                    CommandCenter commandCenter = (CommandCenter) orderableUnit;
                    this.commandCenterAnimationTimerChecksum.value = (long) (this.commandCenterAnimationTimerChecksum.value + (commandCenter.animationTimer1 * 2.0f));
                    this.commandCenterAnimationCounterChecksum.value += (long) commandCenter.animationCounter;
                }
                UnitCommand currentWaypoint = orderableUnit.getCurrentWaypoint();
                if (currentWaypoint != null) {
                    this.waypointChecksum.value += currentWaypoint.getCommandTypeOrdinal();
                    this.waypointPositionChecksum.value = (long) (waypointPositionChecksum.value + (currentWaypoint.getTargetX() * 1000.0f));
                }
                this.unitPathsChecksum.value += orderableUnit.getPathChecksum();
            }
        }
        for (int i = 0; i < PlayerTeam.TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeamK = PlayerTeam.k(i);
            if (playerTeamK != null) {
                this.teamCreditsChecksum.value += (long) ((int) playerTeamK.credits);
                if (i == 0) {
                    this.team1CreditsChecksum.value += (long) ((int) playerTeamK.credits);
                }
                if (i == 1) {
                    this.team2CreditsChecksum.value += (long) ((int) playerTeamK.credits);
                }
                if (i == 2) {
                    this.team3CreditsChecksum.value += (long) ((int) playerTeamK.credits);
                }
                this.unitCountChecksum.value += (long) playerTeamK.getTeamUnitCountInt();
                this.teamInfoChecksum.value += (long) (i + (playerTeamK.teamPingTime * 100) + (playerTeamK.teamColorId * 1000) + ((playerTeamK.isTeamSpectator ? i : 0) * 10000));
            }
        }
    }
}
