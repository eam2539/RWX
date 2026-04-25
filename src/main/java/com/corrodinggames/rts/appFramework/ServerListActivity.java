package com.corrodinggames.rts.appFramework;

import android.os.Handler;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.network.MasterServerClient;
import com.corrodinggames.rts.gameFramework.network.ServerInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/p.class */
public class ServerListActivity extends TaskQueueActivity {

    /* JADX INFO: renamed from: c */
    static ServerListActivity instance;

    /* JADX INFO: renamed from: d */
     Handler uiHandler;

    /* JADX INFO: renamed from: e */
    private Runnable updateUIRunnable;

    /* JADX INFO: renamed from: l */
    public static void refreshUI() {
        if (instance != null) {
            instance.uiHandler.a(instance.updateUIRunnable);
        }
    }

    /* JADX INFO: renamed from: m */
    public static ArrayList getServerList() {
        ArrayList<ServerInfo> arrayList;
        synchronized (MasterServerClient.serverListLock) {
            GameEngine gameEngine = GameEngine.getInstance();
            arrayList = new ArrayList();
            Iterator it = gameEngine.networkEngine.discoveredServerList.iterator();
            while (it.hasNext()) {
                arrayList.add((ServerInfo) it.next());
            }
            arrayList.sort(new Comparator<ServerInfo>() { // from class: com.corrodinggames.rts.appFramework.p.1
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
                                if (serverInfo.currentPlayers != 0) {
                                    return 3;
                                }
                                return 4;
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

                @Override // java.util.Comparator
                /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
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
                    return serverInfo.mapPath.compareTo(serverInfo2.mapPath);
                }
            });
        }
        return arrayList;
    }
}
