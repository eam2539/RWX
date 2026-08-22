package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bb */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bb.class */
class ReplayWriter implements Runnable {

    /* JADX INFO: renamed from: b */
    volatile int stopFrame;

    /* JADX INFO: renamed from: c */
    int stopGameTime;

    int d;
    int e;

    /* JADX INFO: renamed from: f */
    int lastCommandFrame;

    /* JADX INFO: renamed from: g */
    int lastWrittenFrame;

    final /* synthetic */ ReplayEngine k;

    /* JADX INFO: renamed from: a */
    volatile boolean running = true;

    /* JADX INFO: renamed from: h */
    boolean stopped = false;

    /* JADX INFO: renamed from: i */
    public ConcurrentLinkedQueue commandQueue = new ConcurrentLinkedQueue();

    /* JADX INFO: renamed from: j */
    public long lastFlushTime = 0;

    ReplayWriter(ReplayEngine replayEngine) {
        this.k = replayEngine;
    }

    public synchronized void a(ReplayCommand replayCommand) {
        if (this.stopped) {
            GameEngine.log("Replay:addCommand skipped due to stopped recording");
        }
        this.commandQueue.add(replayCommand);
        this.lastCommandFrame = replayCommand.tick;
        if (replayCommand.command != null) {
            this.k.recordedCommandCount++;
        }
        if (replayCommand.resyncData != null) {
            this.k.recordedResyncCount++;
        }
        notifyAll();
    }

    public synchronized void a() {
        this.running = false;
        GameEngine gameEngine = GameEngine.getInstance();
        ReplayEngine.a("stop requested at:" + gameEngine.currentTick);
        if (!this.k.P) {
            ReplayEngine.a("Replay stop: warning: active==false");
        }
        if (this.k.isReplaying) {
            ReplayEngine.a("Replay stop: warning: replaying==true");
        }
        this.stopFrame = gameEngine.currentTick;
        this.stopGameTime = gameEngine.gameTimeMillis;
        this.d = this.k.recordedCommandCount;
        this.e = this.k.recordedResyncCount;
        if (this.stopFrame < this.lastCommandFrame) {
            GameEngine.log("Replay: stoppedFrame<lastCommandFrame: " + this.stopFrame + "<" + this.lastCommandFrame);
            this.stopFrame = this.lastCommandFrame;
        }
        this.lastFlushTime = 0L;
        notifyAll();
    }

    private synchronized void b() {
        try {
            if (this.running) {
                wait();
            }
        } catch (InterruptedException e) {
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        while (this.running) {
            if (this.commandQueue.size() > 0) {
                ReplayCommand replayCommand = (ReplayCommand) this.commandQueue.remove();
                try {
                    if (replayCommand.command != null) {
                        this.k.gameOutputStream.startBlock("rc");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        replayCommand.command.serializeCommand(this.k.gameOutputStream);
                        this.k.gameOutputStream.endBlock("rc");
                        this.lastWrittenFrame = replayCommand.tick;
                    } else if (replayCommand.checksum != null) {
                        this.k.gameOutputStream.startBlock("cs");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        this.k.gameOutputStream.writeLong(replayCommand.checksum.longValue());
                        this.k.gameOutputStream.endBlock("cs");
                    } else if (replayCommand.checksumData != null) {
                        this.k.gameOutputStream.startBlock("wait");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        this.k.gameOutputStream.endBlock("wait");
                        this.k.gameOutputStream.startBlock("es");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        this.k.gameOutputStream.writeBytesWithLength(replayCommand.checksumData);
                        this.k.gameOutputStream.endBlock("es");
                    } else if (replayCommand.resyncData != null) {
                        this.k.gameOutputStream.startBlock("wait");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        this.k.gameOutputStream.endBlock("wait");
                        this.k.gameOutputStream.startBlock("resync");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        this.k.gameOutputStream.writeInt(replayCommand.resyncTick);
                        this.k.gameOutputStream.writeInt(replayCommand.resyncGameTimeMillis);
                        this.k.gameOutputStream.writeFloat(replayCommand.resyncStepRate);
                        this.k.gameOutputStream.writeFloat(replayCommand.k);
                        this.k.gameOutputStream.writeBytesWithLength(replayCommand.resyncData);
                        this.k.gameOutputStream.endBlock("resync");
                    } else if (replayCommand.chatMessage != null) {
                        this.k.gameOutputStream.startBlock("chat");
                        this.k.gameOutputStream.writeInt(replayCommand.tick);
                        this.k.gameOutputStream.writeInt(replayCommand.chatMessage.a);
                        this.k.gameOutputStream.writeStringNullable(replayCommand.chatMessage.b);
                        this.k.gameOutputStream.writeStringNullable(replayCommand.chatMessage.c);
                        this.k.gameOutputStream.endBlock("chat");
                    } else {
                        throw new RuntimeException("Unknown saved command");
                    }
                    if (this.lastFlushTime == 0 || this.lastFlushTime + 3000 < System.currentTimeMillis()) {
                        this.lastFlushTime = System.currentTimeMillis();
                        this.k.gameOutputStream.flushAllBuffers();
                    }
                } catch (IOException e) {
                    GameEngine gameEngine = GameEngine.getInstance();
                    GameEngine.log("Replay error", (Throwable) e);
                    gameEngine.gameUI.messageManager.addMessage(VariableScope.nullOrMissingString, "IO error recording replay, disabling record");
                    this.k.P = false;
                    this.stopped = true;
                    return;
                }
            }
            if (this.commandQueue.size() == 0) {
                b();
            }
        }
        try {
            this.k.gameOutputStream.startBlock("wait");
            this.k.gameOutputStream.writeInt(this.stopFrame);
            this.k.gameOutputStream.endBlock("wait");
            this.k.gameOutputStream.startBlock("end");
            this.k.gameOutputStream.endBlock("end");
            this.k.gameOutputStream.startBlock("endReplayMetaData");
            this.k.gameOutputStream.writeByte(0);
            this.k.gameOutputStream.writeInt(this.stopFrame);
            this.k.gameOutputStream.writeInt(this.stopGameTime);
            this.k.gameOutputStream.writeInt(this.d);
            this.k.gameOutputStream.writeInt(this.e);
            this.k.gameOutputStream.writeStringUTF("{frames:" + this.stopFrame + ",time:" + this.stopGameTime + ",commandCount:" + this.d + ",resyncCount:" + this.e + "}");
            this.k.gameOutputStream.endBlock("endReplayMetaData");
            this.k.gameOutputStream.flushAllBuffers();
            ReplayEngine.a("Background writer stopping");
            ReplayEngine.a("Remainding commands: " + this.commandQueue.size());
            ReplayEngine.a("last command: " + this.lastCommandFrame);
            ReplayEngine.a("last command write: " + this.lastWrittenFrame);
            ReplayEngine.a("Commands issued: " + this.d);
            this.stopped = true;
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
}
