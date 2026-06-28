package com.corrodinggames.rts.gameFramework.utility;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/a.class */
public class ANRException extends Error {
    private ANRException(CachedThrowable cachedThrowable) {
        super("Application Not Responding", cachedThrowable);
    }

    @Override // java.lang.Throwable
    public Throwable fillInStackTrace() {
        setStackTrace(new StackTraceElement[0]);
        return this;
    }

    static ANRException a(String str, boolean z) {
        final Thread threadE = b();
        // from class: com.corrodinggames.rts.gameFramework.utility.a.1
// java.util.Comparator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        TreeMap<Thread, StackTraceElement[]> treeMap = new TreeMap((Comparator<Thread>) (thread, thread2) -> {
            if (thread == thread2) {
                return 0;
            }
            if (thread == threadE) {
                return 1;
            }
            if (thread2 == threadE) {
                return -1;
            }
            return thread2.getName().compareTo(thread.getName());
        });
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            if (entry.getKey() == threadE || (entry.getKey().getName().startsWith(str) && (z || entry.getValue().length > 0))) {
                treeMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (!treeMap.containsKey(threadE)) {
            treeMap.put(threadE, threadE.getStackTrace());
        }
        CachedThrowable cachedThrowable = null;
        for (Map.Entry entry2 : treeMap.entrySet()) {
            CachedStackTrace cachedStackTrace = new CachedStackTrace(a((Thread) entry2.getKey()), (StackTraceElement[]) entry2.getValue());
            cachedStackTrace.getClass();
            cachedThrowable = new CachedThrowable(cachedStackTrace, cachedThrowable);
        }
        return new ANRException(cachedThrowable);
    }

    static ANRException a() {
        Thread threadE = b();
        CachedStackTrace cachedStackTrace = new CachedStackTrace(a(threadE), threadE.getStackTrace());
        cachedStackTrace.getClass();
        return new ANRException(new CachedThrowable(cachedStackTrace, null));
    }

    private static Thread b() {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if ("main".equals(thread.getName())) {
                return thread;
            }
        }
        return Thread.currentThread();
    }

    private static String a(Thread thread) {
        return thread.getName() + " (state = " + thread.getState() + ")";
    }
}
