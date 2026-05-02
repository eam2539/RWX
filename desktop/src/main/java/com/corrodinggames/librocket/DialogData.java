package com.corrodinggames.librocket;

import com.ElementDocument;

/* JADX INFO: renamed from: com.corrodinggames.librocket.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/d.class */
public class DialogData {

    /* JADX INFO: renamed from: a */
    ElementDocument document;

    /* JADX INFO: renamed from: b */
    public String title;

    /* JADX INFO: renamed from: c */
    public String message;

    /* JADX INFO: renamed from: d */
    public String textInputValue;

    /* JADX INFO: renamed from: e */
    public Object button1;

    /* JADX INFO: renamed from: f */
    public Object button2;

    /* JADX INFO: renamed from: g */
    public boolean isAlert = true;

    /* JADX INFO: renamed from: h */
    public boolean showBackButton = true;

    /* JADX INFO: renamed from: i */
    public Runnable onClose;

    public DialogData() {
    }

    public DialogData(ElementDocument elementDocument) {
        this.document = elementDocument;
    }
}
