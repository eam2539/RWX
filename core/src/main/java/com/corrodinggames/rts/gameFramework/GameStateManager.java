package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.gameFramework.utility.CoreFileSystem;
import com.corrodinggames.rts.gameFramework.utility.Log;
import io.github.rwx.LegacyAssetBridge;

import java.io.*;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.be */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/be.class */
public class GameStateManager {
    static GameStateManager e = null;
    public boolean a = false;
    String b = "rtsSave";
    String c = "rtsSave.bak";
    public boolean d = false;
    HashMap<String, GameStateData> f = new HashMap();

    public void a() {
        if (GameEngine.isNonAndroidVersion || this.a) {
            return;
        }
        try {
            try {
                FileOutputStream fileOutputStreamB = new FileOutputStream(localFile(this.b));
                DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStreamB);
                a(dataOutputStream);
                dataOutputStream.close();
                fileOutputStreamB.close();
            } catch (NullPointerException e2) {
                throw new IOException("openFileOutput NullPointerException", e2);
            }
        } catch (FileNotFoundException e3) {
            Log.b("RustedWarfare", "file save error:", e3);
        } catch (IOException e4) {
            Log.b("RustedWarfare", "file save error:", e4);
        }
        if (this.d) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(CoreFileSystem.externalStorageDirectory(), this.c));
                DataOutputStream dataOutputStream2 = new DataOutputStream(fileOutputStream);
                a(dataOutputStream2);
                dataOutputStream2.close();
                fileOutputStream.close();
            } catch (IOException e5) {
                Log.b("RustedWarfare", "file read error:", e5);
            }
        }
    }

    public boolean a(DataOutputStream dataOutputStream) {
        if (GameEngine.isNonAndroidVersion) {
            return false;
        }
        try {
            dataOutputStream.writeInt(1);
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(this.f.size());
            for (GameStateData gameStateData : this.f.values()) {
                dataOutputStream.writeInt(0);
                dataOutputStream.writeInt(gameStateData.a);
                dataOutputStream.writeUTF(gameStateData.b);
                dataOutputStream.writeInt(gameStateData.c);
                dataOutputStream.writeBoolean(gameStateData.d);
                dataOutputStream.writeBoolean(gameStateData.e);
                dataOutputStream.writeBoolean(gameStateData.f);
                dataOutputStream.writeLong(gameStateData.g);
                dataOutputStream.writeInt(gameStateData.h);
            }
            dataOutputStream.flush();
            return true;
        } catch (IOException e2) {
            Log.b("RustedWarfare", "file save error:", e2);
            return false;
        }
    }

    public void b() {
        if (this.a) {
            return;
        }
        boolean zA = false;
        Log.d("RustedWarfare", "Trying to load from internal memory");
        try {
            FileInputStream fileInputStreamA = new FileInputStream(localFile(this.b));
            zA = a(new DataInputStream(fileInputStreamA));
            if (zA) {
                Log.d("RustedWarfare", "loaded from internal memory");
            }
            fileInputStreamA.close();
        } catch (IOException e2) {
            Log.b("RustedWarfare", "file read error:", e2);
        }
        if (this.d && !zA) {
            Log.d("RustedWarfare", "Trying to load from SD");
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(CoreFileSystem.externalStorageDirectory(), this.c));
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                if (a(dataInputStream)) {
                    Log.d("RustedWarfare", "loaded from SD");
                }
                dataInputStream.close();
                fileInputStream.close();
            } catch (IOException e3) {
                Log.b("RustedWarfare", "file read error:", e3);
            }
        }
    }

    public boolean a(DataInputStream dataInputStream) {
        try {
            int i = dataInputStream.readInt();
            if (i > 1) {
                Log.d("RustedWarfare", "Warning file is at version:" + i);
                return false;
            }
            dataInputStream.readInt();
            int i2 = dataInputStream.readInt();
            this.f.clear();
            for (int i3 = 0; i3 < i2; i3++) {
                GameStateData gameStateData = new GameStateData(this);
                dataInputStream.readInt();
                gameStateData.a = dataInputStream.readInt();
                String utf = dataInputStream.readUTF();
                if (utf.equals("maps/challenge/l030;Level 5.tmx")) {
                    Log.d("RustedWarfare", "converting:" + utf);
                    utf = "maps/challenge/l090;Level 7.tmx";
                }
                gameStateData.b = utf;
                gameStateData.c = dataInputStream.readInt();
                gameStateData.d = dataInputStream.readBoolean();
                gameStateData.e = dataInputStream.readBoolean();
                gameStateData.f = dataInputStream.readBoolean();
                gameStateData.g = dataInputStream.readLong();
                gameStateData.h = dataInputStream.readInt();
                this.f.put(a(gameStateData.b), gameStateData);
            }
            return true;
        } catch (IOException e2) {
            Log.b("RustedWarfare", "file read error:", e2);
            return false;
        }
    }

    public static GameStateManager c() {
        if (e == null) {
            e = new GameStateManager();
            if (!GameEngine.isNonAndroidVersion) {
                e.b();
            }
        }
        return e;
    }

    private GameStateManager() {
    }

    private File localFile(String name) {
        return new File(LegacyAssetBridge.localDir(), name);
    }

    public String a(String str) {
        Integer mapLevel = GameEngine.getMapLevel(str);
        if (mapLevel != null) {
            return GameEngine.getParentDirectory(str) + "/l" + mapLevel;
        }
        return str;
    }

    public GameStateData b(String str) {
        String strA = a(str);
        GameStateData gameStateData = (GameStateData) this.f.get(strA);
        Log.d("RustedWarfare", "StateEngine: get(" + str + ")=" + gameStateData + "  (key=" + strA + ")");
        if (gameStateData == null) {
            gameStateData = new GameStateData(this);
            gameStateData.a = 1;
            gameStateData.b = str;
            this.f.put(strA, gameStateData);
        }
        return gameStateData;
    }
}
