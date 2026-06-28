package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.af */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/af.class */
final class UdpDiscoveryHandler implements Runnable {

    /* JADX INFO: renamed from: a */
    boolean isRunning;

    /* JADX INFO: renamed from: b */
    DatagramSocket datagramSocket;

    /* JADX INFO: renamed from: c */
    Timer timer;

    /* JADX INFO: renamed from: d */
    /* synthetic */ NetworkEngine networkEngine;

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.networkEngine.d("starting socket for broadcast..");
            this.datagramSocket = new DatagramSocket((SocketAddress) null);
            this.datagramSocket.setReuseAddress(true);
            this.datagramSocket.bind(new InetSocketAddress(this.networkEngine.udpPort));
            this.networkEngine.d("reading..");
            byte[] bArr = new byte[1500];
            DatagramPacket datagramPacket = new DatagramPacket(bArr, bArr.length);
            TimerTask timerTask = new TimerTask() { // from class: com.corrodinggames.rts.gameFramework.j.af.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (!UdpDiscoveryHandler.this.networkEngine.isServer) {
                        UdpDiscoveryHandler.this.a();
                    }
                }
            };
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(timerTask, 20L, 5000L);
            while (this.isRunning) {
                this.datagramSocket.receive(datagramPacket);
                String str = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                this.networkEngine.d("accepted udp socket..");
                GameInputStream gameInputStream = new GameInputStream(str);
                if (!gameInputStream.readUTF().equals("com.corrodinggames.rts")) {
                    this.networkEngine.d("ignoring udp packet: MAGIC_GAME_ID doesn't match");
                } else {
                    int i = gameInputStream.readInt();
                    gameInputStream.readInt();
                    String utf = gameInputStream.readUTF();
                    if (utf.equals("ping")) {
                        this.networkEngine.d("got ping");
                        if (this.networkEngine.isServer) {
                            GameOutputStream gameOutputStream = new GameOutputStream();
                            gameOutputStream.writeStringUTF("com.corrodinggames.rts");
                            gameOutputStream.writeInt(this.networkEngine.e);
                            gameOutputStream.writeInt(0);
                            gameOutputStream.writeStringUTF("pong");
                            gameOutputStream.writeInt(this.networkEngine.m);
                            String bufferAsString = gameOutputStream.getBufferAsString();
                            this.datagramSocket.send(new DatagramPacket(bufferAsString.getBytes(), bufferAsString.length(), datagramPacket.getAddress(), this.networkEngine.udpPort));
                        } else {
                            this.networkEngine.d("not server");
                        }
                    } else if (!utf.equals("pong")) {
                        this.networkEngine.d("got pong");
                        ServerInfo serverInfo = new ServerInfo();
                        serverInfo.isLanServer = true;
                        serverInfo.port = gameInputStream.readInt();
                        serverInfo.publicHost = datagramPacket.getAddress().toString();
                        serverInfo.gameVersionCodeText = VariableScope.nullOrMissingString + i;
                        this.networkEngine.a(serverInfo);
                    } else {
                        this.networkEngine.d("ignoring udp packet: unknown mode:" + utf);
                    }
                }
            }
        } catch (SocketException e) {
            if (this.isRunning) {
                throw new RuntimeException(e);
            }
            e.printStackTrace();
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public void a() {
        this.networkEngine.d("sending ping");
        if (this.datagramSocket == null) {
            this.networkEngine.d("failed to send a broadcast ping: datagramSocket is null");
            return;
        }
        InetAddress broadcastAddress = this.networkEngine.getUdpBroadcastAddress();
        if (broadcastAddress == null) {
            this.networkEngine.d("failed to send a broadcast ping: could not get a broadcast address");
            return;
        }
        try {
            GameOutputStream gameOutputStream = new GameOutputStream();
            gameOutputStream.writeStringUTF("com.corrodinggames.rts");
            gameOutputStream.writeInt(this.networkEngine.e);
            gameOutputStream.writeInt(0);
            gameOutputStream.writeStringUTF("ping");
            String bufferAsString = gameOutputStream.getBufferAsString();
            this.networkEngine.d("sending ping on :" + broadcastAddress.toString());
            this.datagramSocket.send(new DatagramPacket(bufferAsString.getBytes(), bufferAsString.length(), broadcastAddress, this.networkEngine.udpPort));
        } catch (IOException e) {
            e.printStackTrace();
            this.networkEngine.d("failed to send a broadcast ping, IOException");
        }
    }

    public void b() {
        this.isRunning = false;
        if (this.datagramSocket != null) {
            this.datagramSocket.close();
        }
        if (this.timer != null) {
            this.timer.cancel();
        }
    }
}
