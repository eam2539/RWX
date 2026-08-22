package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Serializable;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.am */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/am.class */
public class UnitGroupMarker extends Serializable {
    private final GameInterfaceRenderer i;

    /* JADX INFO: renamed from: a */
    public ArrayList<BaseUnit> units = new ArrayList();

    /* JADX INFO: renamed from: b */
    public float radius;

    /* JADX INFO: renamed from: c */
    public long lastClickTime;

    public float d;
    public float e;
    public float f;

    /* JADX INFO: renamed from: g */
    public boolean isHotkey;

    public boolean h;

    public UnitGroupMarker(GameInterfaceRenderer gameInterfaceRenderer, boolean z) {
        this.i = gameInterfaceRenderer;
        this.isHotkey = z;
    }

    public void a() {
        BaseUnit baseUnit = null;
        for (BaseUnit baseUnit2 : this.units) {
            if (!baseUnit2.isDead && baseUnit2.transportContainer == null && this.i.gameUI.selectUnit(baseUnit2) && baseUnit2.isVisibleToLocalPlayer()) {
                baseUnit = baseUnit2;
            }
        }
        if (this.lastClickTime > GameEngine.getCurrentTimeMillis() - 700 && baseUnit != null) {
            this.i.gameEngine.centerViewpoint(baseUnit.posX, baseUnit.posY);
        }
        this.lastClickTime = GameEngine.getCurrentTimeMillis();
    }

    public void b() {
        this.units.clear();
    }

    public void c() {
        for (GameObject gameObject : GameObject.fastGameObjectList) {
            if (gameObject instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) gameObject;
                if (orderableUnit.isSelected && !this.units.contains(orderableUnit)) {
                    this.units.add(orderableUnit);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        d();
        gameOutputStream.writeFloat(this.radius);
        gameOutputStream.writeLong(this.lastClickTime);
        gameOutputStream.writeInt(this.units.size());
        Iterator it = this.units.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeUnitIdOrNullBaseUnit((BaseUnit) it.next());
        }
        gameOutputStream.writeByte(0);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        this.radius = gameInputStream.readFloat();
        this.lastClickTime = gameInputStream.readLong();
        this.units.clear();
        int i = gameInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            BaseUnit baseUnit = gameInputStream.readBaseUnit();
            if (baseUnit != null) {
                this.units.add(baseUnit);
            }
        }
        gameInputStream.readByte();
    }

    public void d() {
        if (this.units.size() == 0) {
            return;
        }
        Iterator it = this.units.iterator();
        while (it.hasNext()) {
            if (((BaseUnit) it.next()).isDead) {
                it.remove();
            }
        }
    }

    public void e() {
        if (this.units.size() == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = this.units.iterator();
        while (it.hasNext()) {
            BaseUnit baseUnitA = GameObject.a(((BaseUnit) it.next()).objectId, true);
            if (baseUnitA != null && !baseUnitA.isDead) {
                arrayList.add(baseUnitA);
            }
        }
        this.units = arrayList;
    }
}
