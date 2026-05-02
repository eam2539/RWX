package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.m */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/m.class */
public class MultiTouchPointerState {

    /* JADX INFO: renamed from: a */
    private int numPointers;

    /* JADX INFO: renamed from: b */
    private float[] x = new float[10];

    /* JADX INFO: renamed from: c */
    private float[] y = new float[10];

    /* JADX INFO: renamed from: d */
    private float[] pressure = new float[10];

    /* JADX INFO: renamed from: e */
    private int[] pointerIds = new int[10];

    /* JADX INFO: renamed from: f */
    private float startX;

    /* JADX INFO: renamed from: g */
    private float startY;

    /* JADX INFO: renamed from: h */
    private float startPressure;

    /* JADX INFO: renamed from: i */
    private float distX;

    /* JADX INFO: renamed from: j */
    private float distY;

    /* JADX INFO: renamed from: k */
    private boolean isDown;

    /* JADX INFO: renamed from: l */
    private boolean isMultiTouch;

    /* JADX INFO: renamed from: m */
    private boolean wasDown;

    /* JADX INFO: renamed from: n */
    private int lastNumPointers;

    /* JADX INFO: renamed from: o */
    private boolean isDragging;

    /* JADX INFO: renamed from: p */
    private boolean isPinching;

    /* JADX INFO: renamed from: q */
    private boolean isRotating;

    /* JADX INFO: renamed from: r */
    private int historyCount;

    public MultiTouchPointerState() {
        for (int i = 0; i < this.x.length; i++) {
            this.x[i] = 40.0f;
        }
        for (int i2 = 0; i2 < this.y.length; i2++) {
            this.y[i2] = 40.0f;
        }
    }

    /* JADX INFO: renamed from: a */
    public int getLastNumPointers() {
        return this.lastNumPointers;
    }

    /* JADX INFO: renamed from: b */
    public boolean wasDown() {
        return this.wasDown;
    }

    /* JADX INFO: renamed from: c */
    public void updateState() {
        this.wasDown = this.isDown;
        this.lastNumPointers = this.numPointers;
    }

    /* JADX INFO: renamed from: a */
    public void setStart(float f, float f2) {
        this.x[0] = f;
        this.y[0] = f2;
        this.startX = this.x[0];
        this.startY = this.y[0];
        this.distY = 0.0f;
        this.distX = 0.0f;
    }

    /* JADX INFO: renamed from: a */
    public void processEvent(float f, float f2, boolean z, int i) {
        this.historyCount = 0;
        this.numPointers = z ? 1 : 0;
        if (i != -1) {
            MultiTouchController.pointerIndices[0] = i;
        }
        this.x[0] = f;
        this.y[0] = f2;
        this.pressure[0] = 0.0f;
        this.pointerIds[0] = 0;
        this.isDown = z;
        this.isMultiTouch = false;
        if (this.isDown) {
            this.wasDown = this.isDown;
        }
        if (this.numPointers > 0) {
            this.lastNumPointers = this.numPointers;
        }
        this.startX = this.x[0];
        this.startY = this.y[0];
        this.startPressure = this.pressure[0];
        this.distY = 0.0f;
        this.distX = 0.0f;
        this.isRotating = false;
        this.isPinching = false;
        this.isDragging = false;
    }

    /* JADX INFO: renamed from: d */
    public float[] getX() {
        return this.x;
    }

    /* JADX INFO: renamed from: e */
    public int[] getPointerIndices() {
        return MultiTouchController.pointerIndices;
    }

    /* JADX INFO: renamed from: f */
    public float[] getY() {
        return this.y;
    }
}
