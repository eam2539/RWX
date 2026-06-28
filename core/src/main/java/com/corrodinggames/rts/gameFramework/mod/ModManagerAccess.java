package com.corrodinggames.rts.gameFramework.mod;

import java.util.ArrayList;
import java.util.List;

public final class ModManagerAccess {
    private ModManagerAccess() {
    }

    public static List<ModInfo> allMods(ModManager modManager) {
        synchronized (modManager.listLock) {
            return new ArrayList<>(modManager.mods);
        }
    }

    public static ModInfo findById(ModManager modManager, String id) {
        if (id == null) {
            return null;
        }
        synchronized (modManager.listLock) {
            for (ModInfo modInfo : modManager.mods) {
                if (id.equals(modInfo.uuid)) {
                    return modInfo;
                }
            }
        }
        return null;
    }
}
