package com.corrodinggames.rts.gameFramework.network;

import com.corrodinggames.rts.appFramework.ServerListActivity;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.local.Locale;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.j.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/j/n.class */
public class MasterServerClient {

    /* JADX INFO: renamed from: e */
    static int serverListSequence;

    /* JADX INFO: renamed from: g */
    public static String lastMasterServerResponseLog;

    /* JADX INFO: renamed from: a */
    public static boolean ENABLE_STATUS_LOGGING = true;

    /* JADX INFO: renamed from: b */
    public static boolean ENABLE_CONSOLE_LOGGING = true;

    /* JADX INFO: renamed from: c */
    public static String[] MASTER_SERVER_URLS = {"http://gs1.corrodinggames.com/masterserver/1.4", "http://gs4.corrodinggames.net/masterserver/1.4"};

    /* JADX INFO: renamed from: d */
    public static HttpClientManager httpClientManager = new HttpClientManager();

    /* JADX INFO: renamed from: f */
    public static Object serverListLock = new Object();

    /* JADX INFO: renamed from: a */
    public static void logInfo(String str) {
        if (ENABLE_CONSOLE_LOGGING) {
            GameEngine.log(str);
        }
    }

    /* JADX INFO: renamed from: a */
    static void startParallelRequests(List list, boolean z, ServerResponseHandler serverResponseHandler) {
        startParallelRequestsAcrossUrls(list, z, serverResponseHandler, MASTER_SERVER_URLS);
    }

