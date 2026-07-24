package com.corrodinggames.rts.gameFramework.stats;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.g.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/g/d.class */
public abstract class GameObjectComparator implements Comparable<GameObjectComparator> {

    /* JADX INFO: renamed from: a */
    public int cachedStat;

    public abstract String b();

    public abstract boolean a();

    public abstract int c();

    public abstract int d();

    public abstract int a(StatType statType);

    /* JADX INFO: renamed from: b */
    public void updateCachedStat(StatType statType) {
        this.cachedStat = a(statType);
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(GameObjectComparator gameObjectComparator) {
        if (this.cachedStat == gameObjectComparator.cachedStat) {
            return b().compareTo(gameObjectComparator.b());
        }
        return gameObjectComparator.cachedStat - this.cachedStat;
    }
}
