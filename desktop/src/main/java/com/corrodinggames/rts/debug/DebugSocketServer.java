package com.corrodinggames.rts.debug;

import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a.class */
public class DebugSocketServer implements Runnable {

    /* JADX INFO: renamed from: a */
    public ServerSocket serverSocket;

    /* JADX INFO: renamed from: b */
    public boolean running = true;

    /* JADX INFO: renamed from: c */
    public static boolean enabled = false;

    /* JADX INFO: renamed from: d */
    public static boolean field_d = false;

    /* JADX INFO: renamed from: e */
    static ArrayList<String> scriptsToRun = new ArrayList();

    /* JADX INFO: renamed from: a */
    public static boolean isEnabled() {
        return enabled;
    }

    /* JADX INFO: renamed from: a */
    public static void addScriptToRun(String str) {
        enabled = true;
        scriptsToRun.add(str);
    }

    /* JADX INFO: renamed from: b */
    public static void runPendingScripts() {
        if (scriptsToRun.size() == 0) {
            return;
        }
        new Thread(new Runnable() { // from class: com.corrodinggames.rts.a.a.1
            @Override // java.lang.Runnable
            public void run() {
                for (String str : DebugSocketServer.scriptsToRun) {
                    GameEngine.log("Running debug script:" + str);
                    try {
                        FileReader fileReader = new FileReader(str);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            String strTrim = line.trim();
                            if (!strTrim.equals(VariableScope.nullOrMissingString) && !strTrim.startsWith("#")) {
                                GameEngine.log("Running: " + strTrim);
                                GameEngine.log("got: " + DebugSocketServer.executeCommand("script " + strTrim).trim());
                            }
                        }
                        bufferedReader.close();
                        fileReader.close();
                        GameEngine.log("End of:" + str);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    /* JADX INFO: renamed from: a */
    public static void start(int i, String str) {
        try {
            enabled = true;
            GameEngine.isAutomatedTestMode = true;
            DebugSocketServer debugSocketServer = new DebugSocketServer();
            if (i != -1) {
                debugSocketServer.serverSocket = new ServerSocket(i);
                new Thread(debugSocketServer).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DebugSocketServer() {
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.running) {
            try {
                Socket socketAccept = this.serverSocket.accept();
                try {
                    socketAccept.setTcpNoDelay(true);
                    new Thread(new DebugSocketConnection(this, socketAccept)).run();
                } catch (IOException e) {
                    GameEngine.log("Got IOException on debugSocket connection");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    /* JADX INFO: renamed from: b */
    public static String executeCommand(String str) {
        String strSubstring = null;
        int iIndexOf = str.indexOf(" ");
        if (iIndexOf == -1) {
            iIndexOf = str.length();
        }
        String lowerCase = str.substring(0, iIndexOf).toLowerCase(Locale.ENGLISH);
        if (iIndexOf != -1 && str.length() >= iIndexOf + 1) {
            strSubstring = str.substring(iIndexOf + 1);
            strSubstring.split(" ");
        }
        if (ScriptEngine.getInstance() == null) {
            GameEngine.logColored("DebugSocketConnection: waiting for ScriptEngine to start....");
            int i = 0;
            while (true) {
                if (i >= 100) {
                    break;
                }
                if (ScriptEngine.getInstance() != null) {
                    GameEngine.logColored("started");
                    break;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        if (lowerCase.equalsIgnoreCase("ping")) {
            return "pong";
        }
        if (lowerCase.equalsIgnoreCase("crash")) {
            throw new RuntimeException("test crash");
        }
        if (lowerCase.equalsIgnoreCase("script")) {
            if (strSubstring == null) {
                return "argString==null";
            }
            String strWaitForCompletionOrCrash = ScriptEngine.getInstance().addScriptToQueue(strSubstring).waitForCompletionOrCrash(false);
            if (strWaitForCompletionOrCrash == null) {
                return "done";
            }
            return strWaitForCompletionOrCrash;
        }
        if (lowerCase.equalsIgnoreCase("function") || lowerCase.equalsIgnoreCase("functionNoTimeout")) {
            if (strSubstring == null) {
                return "argString==null";
            }
            final ScriptEngine scriptEngine = ScriptEngine.getInstance();
            final String str2 = strSubstring;
            AbstractCallbackTask abstractCallbackTask = new AbstractCallbackTask() { // from class: com.corrodinggames.rts.a.a.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ScriptEngine.inDebugScript = true;
                        this.result = scriptEngine.processArg(str2);
                        ScriptEngine.inDebugScript = false;
                    } catch (Throwable th) {
                        ScriptEngine.inDebugScript = false;
                        throw th;
                    }
                }
            };
            ScriptEngine.Action actionAddRunnableToQueue = scriptEngine.addRunnableToQueue(abstractCallbackTask);
            actionAddRunnableToQueue.tryToCatchCrash = true;
            boolean z = false;
            if (lowerCase.equalsIgnoreCase("functionNoTimeout")) {
                z = true;
            }
            String strWaitForCompletionOrCrash2 = actionAddRunnableToQueue.waitForCompletionOrCrash(z);
            if (strWaitForCompletionOrCrash2 == null) {
                return (abstractCallbackTask.result == null ? "ok\n<NULL>" : "ok\n" + VariableScope.nullOrMissingString + abstractCallbackTask.result) + "\u0000";
            }
            return ("crash\n" + strWaitForCompletionOrCrash2) + "\u0000";
        }
        return "unknown command";
    }
}
