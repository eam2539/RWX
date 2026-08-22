package com.corrodinggames.rts.gameFramework.debug;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.c.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/c/a.class */
public class DebugServer implements Runnable {
    public static float e;
    /* JADX INFO: renamed from: h */
    public ServerSocket serverSocket;
    public static boolean a = false;
    public static boolean b = false;
    public static boolean c = false;
    public static boolean d = false;
    public static boolean g = true;
    static ArrayList j = new ArrayList();
    boolean f = true;
    public boolean i = true;

    public static void a() {
        if (!a) {
            return;
        }
        GameEngine.log("-----");
        GameEngine.log("-----");
        GameEngine.log("----- Debug Active ----");
        GameEngine.log("-----");
        GameEngine.log("-----");
        GameEngine.isDebugServerActive = true;
        GameEngine.getInstance().refreshVersionName();
        new DebugServer().b();
    }

    public void b() {
        if (b) {
            a(5677, VariableScope.nullOrMissingString);
        }
        GameEngine.getInstance().recurringGameThreadTasks.a(new DebugUpdateTask(this));
    }

    public void a(int i, String str) {
        try {
            g = true;
            GameEngine.isAutomatedTestMode = true;
            GameEngine.log(VariableScope.nullOrMissingString);
            GameEngine.log("----- createDebugSocket ----");
            GameEngine.log("port: " + i);
            GameEngine.log("password: " + str);
            GameEngine.log("------------------");
            GameEngine.log(VariableScope.nullOrMissingString);
            if (i != -1) {
                this.serverSocket = new ServerSocket(i);
                new Thread(this).start();
            }
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    private DebugServer() {
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.i) {
            try {
                Socket socketAccept = this.serverSocket.accept();
                try {
                    socketAccept.setTcpNoDelay(true);
                    new Thread(new DebugClientHandler(this, socketAccept)).run();
                } catch (IOException e2) {
                    GameEngine.log("Got IOException on debug connection");
                    e2.printStackTrace();
                    throw new RuntimeException(e2);
                }
            } catch (IOException e3) {
                throw new RuntimeException(e3);
            }
        }
    }

    public static String a(String str) {
        int iIndexOf = str.indexOf(" ");
        if (iIndexOf == -1) {
            iIndexOf = str.length();
        }
        String lowerCase = str.substring(0, iIndexOf).toLowerCase(Locale.ENGLISH);
        if (iIndexOf != -1 && str.length() >= iIndexOf + 1) {
            str.substring(iIndexOf + 1).split(" ");
        }
        if (lowerCase.equalsIgnoreCase("ping")) {
            return "pong";
        }
        if (lowerCase.equalsIgnoreCase("script") || lowerCase.equalsIgnoreCase("function") || lowerCase.equalsIgnoreCase("functionNoTimeout")) {
            return "todo";
        }
        return "unknown command";
    }
}
