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
    public ArrayList<BaseUnit> a = new ArrayList();
    public float b;
    public long c;
    public float d;
    public float e;
    public float f;
    public boolean g;
    public boolean h;

    public UnitGroupMarker(GameInterfaceRenderer gameInterfaceRenderer, boolean z) {
        this.i = gameInterfaceRenderer;
        this.g = z;
    }

    public void a() {
        BaseUnit baseUnit = null;
        for (BaseUnit baseUnit2 : this.a) {
            if (!baseUnit2.isDead && baseUnit2.unitTransportTarget == null && this.i.gameUI.selectUnit(baseUnit2) && baseUnit2.isVisibleToLocalPlayer()) {
                baseUnit = baseUnit2;
            }
        }
        if (this.c > GameEngine.getCurrentTimeMillis() - 700 && baseUnit != null) {
            this.i.gameEngine.centerViewpoint(baseUnit.posX, baseUnit.posY);
        }
        this.c = GameEngine.getCurrentTimeMillis();
    }

    public void b() {
        this.a.clear();
    }

    public void c() {
        for (GameObject gameObject : GameObject.fastGameObjectList) {
            if (gameObject instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) gameObject;
                if (orderableUnit.isSelected && !this.a.contains(orderableUnit)) {
                    this.a.add(orderableUnit);
                }
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        d();
        gameOutputStream.writeFloat(this.b);
        gameOutputStream.writeLong(this.c);
        gameOutputStream.writeInt(this.a.size());
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            gameOutputStream.writeUnitIdOrNullBaseUnit((BaseUnit) it.next());
        }
        gameOutputStream.writeByte(0);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        this.b = gameInputStream.readFloat();
        this.c = gameInputStream.readLong();
        this.a.clear();
        int i = gameInputStream.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            BaseUnit baseUnit = gameInputStream.readBaseUnit();
            if (baseUnit != null) {
                this.a.add(baseUnit);
            }
        }
        gameInputStream.readByte();
    }

    public void d() {
        if (this.a.size() == 0) {
            return;
        }
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            if (((BaseUnit) it.next()).isDead) {
                it.remove();
            }
        }
    }

    public void e() {
        if (this.a.size() == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = this.a.iterator();
        while (it.hasNext()) {
            BaseUnit baseUnitA = GameObject.a(((BaseUnit) it.next()).objectId, true);
            if (baseUnitA != null && !baseUnitA.isDead) {
                arrayList.add(baseUnitA);
            }
        }
        this.a = arrayList;
    }
}
