package com.corrodinggames.rts.gameFramework.utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.ad */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/ad.class */
public class ObjectPool {

    /* JADX INFO: renamed from: a */
    private final Object[] pool;

    /* JADX INFO: renamed from: b */
    private int size;

    /* JADX INFO: renamed from: c */
    private final boolean strict = false;

    public ObjectPool(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("The max pool size must FastArrayList > 0");
        }
        this.pool = new Object[i];
    }

    /* JADX INFO: renamed from: a */
    public Object get() {
        if (this.size > 0) {
            int i = this.size - 1;
            Object obj = this.pool[i];
            this.pool[i] = null;
            this.size--;
            return obj;
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public boolean release(Object obj) {
        if (this.size < this.pool.length) {
            this.pool[this.size] = obj;
            this.size++;
            return true;
        }
        return false;
    }
}
