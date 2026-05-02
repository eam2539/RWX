package com.corrodinggames.rts.gameFramework;

import java.util.Comparator;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.x */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/x.class */
public class GameObjectComparator implements Comparator<GameObject> {
    GameObjectComparator() {
    }

    @Override // java.util.Comparator
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compare(GameObject gameObject, GameObject gameObject2) {
        if (gameObject.syncType > gameObject2.syncType) {
            return 1;
        }
        if (gameObject.syncType < gameObject2.syncType) {
            return -1;
        }
        if (gameObject.value2 > gameObject2.value2) {
            return 1;
        }
        if (gameObject.value2 < gameObject2.value2) {
            return -1;
        }
        if (gameObject.posY > gameObject2.posY) {
            return 1;
        }
        return gameObject.posY < gameObject2.posY ? -1 : 0;
    }
}
