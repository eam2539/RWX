package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.ByteArrayBuilder;
import net.rudp.ReliableSocket;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/e.class */
final class SendWorker implements Runnable {

    /* JADX INFO: renamed from: b */
    OutputStream socketOutputStream;

    /* JADX INFO: renamed from: c */
    BufferedOutputStream bufOut;

    /* JADX INFO: renamed from: d */
    DataOutputStream dataOut;

    /* JADX INFO: renamed from: f */
    final /* synthetic */ NetworkConnection networkConnection;

    /* JADX INFO: renamed from: a */
    Boolean isRunning = true;

    /* JADX INFO: renamed from: e */
    ByteArrayBuilder scratchBufferHelper = new ByteArrayBuilder();

    /* JADX INFO: renamed from: a */
    public synchronized void enqueueOutgoingPacket(PacketData packetData) {
        if (this.networkConnection.isDisconnecting) {
            return;
        }
        this.networkConnection.outboundQueue.add(packetData);
        notifyAll();
    }

    /* JADX INFO: renamed from: a */
    public synchronized void notifySendLoop() {
        notifyAll();
    }

    /* JADX INFO: renamed from: b */
    public synchronized void waitForPackets() {
        try {
            if (this.networkConnection.outboundQueue.isEmpty() && !this.networkConnection.isDisconnecting && !this.networkConnection.disconnectRequested) {
                wait(10000L);
            }
        } catch (InterruptedException e) {
        }
    }

    SendWorker(NetworkConnection networkConnection) throws IOException {
        this.networkConnection = networkConnection;
        this.socketOutputStream = networkConnection.socket.getOutputStream();
        this.bufOut = new BufferedOutputStream(this.socketOutputStream);
        this.dataOut = new DataOutputStream(this.bufOut);
    }


    @Override
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        Thread.currentThread().setName("SendWorker-" + this.networkConnection.getDisplayIpAddress());
        try {
            while (this.isRunning.booleanValue() && !this.networkConnection.isDisconnecting) {
                while (!this.networkConnection.outboundQueue.isEmpty() && !this.networkConnection.isDisconnecting) {
                    Object object;
                    Object object2;
                    PacketData au2 = (PacketData)this.networkConnection.outboundQueue.remove();
                    if (au2 instanceof SplitPacketData) {
                        object2 = (SplitPacketData)au2;
                        if (this.networkConnection.lastPacket == ((SplitPacketData)object2).originalPacket && this.networkConnection.optimizeSplitContinuation) {
                            object = new GameOutputStream();
                            ((GameOutputStream)object).writeInt(((SplitPacketData)object2).steamChannelId);
                            au2 = ((GameOutputStream)object).buildPacketData(176);
                        } else {
                            object = new GameOutputStream();
                            ((GameOutputStream)object).writeInt(((SplitPacketData)object2).steamChannelId);
                            ((GameOutputStream)object).writeInt(((SplitPacketData)object2).originalPacket.packetType);
                            ((GameOutputStream)object).writeBytesWithLength(((SplitPacketData)object2).originalPacket.bytes);
                            au2 = ((GameOutputStream)object).buildPacketData(175);
                        }
                        this.networkConnection.lastPacket = ((SplitPacketData)object2).originalPacket;
                    } else if (this.networkConnection.trackLastPacket) {
                        this.networkConnection.lastPacket = au2;
                    }
                    if (this.networkConnection.socket instanceof SteamSocket) {
                        object2 = (SteamSocket)this.networkConnection.socket;
                        ((SteamSocket)object2).sendPacket(au2);
                    } else if (this.networkConnection.socket instanceof ReliableSocket) {
                        boolean bl = false;
                        if (au2.bytes.length > 500) {
                            object = new ByteArrayBuilder(8 + au2.bytes.length);
                            bl = true;
                        } else {
                            object = this.scratchBufferHelper;
                            ((ByteArrayBuilder)object).a();
                        }
                        boolean bl2 = au2.isUrgent;
                        DataOutputStream dataOutputStream = new DataOutputStream((OutputStream)object);
                        dataOutputStream.writeInt(au2.bytes.length);
                        dataOutputStream.writeInt(au2.packetType);
                        dataOutputStream.write(au2.bytes);
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        ReliableSocket h2 = (ReliableSocket)this.networkConnection.socket;
                        h2.sendDataBytes(((ByteArrayBuilder)object).buffer, 0, ((ByteArrayBuilder)object).b(), bl2);
                        if (bl) {
                            ((ByteArrayBuilder)object).close();
                        }
                    } else {
                        this.dataOut.writeInt(au2.bytes.length);
                        this.dataOut.writeInt(au2.packetType);
                        this.dataOut.write(au2.bytes);
                        this.dataOut.flush();
                    }
                    if (au2.delayMillis == -1) continue;
                    try {
                        Thread.sleep(au2.delayMillis);
                    } catch (InterruptedException interruptedException) {}
                }
                if (this.networkConnection.disconnectRequested) {
                    this.networkConnection.isDisconnecting = true;
                    break;
                }
                this.waitForPackets();
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
            GameEngine.log("network:SendWorker", iOException.getMessage());
        }
        this.networkConnection.handleTimeoutDisconnect(false, true);
    }}
