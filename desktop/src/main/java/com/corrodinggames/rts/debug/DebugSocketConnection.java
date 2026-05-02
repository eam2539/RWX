package com.corrodinggames.rts.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/b.class */
public class DebugSocketConnection implements Runnable {

    /* JADX INFO: renamed from: a */
    Socket socket;

    /* JADX INFO: renamed from: b */
    final /* synthetic */ DebugSocketServer server;

    public DebugSocketConnection(DebugSocketServer debugSocketServer, Socket socket) {
        this.server = debugSocketServer;
        this.socket = socket;
    }

    @Override // java.lang.Runnable
    public void run() {
        String line;
        try {
            try {
                PrintWriter printWriter = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                while (this.server.running && (line = bufferedReader.readLine()) != null) {
                    printWriter.print(DebugSocketServer.executeCommand(line));
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
