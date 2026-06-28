package com.corrodinggames.rts.gameFramework.file;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.RwmodFileLoader;
import com.corrodinggames.rts.gameFramework.utility.StringUtils;

import java.io.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/g.class */
public class CacheManager {

    /* JADX INFO: renamed from: a */
    public static boolean DEBUG = true;

    /* JADX INFO: renamed from: a */
    public static final String getCharAsString(char c) {
        return String.valueOf((int) c);
    }

    /* JADX INFO: renamed from: a */
    public static String encodeChar(String str, char c) {
        if (Utility.containsChar(str, c)) {
            str = Utility.replaceSubstring(str, String.valueOf(c), "%" + getCharAsString(c));
        }
        return str;
    }

    /* JADX INFO: renamed from: a */
    public static String encodeFileName(String str) {
        if (str == null) {
            return "null";
        }
        String strEncodeChar = encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(encodeChar(Utility.replaceSubstring(str, "%", "%%"), '/'), '\\'), ':'), '\"'), '\''), '|'), '?'), '*'), '<'), '>'), (char) 0);
        if (strEncodeChar.contains("/")) {
            throw new IllegalArgumentException();
        }
        if (strEncodeChar.contains("\\")) {
            throw new IllegalArgumentException();
        }
        return strEncodeChar;
    }

    /* JADX INFO: renamed from: a */
    public static String getCachePath(String str, String str2, boolean z) {
        String str3 = FileHelper.getWorkingDirectory() + encodeFileName(str) + ".cachedata";
        if (z) {
            File file = new File(str3);
            if (!file.isDirectory() && !file.mkdirs()) {
                GameEngine.log("Failed to create folder for:" + file.getAbsolutePath());
            }
        }
        return str3 + "/" + encodeFileName(str2);
    }

    /* JADX INFO: renamed from: a */
    public static boolean saveToCache(String str, String str2, String str3) {
        try {
            return saveToCache(str, str2, new ByteArrayInputStream(str3.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: a */
    public static boolean saveToCache(String str, String str2, InputStream inputStream) {
        try {
            String cachePath = getCachePath(str, str2, true);
            File file = new File(cachePath);
            File file2 = new File(cachePath + ".tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            try {
                Utility.copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                if (!FileHelper.renameFile(file2, file)) {
                    GameEngine.logColored("AddToCache: Failed to rename to final file: " + cachePath);
                    return false;
                }
                if (DEBUG) {
                    GameEngine.log("Wrote cache file at: " + file.getAbsolutePath());
                    return true;
                }
                return true;
            } catch (Throwable th) {
                fileOutputStream.close();
                throw th;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: renamed from: a */
    public static FileInputStream readFromCache(String str, String str2) {
        try {
            File file = new File(getCachePath(str, str2, false));
            if (!file.exists()) {
                return null;
            }
            file.setLastModified(System.currentTimeMillis());
            return new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: renamed from: b */
    public static String readStringFromCache(String str, String str2) throws IOException {
        FileInputStream fromCache = readFromCache(str, str2);
        if (fromCache == null) {
            return null;
        }
        return Utility.readStreamToString(fromCache);
    }

    /* JADX INFO: renamed from: c */
    public static void deleteFromCache(String str, String str2) {
        String cachePath = getCachePath(str, str2, false);
        File file = new File(cachePath);
        if (file.exists() && !file.delete()) {
            GameEngine.logWarningAndStack("Failed to delete: " + cachePath);
        }
    }

    /* JADX INFO: renamed from: b */
    private static CachedInputStream openAssetCachedInternal(String str, String str2, String str3) throws IOException {
        String[] strArrB;
        String str4 = str2 + ".data";
        String stringFromCache = readStringFromCache(str, str2 + ".meta");
        if (stringFromCache != null && (strArrB = StringUtils.b(stringFromCache, ":")) != null) {
            Long longOrNull = Utility.parseLongOrNull(strArrB[0]);
            long fileSize = FileHelper.getFileSize(str2);
            String str5 = strArrB[1];
            if (longOrNull == null) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Bad meta data for: " + str2);
                    return null;
                }
                return null;
            }
            if (longOrNull.longValue() != fileSize) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Stale timestamp for: " + str2 + " (" + longOrNull + "!=" + fileSize + ")");
                    return null;
                }
                return null;
            }
            if (str5.startsWith("null")) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Cache hit (null-type) for: " + str2 + " (" + longOrNull + "!=" + fileSize + ")");
                }
                return new CachedInputStream(null);
            }
            if (!str5.startsWith(str3)) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Unsupported type " + str5 + " for: " + str2 + " expected: " + str3);
                }
                return new CachedInputStream(null);
            }
            FileInputStream fromCache = readFromCache(str, str4);
            if (fromCache != null) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Cache hit for: " + str2);
                }
                return new CachedInputStream(fromCache);
            }
            if (DEBUG) {
                GameEngine.log("openAssetCached: meta file but not data for: " + str2);
                return null;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: renamed from: d */
    public static String[] listDirCached(String str, String str2) throws IOException {
        String str3;
        long fileSize;
        if (!RwmodFileLoader.isRwmodPath(str2)) {
            return FileHelper.listFiles(str2);
        }
        String str4 = str2 + ".data";
        String str5 = str2 + ".meta";
        CachedInputStream cachedInputStreamOpenAssetCachedInternal = openAssetCachedInternal(str, str2, "list");
        if (cachedInputStreamOpenAssetCachedInternal != null) {
            if (cachedInputStreamOpenAssetCachedInternal.inputStream == null) {
                return null;
            }
            String streamToString = Utility.readStreamToString(cachedInputStreamOpenAssetCachedInternal.inputStream);
            cachedInputStreamOpenAssetCachedInternal.close();
            return StringUtils.e(streamToString);
        }
        String[] strArrListFiles = FileHelper.listFiles(str2);
        if (strArrListFiles != null) {
            if (DEBUG) {
                GameEngine.log("listDirCached: Listing count: " + strArrListFiles.length);
            }
            str3 = "list";
            fileSize = FileHelper.getFileSize(str2);
            if (fileSize == 0) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Got 0 timestamp for: " + str2 + " cannot cache");
                }
                return strArrListFiles;
            }
            saveToCache(str, str4, StringUtils.a(strArrListFiles));
        } else {
            if (DEBUG) {
                GameEngine.log("listDirCached: Null");
            }
            str3 = "null";
            fileSize = FileHelper.getFileSize(str2);
        }
        saveToCache(str, str5, fileSize + ":" + str3);
        return strArrListFiles;
    }

    /* JADX INFO: renamed from: e */
    public static InputStream openAssetCached(String str, String str2) throws IOException {
        String str3;
        long fileSize;
        String str4 = str2 + ".data";
        String str5 = str2 + ".meta";
        CachedInputStream cachedInputStreamOpenAssetCachedInternal = openAssetCachedInternal(str, str2, "data");
        if (cachedInputStreamOpenAssetCachedInternal != null) {
            return cachedInputStreamOpenAssetCachedInternal.inputStream;
        }
        if (DEBUG) {
            GameEngine.log("openAssetCached: Cache miss for: " + str2);
        }
        AssetInputStream assetInputStreamOpenFileByPath = FileHelper.openFileByPath(str2);
        if (assetInputStreamOpenFileByPath != null) {
            if (DEBUG) {
                GameEngine.log("openAssetCached: Reading: " + str2);
            }
            str3 = "data";
            fileSize = FileHelper.getFileSize(str2);
            if (fileSize == 0) {
                if (DEBUG) {
                    GameEngine.log("openAssetCached: Got 0 timestamp for: " + str2 + " cannot cache");
                }
                return assetInputStreamOpenFileByPath;
            }
            if (!saveToCache(str, str4, assetInputStreamOpenFileByPath)) {
            }
        } else {
            if (DEBUG) {
                GameEngine.log("openAssetCached: Got null for: " + str2);
            }
            str3 = "null";
            fileSize = FileHelper.getFileSize(str2);
        }
        saveToCache(str, str5, fileSize + ":" + str3);
        if (assetInputStreamOpenFileByPath == null) {
            return null;
        }
        try {
            assetInputStreamOpenFileByPath.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fromCache = readFromCache(str, str4);
        if (fromCache == null) {
            GameEngine.logColored("openAssetCached: Error. Failed to reopen cache: " + str2);
            return FileHelper.openFileByPath(str2);
        }
        return fromCache;
    }

    /* JADX INFO: renamed from: f */
    public static boolean existsInCache(String str, String str2) throws IOException {
        InputStream inputStreamOpenAssetCached = openAssetCached(str, str2);
        if (inputStreamOpenAssetCached == null) {
            return false;
        }
        try {
            inputStreamOpenAssetCached.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }
}
