package com.corrodinggames.rts.gameFramework.mission;

import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.geometry.PointF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.n.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/n/g.class */
public class MissionWave {

    /* JADX INFO: renamed from: a */
    public ArrayList waveGroups = new ArrayList();

    /* JADX INFO: renamed from: b */
    public boolean lockSpawn;

    /* JADX INFO: renamed from: c */
    public boolean unlockSpawn;

    /* JADX INFO: renamed from: d */
    public float timeSeconds;

    public int e;

    /* JADX INFO: renamed from: f */
    public String messageText;

    /* JADX INFO: renamed from: g */
    public boolean noTimer;

    /* JADX INFO: renamed from: h */
    public boolean hasMessage;
    final /* synthetic */ MissionEngine i;

    public MissionWave(MissionEngine missionEngine) {
        this.i = missionEngine;
    }

    public boolean waveGroups(String str) throws MapLoadException {
        String strGroup;
        String str2;
        String strTrim = str.trim();
        GameEngine.log("Got:" + strTrim);
        if (strTrim.length() == 0) {
            return false;
        }
        GameEngine.log("..");
        String strGroup2 = null;
        String strGroup3 = null;
        String strGroup4 = null;
        if (strTrim.startsWith("+")) {
            Matcher matcher = Pattern.compile("\\+([^ ]*)([^\\[-]*)(\\[(.*?)\\])? *(-.*)?").matcher(strTrim);
            if (matcher.matches()) {
                strGroup2 = matcher.group(1);
                strGroup3 = matcher.group(2);
                strGroup = matcher.group(4);
                strGroup4 = matcher.group(5);
                GameEngine.log("Got o:" + strGroup + " d:" + strGroup2 + " dn:" + strGroup3 + " units:" + strGroup4);
            } else {
                throw new MapLoadException("Unknown wave line in map: " + strTrim);
            }
        } else if (strTrim.startsWith("!")) {
            Matcher matcher2 = Pattern.compile("\\!(.*)").matcher(strTrim);
            if (matcher2.matches()) {
                strGroup = matcher2.group(1);
            } else {
                throw new MapLoadException("Unknown wave line in map: " + strTrim);
            }
        } else {
            throw new MapLoadException("Unknown wave format: " + strTrim);
        }
        if (strGroup2 != null) {
            String[] strArrSplit = strGroup2.trim().split(":");
            String str3 = "0";
            if (strArrSplit.length == 1) {
                str2 = strArrSplit[0];
            } else if (strArrSplit.length == 2) {
                str3 = strArrSplit[0];
                str2 = strArrSplit[1];
            } else {
                throw new MapLoadException("Unknown time format in wave: " + strTrim);
            }
            try {
                this.timeSeconds = Integer.parseInt(str2) + (Integer.parseInt(str3) * 60);
            } catch (NumberFormatException e) {
                throw new MapLoadException("Failed to parse time on: " + strTrim, e);
            }
        }
        if (strGroup3 != null) {
            this.messageText = strGroup3.trim();
            this.hasMessage = true;
        }
        if (strGroup != null) {
            for (String str4 : strGroup.split(",")) {
                String[] strArrSplit2 = str4.split(":");
                String strTrim2 = strArrSplit2[0].trim();
                if (strArrSplit2.length > 1) {
                    strArrSplit2[1].trim();
                }
                if ("lockSpawn".equalsIgnoreCase(strTrim2)) {
                    this.lockSpawn = true;
                } else if ("unlockSpawn".equalsIgnoreCase(strTrim2)) {
                    this.unlockSpawn = true;
                } else if ("noTimer".equalsIgnoreCase(strTrim2)) {
                    this.noTimer = true;
                } else if (!"paused".equalsIgnoreCase(strTrim2) && !"win".equalsIgnoreCase(strTrim2) && !VariableScope.nullOrMissingString.equalsIgnoreCase(strTrim2)) {
                    throw new MapLoadException("Unknown wave option '" + strTrim2 + "' in: " + strTrim);
                }
            }
        }
        if (strGroup4 != null) {
            String strTrim3 = strGroup4.trim();
            if (strTrim3.startsWith("-")) {
                strTrim3 = strTrim3.substring(1);
            }
            for (String str5 : strTrim3.split(",")) {
                String strTrim4 = str5.trim();
                if (!strTrim4.contains(" ")) {
                    throw new MapLoadException("Unknown wave format '" + strTrim4 + "' in: " + strTrim);
                }
                int iIndexOf = strTrim4.indexOf(" ");
                String strTrim5 = strTrim4.substring(0, iIndexOf).trim();
                String strTrim6 = strTrim4.substring(iIndexOf + 1).trim();
                try {
                    int i = Integer.parseInt(strTrim5);
                    UnitType unitTypeByName = UnitTypeEnum.getUnitTypeByName(strTrim6);
                    if (unitTypeByName == null) {
                        throw new MapLoadException("Could not find unit '" + strTrim6 + "' in: " + strTrim);
                    }
                    WaveUnitGroup waveunitsVar = new WaveUnitGroup(this.i);
                    waveunitsVar.b(unitTypeByName, i);
                    this.waveGroups.add(waveunitsVar);
                } catch (NumberFormatException e2) {
                    throw new MapLoadException("Expected starting number in wave format '" + strTrim4 + "' in: " + strTrim);
                }
            }
            return true;
        }
        return true;
    }

    public void a() {
        GameEngine.log("Activating wave");
        if (!this.i.R) {
            this.i.e();
        }
        PointF pointF = this.i.P;
        Iterator it = this.waveGroups.iterator();
        while (it.hasNext()) {
            ((WaveUnitGroup) it.next()).a(pointF.x, pointF.y);
        }
        if (!this.i.Q) {
            this.i.e();
        }
        if (this.lockSpawn) {
            this.i.Q = true;
        }
        if (this.unlockSpawn) {
            this.i.Q = false;
        }
    }
}
