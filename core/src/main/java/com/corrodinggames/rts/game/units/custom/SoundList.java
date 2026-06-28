package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bl */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bl.class */
public class SoundList {

    /* JADX INFO: renamed from: a */
    ArrayList soundList = new ArrayList();

    /* JADX INFO: renamed from: b */
    int playIndex;

    public void a(float f) {
        Iterator it = this.soundList.iterator();
        while (it.hasNext()) {
            ((SoundEntry) it.next()).volume = f;
        }
    }

    public boolean a() {
        return a(0.0f, 0.0f, 1.0f, true);
    }

    public boolean a(float f, float f2, float f3) {
        return a(f, f2, f3, false);
    }

    public boolean a(float f, float f2, float f3, boolean z) {
        if (this.soundList.size() == 0) {
            return false;
        }
        if (this.playIndex >= this.soundList.size()) {
            this.playIndex = 0;
        }
        SoundEntry soundEntry = (SoundEntry) this.soundList.get(this.playIndex);
        GameEngine gameEngine = GameEngine.getInstance();
        if (z) {
            gameEngine.soundEngine.playGameSound(soundEntry.sound, soundEntry.volume);
        } else {
            gameEngine.soundEngine.playSound(soundEntry.sound, soundEntry.volume, f, f2);
        }
        this.playIndex++;
        return true;
    }

    public static SoundList a(CustomUnitConfig customUnitConfig, String str) {
        return a(customUnitConfig, str, (SoundList) null);
    }

    public static SoundList a(CustomUnitConfig customUnitConfig, String str, SoundList soundList) {
        if ((str == null || str.equals(VariableScope.nullOrMissingString)) && soundList != null) {
            return soundList;
        }
        return new SoundList(customUnitConfig, str);
    }

    public SoundList() {
    }

    public SoundList(CustomUnitConfig customUnitConfig, String str) {
        if (str == null || str.equals(VariableScope.nullOrMissingString) || str.equalsIgnoreCase("NONE")) {
            return;
        }
        for (String str2 : str.split(",")) {
            SoundEntry soundEntry = new SoundEntry(this);
            String strTrim = str2.trim();
            String str3 = VariableScope.nullOrMissingString;
            if (strTrim.startsWith("ROOT:")) {
                strTrim = strTrim.substring("ROOT:".length());
                str3 = str3 + "ROOT:";
            }
            if (strTrim.startsWith("SHARED:")) {
                strTrim = strTrim.substring("SHARED:".length());
                str3 = str3 + "SHARED:";
            }
            String[] strArrSplit = strTrim.split(":");
            String strTrim2 = null;
            String strTrim3 = strArrSplit[0].trim();
            if (strArrSplit.length != 1) {
                if (strArrSplit.length == 2) {
                    strTrim2 = strArrSplit[1].trim();
                } else {
                    throw new RuntimeException("Unknown sound format:" + strTrim);
                }
            }
            if (strTrim2 != null) {
                try {
                    soundEntry.volume = Float.parseFloat(strTrim2);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Failed to parse volume float: '" + strTrim2 + "' of sound: '" + strTrim + "'");
                }
            }
            soundEntry.sound = CustomUnitConfigParser.describeModsForUnitList(customUnitConfig.resourceLoadPath, str3 + strTrim3, customUnitConfig);
            if (soundEntry.sound != null) {
                this.soundList.add(soundEntry);
            }
        }
    }
}
