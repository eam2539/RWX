package com.corrodinggames.rts.game;

import android.graphics.Bitmap;
import java.io.File;
import java.io.FileOutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/k.class */
class JpegFrameWriterTask implements Runnable {
    public Bitmap a;
    public int b;
     /* synthetic */ GameLogic c;

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(this.c.frameWriteDirectoryPath + "image_" + String.format("%07d", Integer.valueOf(this.b)) + ".jpg"));
                this.a.a(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                this.c.isGameRecording = false;
                this.c.alert("Error saving jpg, recording has stopped. Is there free space remaining on the SD card?", 1);
            }
        }
    }
}
