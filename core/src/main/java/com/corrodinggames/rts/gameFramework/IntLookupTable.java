package com.corrodinggames.rts.gameFramework;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bi */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bi.class */
public class IntLookupTable<T> extends ArrayList<T> {
    public int a(int i) {
        if (isEmpty()) {
            return 0;
        }
        int i2 = ((Point2i) get(0)).y;
        Iterator it = iterator();
        while (it.hasNext()) {
            Point2i point2i = (Point2i) it.next();
            if (point2i.x > i) {
                return i2;
            }
            i2 = point2i.y;
        }
        return i2;
    }
}
