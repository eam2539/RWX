package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ai */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ai.class */
public class TextRenderer extends RenderElement {

    /* JADX INFO: renamed from: d */
    String text;

    final /* synthetic */ TextRenderQueue e;

    @Override // com.corrodinggames.rts.gameFramework.ui.RenderElement
    public int a(KoolPaint paint) {
        GameEngine gameEngine = GameEngine.getInstance();
        int iB = gameEngine.renderGraphicsEngine.b(this.text, b(paint));
        if (GameEngine.isAndroidPlatform()) {
        }
        return iB;
    }

    public KoolPaint b(KoolPaint paint) {
        return paint;
    }

    TextRenderer(TextRenderQueue textRenderQueue, String str) {
        this.e = textRenderQueue;
        this.text = str;
    }

    public TextRenderer b(String str) {
        return new TextRenderer(this.e, str);
    }
}
