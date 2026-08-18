package com.corrodinggames.rts.gameFramework;

import android.content.Context;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.units.EditorOrBuilder;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.actions.AbstractUnitAction;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.network.ChecksumField;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.network.GameStateChecksum;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.ba */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/ba.class */
public class ReplayEngine {
    public static boolean b = true;
    public static boolean c = true;
    public static boolean d = false;
    public static boolean e = true;
    public static boolean f = false;
    public boolean h;
    int i;
    int j;
    boolean k;
    int l;
    boolean m;
    public int o;
    public int p;
    public int q;
    public String r;
    boolean s;
    public volatile boolean P;
    String t;
    boolean u;
    ReplayCommand w;
    ReplayCommand x;
    int y;
    int z;
    int A;
    int B;
    InputStream C;
    BufferedInputStream D;
    DataInputStream E;
    GameInputStream F;
    OutputStream G;
    BufferedOutputStream H;
    DataOutputStream I;
    GameOutputStream J;
    ReplayWriter K;
    Thread L;
    public boolean O;
    String a = "replays/";
    public GameStateChecksum g = new GameStateChecksum();
    public boolean n = false;
    public int v = 1;
    Object M = new Object();
    public boolean N = false;

    public static void a(String str) {
        GameEngine.log("Replay: " + str);
    }

    public static void b(String str) {
        GameEngine.logColored("Replay: " + str);
    }

    public static void a(String str, Exception exc) {
        GameEngine.log("Replay: " + str, (Throwable) exc);
    }

    public File a(String str, boolean z) {
        return FileHelper.getRWFile(str, this.a, z);
    }

    public void a(Context context) {
    }

