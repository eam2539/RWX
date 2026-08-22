package com.corrodinggames.rts.gameFramework.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.c.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/c/b.class */
public class DebugClientHandler implements Runnable {

    /* JADX INFO: renamed from: a */
    Socket socket;
    final /* synthetic */ DebugServer b;

    public DebugClientHandler(DebugServer debugServer, Socket socket) {
        this.b = debugServer;
        this.socket = socket;
    }

    @Override // java.lang.Runnable
    public void run() {
        String line;
        try {
            try {
                PrintWriter printWriter = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                while (this.b.i && (line = bufferedReader.readLine()) != null) {
                    printWriter.print(DebugServer.a(line));
                    printWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    this.socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        } finally {
            try {
                this.socket.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }
}
