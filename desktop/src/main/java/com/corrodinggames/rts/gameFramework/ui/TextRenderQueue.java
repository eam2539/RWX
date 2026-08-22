package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Paint;
import android.graphics.Rect;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.ae */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/ae.class */
public class TextRenderQueue {
    static Paint h;
    static GamePaint a = new GamePaint();
    static GamePaint b = new GamePaint();
    static GamePaint c = new GamePaint();
    static Paint coloredTextPaint = new Paint();
    public Paint defaultPaint = a;
    public Paint highlightPaint = a;
    public Paint currentPaint = this.defaultPaint;
    FastArrayList<RenderElement> elements = new FastArrayList();

    static {
        c.a(true);
        h = new Paint();
        h.b(-65536);
        h.a(Paint.Style.STROKE);
    }

    public void a(Paint paint) {
        if (paint == null) {
            this.currentPaint = this.defaultPaint;
        } else {
            this.currentPaint = paint;
        }
    }

    public void a(boolean z) {
        if (z) {
            this.currentPaint = this.highlightPaint;
        } else {
            this.currentPaint = this.defaultPaint;
        }
    }

    public String a() {
        StringBuilder sb = new StringBuilder();
        for (RenderElement renderElement : this.elements) {
            if (renderElement instanceof TextRenderer) {
                sb.append(((TextRenderer) renderElement).text);
            }
        }
        return sb.toString();
    }

    public void a(String str) {
        if (this.elements.size() > 0) {
            int size = this.elements.size() - 1;
            RenderElement renderElement = (RenderElement) this.elements.get(size);
            if (renderElement instanceof TextRenderer) {
                TextRenderer textRenderer = (TextRenderer) renderElement;
                String strBooleanToString = Utility.removeSuffix(textRenderer.text, str);
                if (!textRenderer.text.equals(strBooleanToString)) {
                    this.elements.set(size, textRenderer.b(strBooleanToString));
                }
            }
        }
    }

    public void b() {
        this.elements.clear();
    }

    public void a(RenderElement renderElement) {
        this.elements.add(renderElement);
    }

    public void b(String str) {
        if (this.currentPaint != null && this.currentPaint != this.defaultPaint) {
            a(str, this.currentPaint);
        } else {
            a(new TextRenderer(this, str));
        }
    }

    public void a(String str, Paint paint) {
        a(new ColoredTextRenderer(this, str, paint));
    }

    public void a(String str, int i) {
        if (this.currentPaint != null && this.currentPaint != this.defaultPaint) {
            a(new ColoredTextRenderer(this, str, this.currentPaint, i));
        } else {
            a(new ColoredTextRenderer(this, str, null, i));
        }
    }

    public void a(String str, int i, boolean z) {
        Paint paint = this.defaultPaint;
        if (z) {
            paint = this.highlightPaint;
        }
        a(new ColoredTextRenderer(this, str, paint, i));
    }

    public void a(Texture texture, int i, int i2) {
        TextureRenderer textureRenderer = new TextureRenderer(this);
        textureRenderer.texture = texture;
        float scale = TextUtils.getScale(texture, i, i2);
        textureRenderer.width = (int) (texture.p * scale);
        textureRenderer.height = (int) (texture.q * scale);
        textureRenderer.scale = scale;
        this.elements.add(textureRenderer);
    }

    public int c() {
        return GameEngine.getInstance().renderGraphicsEngine.a("A", this.currentPaint);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public TextRenderLayout a(int i, boolean z) {
        int iLastIndexOf;
        GameEngine.getInstance();
        Rect rect = new Rect((-i) / 2, 0, i / 2, 10);
        FastArrayList<TextRenderLine> fastArrayList = new FastArrayList();
        TextRenderLine textRenderLine = new TextRenderLine();
        Paint paint = this.defaultPaint;
        int i2 = i - 5;
        for (RenderElement renderElement : this.elements) {
            if (textRenderLine.b >= i2 - 5) {
                if (textRenderLine.elements.size() > 0) {
                    fastArrayList.add(textRenderLine);
                }
                textRenderLine = new TextRenderLine();
            }
            if (!(renderElement instanceof TextRenderer)) {
                textRenderLine.a(renderElement);
                textRenderLine.b += renderElement.a(this.defaultPaint);
            } else {
                TextRenderer textRenderer = (TextRenderer) renderElement;
                String str = textRenderer.text;
                int i3 = 0;
                while (i3 < str.length()) {
                    if (str.charAt(i3) == '\n') {
                        i3++;
                        fastArrayList.add(textRenderLine);
                        textRenderLine = new TextRenderLine();
                    } else {
                        int iA = paint.a((CharSequence) str, i3, str.length(), true, i2 - textRenderLine.b, (float[]) null);
                        if (iA == 0) {
                            break;
                        }
                        boolean z2 = true;
                        int iIndexOf = str.indexOf("\n", i3 + 1);
                        if (iIndexOf != -1 && iIndexOf < i3 + iA) {
                            iA = iIndexOf - i3;
                        } else {
                            if (i3 + iA < str.length() && (iLastIndexOf = str.substring(i3, i3 + iA).lastIndexOf(" ")) != -1 && iLastIndexOf != 0) {
                                iA = iLastIndexOf;
                            }
                            if (i3 + iA == str.length()) {
                                z2 = false;
                            }
                        }
                        String strSubstring = str.substring(i3, i3 + iA);
                        if (Utility.containsSubstring(strSubstring, "\\n")) {
                            strSubstring = strSubstring.replaceAll("(\\n)", VariableScope.nullOrMissingString);
                        }
                        TextRenderer textRendererB = textRenderer.b(strSubstring);
                        textRenderLine.a(textRendererB);
                        textRenderLine.b += textRendererB.a(this.defaultPaint);
                        i3 += iA;
                        if (i3 < str.length() && str.charAt(i3) == '\n') {
                            i3++;
                        }
                        if (z2 || textRenderLine.b >= i2 - 5) {
                            if (textRenderLine.elements.size() > 0) {
                                fastArrayList.add(textRenderLine);
                            }
                            textRenderLine = new TextRenderLine();
                        }
                    }
                }
            }
        }
        if (textRenderLine.elements.size() > 0) {
            fastArrayList.add(textRenderLine);
        }
        if (fastArrayList.size() > 0 && ((TextRenderLine) fastArrayList.get(fastArrayList.size() - 1)).elements.size() == 0) {
            fastArrayList.remove(fastArrayList.size() - 1);
        }
        rect.d = rect.b + (fastArrayList.size() * TextUtils.getLineHeight(paint));
        if (z) {
            float fD = rect.d();
            float f2 = 0.0f;
            for (TextRenderLine textRenderLine2 : fastArrayList) {
                if (textRenderLine2.b > f2) {
                    f2 = textRenderLine2.b;
                }
            }
            float f3 = f2;
            if (f3 < rect.b()) {
                rect.a = (int) (fD - (f3 / 2.0f));
                rect.c = (int) (fD + (f3 / 2.0f));
            }
        }
        TextRenderLayout textRenderLayout = new TextRenderLayout();
        textRenderLayout.lines = fastArrayList;
        textRenderLayout.rect = rect;
        textRenderLayout.defaultPaint = this.defaultPaint;
        textRenderLayout.highlightPaint = this.highlightPaint;
        return textRenderLayout;
    }
}