    public void a() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.gameSpeed != 0.0f) {
            gameEngine.gameSpeed = 0.0f;
        } else {
            gameEngine.gameSpeed = 1.0f;
        }
    }

    public void b() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.gameSpeed == 1.0f) {
            gameEngine.gameSpeed = 2.0f;
            return;
        }
        if (gameEngine.gameSpeed == 2.0f) {
            gameEngine.gameSpeed = 4.0f;
            return;
        }
        if (gameEngine.gameSpeed == 4.0f) {
            gameEngine.gameSpeed = 8.0f;
            return;
        }
        if (gameEngine.gameSpeed == 8.0f) {
            gameEngine.gameSpeed = 16.0f;
            return;
        }
        if (gameEngine.gameSpeed == 16.0f) {
            gameEngine.gameSpeed = 32.0f;
            return;
        }
        if (gameEngine.gameSpeed == 32.0f) {
            gameEngine.gameSpeed = 64.0f;
        } else if (gameEngine.gameSpeed == 64.0f) {
            gameEngine.gameSpeed = 1.0f;
        } else {
            gameEngine.gameSpeed = 1.0f;
        }
    }

    public void a(int i, String str, String str2, int i2) {
        ReplayWriter replayWriter = this.K;
        if (this.P && !this.u) {
            if (str2.startsWith("-t ")) {
            }
            ReplayCommand replayCommand = new ReplayCommand();
            replayCommand.a = i2;
            replayCommand.g = new ChatMessage();
            replayCommand.g.a = i;
            replayCommand.g.b = str;
            replayCommand.g.c = str2;
            if (replayWriter == null) {
                GameEngine.logWarningAndStack("Failed to record chat message, replay might have already stopped");
            } else {
                replayWriter.a(replayCommand);
            }
        }
    }

    public void a(byte[] bArr, int i, int i2, int i3, float f2, float f3) {
        ReplayWriter replayWriter = this.K;
        if (this.P && !this.u) {
            ReplayCommand replayCommand = new ReplayCommand();
            replayCommand.a = i;
            replayCommand.f = bArr;
            replayCommand.h = i2;
            replayCommand.i = i3;
            replayCommand.j = f2;
            replayCommand.k = f3;
            if (replayWriter == null) {
                GameEngine.logWarningAndStack("Failed to save resync, replay might have already stopped");
            } else {
                replayWriter.a(replayCommand);
            }
        }
    }

    public void c() throws IOException {
        if (f) {
            d();
        }
    }

    public void a(Command command, int i) {
        ReplayWriter replayWriter = this.K;
        if (this.P && !this.u) {
            if (replayWriter == null) {
                GameEngine.logWarningAndStack("Failed to record command, replay might have already stopped");
                return;
            }
            ReplayCommand replayCommand = new ReplayCommand();
            replayCommand.e = command.cloneCommand();
            replayCommand.a = i;
            replayWriter.a(replayCommand);
            this.j++;
            if (this.j > 5) {
                this.j = 0;
                GameEngine gameEngine = GameEngine.getInstance();
                ReplayCommand replayCommand2 = new ReplayCommand();
                replayCommand2.c = f();
                replayCommand2.a = gameEngine.currentTick;
                replayWriter.a(replayCommand2);
            }
        }
    }

    public void d() throws IOException {
        if (this.P && !this.u) {
            this.g.computeChecksums();
            a(this.g, true);
        }
    }

    public void a(GameStateChecksum gameStateChecksum) throws IOException {
        a(gameStateChecksum, false);
    }

    public void a(GameStateChecksum gameStateChecksum, boolean z) throws IOException {
        if (this.P && !this.u) {
            GameEngine gameEngine = GameEngine.getInstance();
            ReplayCommand replayCommand = new ReplayCommand();
            GameOutputStream gameOutputStream = new GameOutputStream();
            int i = 0;
            if (z) {
                i = 0 + 1;
            }
            gameOutputStream.writeByte(i);
            gameOutputStream.writeInt(gameStateChecksum.fields.size());
            Iterator it = gameStateChecksum.fields.iterator();
            while (it.hasNext()) {
                gameOutputStream.writeLong(((ChecksumField) it.next()).value);
            }
            replayCommand.d = gameOutputStream.toByteArray();
            replayCommand.a = gameEngine.currentTick;
            this.K.a(replayCommand);
        }
    }

    /* JADX WARN: Finally extract failed */
    public void e() {
        synchronized (this.M) {
            try {
                try {
                    if (this.K != null) {
                        this.K.a();
                        try {
                            this.L.join();
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        this.P = false;
                        this.K = null;
                        this.L = null;
                    }
                    if (this.G != null) {
                        this.I.flush();
                        this.I.close();
                        this.H.flush();
                        this.H.close();
                        this.G.flush();
                        this.G.close();
                    }
                    this.G = null;
                    this.H = null;
                    this.I = null;
                    this.J = null;
                } catch (IOException e3) {
                    e3.printStackTrace();
                    this.G = null;
                    this.H = null;
                    this.I = null;
                    this.J = null;
                }
                this.s = false;
                this.P = false;
                this.u = false;
                this.t = null;
                this.i = 0;
                this.j = 0;
                this.k = false;
                this.l = 0;
                this.m = false;
                this.y = 0;
                this.v = 1;
                this.z = 0;
                this.A = 0;
                this.B = 0;
                this.o = -1;
                this.p = 0;
                this.q = -1;
                this.r = null;
                try {
                    try {
                        if (this.C != null) {
                            this.E.close();
                            this.D.close();
                            this.C.close();
                        }
                        this.C = null;
                        this.D = null;
                        this.E = null;
                        this.F = null;
                    } catch (IOException e4) {
                        e4.printStackTrace();
                        this.C = null;
                        this.D = null;
                        this.E = null;
                        this.F = null;
                    }
                } catch (Throwable th) {
                    this.C = null;
                    this.D = null;
                    this.E = null;
                    this.F = null;
                    throw th;
                }
            } catch (Throwable th2) {
                this.G = null;
                this.H = null;
                this.I = null;
                this.J = null;
                throw th2;
            }
        }
    }

    public long f() {
        long j = 0;
        for (GameObject gameObject : GameObject.fastGameObjectList) {
            if (gameObject instanceof OrderableUnit) {
                OrderableUnit orderableUnit = (OrderableUnit) gameObject;
                j = ((long) (((long) (((long) (j + (orderableUnit.posX * 1000.0f))) + (orderableUnit.posY * 1000.0f))) + (orderableUnit.currentHealth * 1.0f))) + orderableUnit.objectId;
            }
        }
        return j;
    }

    public void g() {
        if (!this.N) {
            e();
        }
    }

    public boolean loadReplay(String str)  {
        return a(str, a(str, false));
    }

    private void l() {
        for (int i = 0; i < PlayerTeam.TEAM_NEUTRAL; i++) {
            PlayerTeam playerTeamK = PlayerTeam.k(i);
            if (playerTeamK != null && (playerTeamK instanceof AIController)) {
                ((AIController) playerTeamK).canBuild = true;
            }
        }
    }

    public boolean a(String str, File file) {
        if (this.P) {
            if (this.u) {
                GameEngine.logColored("startReplayingFile: A replay is already playing");
            } else {
                GameEngine.logColored("startReplayingFile: A replay is already saving");
            }
        }
        e();
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.stopAndReset();
        gameEngine.networkEngine.q();
        this.w = null;
        this.s = false;
        this.P = true;
        this.u = true;
        this.t = str;
        try {
            if (file.isDirectory()) {
                GameEngine.log("File is a directory: " + file.getAbsolutePath());
                GameEngine.log("Cannot load replay: Target is a folder, instead of a file");
                gameEngine.alert("Cannot load replay: Target is a folder, instead of a file", 1);
                return false;
            }
            this.C = FileHelper.openFile(file);
            if (this.C == null) {
                GameEngine.log("Cannot load replay: Failed to read replay file");
                gameEngine.alert("Cannot load replay: Failed to read replay file", 1);
                return false;
            }
            this.D = new BufferedInputStream(this.C);
            this.E = new DataInputStream(this.D);
            this.F = new GameInputStream(this.E);
            String utf = this.F.readUTF();
            if (!utf.equals("rustedWarfareReplay")) {
                GameEngine.log("Header is not correct:" + utf);
                GameEngine.log("Cannot load replay: File is missing header (check if this file is a replay)");
                gameEngine.alert("Cannot load replay: File is missing header (check if this file is a replay)", 1);
                return false;
            }
            int i = this.F.readInt();
            int i2 = this.F.readInt();
            a("Loading save from version: " + i2);
            this.F.setProtocolVersion(i2);
            String utf2 = this.F.readUTF();
            if ((i2 != 96 || i != gameEngine.getVersionCode(true)) && !this.n) {
                String str2 = "Cannot load replay: This replay was recording with a different version: " + utf2;
                if (GameEngine.isPC()) {
                    str2 = str2 + " (You can use the beta tab in steam to switch to old versions)";
                }
                gameEngine.alert(str2, 1);
                a("Replay version: " + i2 + " (" + i + ")");
                a("GameSaver.thisSaveVersion: 96 (" + gameEngine.getVersionCode(true) + ")");
                if (!GameEngine.isOldReplayMode) {
                    this.P = false;
                    return false;
                }
            }
            this.q = i2;
            this.r = utf2;
            this.F.readBoolean();
            this.F.startBlockNamed("gamesave");
            this.O = false;
            this.N = true;
            a("Loading replay initial save");
            gameEngine.gameSaver.readSaveFromStream(this.F, false, false, false);
            this.N = false;
            this.F.d("gamesave");
            if (!this.O) {
                a("ReplayEngine: --- No game setup read ----");
                gameEngine.networkEngine.roomSettings.noNukes = true;
                gameEngine.maxUnitCap = gameEngine.settingsEngine.teamUnitCapHostedGame;
                gameEngine.currentUnitCap = gameEngine.maxUnitCap;
            }
            if (!this.h) {
                l();
            }
            a("--- Reply settings ---");
            a("Unit cap: " + gameEngine.maxUnitCap);
            a(gameEngine.networkEngine.roomSettings.getSettingsSummary());
            a("Starting frame:" + gameEngine.currentTick);
            if (!this.h) {
                for (int i3 = 0; i3 < PlayerTeam.TEAM_NEUTRAL; i3++) {
                    PlayerTeam playerTeamK = PlayerTeam.k(i3);
                    if (playerTeamK != null && playerTeamK.teamName != null) {
                        gameEngine.gameUI.messageManager.addMessage(VariableScope.nullOrMissingString, "Player '" + playerTeamK.teamName + "' playing as " + playerTeamK.getTeamColorDisplayName().toLowerCase() + " (team:" + playerTeamK.getTeamSlotLabel() + ")");
                    }
                }
            }
            if (GameEngine.isReplayDebugMode) {
                NetworkEngine.reportDesync("Warning: editor will desync checksums.");
                gameEngine.isGameStarted = true;
                gameEngine.isDebugTempMode = true;
                gameEngine.isTriggerDebugMode = true;
            }
            return true;
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

    public void a(boolean z) {
        if (GameEngine.isPCOrIOSVersion) {
            if (!GameEngine.isReplayRecordingEnabledOnPCOrIOS) {
                return;
            }
        } else if (!GameEngine.isReplayRecordingEnabledOnNonPC) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine.networkEngine.networkGameActive && !z && !this.N && gameEngine.settingsEngine.saveMultiplayerReplays) {
            d(gameEngine.getCurrentMapName() + " [v" + gameEngine.getVersionString() + "] (" + Utility.formatCurrentDate("d MMM yyyy HH.mm.ss") + ").replay");
        }
    }

    public void d(String str) {
        a("Recording replay to: " + str);
        if (this.P) {
            if (this.u) {
                b("startSaving: A replay is already playing");
            } else {
                b("startSaving: A replay is already saving");
            }
        }
        e();
        GameEngine gameEngine = GameEngine.getInstance();
        f = gameEngine.settingsEngine.replayTracing;
        if (f) {
            gameEngine.networkEngine.j("Warning traceChecksumsWriting is on. Large replay file size will be created.");
        }
        this.s = false;
        this.P = true;
        this.u = false;
        this.t = str;
        try {
            File fileA = a(str, true);
            this.G = FileHelper.openOutputStream(fileA, false);
            if (this.G == null) {
                b("Failed to create replay file at:" + fileA.getAbsolutePath());
                GameEngine.getInstance().alert("Failed to create replay file (Replay recording will be disabled)");
                e();
                return;
            }
            this.H = new BufferedOutputStream(this.G);
            this.I = new DataOutputStream(this.H);
            this.J = new GameOutputStream(this.I);
            this.J.writeStringUTF("rustedWarfareReplay");
            this.J.writeInt(gameEngine.getVersionCode(true));
            this.J.writeInt(96);
            this.J.writeStringUTF(gameEngine.getVersionString());
            this.J.writeBoolean(gameEngine.isDemo);
            this.J.startBlock("gamesave");
            gameEngine.gameSaver.writeSaveToStream(this.J);
            this.J.endBlock("gamesave");
            this.I.flush();
            this.K = new ReplayWriter(this);
            this.L = new Thread(this.K);
            this.L.start();
        } catch (IOException e2) {
            a("Failed to start recording replay", e2);
            GameEngine.getInstance().alert("Failed to start recording replay: " + e2.getMessage());
            e();
        } catch (Exception e3) {
            a("Failed to start recording replay (Non IOException)", e3);
            GameEngine.getInstance().alert("Failed to start recording replay (Non IOException): " + e3.getMessage());
            e();
        }
    }

    public boolean h() throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        String strStartBlockAndGetName = this.F.startBlockAndGetName();
        if ("rc".equals(strStartBlockAndGetName)) {
            this.y++;
            ReplayCommand replayCommand = new ReplayCommand();
            replayCommand.a = this.F.readInt();
            Command commandCreateCommand = gameEngine.commandController.createCommand();
            commandCreateCommand.deserializeCommand(this.F);
            commandCreateCommand.isReplayCommand = true;
            replayCommand.e = commandCreateCommand;
            this.F.d("rc");
            this.w = replayCommand;
            this.p++;
            this.o = replayCommand.a;
            if (c) {
                a("updateGameFrame: Command: " + commandCreateCommand.team.teamName + " (" + commandCreateCommand.team.teamId + ") count:" + commandCreateCommand.getAffectedUnitCount() + " id:" + this.y);
                if (commandCreateCommand.unitCommand != null) {
                    a("updateGameFrame: Waypoint: " + commandCreateCommand.unitCommand.getCommandType().name());
                    if (commandCreateCommand.unitCommand.getBuildUnitType() != null) {
                        a("updateGameFrame: Build Type: " + commandCreateCommand.unitCommand.getBuildUnitType().getUnitTypeDescriptionShort());
                    }
                }
                if (AbstractUnitAction.isActionIdSpecified(commandCreateCommand.actionId)) {
                    a("updateGameFrame: SpecialAction: " + commandCreateCommand.actionId.getId());
                }
                if (commandCreateCommand.attackMode != null) {
                    a("updateGameFrame: SetAttackMode: " + commandCreateCommand.attackMode);
                }
                if (commandCreateCommand.stopCurrentAction) {
                    a("updateGameFrame: stopOrUndo is set");
                }
                if (commandCreateCommand.isSystemAction) {
                    if (commandCreateCommand.gameSpeedChange != 0.0f) {
                        a("updateGameFrame: changeStepRate:" + commandCreateCommand.gameSpeedChange);
                    }
                    if (commandCreateCommand.systemActionType != 0) {
                        a("updateGameFrame: systemAction_action:" + commandCreateCommand.systemActionType);
                    }
                }
                a("updateGameFrame: ------");
                return true;
            }
            return true;
        }
        if ("wait".equals(strStartBlockAndGetName)) {
            ReplayCommand replayCommand2 = new ReplayCommand();
            replayCommand2.a = this.F.readInt();
            replayCommand2.b = true;
            this.w = replayCommand2;
            this.F.d("wait");
            return true;
        }
        if ("cs".equals(strStartBlockAndGetName)) {
            int i = this.F.readInt();
            long j = this.F.readLong();
            if (!this.n) {
                if (gameEngine.currentTick != i) {
                    GameEngine.log("replay:updateGameFrame", "expected:" + i + " got:" + gameEngine.currentTick);
                }
                if (f() != j) {
                    b("checksum: checksums don't match!!");
                    b("checksum: game frameNumber:" + gameEngine.currentTick);
                    b("checksum: Replay checksum:" + j);
                    b("checksum: Game checksum  :" + f());
                    this.l++;
                    if (!this.k) {
                        this.k = true;
                        gameEngine.gameUI.messageManager.addMessage(VariableScope.nullOrMissingString, "Error: This replay might be out of sync");
                    }
                } else {
                    a("checksum: checksums are matching frameNumber:" + gameEngine.currentTick);
                }
            }
            this.F.d("cs");
            return true;
        }
        if ("es".equals(strStartBlockAndGetName)) {
            int i2 = this.F.readInt();
            if (!this.n) {
                if (gameEngine.currentTick != i2) {
                    GameEngine.logColored("replay.updateGameFrame: expected:" + i2 + " got:" + gameEngine.currentTick);
                }
                GameInputStream gameInputStream = new GameInputStream(this.F.readBytesWithLength());
                boolean z = false;
                if (UnitPrice.a(gameInputStream.readByte(), 1)) {
                    z = true;
                }
                if (z) {
                    GameEngine.log("replay: -trace checksum-");
                } else {
                    GameEngine.log("replay: -long checksum-");
                }
                gameEngine.networkEngine.resetSyncChecksumState();
                gameInputStream.readInt();
                for (ChecksumField checksumField : gameEngine.networkEngine.stateChecksum.fields) {
                    long j2 = gameInputStream.readLong();
                    if (!this.m && j2 == checksumField.value) {
                        a("extraChecksum: " + checksumField.label + " Checksum [" + i2 + "]. " + j2 + " == " + checksumField.value + " (ok)");
                    }
                    if (j2 != checksumField.value) {
                        if (this.l < 150) {
                            b("extraChecksum: " + checksumField.label + " Checksum [" + i2 + "]. " + j2 + " != " + checksumField.value + " (failed)");
                        }
                        this.l++;
                    }
                }
            }
            this.m = true;
            this.F.d("es");
            return true;
        }
        if ("resync".equals(strStartBlockAndGetName)) {
            int i3 = this.F.readInt();
            GameEngine.log("Loading resync from replay");
            if (gameEngine.currentTick != i3) {
                GameEngine.log("replay:resync", "expected:" + i3 + " got:" + gameEngine.currentTick);
            }
            int i4 = this.F.readInt();
            int i5 = this.F.readInt();
            float f2 = this.F.readFloat();
            float f3 = this.F.readFloat();
            gameEngine.gameSaver.readSaveFromStream(new GameInputStream(this.F.readBytesWithLength()), true, true, true);
            l();
            gameEngine.currentTick = i4;
            gameEngine.gameTimeMillis = i5;
            gameEngine.networkEngine.stateChecksum.totalChecksum = 0L;
            if (f2 < 0.1d) {
                NetworkEngine.a("replay setCurrentStepRate:" + f2 + " is too small", true);
            }
            gameEngine.networkEngine.applyChangedSetup(f2, "replay");
            gameEngine.networkEngine.J = f3;
            this.F.d("resync");
            return true;
        }
        if ("chat".equals(strStartBlockAndGetName)) {
            ReplayCommand replayCommand3 = new ReplayCommand();
            replayCommand3.a = this.F.readInt();
            replayCommand3.g = new ChatMessage();
            replayCommand3.g.a = this.F.readInt();
            replayCommand3.g.b = this.F.readNullableString();
            replayCommand3.g.c = this.F.readNullableString();
            this.w = replayCommand3;
            this.F.d("chat");
            return true;
        }
        if ("end".equals(strStartBlockAndGetName)) {
            GameEngine.log("replay:updateGameFrame", "end of replay block found");
            gameEngine.gameUI.messageManager.addMessage(VariableScope.nullOrMissingString, "Replay has ended");
            if (!gameEngine.isGameStarted) {
                this.s = true;
                gameEngine.gameSpeed = 0.25f;
                GameEngine.getInstance().gameUI.startGameEndSequence();
            } else {
                this.s = false;
                this.P = false;
                this.u = false;
                EditorOrBuilder editorOrBuilder = gameEngine.gameUI.getEditorOrBuilder();
                if (editorOrBuilder != null) {
                    gameEngine.playerTeam = editorOrBuilder.team;
                }
            }
            this.F.d("end");
            GameEngine.log("number of replay commands issued:" + this.z);
            return false;
        }
        if ("endReplayMetaData".equals(strStartBlockAndGetName)) {
            this.F.d("endReplayMetaData");
            return true;
        }
        GameEngine.log("updateGameFrame", "Unknown command block:" + strStartBlockAndGetName);
        this.F.d(strStartBlockAndGetName);
        return true;
    }

    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.s || !this.P || !this.u) {
            return;
        }
        while (true) {
            if (this.w == null) {
                try {
                    if (!h()) {
                        return;
                    }
                } catch (IOException e2) {
                    GameEngine.log("updateGameFrame", "IOException, read of replay?");
                    e2.printStackTrace();
                    gameEngine.gameSpeed = 0.25f;
                    if (!this.s && this.P) {
                        gameEngine.gameUI.messageManager.addMessage(VariableScope.nullOrMissingString, "Replay ended (unexpected)");
                    }
                    this.s = true;
                    return;
                }
            }
            if (this.w != null) {
                if (this.n) {
                    this.w = null;
                } else {
                    if (b && this.w != null && this.x != this.w) {
                        this.x = this.w;
                        GameEngine.log("replay: upcoming in " + (this.w.a - gameEngine.currentTick) + " command:" + (this.w.e != null));
                    }
                    if (this.w.b && this.z == 0) {
                        GameEngine.log("updateGameFrame: replay: Skipping wait on first resync without commands to avoid delay");
                        this.w = null;
                    } else if (gameEngine.currentTick >= this.w.a) {
                        if (this.w.e != null) {
                            if (gameEngine.currentTick > this.w.a) {
                                GameEngine.logColored("updateGameFrame: replay incorrect frameNumber, skipping command:" + gameEngine.currentTick + " vs " + this.w.a);
                            } else {
                                if (d) {
                                    if (this.w.e.sourceTeam == null) {
                                        GameEngine.log("Precommand Team: commandingPlayer==null");
                                        if (this.w.e.team != null) {
                                            GameEngine.log("Precommand Team id:" + this.w.e.team.teamId + " credits:" + this.w.e.team.credits);
                                        }
                                    } else {
                                        GameEngine.log("Precommand Team id:" + this.w.e.sourceTeam.teamId + " credits:" + this.w.e.sourceTeam.credits + " count:" + this.w.e.sourceTeam.getNonBuildingUnitCountIncludingQueued() + " max:" + this.w.e.sourceTeam.getUnitCap());
                                    }
                                }
                                if (this.w.e.isSystemAction && this.w.e.systemActionType != 0) {
                                    GameEngine.log("replay:issueCommand", "systemAction_action:" + this.w.e.systemActionType);
                                }
                                this.w.e.executeCommand();
                                if (d) {
                                    if (this.w.e.sourceTeam != null) {
                                        GameEngine.log("Postcommand credits:" + this.w.e.sourceTeam.credits + " count:" + this.w.e.sourceTeam.getNonBuildingUnitCountIncludingQueued() + " max:" + this.w.e.sourceTeam.getUnitCap());
                                    } else if (this.w.e.team != null) {
                                        GameEngine.log("Postcommand Team id:" + this.w.e.team.teamId + " credits:" + this.w.e.team.credits);
                                    }
                                }
                                this.z++;
                            }
                        } else if (this.w.g != null) {
                            ChatMessage chatMessage = this.w.g;
                            boolean z = false;
                            if (chatMessage.c == null) {
                                z = true;
                            } else {
                                if (chatMessage.c.startsWith("-i ")) {
                                    z = true;
                                }
                                if (chatMessage.c.equals("<All players ready>")) {
                                    z = true;
                                }
                                if (chatMessage.c.equals("--too many desync errors, suppressing output--")) {
                                    z = true;
                                }
                                if (chatMessage.c.startsWith("desync:")) {
                                    z = true;
                                }
                            }
                            if (!gameEngine.settingsEngine.replaysShowRecordedChat) {
                                z = true;
                            }
                            if (z) {
                                GameEngine.log("replay:updateGameFrame", "Skipping message: " + chatMessage.b + ":" + chatMessage.c);
                            } else {
                                GameEngine.log("replay:updateGameFrame", "message: " + chatMessage.b + ":" + chatMessage.c);
                                gameEngine.gameUI.messageManager.addMessage(chatMessage.b, chatMessage.c);
                            }
                        } else if (this.w.b) {
                            if (c) {
                            }
                        } else {
                            GameEngine.log("updateGameFrame", "error: lastReadCommand null action");
                        }
                        this.w = null;
                    }
                }
            }
            if (this.w != null) {
                return;
            }
        }
    }

    public void e(String str) {
        GameEngine.log("ReplayEngine deleteGame: " + str);
        String strMapPath = FileHelper.fixPath(str);
        if (strMapPath.contains("\\") || strMapPath.contains("/")) {
            GameEngine.log("Cannot get replay with path: " + str);
            return;
        }
        File fileA = a(str, true);
        GameEngine.log("ReplayEngine path: " + fileA.getAbsolutePath());
        if (!fileA.exists()) {
            GameEngine.log("ReplayEngine deleteGame: file doesn't exist");
        }
        if (!FileHelper.deleteDirectory(fileA)) {
            GameEngine.log("ReplayEngine deleteGame: failed to delete: " + fileA.getAbsolutePath());
        }
        File fileA2 = a(str + ".map", true);
        if (fileA2.exists()) {
            FileHelper.deleteDirectory(fileA2);
        }
    }

    public boolean i() {
        return this.P;
    }

    public boolean j() {
        return this.P && this.u;
    }

    public boolean k() {
        return this.P && !this.u;
    }
}
