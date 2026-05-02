package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.IOException;
import java.net.Socket;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.an */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/an.class */
public class SocketConnector implements Runnable {

    /* JADX INFO: renamed from: a */
    String serverAddress;

    /* JADX INFO: renamed from: b */
    boolean useUdp;

    /* JADX INFO: renamed from: c */
    boolean isConnecting;

    /* JADX INFO: renamed from: d */
    Thread connectorThread;

    /* JADX INFO: renamed from: e */
    public String errorMessage;

    /* JADX INFO: renamed from: f */
    Runnable onComplete;

    /* JADX INFO: renamed from: g */
    public Socket connectedSocket;

    /* JADX INFO: renamed from: h */
    boolean cancelRequested = false;

    public SocketConnector(String str, boolean z, Runnable runnable) {
        this.serverAddress = str;
        this.useUdp = z;
        this.onComplete = runnable;
    }

    public boolean a() {
        if (!this.isConnecting) {
            return false;
        }
        this.cancelRequested = true;
        return true;
    }

    public void b() {
        this.isConnecting = true;
        this.connectorThread = new Thread(this);
        this.connectorThread.start();
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                this.connectedSocket = NetworkEngine.b(this.serverAddress, this.useUdp);
                this.isConnecting = false;
                if (!this.cancelRequested) {
                    this.onComplete.run();
                    return;
                }
                if (this.connectedSocket != null) {
                    try {
                        this.connectedSocket.close();
                        this.connectedSocket = null;
                        this.errorMessage = "cancelled";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NetworkException e2) {
                GameEngine.log("Cancelled connectSocketToServer");
                this.errorMessage = "CANCELLED";
                this.isConnecting = false;
                if (!this.cancelRequested) {
                    this.onComplete.run();
                    return;
                }
                if (this.connectedSocket != null) {
                    try {
                        this.connectedSocket.close();
                        this.connectedSocket = null;
                        this.errorMessage = "cancelled";
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (IOException e4) {
                this.errorMessage = e4.getMessage();
                e4.printStackTrace();
                this.isConnecting = false;
                if (!this.cancelRequested) {
                    this.onComplete.run();
                    return;
                }
                if (this.connectedSocket != null) {
                    try {
                        this.connectedSocket.close();
                        this.connectedSocket = null;
                        this.errorMessage = "cancelled";
                    } catch (IOException e5) {
                        e5.printStackTrace();
                    }
                }
            }
        } catch (Throwable th) {
            this.isConnecting = false;
            if (!this.cancelRequested) {
                this.onComplete.run();
            } else if (this.connectedSocket != null) {
                try {
                    this.connectedSocket.close();
                    this.connectedSocket = null;
                    this.errorMessage = "cancelled";
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            throw th;
        }
    }
}
