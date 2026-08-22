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
    MediaPlayer music;
    AndroidMusic factory;
    AndroidMusicFactory c;

    public AndroidMusicTrack(AndroidMusicFactory androidMusicFactory) {
        this.c = androidMusicFactory;
        if (androidMusicFactory.availablePlayers.size() == 0) {
            throw new RuntimeException("Music player pool empty");
        }
        MediaPlayer mediaPlayer = (MediaPlayer) androidMusicFactory.availablePlayers.remove(0);
        androidMusicFactory.playingPlayers.add(this);
        this.music = mediaPlayer;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(Music music) {
        this.factory = (AndroidMusic) music;
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(boolean z) {
        try {
            MediaPlayer mediaPlayer = this.music;
            mediaPlayer.reset();
            AssetFileDescriptor assetFileDescriptorB = null;
            if (this.factory.path.startsWith("music")) {
                try {
                    assetFileDescriptorB = this.c.musicManager.context.d().b(FileHelper.convertAbstractPath(this.factory.path));
                    mediaPlayer.setDataSource(assetFileDescriptorB.getFileDescriptor(), assetFileDescriptorB.getStartOffset(), assetFileDescriptorB.getLength());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String strConvertAbstractPath = FileHelper.convertAbstractPath(this.factory.path);
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
        this.music.pause();
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void b() {
        this.music.start();
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public boolean c() {
        return this.music.isPlaying();
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void d() {
        if (this.music != null) {
            this.music.stop();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void e() {
        if (this.music != null) {
            this.music.stop();
        }
        this.music = null;
        this.c.playingPlayers.remove(this);
        this.c.availablePlayers.add(this.music);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(float f) {
        this.music.setVolume(f, f);
    }
}
