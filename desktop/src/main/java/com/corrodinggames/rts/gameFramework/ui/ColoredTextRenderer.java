package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ag */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ag.class */
public class ColoredTextRenderer extends TextRenderer {
    public Paint a;
    public int b;
    final /* synthetic */ TextRenderQueue c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ColoredTextRenderer(TextRenderQueue textRenderQueue, String str, Paint paint) {
        super(textRenderQueue, str);
        this.c = textRenderQueue;
        this.b = 0;
        this.a = paint;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ColoredTextRenderer(TextRenderQueue textRenderQueue, String str, Paint paint, int i) {
        super(textRenderQueue, str);
        this.c = textRenderQueue;
        this.b = 0;
        this.a = paint;
        this.b = i;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.TextRenderer
    public Paint b(Paint paint) {
        if (this.a == null) {
            if (this.b != 0) {
                TextRenderQueue.f.a(paint);
                TextRenderQueue.f.b(this.b);
                return TextRenderQueue.f;
            }
            return paint;
        }
        if (this.b != 0) {
            TextRenderQueue.f.a(this.a);
            TextRenderQueue.f.b(this.b);
            return TextRenderQueue.f;
        }
        return this.a;
    }

    @Override // com.corrodinggames.rts.gameFramework.ui.TextRenderer
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public ColoredTextRenderer b(String str) {
        return new ColoredTextRenderer(this.c, str, this.a, this.b);
    }
}
