package com.corrodinggames.rts.appFramework;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/e.class */
public enum RenderMethod {
    singleThreadedSurface,
    singleThreadedSurfaceIfHardware,
    multiThreadedSurface,
    multiThreadedNonSurface,
    opengl,
    dynamicDefault
}
