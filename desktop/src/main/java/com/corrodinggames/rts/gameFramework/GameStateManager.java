package com.corrodinggames.rts.gameFramework;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
    HashMap<String,GameStateData> f = new HashMap();

    public void a(Context context) {
        if (GameEngine.isNonAndroidVersion || this.a) {
            return;
        }
        try {
            if (context == null) {
                throw new IOException("context==null");
            }
            try {
                FileOutputStream fileOutputStreamB = context.b(this.b, 0);
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
                FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + this.c));
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

    public void b(Context context) {
        if (this.a) {
            return;
        }
        boolean zA = false;
        Log.d("RustedWarfare", "Trying to load from internal memory");
        try {
            FileInputStream fileInputStreamA = context.a(this.b);
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
                FileInputStream fileInputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + this.c));
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

    public static GameStateManager c(Context context) {
        if (e == null) {
            e = new GameStateManager(context);
            if (!GameEngine.isNonAndroidVersion) {
                e.b(context);
            }
        }
        return e;
    }

    private GameStateManager(Context context) {
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
