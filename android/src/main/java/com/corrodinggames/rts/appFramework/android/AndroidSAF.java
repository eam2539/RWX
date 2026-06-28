package com.corrodinggames.rts.appFramework.android;

import android.util.Log;

public final class AndroidSAF {
    public static final String TAG = "RustedWarfare";

    private AndroidSAF() {
    }

    public static void writeStdOut(String message) {
        Log.d(TAG, message);
    }

    public static void log(String message) {
        writeStdOut(message);
    }
}
