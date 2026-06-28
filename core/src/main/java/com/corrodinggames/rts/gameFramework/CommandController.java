package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.PlayerTeam;
import io.github.rwx.mod.NativeCommandQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/c.class */
public class CommandController {

    /* JADX INFO: renamed from: a */
    static final boolean DEBUG_TRACE_ENABLED = false;

    /* JADX INFO: renamed from: b */
    public ArrayList<Command> pendingCommands = new ArrayList();

    /* JADX INFO: renamed from: c */
    public ArrayList<Command> executedCommands = new ArrayList();

    /* JADX INFO: renamed from: d */
    public ArrayList<Command> queuedCommands = new ArrayList();

    /* JADX INFO: renamed from: e */
    static int logRateLimitCounter;

    /* JADX INFO: renamed from: a */
    public static void logWithRateLimit(String str) {
        logRateLimitCounter++;
        if (logRateLimitCounter == 5) {
            GameEngine.log("(Rate Limiting...)");
        }
        if (logRateLimitCounter >= 5) {
            return;
        }
        GameEngine.log(str);
    }

    /* JADX INFO: renamed from: a */
    public void clearAllCommands() {
        NativeCommandQueue.clear();
        this.pendingCommands.clear();
        this.executedCommands.clear();
        this.queuedCommands.clear();
    }

    /* JADX INFO: renamed from: b */
    public Command createCommand() {
        Command command = new Command(this);
        if (DEBUG_TRACE_ENABLED) {
            GameEngine.log("Tracing source");
            command.debugStackTrace = GameEngine.getStackTrace(new Exception("Test"));
        }
        return command;
    }

    /* JADX INFO: renamed from: a */
    public Command newCommandForTeam(PlayerTeam playerTeam) {
        return createCommandForTeam(playerTeam);
    }

    /* JADX INFO: renamed from: b */
    public Command createCommandForTeam(PlayerTeam playerTeam) {
        if (playerTeam == null) {
            throw new RuntimeException("team==null");
        }
        GameEngine gameEngine = GameEngine.getInstance();
        Command command = new Command(this);
        command.team = playerTeam;
        command.createdTick = gameEngine.gameTimeMillis;
        if (DEBUG_TRACE_ENABLED) {
            GameEngine.log("Tracing source");
            command.debugStackTrace = GameEngine.getStackTrace(new Exception("Test"));
        }
        if (!gameEngine.networkEngine.networkGameActive) {
            if (!command.prepareAndValidateCommand()) {
                GameEngine.logColored("Command failed prepareAndCheckOnServer()");
            }
            this.pendingCommands.add(command);
        } else {
            this.queuedCommands.add(command);
        }
        return command;
    }

    /* JADX INFO: renamed from: c */
    public void executeAllCommands() throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        logRateLimitCounter = 0;
        if (!gameEngine.networkEngine.networkGameActive) {
            executeLocalCommands();
        } else {
            executeNetworkCommands();
        }
    }

    /* JADX INFO: renamed from: d */
    public void executeLocalCommands() throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = gameEngine.currentTick;
        int i2 = 0;
        for (Command command : this.pendingCommands) {
            gameEngine.replayEngine.a(command, i);
            command.executeCommand();
            i2++;
        }
        this.pendingCommands.clear();
        if (i2 > 0) {
            gameEngine.replayEngine.c();
        }
    }

    /* JADX INFO: renamed from: e */
    public void executeNetworkCommands() throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = gameEngine.currentTick;
        int i2 = 0;
        Iterator it = this.pendingCommands.iterator();
        while (it.hasNext()) {
            Command command = (Command) it.next();
            if (command.scheduledTick == i) {
                gameEngine.replayEngine.a(command, i);
                command.executeCommand();
                it.remove();
                i2++;
            }
        }
        if (i2 > 0) {
            gameEngine.replayEngine.c();
        }
    }
}
