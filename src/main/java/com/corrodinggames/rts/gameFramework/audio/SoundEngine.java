package com.corrodinggames.rts.gameFramework.audio;

import android.content.Context;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/e.class */
public class SoundEngine {

    /* JADX INFO: renamed from: a */
    ArrayList playingSounds = new ArrayList();

    /* JADX INFO: renamed from: b */
    public boolean soundDisabled;

    /* JADX INFO: renamed from: c */
    public static SoundFactory soundFactory = new AndroidSoundFactory();

    /* JADX INFO: renamed from: d */
    public static Sound attack2Sound;

    /* JADX INFO: renamed from: e */
    public static Sound attackSound;

    /* JADX INFO: renamed from: f */
    public static Sound moveSound;

    /* JADX INFO: renamed from: g */
    public static Sound clickSound;

    /* JADX INFO: renamed from: h */
    public static Sound clickAddSound;

    /* JADX INFO: renamed from: i */
    public static Sound clickRemoveSound;

    /* JADX INFO: renamed from: j */
    public static Sound warningSound;

    /* JADX INFO: renamed from: k */
    public static Sound messageSound;

    /* JADX INFO: renamed from: l */
    public static Sound interfaceErrorSound;

    /* JADX INFO: renamed from: m */
    public static Sound missileFireSound;

    /* JADX INFO: renamed from: n */
    public static Sound missileHitSound;

    /* JADX INFO: renamed from: o */
    public static Sound unitExplodeSound;

    /* JADX INFO: renamed from: p */
    public static Sound buildingExplodeSound;

    /* JADX INFO: renamed from: q */
    public static Sound tankFiringSound;

    /* JADX INFO: renamed from: r */
    public static Sound cannonFiringSound;

    /* JADX INFO: renamed from: s */
    public static Sound gunFireSound;

    /* JADX INFO: renamed from: t */
    public static Sound gunFire3Sound;

    /* JADX INFO: renamed from: u */
    public static Sound gunFire4Sound;

    /* JADX INFO: renamed from: v */
    public static Sound largeGunFire1Sound;

    /* JADX INFO: renamed from: w */
    public static Sound largeGunFire2Sound;

    /* JADX INFO: renamed from: x */
    public static Sound lightingBurstSound;

    /* JADX INFO: renamed from: y */
    public static Sound plasmaFireSound;

    /* JADX INFO: renamed from: z */
    public static Sound plasmaFire2Sound;

    /* JADX INFO: renamed from: A */
    public static Sound bugDieSound;

    /* JADX INFO: renamed from: B */
    public static Sound bugAttackSound;

    /* JADX INFO: renamed from: C */
    public static Sound nukeExplodeSound;

    /* JADX INFO: renamed from: D */
    public static Sound nukeLaunchSound;

    /* JADX INFO: renamed from: E */
    public static Sound laserDeflectSound;

    /* JADX INFO: renamed from: F */
    public static Sound laserDeflect2Sound;

    /* JADX INFO: renamed from: a */
    public boolean trackSound(Sound sound, float f) {
        if (this.playingSounds.contains(sound)) {
            return false;
        }
        this.playingSounds.add(sound);
        return true;
    }

    /* JADX INFO: renamed from: a */
    public boolean isSoundEnabled() {
        GameEngine gameEngine = GameEngine.getInstance();
        return isVolumeEnabled(gameEngine.settingsEngine.masterVolume * gameEngine.settingsEngine.gameVolume);
    }

    /* JADX INFO: renamed from: a */
    public boolean isVolumeEnabled(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (f < 0.01f || this.soundDisabled) {
            return false;
        }
        return gameEngine.settingsEngine.enableSounds;
    }

    /* JADX INFO: renamed from: b */
    public static void noop() {
    }

    /* JADX INFO: renamed from: a */
    public void loadSounds(Context context) {
        soundFactory.a(context);
        attackSound = soundFactory.a(R.raw.attack);
        attackSound.d = 0.2f;
        attack2Sound = soundFactory.a(R.raw.attack2);
        moveSound = soundFactory.a(R.raw.move);
        clickSound = soundFactory.a(R.raw.click);
        clickAddSound = soundFactory.a(R.raw.click_add);
        clickRemoveSound = soundFactory.a(R.raw.click_remove);
        warningSound = soundFactory.a(R.raw.warning);
        messageSound = soundFactory.a(R.raw.message);
        missileFireSound = soundFactory.a(R.raw.missile_fire);
        missileHitSound = soundFactory.a(R.raw.missile_hit);
        unitExplodeSound = soundFactory.a(R.raw.unit_explode);
        buildingExplodeSound = soundFactory.a(R.raw.buiding_explode);
        tankFiringSound = soundFactory.a(R.raw.tank_firing);
        cannonFiringSound = soundFactory.a(R.raw.cannon_firing);
        gunFireSound = soundFactory.a(R.raw.gun_fire);
        lightingBurstSound = soundFactory.a(R.raw.lighting_burst);
        plasmaFireSound = soundFactory.a(R.raw.plasma_fire);
        plasmaFire2Sound = soundFactory.a(R.raw.plasma_fire2);
        gunFire3Sound = soundFactory.a(R.raw.firing3);
        gunFire4Sound = soundFactory.a(R.raw.firing4);
        largeGunFire1Sound = soundFactory.a(R.raw.large_gun_fire1);
        largeGunFire2Sound = soundFactory.a(R.raw.large_gun_fire2);
        bugDieSound = soundFactory.a(R.raw.bug_die);
        bugAttackSound = soundFactory.a(R.raw.bug_attack);
        interfaceErrorSound = soundFactory.a(R.raw.interface_error);
        nukeExplodeSound = soundFactory.a(R.raw.nuke_explode);
        nukeLaunchSound = soundFactory.a(R.raw.nuke_launch);
        laserDeflectSound = soundFactory.a(R.raw.laser_deflect);
        laserDeflect2Sound = soundFactory.a(R.raw.laser_deflect2);
        soundFactory.a();
    }

