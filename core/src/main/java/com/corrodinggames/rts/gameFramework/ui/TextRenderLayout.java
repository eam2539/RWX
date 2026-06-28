package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.aj */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/aj.class */
public class TextRenderLayout {
    FastArrayList<TextRenderLine> a;
    Rect b;
    KoolPaint c;
    KoolPaint d;

    public void a(final float float1, final float float2) {
        final GameEngine instance = GameEngine.getInstance();
        int n = 0;
        final int lineHeight = TextUtils.getLineHeight(this.c);
        for (final TextRenderLine textRenderLine : this.a) {
            int n2 = 0;
            RenderElement renderElement = null;
            for (final RenderElement renderElement2 : textRenderLine.a) {
                if (renderElement != null) {
                    n2 += renderElement.a(this.c);
                }
                int n3 = (int) (float1 + n2 + this.b.d());
                n3 -= textRenderLine.b / 2;
                final int n4 = (int) (float2 + this.b.b + lineHeight / 2 + n * lineHeight);
                if (!(renderElement2 instanceof TextRenderer)) {
                    if (renderElement2 instanceof TextureRenderer) {
                        final TextureRenderer textureRenderer = (TextureRenderer) renderElement2;
                        final Texture a = textureRenderer.a;
                        instance.renderGraphicsEngine.a(a, (float) n3, n4 - a.q * textureRenderer.b, TextRenderQueue.c, 0.0f, textureRenderer.b);
                    }
                    renderElement = renderElement2;
                } else {
                    final TextRenderer textRenderer = (TextRenderer) renderElement2;
                    instance.renderGraphicsEngine.a(textRenderer.d, (float) n3, (float) n4, textRenderer.b(this.c));
                    renderElement = renderElement2;
                }
            }
            ++n;
        }
    }
}
