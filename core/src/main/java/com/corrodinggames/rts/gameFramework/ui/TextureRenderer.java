package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ah */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ah.class */
public class TextureRenderer extends RenderElement {
    Texture a;
    float b = 1.0f;
    int c;
    int d;
    final /* synthetic */ TextRenderQueue e;

    public TextureRenderer(TextRenderQueue textRenderQueue) {
        this.e = textRenderQueue;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.RenderElement
    public int a(KoolPaint paint) {
        return this.c;
    }
}
