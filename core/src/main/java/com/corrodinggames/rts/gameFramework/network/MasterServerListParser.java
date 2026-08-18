package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.q */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/q.class */
class MasterServerListParser implements Runnable {

    /* JADX INFO: renamed from: a */
    Runnable completionCallback;

    MasterServerListParser(Runnable runnable) {
        this.completionCallback = runnable;
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        final GameEngine gameEngine = GameEngine.getInstance();
        GameEngine.log("LoadFromMasterServer", "Starting load");
        final int i = MasterServerClient.serverListSequence;
        MasterServerClient.serverListSequence++;
        try {
            new Timer().schedule(new ServerTimeoutTask(i), 5000L);
            ArrayList arrayList = new ArrayList(2);
            arrayList.add(new BasicNameValuePair("action", "list"));
            arrayList.add(new BasicNameValuePair("game_version", Integer.toString(gameEngine.getVersionCode(true))));
            arrayList.add(new BasicNameValuePair("game_version_beta", Utility.padString(gameEngine.isBetaOrPreview())));
            MasterServerClient.startParallelRequests((List) arrayList, false, new ServerResponseHandler() { // from class: com.corrodinggames.rts.gameFramework.j.q.1
                @Override
                    // com.corrodinggames.rts.gameFramework.network.ServerResponseHandler
                    /* JADX INFO: renamed from: a */
                void handleServerListResponse(BufferedReader bufferedReader, int i2, String str) throws IOException {
                    GameEngine gameEngine2 = GameEngine.getInstance();
                    String line = bufferedReader.readLine();
                    if (line == null || !line.contains("CORRODINGGAMES")) {
                        String str2 = i2 + ": Unknown header from the master server: '" + Utility.truncateToLength(line, 30) + "'";
                        GameEngine.log("LoadFromMasterServer", str2);
                        this.errorOrResponseText = str2;
                        try {
                            String str3 = VariableScope.nullOrMissingString + line + "\n";
                            GameEngine.log("----------- Full response ----------");
                            GameEngine.log("LoadFromMasterServer", "line:" + line);
                            while (true) {
                                String line2 = bufferedReader.readLine();
                                if (line2 != null) {
                                    GameEngine.log("LoadFromMasterServer", "line:" + line2);
                                    str3 = str3 + line2 + "\n";
                                } else {
                                    GameEngine.log("------------------------------------");
                                    MasterServerClient.lastMasterServerResponseLog = str3;
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        GameEngine.log("LoadFromMasterServer", i2 + ": Starting load");
                        int i3 = 0;
                        while (true) {
                            String line3 = bufferedReader.readLine();
                            if (line3 != null) {
                                String[] strArrSplit = line3.split(",", -1);
                                if (strArrSplit.length <= 21) {
                                    GameEngine.log("LoadFromMasterServer", i2 + ": columns.length too short at:" + strArrSplit.length);
                                    GameEngine.log("LoadFromMasterServer", i2 + ": short line is:" + line3);
                                } else {
                                    String str4 = strArrSplit[0];
                                    String str5 = strArrSplit[1];
                                    String str6 = strArrSplit[2];
                                    String str7 = strArrSplit[3];
                                    String str8 = strArrSplit[4];
                                    String str9 = strArrSplit[5];
                                    String str10 = strArrSplit[6];
                                    String str11 = strArrSplit[7];
                                    String str12 = strArrSplit[8];
                                    String str13 = strArrSplit[9];
                                    String str14 = strArrSplit[10];
                                    String str15 = strArrSplit[11];
                                    String str16 = strArrSplit[12];
                                    String str17 = strArrSplit[13];
                                    String str18 = strArrSplit[15];
                                    String str19 = strArrSplit[16];
                                    String str20 = strArrSplit[17];
                                    String str21 = strArrSplit[18];
                                    String str22 = strArrSplit[19];
                                    String str23 = strArrSplit[20];
                                    String str24 = strArrSplit[21];
                                    String strSubstring = null;
                                    String str25 = null;
                                    if (str7 != null && str7.startsWith("url:") && Boolean.parseBoolean(str20)) {
                                        strSubstring = str7.substring(4);
                                        str25 = str5;
                                        if (!Utility.sha256ShortHash(strSubstring + ";" + str25).equals(str8)) {
                                            GameEngine.log("Skipping " + str21);
                                        }
                                    }
                                    if (str21 == null || str21.trim().length() == 0) {
                                        str21 = str4;
                                    }
                                    try {
                                        String str26 = str21;
                                        synchronized (MasterServerClient.serverListLock) {
                                            ServerInfo serverInfoFindOrCreateServerById = MasterServerClient.findOrCreateServerById(str26);
                                            serverInfoFindOrCreateServerById.publicHost = str7;
                                            serverInfoFindOrCreateServerById.lanHost = str8;
                                            serverInfoFindOrCreateServerById.serverUrl = strSubstring;
                                            serverInfoFindOrCreateServerById.serverMessage = str25;
                                            serverInfoFindOrCreateServerById.port = Integer.valueOf(str9).intValue();
                                            serverInfoFindOrCreateServerById.isPortOpen = Boolean.parseBoolean(str10);
                                            serverInfoFindOrCreateServerById.requiresPassword = Boolean.parseBoolean(str12);
                                            serverInfoFindOrCreateServerById.gameVersionCodeText = str6;
                                            try {
                                                serverInfoFindOrCreateServerById.gameVersionCode = Integer.parseInt(serverInfoFindOrCreateServerById.gameVersionCodeText);
                                            } catch (NumberFormatException e2) {
                                                GameEngine.logColored("game_version_int:" + e2.getMessage());
                                            }
                                            serverInfoFindOrCreateServerById.createdBy = str11;
                                            serverInfoFindOrCreateServerById.mapPath = str13;
                                            serverInfoFindOrCreateServerById.gameMode = str14;
                                            serverInfoFindOrCreateServerById.gameState = str15;
                                            serverInfoFindOrCreateServerById.gameVersionString = str16;
                                            serverInfoFindOrCreateServerById.isLanServer = Boolean.parseBoolean(str17);
                                            serverInfoFindOrCreateServerById.currentPlayersText = str18;
                                            serverInfoFindOrCreateServerById.maxPlayersText = str19;
                                            serverInfoFindOrCreateServerById.hasMods = Boolean.parseBoolean(str22);
                                            if (VariableScope.nullOrMissingString.equals(str23)) {
                                                str23 = null;
                                            }
                                            serverInfoFindOrCreateServerById.modsRequired = str23;
                                            if (!str24.trim().equals(VariableScope.nullOrMissingString)) {
                                                serverInfoFindOrCreateServerById.gameVersionNumber = Integer.valueOf(str24).intValue();
                                            }
                                            try {
                                                serverInfoFindOrCreateServerById.currentPlayers = Integer.parseInt(serverInfoFindOrCreateServerById.currentPlayersText);
                                            } catch (NumberFormatException e3) {
                                                GameEngine.logColored("game_player_count_int:" + e3.getMessage());
                                            }
                                            try {
                                                serverInfoFindOrCreateServerById.maxPlayers = Integer.parseInt(serverInfoFindOrCreateServerById.maxPlayersText);
                                            } catch (NumberFormatException e4) {
                                                GameEngine.logColored("game_max_player_count_int:" + e4.getMessage());
                                            }
                                            serverInfoFindOrCreateServerById.isDedicatedServer = Boolean.parseBoolean(str20);
                                            if (serverInfoFindOrCreateServerById.lastSeenSequence < i) {
                                                serverInfoFindOrCreateServerById.lastSeenSequence = i;
                                            }
                                            if (MasterServerClient.findServerById(serverInfoFindOrCreateServerById.serverId) == null) {
                                                gameEngine2.networkEngine.discoveredServerList.add(serverInfoFindOrCreateServerById);
                                            }
                                            i3++;
                                        }
                                    } catch (NumberFormatException e5) {
                                        GameEngine.log("LoadFromMasterServer", i2 + ": failed to load server");
                                        e5.printStackTrace();
                                    }
                                }
                            } else {
                                GameEngine.log("LoadFromMasterServer", "[" + i2 + "]: Found " + i3 + " servers");
                                if (i3 == 0) {
                                    try {
                                        Thread.sleep(2000L);
                                    } catch (InterruptedException e6) {
                                        e6.printStackTrace();
                                    }
                                }
                                this.parsedSuccessfully = true;
                                MasterServerListParser.this.completionCallback.run();
                                try {
                                    Thread.sleep(2000L);
                                } catch (InterruptedException e7) {
                                    e7.printStackTrace();
                                }
                                MasterServerClient.removeStaleServers(i, i2);
                                GameEngine.log("LoadFromMasterServer", i2 + ": Completed load from master server without error");
                                return;
                            }
                        }
                    }
                }

                @Override
                    // com.corrodinggames.rts.gameFramework.network.ServerResponseHandler
                    /* JADX INFO: renamed from: a */
                void onComplete() {
                    if (!this.parsedSuccessfully) {
                        gameEngine.networkEngine.masterServerErrorMessage = this.errorOrResponseText;
                        MasterServerListParser.this.completionCallback.run();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            gameEngine.alert("Error getting game list from server", 1);
        }
    }
}
