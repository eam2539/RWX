package com.corrodinggames.rts.game.units.actions;

import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;
import java.util.HashMap;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/a/c.class */
public class ActionId {

    /* JADX INFO: renamed from: c */
    private static final HashMap internMap = new HashMap();

    /* JADX INFO: renamed from: a */
    public static final ActionId NONE = intern("-1");

    /* JADX INFO: renamed from: b */
    String id;

    /* JADX INFO: renamed from: a */
    public static ActionId intern(String str) {
        ActionId actionId = (ActionId) internMap.get(str);
        if (actionId != null) {
            return actionId;
        }
        ActionId actionId2 = new ActionId(str);
        internMap.put(str, actionId2);
        return actionId2;
    }

    /* JADX INFO: renamed from: a */
    public String getId() {
        return this.id;
    }

    private ActionId(String str) {
        this.id = str;
    }

    /* JADX INFO: renamed from: a */
    public static void serialize(GameOutputStream gameOutputStream, ActionId actionId) throws IOException {
        String str = null;
        if (actionId != null) {
            str = actionId.id;
        }
        gameOutputStream.writeStringNullable(str);
    }

    /* JADX INFO: renamed from: a */
    public static ActionId deserialize(GameInputStream gameInputStream) throws IOException {
        String nullableString = gameInputStream.readNullableString();
        if (nullableString != null) {
            return intern(nullableString);
        }
        return null;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "ActionId(" + this.id + ")";
    }

    /* JADX INFO: renamed from: a */
    public final boolean fromString(ActionId actionId) {
        return this == actionId;
    }
}
