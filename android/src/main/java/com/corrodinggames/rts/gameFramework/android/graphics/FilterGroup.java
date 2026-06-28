package com.corrodinggames.rts.gameFramework.android.graphics;

import com.corrodinggames.rts.gameFramework.GameEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.n */
/* JADX INFO: loaded from: classes.dex */
public final class FilterGroup extends DefaultGraphicsOption {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    protected List f571a;
    private final List b;
    private ImageBase c;
    private ImageBase d;

    public FilterGroup() {
        this.f571a = new ArrayList();
        this.b = new ArrayList();
    }

    public final ImageBase a(ImageBase imageBase, GraphicsRenderer graphicsRenderer, ImageLoadInterface imageLoadInterface) {
        if (imageBase instanceof RawTexture) {
            if (!((RawTexture) imageBase).j()) {
                return this.c;
            }
        } else if (this.d == imageBase && this.c != null) {
            return this.c;
        }
        if (this.b.size() != this.f571a.size() || this.d != imageBase) {
            Iterator it = this.b.iterator();
            while (it.hasNext()) {
                ((RawTexture) it.next()).i();
            }
            this.b.clear();
            for (int i = 0; i < this.f571a.size(); i++) {
                this.b.add(new RawTexture(imageBase.b(), imageBase.c()));
            }
        }
        this.d = imageBase;
        int size = this.b.size();
        int i2 = 0;
        ImageBase imageBase2 = imageBase;
        while (i2 < size) {
            RawTexture rawTexture = (RawTexture) this.b.get(i2);
            GraphicsOption graphicsOption = (GraphicsOption) this.f571a.get(i2);
            graphicsRenderer.c(rawTexture);
            imageLoadInterface.a(imageBase2, graphicsOption);
            graphicsRenderer.e();
            GameEngine.log("FilterGroup: renderTarget");
            i2++;
            imageBase2 = rawTexture;
        }
        this.c = imageBase2;
        return imageBase2;
    }
}
