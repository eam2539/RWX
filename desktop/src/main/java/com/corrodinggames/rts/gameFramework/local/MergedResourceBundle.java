package com.corrodinggames.rts.gameFramework.local;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.h.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/h/b.class */
class MergedResourceBundle extends ResourceBundle {
    ResourceBundle a;
    ResourceBundle b;

    public MergedResourceBundle(ResourceBundle resourceBundle, ResourceBundle resourceBundle2) {
        this.a = resourceBundle;
        this.b = resourceBundle2;
    }

    @Override // java.util.ResourceBundle
    public Enumeration getKeys() {
        GameEngine.log("MultipleResourceBundle: Slow get keys");
        Vector vector = new Vector();
        vector.addAll(Collections.list(this.a.getKeys()));
        if (this.b != null) {
            for (String str : Collections.list(this.b.getKeys())) {
                if (!vector.contains(str)) {
                    vector.add(str);
                }
            }
        }
        return vector.elements();
    }

    @Override // java.util.ResourceBundle
    protected Object handleGetObject(String str) {
        Object object;
        try {
            object = this.a.getObject(str);
        } catch (MissingResourceException e) {
            object = null;
        }
        if (object == null && this.b != null) {
            try {
                object = this.b.getObject(str);
            } catch (MissingResourceException e2) {
                object = null;
            }
        }
        return object;
    }
}
