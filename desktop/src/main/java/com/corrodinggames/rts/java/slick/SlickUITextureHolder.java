package com.corrodinggames.rts.java.slick;

import com.corrodinggames.librocket.UITextureHolder;
import com.corrodinggames.rts.gameFramework.AssetType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.FileLoaderFactory;
import com.corrodinggames.rts.gameFramework.utility.IFileLoader;
import com.corrodinggames.rts.java.SlickGraphicsEngine;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.PNGImageData;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.d.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/d/b.class */
public class SlickUITextureHolder extends UITextureHolder {
    Image h;
    boolean i;
    ImageBuffer j;
    final /* synthetic */ SlickLibRocketManager k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SlickUITextureHolder(SlickLibRocketManager slickLibRocketManager) {
        super(slickLibRocketManager);
        this.k = slickLibRocketManager;
    }

    @Override // com.corrodinggames.librocket.UITextureHolder
    /* JADX INFO: renamed from: a */
    public boolean loadTexture() throws IOException {
        InputStream fileInputStream;
        BufferedInputStream bufferedInputStream = null;
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(this.texturePath);
        if (fileLoaderForPath != null) {
            fileInputStream = fileLoaderForPath.openAssetInputStream(this.texturePath, true);
            if (fileInputStream == null) {
                GameEngine.logWarningAndStack("Failed to open zipped file: " + this.texturePath);
                return false;
            }
        } else {
            try {
                fileInputStream = new FileInputStream(this.texturePath);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            bufferedInputStream = new BufferedInputStream(fileInputStream);
        } catch (Exception e2) {
            GameEngine.log("Exception loading image: " + this.texturePath, (Throwable) e2);
            this.h = SlickGraphicsEngine.generalErrorImage.C();
            this.i = true;
        } catch (OutOfMemoryError e3) {
            GameEngine.reportOOM(AssetType.uiImage, e3);
            this.h = SlickGraphicsEngine.outOfMemoryImage.C();
            this.i = true;
        } catch (Throwable e4) {
            e4.printStackTrace();
            GameEngine.log("Exception loading image: " + this.texturePath, (Throwable) e4);
            this.h = SlickGraphicsEngine.generalErrorImage.C();
            this.i = true;
        }
        try {
            PNGImageData pNGImageData = new PNGImageData();
            pNGImageData.loadImage(bufferedInputStream);
            bufferedInputStream.close();
            this.h = new Image(pNGImageData);
            this.width = this.h.getWidth();
            this.height = this.h.getHeight();
            if (this.isThumbnail) {
                if (this.width > 500 || this.height > 500) {
                    GameEngine.log("Map thumbnail is too large. Size:(" + this.width + "," + this.height + ") (max:500 pixels)");
                    this.h = SlickGraphicsEngine.largeThumbnailImage.C();
                    this.i = true;
                    this.width = this.h.getWidth();
                    this.height = this.h.getHeight();
                    return true;
                }
                return true;
            }
            return true;
        } catch (Throwable th) {
            bufferedInputStream.close();
            throw th;
        }
    }

    @Override // com.LibRocket.TextureHolder
    public void remove() {
        if (this.h != null && !this.i) {
            try {
                this.h.destroy();
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
        this.texturePath = null;
        this.j = null;
        this.h = null;
        this.i = false;
    }
}
