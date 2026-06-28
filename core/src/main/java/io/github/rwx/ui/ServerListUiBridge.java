package io.github.rwx.ui;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.MasterServerClient;
import com.corrodinggames.rts.gameFramework.network.ServerInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class ServerListUiBridge {
    public static void refreshUI() {
        CoreUiEventQueue.requestOriginalRoomListRefresh();
    }

    public static ArrayList getServerList() {
        ArrayList<ServerInfo> arrayList;
        synchronized (MasterServerClient.serverListLock) {
            GameEngine gameEngine = GameEngine.getInstance();
            arrayList = new ArrayList();
            Iterator it = gameEngine.networkEngine.discoveredServerList.iterator();
            while (it.hasNext()) {
                arrayList.add((ServerInfo) it.next());
            }
            arrayList.sort(new Comparator<ServerInfo>() {
                public int a(ServerInfo serverInfo) {
                    if (serverInfo.hasUrl()) {
                        return 0;
                    }
                    if (serverInfo.isDedicatedServer && serverInfo.gameState.equals("chat")) {
                        return 1;
                    }
                    if (serverInfo.isLanServer) {
                        return 2;
                    }
                    if (serverInfo.gameState.equals("battleroom")) {
                        if (serverInfo.currentPlayers != -1 && serverInfo.currentPlayers < serverInfo.maxPlayers) {
                            if (serverInfo.isDedicatedServer) {
                                return serverInfo.currentPlayers != 0 ? 3 : 4;
                            }
                            if (serverInfo.isPortOpen && !serverInfo.isDedicatedServer) {
                                return 7;
                            }
                            return 9;
                        }
                        if (serverInfo.isDedicatedServer) {
                            return 6;
                        }
                        if (serverInfo.isPortOpen && !serverInfo.isDedicatedServer) {
                            return 8;
                        }
                        return 9;
                    }
                    return 99;
                }

                @Override
                public int compare(ServerInfo serverInfo, ServerInfo serverInfo2) {
                    Integer numValueOf = Integer.valueOf(a(serverInfo));
                    Integer numValueOf2 = Integer.valueOf(a(serverInfo2));
                    if (!serverInfo.isVersionCompatible()) {
                        numValueOf = Integer.valueOf(numValueOf.intValue() + 20);
                    }
                    if (!serverInfo2.isVersionCompatible()) {
                        numValueOf2 = Integer.valueOf(numValueOf2.intValue() + 20);
                    }
                    int iCompareTo = numValueOf.compareTo(numValueOf2);
                    if (iCompareTo != 0) {
                        return iCompareTo;
                    }
                    String mapPath = serverInfo.mapPath == null ? "" : serverInfo.mapPath;
                    String otherMapPath = serverInfo2.mapPath == null ? "" : serverInfo2.mapPath;
                    return mapPath.compareTo(otherMapPath);
                }
            });
        }
        return arrayList;
    }
}
