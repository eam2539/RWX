package com.corrodinggames.rts.java.slick;

import android.graphics.Rect;
import android.graphics.RectF;
import com.LibRocket;
import com.corrodinggames.librocket.LibRocketManager;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.d.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/d/a.class */
public class SlickLibRocketManager extends LibRocketManager {
    private static SGL k = Renderer.get();
    Graphics j;

    @Override // com.corrodinggames.librocket.LibRocketManager
    /* JADX INFO: renamed from: a */
    public void resetRenderCount() {
        throw new RuntimeException("startNewFrame() not supported on SlickLibRocket");
    }

    public void a(Graphics graphics) {
        this.j = graphics;
        super.resetRenderCount();
    }

    @Override // com.LibRocket
    public boolean GenerateTexture(int i, byte[] bArr) {
        try {
            SlickUITextureHolder slickUITextureHolder = (SlickUITextureHolder) findTextureHolder(i);
            ImageBuffer imageBuffer = new ImageBuffer(slickUITextureHolder.width, slickUITextureHolder.height);
            byte[] rgba = imageBuffer.getRGBA();
            for (int i2 = 0; i2 < bArr.length; i2++) {
                rgba[i2] = bArr[i2];
            }
            slickUITextureHolder.h = new Image(imageBuffer);
            if (slickUITextureHolder.h == null) {
                throw new RuntimeException("slickTextureHolder.image==null");
            }
            return true;
        } catch (OutOfMemoryError e) {
            GameEngine.reportOOM(AssetType.uiImage, e);
            return false;
        } catch (Throwable th) {
            ScriptEngine.throwDelayedException("GenerateTexture Failed", th);
            return true;
        }
    }

