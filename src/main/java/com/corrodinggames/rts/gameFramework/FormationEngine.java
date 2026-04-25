package com.corrodinggames.rts.gameFramework;

import android.graphics.PointF;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.aa */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/aa.class */
public class FormationEngine extends Serializable {
    int a;
    PointF b = new PointF();

    public void a() {
        this.a = 1;
    }

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(0);
        gameOutputStream.writeInt(this.a);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        gameInputStream.readInt();
        this.a = gameInputStream.readInt();
    }

    public void a(float f) {
    }

    public FormationGroup b() {
        FormationGroup formationGroup = new FormationGroup(this);
        formationGroup.formationId = this.a;
        this.a++;
        return formationGroup;
    }

    public FormationGroup c() {
        FormationGroup formationGroup = new FormationGroup(this);
        formationGroup.formationId = -1;
        formationGroup.isActive = true;
        return formationGroup;
    }

    public void a(FastArrayList fastArrayList, OrderableUnit orderableUnit, FastArrayList fastArrayList2, float f, int i) {
        int i2 = 0;
        while (!fastArrayList2.isEmpty()) {
            OrderableUnit orderableUnit2 = null;
            float f2 = -1.0f;
            PointF pointF = null;
            int i3 = -1;
            Object[] objArrA = fastArrayList2.a();
            Object[] objArrA2 = fastArrayList.a();
            int size = fastArrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                OrderableUnit orderableUnit3 = (OrderableUnit) objArrA2[i4];
                if (orderableUnit3.transportedBy == orderableUnit && !orderableUnit3.aj) {
                    float f3 = -1.0f;
                    PointF pointF2 = null;
                    int i5 = -1;
                    for (int i6 = 0; i6 < fastArrayList2.size; i6++) {
                        PointF pointF3 = (PointF) objArrA[i6];
                        float fDistanceSq = Utility.distanceSq(orderableUnit3.posX, orderableUnit3.posY, orderableUnit.posX + pointF3.x, orderableUnit.posY + pointF3.y);
                        if (f3 == -1.0f || fDistanceSq < f3) {
                            f3 = fDistanceSq;
                            pointF2 = pointF3;
                            i5 = i6;
                        }
                    }
                    if (f3 > f2) {
                        orderableUnit2 = orderableUnit3;
                        f2 = f3;
                        pointF = pointF2;
                        i3 = i5;
                    }
                }
            }
            if (orderableUnit2 != null) {
                i2++;
                orderableUnit2.aj = true;
                orderableUnit2.transportOffsetX = pointF.x;
                orderableUnit2.transportOffsetY = pointF.y;
                orderableUnit2.am = f;
                orderableUnit2.ao = f2;
                orderableUnit2.ah = (short) (i + 1);
                fastArrayList2.remove(i3);
            } else {
                return;
            }
        }
    }

    public void a(FastArrayList fastArrayList, OrderableUnit orderableUnit) {
        while (true) {
            OrderableUnit orderableUnit2 = null;
            Iterator it = fastArrayList.iterator();
            while (it.hasNext()) {
                OrderableUnit orderableUnit3 = (OrderableUnit) it.next();
                if (orderableUnit3.transportedBy == orderableUnit && orderableUnit3.ao > 0.0f && (orderableUnit2 == null || orderableUnit3.ao > orderableUnit2.ao)) {
                    if (orderableUnit3.aj && orderableUnit3.ao > 100.0f) {
                        orderableUnit2 = orderableUnit3;
                    }
                }
            }
            if (orderableUnit2 != null) {
                orderableUnit2.aj = false;
                OrderableUnit orderableUnit4 = null;
                float f = 0.0f;
                OrderableUnit orderableUnit5 = orderableUnit2;
                int iSqrt = Utility.sqrt((int) orderableUnit5.ao);
                Iterator it2 = fastArrayList.iterator();
                while (it2.hasNext()) {
                    OrderableUnit orderableUnit6 = (OrderableUnit) it2.next();
                    if (orderableUnit6.transportedBy == orderableUnit && orderableUnit6.ao > 0.0f && orderableUnit6 != orderableUnit5) {
                        float fDistanceInt = ((0 + Utility.distanceInt(orderableUnit5.posX, orderableUnit5.posY, orderableUnit.posX + orderableUnit6.transportOffsetX, orderableUnit.posY + orderableUnit6.transportOffsetY)) + Utility.distanceInt(orderableUnit6.posX, orderableUnit6.posY, orderableUnit.posX + orderableUnit5.transportOffsetX, orderableUnit.posY + orderableUnit5.transportOffsetY)) - (Utility.sqrt((int) orderableUnit6.ao) + iSqrt);
                        if (fDistanceInt < f) {
                            f = fDistanceInt;
                            orderableUnit4 = orderableUnit6;
                        }
                    }
                }
                if (orderableUnit4 != null) {
                    float f2 = orderableUnit5.transportOffsetX;
                    float f3 = orderableUnit5.transportOffsetY;
                    orderableUnit5.transportOffsetX = orderableUnit4.transportOffsetX;
                    orderableUnit5.transportOffsetY = orderableUnit4.transportOffsetY;
                    orderableUnit5.ao = Utility.distanceSq(orderableUnit5.posX, orderableUnit5.posY, orderableUnit.posX + orderableUnit5.transportOffsetX, orderableUnit.posY + orderableUnit5.transportOffsetY);
                    orderableUnit4.transportOffsetX = f2;
                    orderableUnit4.transportOffsetY = f3;
                    orderableUnit4.ao = Utility.distanceSq(orderableUnit4.posX, orderableUnit4.posY, orderableUnit.posX + orderableUnit4.transportOffsetX, orderableUnit.posY + orderableUnit4.transportOffsetY);
                }
            } else {
                return;
            }
        }
    }

    public FastArrayList a(int i, float f, float f2) {
        int i2;
        int i3 = 1;
        int i4 = 0;
        int i5 = 6 / 2;
        float f3 = 2.0f + (f * 2.0f * 1.5f);
        FastArrayList fastArrayList = new FastArrayList();
        int i6 = i;
        if (i6 % 2 != 0) {
            i6++;
        }
        float fFastCos = Utility.fastCos(f2);
        float fFastSin = Utility.fastSin(f2);
        for (int i7 = 0; i7 < i6; i7++) {
            if (i3 % 2 == 0) {
                i2 = i5 + (i3 / 2);
            } else {
                i2 = i5 - ((i3 + 1) / 2);
            }
            float f4 = (i2 - i5) * f3;
            float f5 = (-i4) * f3;
            fastArrayList.add(new PointF((f5 * fFastCos) - (f4 * fFastSin), (f4 * fFastCos) + (f5 * fFastSin)));
            i3++;
            if (i3 > 6) {
                i3 = 0;
                i4++;
            }
        }
        return fastArrayList;
    }
}
