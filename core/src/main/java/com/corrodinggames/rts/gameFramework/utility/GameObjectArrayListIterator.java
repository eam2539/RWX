package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.GameObject;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/t.class */
class GameObjectArrayListIterator implements Iterator {

    /* JADX INFO: renamed from: b */
    private int remaining;

    /* JADX INFO: renamed from: c */
    private int lastRetrieved;

    /* JADX INFO: renamed from: d */
    private int expectedModCount;
    final /* synthetic */ GameObjectArrayList a;

    GameObjectArrayListIterator(GameObjectArrayList gameObjectArrayList) {
        this.a = gameObjectArrayList;
        this.remaining = this.a.size;
        this.lastRetrieved = -1;
        this.expectedModCount = GameObjectArrayList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.remaining != 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public GameObject next() {
        GameObjectArrayList gameObjectArrayList = this.a;
        int i = this.remaining;
        int i2 = GameObjectArrayList.e(gameObjectArrayList);
        if (i2 != this.expectedModCount) {
            throw new ConcurrentModificationException("on:" + this.a.name + " (modCount:" + i2 + " expectedModCount:" + this.expectedModCount + ")");
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.remaining = i - 1;
        GameObject[] gameObjectArr = gameObjectArrayList.c;
        int i3 = gameObjectArrayList.size - i;
        this.lastRetrieved = i3;
        return gameObjectArr[i3];
    }

    @Override // java.util.Iterator
    public void remove() {
        GameObject[] gameObjectArr = this.a.c;
        int i = this.lastRetrieved;
        int i2 = GameObjectArrayList.e(this.a);
        if (i2 != this.expectedModCount) {
            throw new ConcurrentModificationException("on:" + this.a.name + " (modCount:" + i2 + " expectedModCount:" + this.expectedModCount + ")");
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(gameObjectArr, i + 1, gameObjectArr, i, this.remaining);
        GameObjectArrayList gameObjectArrayList = this.a;
        int i3 = gameObjectArrayList.size - 1;
        gameObjectArrayList.size = i3;
        gameObjectArr[i3] = null;
        this.lastRetrieved = -1;
        this.expectedModCount = GameObjectArrayList.f(this.a);
    }
}
