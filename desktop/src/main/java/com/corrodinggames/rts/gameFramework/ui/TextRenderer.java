package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ai.class */
public class TextRenderer extends RenderElement {
    String d;
    final /* synthetic */ TextRenderQueue e;

    @Override // com.corrodinggames.rts.gameFramework.ui.RenderElement
    public int a(Paint paint) {
        GameEngine gameEngine = GameEngine.getInstance();
        int iB = gameEngine.renderGraphicsEngine.b(this.d, b(paint));
        if (GameEngine.isAndroidPlatform()) {
        }
        return iB;
    }

    public Paint b(Paint paint) {
        return paint;
    }

    TextRenderer(TextRenderQueue textRenderQueue, String str) {
        this.e = textRenderQueue;
        this.d = str;
    }

    public TextRenderer b(String str) {
        return new TextRenderer(this.e, str);
    }
}
