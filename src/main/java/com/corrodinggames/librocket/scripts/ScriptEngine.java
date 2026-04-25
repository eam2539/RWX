package com.corrodinggames.librocket.scripts;

import com.corrodinggames.librocket.GameMainManager;
import com.corrodinggames.librocket.LibRocketManager;
import com.corrodinggames.rts.debug.DebugSocketServer;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/ScriptEngine.class */
public class ScriptEngine {
    LibRocketManager slickLibRocket;
    static ScriptEngine scriptEngine;
    public static boolean inDebugScript;
    static boolean mainScriptThreadMarked;
    static ThreadLocal isMainScriptThread = new ThreadLocal() { // from class: com.corrodinggames.librocket.scripts.ScriptEngine.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public Boolean initialValue() {
            return false;
        }
    };
    static Throwable scriptError;
    static String scriptErrorMessage;
    ArrayList queuedScripts = new ArrayList();
    ArrayList runningScripts = new ArrayList();
    HashMap globals = new HashMap();
    private Root root = new Root();

    public static boolean isStrict() {
        if (DebugSocketServer.isEnabled()) {
            return true;
        }
        return false;
    }

    public static void checkThreadAccess() {
        if (!((Boolean) isMainScriptThread.get()).booleanValue()) {
            GameEngine.updatePaintTextSizeIfNeeded("ScriptEngine: thread is not marked as main script thread!");
            GameEngine.printStackTrace();
        }
    }

    public Root getRoot() {
        checkThreadAccess();
        return this.root;
    }

    public Root getRootNoCheck() {
        return this.root;
    }

    public static ScriptEngine getInstance() {
        return scriptEngine;
    }

    public static ScriptEngine createScriptEngine(LibRocketManager libRocketManager) {
        if (scriptEngine != null) {
            throw new RuntimeException("scriptEngine already exists");
        }
        scriptEngine = new ScriptEngine(libRocketManager);
        return scriptEngine;
    }

    private ScriptEngine(LibRocketManager libRocketManager) {
        this.slickLibRocket = libRocketManager;
        setupScriptContext(this.root);
        setGlobalVariable("root", this.root);
        Multiplayer multiplayer = new Multiplayer(this.root);
        setupScriptContext(multiplayer);
        setGlobalVariable("multiplayer", multiplayer);
        setGlobalVariable("mp", multiplayer);
        this.root.multiplayer = multiplayer;
        Mods mods = new Mods(this.root);
        setupScriptContext(mods);
        setGlobalVariable("mods", mods);
        this.root.mods = mods;
        if (DebugSocketServer.isEnabled()) {
            ScriptContext debug = new Debug(this.root);
            setupScriptContext(debug);
            setGlobalVariable("debug", debug);
        }
    }

    public void setupScriptContext(ScriptContext scriptContext) {
        scriptContext.libRocket = this.slickLibRocket;
        scriptContext.guiEngine = GameMainManager.getInstance();
        scriptContext.scriptEngine = this;
        for (Method method : scriptContext.getClass().getMethods()) {
            String name = method.getName();
            if (!name.equals("wait") && !name.equals("getClass")) {
                if (scriptContext.methods.get(name) != null) {
                    logError("method: " + name + " already exists");
                }
                scriptContext.methods.put(name, method);
            }
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/ScriptEngine$Action.class */
    public class Action {
        public String script;
        public boolean tryToCatchCrash;
        public String caughtCrash;
        public boolean completed;
        public int framesDelay;

        public void run(ScriptEngine scriptEngine) {
            try {
                scriptEngine.processScript(this.script);
            } catch (Exception e) {
                if (this.tryToCatchCrash) {
                    GameEngine.log("caught script crash", (Throwable) e);
                    this.caughtCrash = Utility.formatLong(e);
                    return;
                }
                throw new RuntimeException(e);
            } finally {
                this.completed = true;
            }
        }

        public String waitForCompletionOrCrash(boolean z) {
            int i = 0;
            while (i < 3000) {
                if (this.completed) {
                    return this.caughtCrash;
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (z) {
                    i = 0;
                }
                i++;
            }
            return "Time Out";
        }
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/scripts/ScriptEngine$RunnableAction.class */
    public class RunnableAction extends Action {
        Runnable runnable;

        RunnableAction(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override // com.corrodinggames.librocket.scripts.ScriptEngine.Action
        public void run(ScriptEngine scriptEngine) {
            try {
                this.runnable.run();
            } catch (Exception e) {
                if (this.tryToCatchCrash) {
                    GameEngine.log("caught script crash", (Throwable) e);
                    this.caughtCrash = Utility.formatLong(e);
                    return;
                }
                throw new RuntimeException(e);
            } finally {
                this.completed = true;
            }
        }
    }

    public void update(float f) {
        if (!mainScriptThreadMarked) {
            mainScriptThreadMarked = true;
            isMainScriptThread.set(true);
        }
        if (this.queuedScripts.size() != 0) {
            synchronized (this.queuedScripts) {
                Iterator it = this.queuedScripts.iterator();
                while (it.hasNext()) {
                    Action action = (Action) it.next();
                    if (action.framesDelay > 0) {
                        action.framesDelay--;
                    } else {
                        this.runningScripts.add(action);
                        it.remove();
                    }
                }
            }
            Iterator it2 = this.runningScripts.iterator();
            while (it2.hasNext()) {
                ((Action) it2.next()).run(this);
            }
            this.runningScripts.clear();
        }
        this.root.onFrameUpdate(f);
    }

    public Action addScriptToQueue(String str, boolean z) {
        Action action;
        synchronized (this.queuedScripts) {
            action = new Action();
            action.script = str;
            action.tryToCatchCrash = z;
            this.queuedScripts.add(action);
        }
        return action;
    }

    public Action addScriptToQueueIfNotAlreadyQueued(String str) {
        synchronized (this.queuedScripts) {
            Iterator it = this.queuedScripts.iterator();
            while (it.hasNext()) {
                if (str.equals(((Action) it.next()).script)) {
                    return null;
                }
            }
            return addScriptToQueue(str, false);
        }
    }

    public Action addScriptToQueue(String str) {
        return addScriptToQueue(str, false);
    }

    public Action addRunnableToQueue(Runnable runnable) {
        RunnableAction runnableAction;
        synchronized (this.queuedScripts) {
            runnableAction = new RunnableAction(runnable);
            this.queuedScripts.add(runnableAction);
        }
        return runnableAction;
    }

    public void processScript(String str) {
        if (!"mp.refreshUI()".equals(str)) {
            System.out.println("ScriptEngine:HandleEvent:" + str);
        }
        try {
            for (String str2 : StringUtils.a(str, ';')) {
                processArg(str2);
            }
        } catch (Exception e) {
            throwDelayedException("Found error running:" + str, e);
            throw new RuntimeException(e);
        }
    }

    public static void throwDelayedException(String str, Throwable th) {
        GameEngine.log("throwDelayedException", th);
        if (scriptError == null) {
            scriptError = th;
            scriptErrorMessage = str;
        }
    }

    public void checkForErrors() {
        if (scriptError != null) {
            throw new RuntimeException(scriptErrorMessage, scriptError);
        }
    }

    public Matcher match(String str, String str2) {
        Matcher matcher = Pattern.compile(str).matcher(str2);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }

    public Object processArg(String str) {
        String strTrim = str.trim();
        if (strTrim.length() == 0 || strTrim.equals("null")) {
            return null;
        }
        Matcher matcherMatch = match("'(.*)'", strTrim);
        if (matcherMatch != null) {
            return Utility.urlDecode(matcherMatch.group(1));
        }
        Matcher matcherMatch2 = match("(-?\\d*)", strTrim);
        if (matcherMatch2 != null) {
            return Integer.valueOf(Integer.parseInt(matcherMatch2.group(1)));
        }
        Matcher matcherMatch3 = match("(-?\\d*\\.\\d*)", strTrim);
        if (matcherMatch3 != null) {
            return Float.valueOf(Float.parseFloat(matcherMatch3.group(1)));
        }
        Matcher matcherMatch4 = match("\\s*([^\\s\"']*)\\s*=(.*)", strTrim);
        if (matcherMatch4 != null) {
            String strGroup = matcherMatch4.group(1);
            String strGroup2 = matcherMatch4.group(2);
            System.out.println("processArg: setting: " + strGroup + "=" + strGroup2);
            Object objProcessArg = processArg(strGroup2);
            setLocalVariable(strGroup, objProcessArg);
            return objProcessArg;
        }
        Matcher matcherMatch5 = match("\\s*([\\w\\.]+)\\((.*)\\)\\s*", strTrim);
        if (matcherMatch5 != null) {
            return processFunction(strTrim, matcherMatch5);
        }
        if ("false".equalsIgnoreCase(strTrim)) {
            return Boolean.FALSE;
        }
        if ("true".equalsIgnoreCase(strTrim)) {
            return Boolean.TRUE;
        }
        Object scriptVariable = getScriptVariable(strTrim, false);
        if (scriptVariable != null) {
            return scriptVariable;
        }
        System.out.println("processArg: no variable:" + strTrim);
        getScriptVariable(strTrim, true);
        System.out.println("SlickLibRocket:HandleEvent: failed to match:" + strTrim);
        return null;
    }

    public void printMetadata(HashMap map) {
        if (map == null) {
            System.out.println("No metadata");
            return;
        }
        String str = VariableScope.nullOrMissingString;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            str = str + ((String) it.next()) + ",";
        }
        System.out.println("metadata:" + str);
    }

    public Object getScriptVariable(String str, boolean z) {
        if (this.slickLibRocket.getCurrentAlert() != null) {
            Object metadata = this.slickLibRocket.getCurrentAlert().getMetadata(str);
            if (metadata != null) {
                return metadata;
            }
            if (z) {
                System.out.println("getScriptVariable: alert");
                printMetadata(this.slickLibRocket.getCurrentAlert().metadata);
            }
        }
        if (this.slickLibRocket.getCurrentPopup() != null) {
            Object metadata2 = this.slickLibRocket.getCurrentPopup().getMetadata(str);
            if (metadata2 != null) {
                return metadata2;
            }
            if (z) {
                System.out.println("getScriptVariable: popup");
                printMetadata(this.slickLibRocket.getCurrentPopup().metadata);
            }
        }
        Object activeDocumentMetadata = this.slickLibRocket.getActiveDocumentMetadata(str);
        if (activeDocumentMetadata != null) {
            return activeDocumentMetadata;
        }
        if (z) {
            System.out.println("getScriptVariable: document");
            printMetadata(this.slickLibRocket.getActiveDocumentMetadata());
        }
        Object obj = this.globals.get(str);
        if (obj != null) {
            return obj;
        }
        if (z) {
            System.out.println("getScriptVariable: globals");
            printMetadata(this.globals);
            return null;
        }
        return null;
    }

    public void setLocalVariable(String str, Object obj) {
        this.slickLibRocket.getActiveDocumentMetadata().put(str, obj);
    }

    public void setGlobalVariable(String str, Object obj) {
        this.globals.put(str, obj);
    }

    public Object processFunction(String str, Matcher matcher) {
        String[] strArrA;
        GameEngine.getInstance();
        String strGroup = matcher.group(1);
        String strGroup2 = matcher.group(2);
        if (strGroup2.equals(VariableScope.nullOrMissingString)) {
            strArrA = new String[0];
        } else {
            strArrA = StringUtils.a(strGroup2, ',');
        }
        Object[] objArr = new Object[strArrA.length];
        for (int i = 0; i < objArr.length; i++) {
            objArr[i] = processArg(strArrA[i]);
        }
        return runFunction(strGroup, objArr);
    }

    public Object runFunction(String str, Object[] objArr) {
        String[] strArrSplit = str.split("\\.");
        ScriptContext scriptContext = this.root;
        if (strArrSplit.length > 2) {
            logCritical("Unsupported nameParts: " + str);
            return null;
        }
        if (strArrSplit.length == 2) {
            Object scriptVariable = getScriptVariable(strArrSplit[0], false);
            if (!(scriptVariable instanceof ScriptContext)) {
                logCritical("Could not find context for: " + str);
                return null;
            }
            scriptContext = (ScriptContext) scriptVariable;
            str = strArrSplit[1];
        }
        Method method = (Method) scriptContext.methods.get(str);
        if (method == null) {
            logCritical("Could not find function: " + str);
            return null;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        ArrayList arrayList = new ArrayList();
        if (objArr.length > parameterTypes.length) {
            logCritical("function: " + str + " does not accept " + objArr.length + " parameters");
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> cls = parameterTypes[i];
            Object obj = null;
            if (i < objArr.length) {
                obj = objArr[i];
            }
            if (obj == null || cls.isInstance(obj) || cls.equals(Float.class)) {
            }
            arrayList.add(obj);
        }
        try {
            return method.invoke(scriptContext, arrayList.toArray());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e2) {
            GameEngine.updatePaintTextSizeIfNeeded("convertedParameters:");
            for (Object obj2 : arrayList) {
                if (obj2 == null) {
                    GameEngine.updatePaintTextSizeIfNeeded("=null");
                } else {
                    GameEngine.updatePaintTextSizeIfNeeded("=" + obj2.getClass().getName());
                }
            }
            GameEngine.updatePaintTextSizeIfNeeded("-----");
            e2.printStackTrace();
            throw new RuntimeException(e2);
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            throw new RuntimeException(e3);
        }
    }

    public static void logError(String str) {
        GameEngine.isInSpace("ScriptEngine - error: " + str);
    }

    public static void logCritical(String str) {
        GameEngine.isInSpace("ScriptEngine - critical: " + str);
        if (isStrict()) {
            throw new RuntimeException("ScriptEngine - critical:" + str);
        }
    }
}
