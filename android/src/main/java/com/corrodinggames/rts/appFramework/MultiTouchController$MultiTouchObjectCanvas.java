package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.eo */
/* JADX INFO: loaded from: classes.dex */
public interface MultiTouchController$MultiTouchObjectCanvas {
    Object getDraggableObjectAtPoint(CurrTouchPoint currTouchPoint);

    void getPositionAndScale(Object obj, MultiTouchController$PositionAndScale multiTouchController$PositionAndScale);

    void selectObject(Object obj, CurrTouchPoint currTouchPoint);

    boolean setPositionAndScale(Object obj, MultiTouchController$PositionAndScale multiTouchController$PositionAndScale, CurrTouchPoint currTouchPoint);
}
