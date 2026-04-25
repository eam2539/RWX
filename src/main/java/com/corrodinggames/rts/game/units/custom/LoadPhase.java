package com.corrodinggames.rts.game.units.custom;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/ah.class */
enum LoadPhase {
    imageLoad,
    imageLoadOrGet,
    soundLoad,
    soundLoadOrGet,
    iniParse,
    unitParse,
    iniOpen,
    iniClose,
    iniSetup,
    actionParse,
    unitParsePartA,
    unitParsePartB,
    unitParsePartC,
    unitParsePartD;

    double o;
}
