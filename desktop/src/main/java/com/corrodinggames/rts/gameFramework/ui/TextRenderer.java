package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;
import com.corrodinggames.rts.gameFramework.GameEngine;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ai.class */
public class TextRenderer extends RenderElement {

    /* JADX INFO: renamed from: d */
    String text;

    final /* synthetic */ TextRenderQueue e;

    TextRenderer(TextRenderQueue textRenderQueue, String str) {
        this.e = textRenderQueue;
        this.text = str;
    }

    public Paint b(Paint paint) {
        return paint;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.RenderElement
    public int a(Paint paint) {
        GameEngine gameEngine = GameEngine.getInstance();
        int iB = gameEngine.renderGraphicsEngine.b(this.text, b(paint));
        if (GameEngine.isAndroidPlatform()) {
        }
        return iB;
    }

    public TextRenderer b(String str) {
        return new TextRenderer(this.e, str);
    }
}
