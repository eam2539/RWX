package com.corrodinggames.rts.gameFramework.gl;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/i.class */
public class FilterGroup extends ShaderInterface {
    protected List a;
    private  List b;
    private Texture c;
    private Texture d;

    private void a(Texture texture) {
        a();
        for (int i = 0; i < this.a.size(); i++) {
            this.b.add(new GLTexture(texture.b(), texture.c(), false));
        }
    }

    private void a() {
        Iterator it = this.b.iterator();
        while (it.hasNext()) {
            ((GLTexture) it.next()).j();
        }
        this.b.clear();
    }

    public Texture a(Texture texture, IGraphicsEngine iGraphicsEngine, FilterCallback filterCallback) {
        if (texture instanceof GLTexture) {
            if (!((GLTexture) texture).k()) {
                return this.c;
            }
        } else if (this.d == texture && this.c != null) {
            return this.c;
        }
        if (this.b.size() != this.a.size() || this.d != texture) {
            a(texture);
        }
        this.d = texture;
        Texture texture2 = texture;
        int i = 0;
        int size = this.b.size();
        while (i < size) {
            GLTexture gLTexture = (GLTexture) this.b.get(i);
            ITextureFilter iTextureFilter = (ITextureFilter) this.a.get(i);
            iGraphicsEngine.c(gLTexture);
            filterCallback.a(texture2, iTextureFilter, i == 0);
            iGraphicsEngine.d();
            texture2 = gLTexture;
            GameEngine.log("FilterGroup: renderTarget");
            i++;
        }
        this.c = texture2;
        return texture2;
    }
}
