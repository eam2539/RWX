package io.github.rwx.audio;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Music;
import com.corrodinggames.rts.gameFramework.MusicTrack;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;

import java.io.*;

final class AndroidMediaMusicTrack extends MusicTrack {
    MediaPlayer player;
    AndroidMediaMusic music;
    AndroidMediaMusicFactory factory;

    AndroidMediaMusicTrack(AndroidMediaMusicFactory factory) {
        this.factory = factory;
        if (factory.idlePlayers.size() == 0) {
            throw new RuntimeException("Music player pool empty");
        }
        MediaPlayer mediaPlayer = factory.idlePlayers.remove(0);
        factory.activePlayers.add(this);
        this.player = mediaPlayer;
    }

    @Override
    public void a(Music music) {
        this.music = (AndroidMediaMusic) music;
    }

    @Override
    public void a(boolean looping) {
        AssetFileDescriptor assetFileDescriptor = null;
        try {
            MediaPlayer mediaPlayer = this.player;
            mediaPlayer.reset();
            String convertedPath = FileHelper.convertAbstractPath(this.music.path);
            String assetPath = toAssetPath(convertedPath);
            if (this.music.path.startsWith("music") || assetPath.startsWith("music/")) {
                try {
                    assetFileDescriptor = this.factory.context.getAssets().openFd(assetPath);
                    mediaPlayer.setDataSource(
                            assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(),
                            assetFileDescriptor.getLength()
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                File file = new File(convertedPath);
                if (file.isFile()) {
                    mediaPlayer.setDataSource(file.getAbsolutePath());
                } else {
                    setDataSourceFromStream(mediaPlayer, convertedPath);
                }
            }
            if (looping) {
                mediaPlayer.setLooping(true);
            }
            mediaPlayer.setVolume(0.0f, 0.0f);
            mediaPlayer.setOnInfoListener(new AndroidMediaInfoListener(this));
            mediaPlayer.setOnPreparedListener(new AndroidMediaPlayerOnPreparedListener(this));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (assetFileDescriptor != null) {
                try {
                    assetFileDescriptor.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void a() {
        this.player.pause();
    }

    @Override
    public void b() {
        this.player.start();
    }

    @Override
    public boolean c() {
        return this.player.isPlaying();
    }

    @Override
    public void d() {
        if (this.player != null) {
            this.player.stop();
        }
    }

    @Override
    public void e() {
        if (this.player != null) {
            this.player.stop();
        }
        this.factory.activePlayers.remove(this);
        this.player = null;
    }

    @Override
    public void a(float volume) {
        this.player.setVolume(volume, volume);
    }

    private static String toAssetPath(String path) {
        return path.replace('\\', '/').replaceFirst("^assets/", "");
    }

    private void setDataSourceFromStream(MediaPlayer mediaPlayer, String path) throws IOException {
        AssetInputStream inputStream = FileHelper.openFileByPath(path);
        if (inputStream == null) {
            inputStream = FileHelper.openFileByPath(this.music.path);
        }
        if (inputStream == null) {
            throw new RuntimeException("openAssetSteam() null for '" + path + "'");
        }
        File tempFile = File.createTempFile("music", "ogg", this.factory.context.getCacheDir());
        GameEngine.log("Temp file needed for this music from zipped/abstract mod file");
        try {
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            try {
                copyStream(inputStream, outputStream);
            } finally {
                outputStream.close();
                inputStream.close();
            }
            FileInputStream fileInputStream = new FileInputStream(tempFile);
            try {
                mediaPlayer.setDataSource(fileInputStream.getFD(), 0L, fileInputStream.available());
            } finally {
                fileInputStream.close();
            }
        } finally {
            tempFile.delete();
        }
    }

    private static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }
}
