package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.gameFramework.GameEngine;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/d.class */
final class ReceiveWorker implements Runnable {

    /* JADX INFO: renamed from: a */
    Boolean isRunning;

    /* JADX INFO: renamed from: b */
    final /* synthetic */ NetworkConnection networkConnection;

    ReceiveWorker(NetworkConnection networkConnection) {
        this.networkConnection = networkConnection;
        this.isRunning = true;
    }

    @Override // java.lang.Runnable
    public void run() {
        String message;
        GameEngine.setupUncaughtExceptionHandler();
        Thread.currentThread().setName("ReceiveWorker-" + this.networkConnection.getDisplayIpAddress());
        try {
            readPacketsLoop();
        } catch (EOFException e) {
            this.networkConnection.logErrorWithException("network:ReceiveWorker: EOF reading packet", e);
        } catch (IOException e2) {
            if (!this.networkConnection.isDisconnecting) {
                e2.printStackTrace();
            }
            if (GameEngine.isIOSVersion && (e2 instanceof SocketException) && !this.networkConnection.isDisconnecting) {
                GameEngine gameEngine = GameEngine.getInstance();
                if (!gameEngine.networkEngine.isServer && gameEngine.networkEngine.gameHasBeenStarted && (message = e2.getMessage()) != null && message.contains("EBADF")) {
                    gameEngine.alert("Warning: This disconnect likely due to iOS removing sockets of background apps. Avoid minimising the game in multiplayer. Note: Games can FastArrayList rejoined.");
                }
            }
            this.networkConnection.logInfo("network:ReceiveWorker: " + e2.getMessage());
        } catch (OutOfMemoryError e3) {
            GameEngine.printStackTrace(e3);
            this.networkConnection.logInfo("network:ReceiveWorker OutOfMemoryError: " + e3.getMessage());
        } catch (ConfigParseException e) {
            throw new RuntimeException(e);
        }
        this.networkConnection.handleTimeoutDisconnect(true, false);
    }

    /* JADX INFO: renamed from: a */
    void readPacketsLoop() throws IOException, ConfigParseException {
        DataInputStream dataInputStream = new DataInputStream(this.networkConnection.socket.getInputStream());
        while (this.isRunning.booleanValue() && !this.networkConnection.isDisconnecting && !this.networkConnection.socket.isClosed()) {
            int i = dataInputStream.readInt();
            int i2 = dataInputStream.readInt();
            if (i > 20000000) {
                this.networkConnection.logDebug("readData(): new packet of type:" + i2 + " has size of:" + i);
            }
            if (i > 10000) {
                int i3 = 50000000;
                if (this.networkConnection.networkEngine.isServer) {
                    i3 = 1000000;
                }
                if (!this.networkConnection.allowLargeIncomingPackets) {
                    i3 = 10000;
                }
                if (i > i3) {
                    this.networkConnection.logDebug("Requested packet too large rejecting (max:" + i3 + ")");
                    return;
                }
            }
            if (i < 0) {
                this.networkConnection.logDebug("Requested packet negative size:" + i + " rejecting");
                return;
            }
            PacketData packetData = new PacketData(i2);
            packetData.bytes = new byte[i];
            this.networkConnection.bytesReadSoFar = 0;
            this.networkConnection.bytesReadTotalCurrentPacket = i;
            int i4 = 0;
            packetData.connection = this.networkConnection;
            while (i4 < i && !this.networkConnection.isDisconnecting) {
                int i5 = dataInputStream.read(packetData.bytes, i4, i - i4);
                if (i5 == -1) {
                    this.networkConnection.logDebug("we got to the end of the stream?!?");
                    return;
                }
                i4 += i5;
                this.networkConnection.bytesReadIterations++;
                this.networkConnection.bytesReadSoFar = i4;
            }
            this.networkConnection.bytesReadTotalCurrentPacket = 0;
            this.networkConnection.bytesReadSoFar = 0;
            if (!this.networkConnection.isDisconnecting) {
                if (packetData.packetType > 100) {
                    this.networkConnection.networkEngine.handlePreregisterInfo(packetData);
                } else {
                    this.networkConnection.networkEngine.recvQueue.add(packetData);
                }
            }
        }
    }
}
