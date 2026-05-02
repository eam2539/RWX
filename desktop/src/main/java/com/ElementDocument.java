package com;

import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: game-lib.jar:com/ElementDocument.class */
public class ElementDocument extends Element {
    public static final int NONE = 0;
    public static final int FOCUS = 2;
    public static final int MODAL = 4;
    public String documentPath;
    public HashMap metadata;
    public boolean translatedToUnicode;
    public ArrayList pageTimers = new ArrayList(1);

    public native void show(int i);

    @Override // com.Element
    public native void hide();

    private native void close();

    public native void pullToFront();

    public native void pushToBack();

    @Override // com.Element
    public void show() {
        show(2);
    }

    public void showWithWorkaround(int i) {
    }

    public void closeDocument() {
        close();
    }

    public Object getMetadata(String str) {
        if (this.metadata == null) {
            return null;
        }
        return this.metadata.get(str);
    }

    public Object getMetadata(String str, Object obj) {
        if (this.metadata == null) {
            return obj;
        }
        Object obj2 = this.metadata.get(str);
        if (obj2 == null) {
            return obj;
        }
        return obj2;
    }

    public Float getMetadataFloat(String str) {
        Object metadata = getMetadata(str);
        if (metadata instanceof Float) {
            return (Float) metadata;
        }
        return null;
    }

    public void setMetadata(String str, Object obj) {
        if (this.metadata == null) {
            this.metadata = new HashMap();
        }
        this.metadata.put(str, obj);
    }

    public void setMetadataFloat(String str, Float f) {
        setMetadata(str, f);
    }

    public HashMap getMetadataMap() {
        if (this.metadata == null) {
            this.metadata = new HashMap();
        }
        return this.metadata;
    }
}
