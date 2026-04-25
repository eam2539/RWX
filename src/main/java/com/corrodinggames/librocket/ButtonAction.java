package com.corrodinggames.librocket;

/* JADX INFO: renamed from: com.corrodinggames.librocket.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/e.class */
public class ButtonAction {

    /* JADX INFO: renamed from: a */
    public String text;

    /* JADX INFO: renamed from: b */
    public Runnable runnable;

    /* JADX INFO: renamed from: c */
    public boolean closesDialog = false;

    public ButtonAction(String str, Runnable runnable) {
        this.text = str;
        this.runnable = runnable;
    }
}