    @Override // com.LibRocket
    public void RenderGeometryPossiblyCompiled(float[] fArr, float[] fArr2, int[] iArr, int[] iArr2, int i, float f, float f2, LibRocket.CompiledGeometry compiledGeometry) {
        try {
            if (this.debug) {
                System.out.println("SlickLibRocket:RenderGeometry(" + fArr.length + "," + i + ")");
                System.out.println("indices.length=" + iArr2.length + VariableScope.nullOrMissingString);
            }
            SlickUITextureHolder slickUITextureHolder = null;
            if (i != 0) {
                slickUITextureHolder = (SlickUITextureHolder) findTextureHolder(i);
            }
            RectF rectF = null;
            if (compiledGeometry != null) {
                rectF = (RectF) compiledGeometry.bbox;
            }
            if (rectF == null) {
                rectF = new RectF();
                for (int i2 = 0; i2 < iArr2.length; i2 += 3) {
                    int i3 = iArr2[i2];
                    for (int i4 = 0; i4 <= 2; i4++) {
                        int i5 = iArr2[i2 + i4];
                        float f3 = fArr[(i5 * 2) + 0];
                        float f4 = fArr[(i5 * 2) + 1];
                        if (rectF.a()) {
                            rectF.a(f3, f4, f3 + 1.0f, f4 + 1.0f);
                        } else {
                            rectF.c(f3, f4);
                        }
                    }
                }
                rectF.g();
                if (compiledGeometry != null) {
                    compiledGeometry.bbox = rectF;
                }
            }
            RectF rectF2 = new RectF(rectF);
            rectF2.a(f, f2);
            if (this.h && !Utility.rectanglesOverlap(rectF2, this.scissorRectF)) {
                boolean z = true;
                if (slickUITextureHolder != null && slickUITextureHolder.h == null && slickUITextureHolder.lazyLoad && slickUITextureHolder.unitType == null) {
                    z = false;
                }
                if (z) {
                    return;
                }
            }
            if (slickUITextureHolder != null && slickUITextureHolder.j != null) {
                System.out.println("Loading image for: " + slickUITextureHolder.index);
                slickUITextureHolder.h = new Image(slickUITextureHolder.j);
                if (slickUITextureHolder.h == null) {
                    throw new RuntimeException("slickTextureHolder.image==null");
                }
                slickUITextureHolder.j = null;
            }
            this.j.pushTransform();
            this.j.setDrawMode(Graphics.MODE_NORMAL);
            this.j.translate(f, f2);
            float textureWidth = 1.0f;
            float textureHeight = 1.0f;
            boolean z2 = false;
            float f5 = 1.0f;
            boolean z3 = false;
            if (slickUITextureHolder != null) {
                z2 = slickUITextureHolder.noColor;
                f5 = slickUITextureHolder.alpha;
                TextureImpl.getLastBind();
                if (slickUITextureHolder.h == null && slickUITextureHolder.lazyLoad) {
                    if (slickUITextureHolder.unitType != null) {
                        GraphicsEngine graphicsEngine = GameEngine.getInstance().renderGraphicsEngine;
                        this.j.pushTransform();
                        graphicsEngine.i();
                        float f6 = (((GameEngine.getInstance().renderTimeMillis / 1000.0f) / 10.0f) * 360.0f) % 360.0f;
                        this.j.translate(-f, -f2);
                        Rect rect = new Rect(this.scissorRect.a, this.scissorRect.b, this.scissorRect.c, this.scissorRect.d);
                        this.j.setClip((Rectangle) null);
                        this.j.setWorldClip((Rectangle) null);
                        graphicsEngine.a(rect);
                        PlayerTeam playerTeamK = PlayerTeam.k(0);
                        if (playerTeamK == null) {
                            playerTeamK = PlayerTeam.TEAM_ALL;
                        }
                        UnitTypeEnum.drawUnit(slickUITextureHolder.unitType, rectF2.d(), rectF2.e(), f6, 3.0f, playerTeamK, rectF2.c() * 0.6f, rectF2.c(), false, false, 1, null);
                        graphicsEngine.p();
                        f5 = 0.0f;
                        graphicsEngine.j();
                        this.j.popTransform();
                    } else if (this.renderCount < 1) {
                        slickUITextureHolder.loadTexture();
                        this.renderCount++;
                    }
                }
                if (slickUITextureHolder.h != null) {
                    slickUITextureHolder.h.getTexture().bind();
                    textureWidth = slickUITextureHolder.h.getTextureWidth();
                    textureHeight = slickUITextureHolder.h.getTextureHeight();
                    z3 = true;
                } else if (slickUITextureHolder.lazyLoad) {
                    f5 = 0.1f;
                }
            }
            if (!z3) {
                TextureImpl.bindNone();
            }
            Color color = new Color(1.0f, 1.0f, 1.0f, f5);
            this.j.setColor(color);
            k.glBegin(4);
            for (int i6 = 0; i6 < iArr2.length; i6 += 3) {
                int i7 = iArr2[i6];
                if (!z2) {
                    int i8 = iArr[i7];
                    color.r = ((i8 >> 24) & 255) / 255.0f;
                    color.g = ((i8 >> 16) & 255) / 255.0f;
                    color.b = ((i8 >> 8) & 255) / 255.0f;
                    color.a = (i8 & 255) / 255.0f;
                    color.a *= f5;
                    color.bind();
                }
                for (int i9 = 0; i9 <= 2; i9++) {
                    int i10 = iArr2[i6 + i9];
                    k.glTexCoord2f(fArr2[(i10 * 2) + 0] * textureWidth, fArr2[(i10 * 2) + 1] * textureHeight);
                    k.glVertex2f(fArr[(i10 * 2) + 0], fArr[(i10 * 2) + 1]);
                }
            }
            k.glEnd();
            this.j.popTransform();
            this.j.setColor(Color.white);
        } catch (Throwable th) {
            ScriptEngine.throwDelayedException("UI Render Failed", th);
        }
    }

    @Override // com.LibRocket
    public LibRocket.TextureHolder getFromTextureHolderFactory() {
        return new SlickUITextureHolder(this);
    }

    @Override // com.corrodinggames.librocket.LibRocketManager, com.LibRocket
    public void EnableScissorRegion(boolean z) {
        if (z) {
            this.j.setWorldClip(this.scissorRect.a, this.scissorRect.b, this.scissorRect.b(), this.scissorRect.c());
            this.h = true;
        } else {
            this.j.clearWorldClip();
            this.h = false;
        }
    }
}
