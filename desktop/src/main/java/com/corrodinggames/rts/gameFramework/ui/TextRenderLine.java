package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ak */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ak.class */
public class TextRenderLine {
    FastArrayList<RenderElement> a = new FastArrayList();
    int b;

    public void a(RenderElement renderElement) {
        this.a.add(renderElement);
    }
}
