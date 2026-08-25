package com.corrodinggames.rts.gameFramework.utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/c.class */
class CachedThrowable extends Throwable {
    final /* synthetic */ CachedStackTrace a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    CachedThrowable(CachedStackTrace cachedStackTrace, CachedThrowable cachedThrowable) {
        super(cachedStackTrace.stackTrace, cachedThrowable);
        this.a = cachedStackTrace;
    }

    @Override // java.lang.Throwable
    public Throwable fillInStackTrace() {
        setStackTrace(this.a.b);
        return this;
    }
}