    /* JADX INFO: renamed from: a */
    public Sound getSound(String str) {
        Sound sound = (Sound) soundFactory.h.get(str);
        if (sound == null) {
            throw new RuntimeException("Could not find sound:" + str);
        }
        return sound;
    }

    /* JADX INFO: renamed from: b */
    public void playInterfaceSound(Sound sound, float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        float f2 = f * gameEngine.settingsEngine.masterVolume * gameEngine.settingsEngine.interfaceVolume * sound.d;
        if (!isVolumeEnabled(f2) || f2 < 0.01d || !trackSound(sound, f2)) {
            return;
        }
        if (gameEngine.isStopped) {
            f2 /= 20.0f;
        }
        sound.a(f2, f2, 1, 0, 1.0f);
    }

    /* JADX INFO: renamed from: c */
    public void playGameSound(Sound sound, float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        float f2 = f * gameEngine.settingsEngine.masterVolume * gameEngine.settingsEngine.gameVolume * sound.d;
        if (isVolumeEnabled(f2)) {
            if (gameEngine.isStopped) {
                f2 /= 20.0f;
            }
            if (!trackSound(sound, f2)) {
                return;
            }
            sound.a(f2, f2, 1, 0, 1.0f);
        }
    }

    /* JADX INFO: renamed from: a */
    public void playSound(Sound sound, float f, float f2, float f3) {
        playSoundAt(sound, f, 1.0f, f2, f3);
    }

    /* JADX INFO: renamed from: a */
    public void playSoundAt(Sound sound, float f, float f2, float f3, float f4) {
        if (isSoundEnabled()) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine.isStopped) {
                f /= 20.0f;
            }
            if (gameEngine.replayEngine.j() && gameEngine.gameSpeed > 1.5d) {
                f /= gameEngine.gameSpeed;
            }
            float fDistanceSq = Utility.distanceSq((int) (gameEngine.viewpointXSnapped + gameEngine.halfViewpointWidth), (int) (gameEngine.viewpointYSnapped + gameEngine.halfViewpointHeight), f3, f4);
            float f5 = gameEngine.halfViewpointWidth * 1.72f;
            if (gameEngine.zoom < 0.5d) {
                f = f * 4.0f * gameEngine.zoom * gameEngine.zoom;
            }
            if (f <= 1.0f && !sound.f && fDistanceSq > f5 * f5) {
                return;
            }
            float fSqrt = (float) Math.sqrt(fDistanceSq);
            float f6 = 1.0f;
            if (fSqrt > gameEngine.halfViewpointWidth) {
                f6 = 1.0f - ((fSqrt - gameEngine.halfViewpointWidth) / gameEngine.halfViewpointWidth);
            }
            float f7 = f6 * f;
            if (f7 <= 0.05d && !sound.f) {
                return;
            }
            if (f7 > 1.0f) {
                f7 = 1.0f;
            }
            float f8 = f7 * gameEngine.settingsEngine.masterVolume * gameEngine.settingsEngine.gameVolume * sound.d;
            if (!trackSound(sound, f8)) {
                return;
            }
            sound.a(f8, f8, 1, 0, f2);
        }
    }

    /* JADX INFO: renamed from: a */
    public Sound loadSound(String str, AssetInputStream assetInputStream, boolean z) {
        try {
            return soundFactory.a(str, assetInputStream, z);
        } catch (OutOfMemoryError e) {
            GameEngine.reportOOM(AssetType.gameSound, e);
            return NullSound.b();
        }
    }

    /* JADX INFO: renamed from: b */
    public Sound getNullSound(String str) {
        return NullSound.a(str);
    }

    /* JADX INFO: renamed from: b */
    public void clearPlayingSounds(float f) {
        this.playingSounds.clear();
    }
}
