package com.corrodinggames.rts.game.units.custom.tracking;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.ConnectionStatus;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.c.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/c/c.class */
public class AnimationTrackingManager {
    FastArrayList d = new FastArrayList();
    public static TrackingSpatialCallback f;
    static final Rect a = new Rect();
    static final RectF b = new RectF();
    static final Paint c = new Paint();
    static Paint e = new Paint();

    static {
        e.a(255, 0, 0, 200);
        f = new TrackingSpatialCallback();
    }

    public void a(CustomUnitConfig customUnitConfig) {
        Object[] objArrA = this.d.a();
        for (int i = this.d.size - 1; i >= 0; i--) {
            TrackingGroup trackingGroup = (TrackingGroup) objArrA[i];
            AnimationTrackingEntry animationTrackingEntryA = customUnitConfig.a(trackingGroup.a.g);
            if (animationTrackingEntryA != null) {
                trackingGroup.a = animationTrackingEntryA;
                while (trackingGroup.b.size() > trackingGroup.a.d) {
                    trackingGroup.b.remove(trackingGroup.b.size() - 1);
                }
            } else {
                this.d.remove(i);
            }
        }
    }

    public TrackingGroup a(AnimationTrackingEntry animationTrackingEntry, boolean z) {
        int i = this.d.size;
        Object[] objArrA = this.d.a();
        for (int i2 = 0; i2 < i; i2++) {
            TrackingGroup trackingGroup = (TrackingGroup) objArrA[i2];
            if (trackingGroup.a == animationTrackingEntry) {
                return trackingGroup;
            }
        }
        if (z) {
            TrackingGroup trackingGroup2 = new TrackingGroup(animationTrackingEntry);
            this.d.add(trackingGroup2);
            return trackingGroup2;
        }
        return null;
    }

    public int a(AnimationTrackingEntry animationTrackingEntry) {
        TrackingGroup trackingGroupA = a(animationTrackingEntry, false);
        if (trackingGroupA == null) {
            return 0;
        }
        return trackingGroupA.b.size;
    }

    public void a(float f2, BaseUnit baseUnit) {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = this.d.size;
        if (i == 0) {
            return;
        }
        Object[] objArrA = this.d.a();
        for (int i2 = 0; i2 < i; i2++) {
            TrackingGroup trackingGroup = (TrackingGroup) objArrA[i2];
            AnimationTrackingEntry animationTrackingEntry = trackingGroup.a;
            int i3 = trackingGroup.b.size;
            Object[] objArrA2 = trackingGroup.b.a();
            for (int i4 = i3 - 1; i4 >= 0; i4--) {
                TrackingData trackingData = (TrackingData) objArrA2[i4];
                if (trackingData.c) {
                    BaseUnit baseUnit2 = trackingData.a;
                    if (animationTrackingEntry.e != null) {
                        Texture texture = animationTrackingEntry.e;
                        float f3 = baseUnit.posX - GameEngine.getInstance().viewpointXSnapped;
                        float f4 = ((baseUnit.posY - GameEngine.getInstance().viewpointYSnapped) - baseUnit.posZ) - 10.0f;
                        float fSqrt = texture.u;
                        float angleBetweenPoints = Utility.getAngleBetweenPoints(baseUnit.posX, baseUnit.posY - baseUnit.posZ, baseUnit2.posX, baseUnit2.posY - baseUnit2.posZ);
                        float fDistanceSq = Utility.distanceSq(baseUnit.posX, baseUnit.posY - baseUnit.posZ, baseUnit2.posX, baseUnit2.posY - baseUnit2.posZ);
                        if (fDistanceSq < (texture.q - 2) * (texture.q - 2)) {
                            fSqrt = Utility.sqrt((int) fDistanceSq);
                        }
                        gameEngine.graphicsEngine2.k();
                        gameEngine.graphicsEngine2.a(angleBetweenPoints + 90.0f, f3, f4);
                        a.a(0, (int) (texture.q - fSqrt), texture.p, texture.q);
                        b.a(f3 - texture.r, f4 - fSqrt, f3 + texture.r, f4);
                        Paint paint = GamePaint.r;
                        if (trackingData.d != 0.0f) {
                            paint = c;
                            int iAbs = (int) Math.abs(trackingData.d * 5.0f);
                            if (iAbs > 250) {
                                iAbs = 250;
                            }
                            paint.a(255, 255, 255 - iAbs, 255 - iAbs);
                        }
                        gameEngine.graphicsEngine2.a(texture, a, b, paint);
                        gameEngine.graphicsEngine2.l();
                    }
                    if (animationTrackingEntry.f != null) {
                        gameEngine.graphicsEngine2.a(baseUnit.posX - gameEngine.viewpointXSnapped, (baseUnit.posY - gameEngine.viewpointYSnapped) - baseUnit.posZ, baseUnit2.posX - gameEngine.viewpointXSnapped, (baseUnit2.posY - gameEngine.viewpointYSnapped) - baseUnit2.posZ, animationTrackingEntry.f);
                    }
                }
            }
        }
    }

    public void a(GameOutputStream gameOutputStream) throws IOException {
        if (this.d.size == 0) {
            gameOutputStream.writeByte(-1);
            return;
        }
        gameOutputStream.writeByte(0);
        short size = (short) this.d.size();
        gameOutputStream.writeShort(size);
        Object[] objArrA = this.d.a();
        for (int i = 0; i < size; i++) {
            TrackingGroup trackingGroup = (TrackingGroup) objArrA[i];
            gameOutputStream.writeAnimationTag(trackingGroup.a.g);
            gameOutputStream.writeShort((short) trackingGroup.b.size());
            for (TrackingData trackingData : trackingGroup.b) {
                gameOutputStream.writeUnitIdOrNullBaseUnit(trackingData.a);
                gameOutputStream.writeBoolean(trackingData.b);
                gameOutputStream.writeBoolean(trackingData.c);
            }
        }
    }

    public void a(BaseUnit baseUnit, GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.readByte() == -1) {
            return;
        }
        short shortValue = gameInputStream.readShortValue();
        this.d.clear();
        for (int i = 0; i < shortValue; i++) {
            AnimationTag animationTag = gameInputStream.readAnimationTag();
            AnimationTrackingEntry animationTrackingEntryA = null;
            if (baseUnit instanceof CustomUnit) {
                animationTrackingEntryA = ((CustomUnit) baseUnit).unitConfig.a(animationTag);
            }
            TrackingGroup trackingGroup = null;
            if (animationTrackingEntryA != null) {
                trackingGroup = new TrackingGroup(animationTrackingEntryA);
                this.d.add(trackingGroup);
            }
            short shortValue2 = gameInputStream.readShortValue();
            for (int i2 = 0; i2 < shortValue2; i2++) {
                TrackingData trackingData = new TrackingData();
                trackingData.a = gameInputStream.startBlockNamed(ConnectionStatus.expected);
                trackingData.b = gameInputStream.readBoolean();
                trackingData.c = gameInputStream.readBoolean();
                if (trackingData.a != null && trackingGroup != null) {
                    trackingGroup.b.add(trackingData);
                }
            }
        }
    }
}
