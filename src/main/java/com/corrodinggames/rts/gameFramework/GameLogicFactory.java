package com.corrodinggames.rts.gameFramework;

import android.content.Context;
import com.corrodinggames.rts.game.GameLogic;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.v */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/v.class */
public class GameLogicFactory extends GameEngineFactory {
    @Override // com.corrodinggames.rts.gameFramework.GameEngineFactory
    /* JADX INFO: renamed from: a */
    public GameEngine createGameEngine(Context context) {
        return new GameLogic(context);
    }
}
