package com.corrodinggames.rts.gameFramework.utility;

import java.io.File;

public final class CoreFileSystem {
    private CoreFileSystem() {
    }

    public static File externalStorageDirectory() {
        String configuredPath = System.getProperty("rwx.externalStorage");
        File directory;
        if (configuredPath != null && configuredPath.length() > 0) {
            directory = new File(configuredPath);
        } else {
            directory = new File(System.getProperty("user.home", "."), ".rwx");
        }
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}
