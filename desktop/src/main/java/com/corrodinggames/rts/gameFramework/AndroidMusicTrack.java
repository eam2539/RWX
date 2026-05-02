package com.corrodinggames.rts.gameFramework;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.FileLoaderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ap */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ap.class */
public class AndroidMusicTrack extends MusicTrack {
    MediaPlayer a;
    AndroidMusic b;
    AndroidMusicFactory c;

    public AndroidMusicTrack(AndroidMusicFactory androidMusicFactory) {
        this.c = androidMusicFactory;
        if (androidMusicFactory.availablePlayers.size() == 0) {
            throw new RuntimeException("Music player pool empty");
        }
        MediaPlayer mediaPlayer = (MediaPlayer) androidMusicFactory.availablePlayers.remove(0);
        androidMusicFactory.playingPlayers.add(this);
        this.a = mediaPlayer;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(Music music) {
        this.b = (AndroidMusic) music;
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(boolean z) {
        try {
            MediaPlayer mediaPlayer = this.a;
            mediaPlayer.reset();
            AssetFileDescriptor assetFileDescriptorB = null;
            if (this.b.path.startsWith("music")) {
                try {
                    assetFileDescriptorB = this.c.musicManager.context.d().b(FileHelper.convertAbstractPath(this.b.path));
                    mediaPlayer.setDataSource(assetFileDescriptorB.getFileDescriptor(), assetFileDescriptorB.getStartOffset(), assetFileDescriptorB.getLength());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String strConvertAbstractPath = FileHelper.convertAbstractPath(this.b.path);
                if (FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath) == null) {
                    mediaPlayer.setDataSource(strConvertAbstractPath);
                } else {
                    AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath(strConvertAbstractPath);
                    if (assetInputStreamOpenFileByPath == null) {
                        throw new RuntimeException("openAssetSteam() null for '" + strConvertAbstractPath + "'");
                    }
                    File fileCreateTempFileInContext = FileHelper.createTempFileInContext(this.c.musicManager.context, "music", "ogg");
                    GameEngine.log("Temp file needed for this music from zipped/abstract mod file");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFileInContext);
                        Utility.copyStream(assetInputStreamOpenFileByPath, fileOutputStream);
                        fileOutputStream.close();
                        assetInputStreamOpenFileByPath.close();
                        FileInputStream fileInputStream = new FileInputStream(fileCreateTempFileInContext);
                        try {
                            mediaPlayer.setDataSource(fileInputStream.getFD(), 0L, fileInputStream.available());
                            fileInputStream.close();
                            fileCreateTempFileInContext.delete();
                        } catch (Throwable th) {
                            fileInputStream.close();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        fileCreateTempFileInContext.delete();
                        throw th2;
                    }
                }
            }
            if (z) {
                mediaPlayer.setLooping(true);
            }
            mediaPlayer.setVolume(0.0f, 0.0f);
            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() { // from class: com.corrodinggames.rts.gameFramework.ap.1
                @Override // android.media.MediaPlayer.OnInfoListener
                public boolean onInfo(MediaPlayer mediaPlayer2, int i, int i2) {
                    return true;
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.corrodinggames.rts.gameFramework.ap.2
                @Override // android.media.MediaPlayer.OnPreparedListener
                public void onPrepared(MediaPlayer mediaPlayer2) {
                    mediaPlayer2.start();
                }
            });
            mediaPlayer.prepareAsync();
            if (assetFileDescriptorB != null) {
                assetFileDescriptorB.close();
            }
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a() {
        this.a.pause();
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void b() {
        this.a.start();
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public boolean c() {
        return this.a.isPlaying();
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void d() {
        if (this.a != null) {
            this.a.stop();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void e() {
        if (this.a != null) {
            this.a.stop();
        }
        this.a = null;
        this.c.playingPlayers.remove(this);
        this.c.availablePlayers.add(this.a);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(float f) {
        this.a.setVolume(f, f);
    }
}
