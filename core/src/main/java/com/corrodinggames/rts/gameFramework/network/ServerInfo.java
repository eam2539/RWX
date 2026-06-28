package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.map.MapMetadata;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/g.class */
public class ServerInfo {

    /* JADX INFO: renamed from: a */
    public boolean isLanServer;

    /* JADX INFO: renamed from: b */
    public String serverId;

    /* JADX INFO: renamed from: c */
    public String publicHost;

    /* JADX INFO: renamed from: d */
    public String lanHost;

    /* JADX INFO: renamed from: e */
    public String serverUrl;

    /* JADX INFO: renamed from: f */
    public String serverMessage;

    /* JADX INFO: renamed from: g */
    public int port;

    /* JADX INFO: renamed from: h */
    public boolean isPortOpen;

    /* JADX INFO: renamed from: j */
    public String gameVersionCodeText;

    /* JADX INFO: renamed from: k */
    public String gameVersionString;

    /* JADX INFO: renamed from: l */
    public int gameVersionCode;

    /* JADX INFO: renamed from: m */
    public boolean requiresPassword;

    /* JADX INFO: renamed from: n */
    public String createdBy;

    /* JADX INFO: renamed from: o */
    public long firstSeenTimeMs;

    /* JADX INFO: renamed from: p */
    public int lastSeenSequence;

    /* JADX INFO: renamed from: q */
    public String mapPath;

    /* JADX INFO: renamed from: r */
    public String gameMode;

    /* JADX INFO: renamed from: s */
    public String gameState;

    /* JADX INFO: renamed from: t */
    public String currentPlayersText;

    /* JADX INFO: renamed from: u */
    public String maxPlayersText;

    /* JADX INFO: renamed from: x */
    public boolean isDedicatedServer;

    /* JADX INFO: renamed from: y */
    public boolean hasMods;

    /* JADX INFO: renamed from: z */
    public String modsRequired;

    /* JADX INFO: renamed from: A */
    public int gameVersionNumber;

    /* JADX INFO: renamed from: i */
    public long lastUpdateTimeMs = -1;

    /* JADX INFO: renamed from: v */
    public int currentPlayers = -1;

    /* JADX INFO: renamed from: w */
    public int maxPlayers = 8;

    /* JADX INFO: renamed from: a */
    public boolean isCurrentServer() {
        String str = GameEngine.getInstance().networkEngine.serverAddress;
        if (str != null && str.equals(this.serverId)) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: b */
    public String getInfoText() {
        String str;
        String url = getUrl();
        if (url != null) {
            String strReplace = this.serverMessage;
            if (strReplace != null) {
                strReplace = strReplace.replace("\\n", "\n");
            }
            return (VariableScope.nullOrMissingString + strReplace + "\n") + "Url: " + url + "\n";
        }
        String str2 = VariableScope.nullOrMissingString;
        if (this.isLanServer) {
            str2 = str2 + "Lan: " + this.lanHost + ":" + this.port + "\n";
        }
        String str3 = (str2 + "User: " + this.createdBy + "\n") + "Map: " + MapMetadata.getMapName(this.mapPath) + "\n";
        if (this.requiresPassword) {
            str3 = str3 + "Password Required\n";
        }
        if (!this.isPortOpen && !this.isLanServer) {
            str3 = str3 + "Port: not open (Connecting over the internet may fail)\n";
        }
        if ("ANY".equalsIgnoreCase(this.gameVersionString)) {
            str = str3 + "Version: " + this.gameVersionString + "\n";
        } else {
            str = str3 + "Version: v" + this.gameVersionString + (isVersionCompatible() ? VariableScope.nullOrMissingString : " (different game version!)") + "\n";
        }
        if (this.modsRequired != null && !this.modsRequired.equals(VariableScope.nullOrMissingString)) {
            str = str + "Mods Needed: " + this.modsRequired + "\n";
        }
        return str;
    }

    /* JADX INFO: renamed from: c */
    public String getUrl() {
        return this.serverUrl;
    }

    /* JADX INFO: renamed from: d */
    public boolean hasUrl() {
        return this.serverUrl != null;
    }

    /* JADX INFO: renamed from: e */
    public String getConnectDescriptor() {
        if (this.gameVersionNumber == 0) {
            return this.publicHost + ":" + this.port;
        }
        return "get|" + this.serverId.replace("|", ".") + "|" + this.gameVersionNumber + "|" + this.requiresPassword + "|" + this.port;
    }

    /* JADX INFO: renamed from: f */
    public String getLanAddress() {
        return this.lanHost + ":" + this.port;
    }

    /* JADX INFO: renamed from: g */
    public boolean isVersionCompatible() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.isDedicatedServer && "ANY".equals(this.gameVersionString)) {
            return true;
        }
        return (this.isDedicatedServer && this.gameVersionString != null && this.gameVersionString.contains("+") && gameEngine.getVersionCode(true) >= this.gameVersionCode) || gameEngine.getVersionCode(true) == this.gameVersionCode;
    }
}