    /* JADX INFO: renamed from: a */
    static void startParallelRequestsAcrossUrls(List list, boolean z, ServerResponseHandler serverResponseHandler, String[] strArr) {
        serverResponseHandler.pendingRequests = strArr.length;
        int i = 0;
        for (String str : strArr) {
            i++;
            new Thread(new MasterServerRequestTask(list, serverResponseHandler, str, z, i)).start();
            if (ENABLE_STATUS_LOGGING) {
                GameEngine.log("LoadFromMasterServer", i + ": Started RequestsParallelRunnable thread");
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public static String getParam(List list, String str) {
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                NameValuePair nameValuePair = (NameValuePair) it.next();
                if (str.equals(nameValuePair.getName())) {
                    return nameValuePair.getValue();
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public static BufferedReader requestMasterServerResponse(List list) throws IOException, ExecutionException, InterruptedException {
        return requestFromMasterServers(list, true, MASTER_SERVER_URLS, 10, true);
    }

    /* JADX INFO: renamed from: a */
    public static BufferedReader requestMasterServerResponseWithTimeout(List list, int i) throws IOException, ExecutionException, InterruptedException {
        return requestFromMasterServers(list, true, MASTER_SERVER_URLS, i, true);
    }

    /* JADX INFO: renamed from: a */
    public static BufferedReader requestFromMasterServers(final List list, final boolean z, String[] strArr, int i, final boolean z2) throws IOException, ExecutionException, InterruptedException {
        Future futurePoll = null;
        String param = getParam(list, "action");
        ExecutorService executorServiceNewFixedThreadPool = Executors.newFixedThreadPool(strArr.length);
        try {
            ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorServiceNewFixedThreadPool);
            ArrayList arrayList = new ArrayList();
            for (final String str : strArr) {
                arrayList.add(executorCompletionService.submit(new Callable() { // from class: com.corrodinggames.rts.gameFramework.j.n.1
                    @Override // java.util.concurrent.Callable
                    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
                    public HttpResponseData call() {
                        try {
                            MasterServerClient.logInfo("Running doSingleRequest:" + str);
                            return MasterServerClient.doSingleRequest(list, str, z);
                        } catch (Exception e) {
                            GameEngine.log("Error on doSingleRequest:" + str + " - " + e.getMessage());
                            if (z2) {
                                e.printStackTrace();
                                return null;
                            }
                            return null;
                        }
                    }
                }));
            }
            int length = strArr.length;
            HttpResponseData httpResponseData = null;
            HttpResponseData httpResponseData2 = null;
            HttpResponseData httpResponseData3 = null;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                try {
                    futurePoll = executorCompletionService.poll(10L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                }
                if (futurePoll == null) {
                    GameEngine.logColored("MULTI_MASTERSERVERS: poll timed out (" + param + ")");
                    break;
                }
                HttpResponseData httpResponseData4 = (HttpResponseData) futurePoll.get();
                if (httpResponseData4 != null) {
                    httpResponseData = httpResponseData4;
                    if (httpResponseData4.isValidResponse) {
                        if (httpResponseData4.hasErrorMessage) {
                            httpResponseData3 = httpResponseData4;
                        } else {
                            httpResponseData2 = httpResponseData4;
                            break;
                        }
                    }
                }
                i2++;
            }
            if (httpResponseData2 == null && httpResponseData3 != null) {
                GameEngine.logColored("All masterserver results included an error message (" + param + ")");
                httpResponseData2 = httpResponseData3;
            }
            if (httpResponseData2 == null) {
                GameEngine.logColored("No valid result found on any masterserver (" + param + ")");
                httpResponseData2 = httpResponseData;
            }
            if (httpResponseData2 != null) {
                BufferedReader bufferedReader = httpResponseData2.bufferedReader;
                executorServiceNewFixedThreadPool.shutdown();
                return bufferedReader;
            }
            throw new IOException("No results found (" + param + ")");
        } catch (Throwable th) {
            executorServiceNewFixedThreadPool.shutdown();
            throw th;
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    /* JADX INFO: renamed from: a */
    public static HttpResponseData doSingleRequest(List list, String str, boolean z) throws IOException {
        HttpUriRequest httpGet;
        String str2;
        HttpResponse httpResponseExecute;
        String param = getParam(list, "action");
        long jA = PerformanceProfiler.a();
        String str3 = str + "/interface";
        if (z) {
            HttpPost httpPost = new HttpPost(str3);
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            httpGet = httpPost;
        } else {
            str3 = str3 + "?" + URLEncodedUtils.format(list, "utf-8");
            httpGet = new HttpGet(str3);
        }
        if (GameEngine.isDedicatedServer()) {
            str2 = "rw server";
        } else {
            str2 = "rw " + (GameEngine.isPC() ? "pc" : "android");
        }
        String language = Locale.getLanguage();
        GameEngine gameEngine = GameEngine.getInstance();
        if (gameEngine != null) {
            str2 = str2 + " " + gameEngine.getVersionCode(true) + " " + language;
        }
        httpGet.setHeader("User-Agent", str2);
        httpGet.setHeader("Language", language);
        HttpClient androidHttpClient = httpClientManager.getAndroidHttpClient();
        try {
            httpResponseExecute = androidHttpClient.execute(httpGet);
        } catch (NullPointerException e) {
            GameEngine.logColored("doRequest: httpclient.execute threw NullPointerException, running workaround");
            androidHttpClient = httpClientManager.getDefaultHttpClient();
            httpResponseExecute = androidHttpClient.execute(httpGet);
        }
        float fA = PerformanceProfiler.a(jA);
        HttpEntity entity = httpResponseExecute.getEntity();
        InputStream content = entity.getContent();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[16384];
        while (true) {
            int i = content.read(bArr, 0, bArr.length);
            if (i == -1) {
                break;
            }
            byteArrayOutputStream.write(bArr, 0, i);
        }
        byteArrayOutputStream.flush();
        content.close();
        entity.consumeContent();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        HttpResponseData httpResponseData = new HttpResponseData();
        String strExtractFirstLine = extractFirstLine(byteArray);
        httpResponseData.isValidResponse = strExtractFirstLine.startsWith("CORRODINGGAMES");
        httpResponseData.hasErrorMessage = strExtractFirstLine.contains("[FAILED]");
        if (!httpResponseData.isValidResponse || httpResponseData.hasErrorMessage) {
            String str4 = str3 + (param != null ? "?action=" + param : VariableScope.nullOrMissingString) + " (" + fA + "ms)";
            if (!"list".equals(param)) {
                str4 = str4 + ":\n" + new String(byteArray);
            }
            GameEngine.log(str4);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteArray)));
        httpClientManager.closeIfAndroidClient(androidHttpClient);
        httpResponseData.bufferedReader = bufferedReader;
        return httpResponseData;
    }

    /* JADX INFO: renamed from: a */
    public static String extractFirstLine(byte[] bArr) {
        int length = bArr.length;
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] == 10 || bArr[i] == 13) {
                length = i;
                break;
            }
        }
        return new String(bArr, 0, length);
    }

    /* JADX INFO: renamed from: b */
    public static ServerInfo findServerById(String str) throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        if (str == null) {
            throw new IOException("findOrCreateServer id cannot be null");
        }
        for (ServerInfo serverInfo : gameEngine.networkEngine.discoveredServerList) {
            if (str.equals(serverInfo.serverId)) {
                return serverInfo;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: c */
    public static ServerInfo findOrCreateServerById(String str) throws IOException {
        GameEngine gameEngine = GameEngine.getInstance();
        if (str == null) {
            throw new IOException("findOrCreateServer id cannot be null");
        }
        ServerInfo serverInfoFindServerById = findServerById(str);
        if (serverInfoFindServerById != null) {
            return serverInfoFindServerById;
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.serverId = str;
        serverInfo.isLanServer = false;
        serverInfo.firstSeenTimeMs = gameEngine.networkEngine.p();
        return serverInfo;
    }

    /* JADX INFO: renamed from: a */
    public static void loadServerListAsync(Runnable runnable) {
        GameEngine.log("LoadFromMasterServer", "Load requested");
        new Thread(new MasterServerListParser(runnable)).start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: renamed from: a */
    public static void removeStaleServers(int i, int i2) {
        GameEngine gameEngine = GameEngine.getInstance();
        boolean z = false;
        synchronized (serverListLock) {
            Iterator it = gameEngine.networkEngine.discoveredServerList.iterator();
            while (it.hasNext()) {
                ServerInfo serverInfo = (ServerInfo) it.next();
                if (serverInfo.lastSeenSequence < i) {
                    GameEngine.log("LoadFromMasterServer", i2 + ": Removing stale server with id:" + serverInfo.serverId);
                    it.remove();
                    z = true;
                }
            }
        }
        if (z) {
            ServerListActivity.refreshUI();
        }
    }

    /* JADX INFO: renamed from: a */
    public static void getOwnInfoFromMasterServerAsync() {
        GameEngine.log("GetOwnInfoRunnable", "getOwnInfoFromMasterServer");
        MasterServerAuth.saltD = 6;
        new Thread(new GetOwnInfoRunnable()).start();
    }

    /* JADX INFO: renamed from: a */
    static void addParam(List list, String str, String str2) {
        list.add(new BasicNameValuePair(str, str2));
    }

    /* JADX INFO: renamed from: b */
    static void addServerStatusParams(List list) {
        String str;
        GameEngine gameEngine = GameEngine.getInstance();
        addParam(list, "password_required", Utility.padString(gameEngine.networkEngine.roomPassword != null));
        addParam(list, "created_by", gameEngine.networkEngine.playerName);
        addParam(list, "private_ip", gameEngine.networkEngine.getPrimaryLocalIpAddress());
        addParam(list, "port_number", Integer.toString(gameEngine.networkEngine.m));
        if (gameEngine.networkEngine.u != null) {
            addParam(list, "game_map", FileHelper.fixPath(gameEngine.networkEngine.u));
        } else {
            addParam(list, "game_map", FileHelper.fixPath(gameEngine.networkEngine.roomSettings.mapPath));
        }
        GameModeType gameModeType = gameEngine.networkEngine.roomSettings.gameModeType;
        if (gameModeType == null) {
            gameModeType = GameModeType.skirmishMap;
        }
        addParam(list, "game_mode", gameModeType.name());
        if (!gameEngine.networkEngine.chatOnlyMode) {
            if (gameEngine.networkEngine.gameHasBeenStarted) {
                str = "ingame";
            } else if (gameEngine.networkEngine.roomSettings.roomLock) {
                str = "locked";
            } else {
                str = "battleroom";
            }
            addParam(list, "game_status", str);
        } else {
            addParam(list, "game_status", "chat");
        }
        addParam(list, "player_count", Integer.toString(gameEngine.networkEngine.getPlayerCount()));
        String string = Integer.toString(PlayerTeam.TEAM_NEUTRAL);
        if (gameEngine.networkEngine.chatOnlyMode) {
        }
        addParam(list, "max_player_count", string);
    }

    /* JADX INFO: renamed from: b */
    public static void createServerAsync() {
        GameEngine.log("StartCreateOnMasterServer", "Create requested");
        MasterServerAuth.saltA = 5;
        new Thread(new CreateServerRunnable()).start();
    }

    /* JADX INFO: renamed from: c */
    public static void updateServerAsync() {
        new Thread(new UpdateServerRunnable()).start();
    }

    /* JADX INFO: renamed from: d */
    public static void removeServerAsync() {
        GameEngine.log("startRemoveOnMasterServer", "Remove requested");
        new Thread(new RemoveServerRunnable()).start();
    }

    /* JADX INFO: renamed from: a */
    public static void sendErrorReportAsync(String str, String str2) {
        GameEngine.log("startErrorReport", "ErrorReport requested");
        ErrorReportSender errorReportSender = new ErrorReportSender();
        errorReportSender.stacktrace = str2;
        errorReportSender.message = str;
        new Thread(errorReportSender).start();
    }

    /* JADX INFO: renamed from: a */
    public static String formatServerCode(int i) {
        if (i == 0) {
            return VariableScope.nullOrMissingString;
        }
        if (i > 0) {
            if (i < 100000) {
                return Utility.truncateToLength(Utility.md5Hex("x" + i), 10);
            }
            if (i < 200000) {
                return Utility.truncateToLength(Utility.md5Hex("y" + i), 11);
            }
            if (i < 300000) {
                return Utility.truncateToLength(Utility.md5Hex("z" + i), 12);
            }
            if (i < 1000000) {
                return Utility.truncateToLength(Utility.md5Hex("xx" + i), 13) + "-" + GameEngine.getInstance().networkEngine.g(i - 300000);
            }
            if (i < 2000000) {
                return Utility.truncateToLength(Utility.md5Hex("yy" + i), 14) + "-" + GameEngine.getInstance().networkEngine.g(i - 1000000);
            }
            return "NA";
        }
        return "NA";
    }

    /* JADX INFO: renamed from: a */
    public static void getGameServerInfoFromMasterServerAsync(ConnectionResult connectionResult, String str, int i, String str2) {
        GameEngine.log("getGameServerInfoFromMasterServer");
        GetServerInfoRunnable getServerInfoRunnable = new GetServerInfoRunnable();
        getServerInfoRunnable.result = connectionResult;
        getServerInfoRunnable.gameId = str;
        getServerInfoRunnable.serverCode = i;
        getServerInfoRunnable.password = str2;
        new Thread(getServerInfoRunnable).start();
    }
}
