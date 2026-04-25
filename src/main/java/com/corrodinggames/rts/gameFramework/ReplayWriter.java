package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.bb */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/bb.class */
class ReplayWriter implements Runnable {
    volatile int b;
    int c;
    int d;
    int e;
    int f;
    int g;
    final /* synthetic */ ReplayEngine k;
    volatile boolean a = true;
    boolean h = false;
    public ConcurrentLinkedQueue i = new ConcurrentLinkedQueue();
    public long j = 0;

    ReplayWriter(ReplayEngine replayEngine) {
        this.k = replayEngine;
    }

    public synchronized void a(ReplayCommand replayCommand) {
        if (this.h) {
            GameEngine.isInSpace("Replay:addCommand skipped due to stopped recording");
        }
        this.i.add(replayCommand);
        this.f = replayCommand.a;
        if (replayCommand.e != null) {
            this.k.A++;
        }
        if (replayCommand.f != null) {
            this.k.B++;
        }
        notifyAll();
    }

    public synchronized void a() {
        this.a = false;
        GameEngine gameEngine = GameEngine.getInstance();
        ReplayEngine.a("stop requested at:" + gameEngine.currentTick);
        if (!this.k.P) {
            ReplayEngine.a("Replay stop: warning: active==false");
        }
        if (this.k.u) {
            ReplayEngine.a("Replay stop: warning: replaying==true");
        }
        this.b = gameEngine.currentTick;
        this.c = gameEngine.lastTick;
        this.d = this.k.A;
        this.e = this.k.B;
        if (this.b < this.f) {
            GameEngine.isInSpace("Replay: stoppedFrame<lastCommandFrame: " + this.b + "<" + this.f);
            this.b = this.f;
        }
        this.j = 0L;
        notifyAll();
    }

    private synchronized void b() {
        try {
            if (this.a) {
                wait();
            }
        } catch (InterruptedException e) {
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        while (this.a) {
            if (this.i.size() > 0) {
                ReplayCommand replayCommand = (ReplayCommand) this.i.remove();
                try {
                    if (replayCommand.e != null) {
                        this.k.J.startBlock("rc");
                        this.k.J.writeInt(replayCommand.a);
                        replayCommand.e.serializeCommand(this.k.J);
                        this.k.J.endBlock("rc");
                        this.g = replayCommand.a;
                    } else if (replayCommand.c != null) {
                        this.k.J.startBlock("cs");
                        this.k.J.writeInt(replayCommand.a);
                        this.k.J.writeLong(replayCommand.c.longValue());
                        this.k.J.endBlock("cs");
                    } else if (replayCommand.d != null) {
                        this.k.J.startBlock("wait");
                        this.k.J.writeInt(replayCommand.a);
                        this.k.J.endBlock("wait");
                        this.k.J.startBlock("es");
                        this.k.J.writeInt(replayCommand.a);
                        this.k.J.writeBytesWithLength(replayCommand.d);
                        this.k.J.endBlock("es");
                    } else if (replayCommand.f != null) {
                        this.k.J.startBlock("wait");
                        this.k.J.writeInt(replayCommand.a);
                        this.k.J.endBlock("wait");
                        this.k.J.startBlock("resync");
                        this.k.J.writeInt(replayCommand.a);
                        this.k.J.writeInt(replayCommand.h);
                        this.k.J.writeInt(replayCommand.i);
                        this.k.J.writeFloat(replayCommand.j);
                        this.k.J.writeFloat(replayCommand.k);
                        this.k.J.writeBytesWithLength(replayCommand.f);
                        this.k.J.endBlock("resync");
                    } else if (replayCommand.g != null) {
                        this.k.J.startBlock("chat");
                        this.k.J.writeInt(replayCommand.a);
                        this.k.J.writeInt(replayCommand.g.a);
                        this.k.J.writeStringNullable(replayCommand.g.b);
                        this.k.J.writeStringNullable(replayCommand.g.c);
                        this.k.J.endBlock("chat");
                    } else {
                        throw new RuntimeException("Unknown saved command");
                    }
                    if (this.j == 0 || this.j + 3000 < System.currentTimeMillis()) {
                        this.j = System.currentTimeMillis();
                        this.k.J.flushAllBuffers();
                    }
                } catch (IOException e) {
                    GameEngine gameEngine = GameEngine.getInstance();
                    GameEngine.log("Replay error", (Throwable) e);
                    gameEngine.gameUI.messageManager.addMessage(VariableScope.nullOrMissingString, "IO error recording replay, disabling record");
                    this.k.P = false;
                    this.h = true;
                    return;
                }
            }
            if (this.i.size() == 0) {
                b();
            }
        }
        try {
            this.k.J.startBlock("wait");
            this.k.J.writeInt(this.b);
            this.k.J.endBlock("wait");
            this.k.J.startBlock("end");
            this.k.J.endBlock("end");
            this.k.J.startBlock("endReplayMetaData");
            this.k.J.writeByte(0);
            this.k.J.writeInt(this.b);
            this.k.J.writeInt(this.c);
            this.k.J.writeInt(this.d);
            this.k.J.writeInt(this.e);
            this.k.J.writeStringUTF("{frames:" + this.b + ",time:" + this.c + ",commandCount:" + this.d + ",resyncCount:" + this.e + "}");
            this.k.J.endBlock("endReplayMetaData");
            this.k.J.flushAllBuffers();
            ReplayEngine.a("Background writer stopping");
            ReplayEngine.a("Remainding commands: " + this.i.size());
            ReplayEngine.a("last command: " + this.f);
            ReplayEngine.a("last command write: " + this.g);
            ReplayEngine.a("Commands issued: " + this.d);
            this.h = true;
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
}
