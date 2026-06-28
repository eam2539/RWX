package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.gameFramework.GameObject;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.t */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/t.class */
class GameObjectArrayListIterator implements Iterator {
    private int b;
    private int c;
    private int d;
    final /* synthetic */ GameObjectArrayList a;

    GameObjectArrayListIterator(GameObjectArrayList gameObjectArrayList) {
        this.a = gameObjectArrayList;
        this.b = this.a.b;
        this.c = -1;
        this.d = GameObjectArrayList.e(this.a);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.b != 0;
    }

    @Override // java.util.Iterator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public GameObject next() {
        GameObjectArrayList gameObjectArrayList = this.a;
        int i = this.b;
        int i2 = GameObjectArrayList.e(gameObjectArrayList);
        if (i2 != this.d) {
            throw new ConcurrentModificationException("on:" + this.a.d + " (modCount:" + i2 + " expectedModCount:" + this.d + ")");
        }
        if (i == 0) {
            throw new NoSuchElementException();
        }
        this.b = i - 1;
        GameObject[] gameObjectArr = gameObjectArrayList.c;
        int i3 = gameObjectArrayList.b - i;
        this.c = i3;
        return gameObjectArr[i3];
    }

    @Override // java.util.Iterator
    public void remove() {
        GameObject[] gameObjectArr = this.a.c;
        int i = this.c;
        int i2 = GameObjectArrayList.e(this.a);
        if (i2 != this.d) {
            throw new ConcurrentModificationException("on:" + this.a.d + " (modCount:" + i2 + " expectedModCount:" + this.d + ")");
        }
        if (i < 0) {
            throw new IllegalStateException();
        }
        System.arraycopy(gameObjectArr, i + 1, gameObjectArr, i, this.b);
        GameObjectArrayList gameObjectArrayList = this.a;
        int i3 = gameObjectArrayList.b - 1;
        gameObjectArrayList.b = i3;
        gameObjectArr[i3] = null;
        this.c = -1;
        this.d = GameObjectArrayList.f(this.a);
    }
}
