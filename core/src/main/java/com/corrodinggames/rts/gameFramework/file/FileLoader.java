package com.corrodinggames.rts.gameFramework.file;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.CoreFileSystem;
import com.corrodinggames.rts.gameFramework.utility.FileLoaderFactory;
import com.corrodinggames.rts.gameFramework.utility.IFileLoader;
import com.corrodinggames.rts.gameFramework.utility.saf.SafFileLoader;
import io.github.rwx.LegacyAssetBridge;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/c.class */
public class FileLoader {

    /* JADX INFO: renamed from: a */
    public String TAG = "FileLoader: ";

    /* JADX INFO: renamed from: b */
    public boolean debug = false;

    /* JADX INFO: renamed from: c */
    public boolean trace = false;

    /* JADX INFO: renamed from: d */
    public boolean disableAssets = false;

    /* JADX INFO: renamed from: e */
    String lastError;

    /* JADX INFO: renamed from: f */
    String externalStoragePath;

    /* JADX INFO: renamed from: a */
    public String getLastError() {
        String str = this.lastError;
        this.lastError = null;
        return str;
    }

    /* JADX INFO: renamed from: a */
    public void setLastError(String str) {
        this.lastError = str;
    }

    /* JADX INFO: renamed from: a */
    public String findFileExtension(String str, String str2) {
        File[] fileArrListFiles = new File(str).listFiles();
        if (fileArrListFiles == null) {
            GameEngine.log(this.TAG + "findFileExtension('" + str + "','" + str2 + "'): path is not a folder");
            return null;
        }
        for (File file : fileArrListFiles) {
            String name = file.getName();
            if (name.contains(".")) {
                name = name.substring(0, name.lastIndexOf(46));
            }
            if (name.equals(str2)) {
                return str + "/" + file.getName();
            }
        }
        GameEngine.log(this.TAG + "Could not find file with path: " + str + " file:" + str2);
        return null;
    }

    /* JADX INFO: renamed from: b */
    public boolean isAbstractPath(String str) {
        if (GameEngine.isNonAndroidVersion || str.startsWith("/") || str.startsWith("/SD/")) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: c */
    public boolean isAbsolutePath(String str) {
        if (str.startsWith("/") || str.startsWith("\\")) {
            return true;
        }
        if (GameEngine.isNonAndroidVersion) {
            if (str.startsWith("mods")) {
                return true;
            }
            if (GameEngine.isIOSVersion && str.startsWith("converted-sounds")) {
                return true;
            }
        }
        if (str.split("\\\\")[0].endsWith(":")) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: d */
    public String applyModPath(String str) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (str.contains("MOD|")) {
            String[] strArrSplit = str.split("/");
            if (strArrSplit.length >= 2) {
                String randomUUID = strArrSplit[strArrSplit.length - 1];
                boolean z = false;
                int length = strArrSplit.length - 2;
                while (true) {
                    if (length < 0) {
                        break;
                    }
                    String str2 = strArrSplit[length];
                    if (str2.startsWith("MOD|")) {
                        String strSubstring = str2.substring("MOD|".length());
                        ModInfo modByUuid = gameEngine.modManager.getModByUuid(strSubstring);
                        if (modByUuid == null) {
                            GameEngine.log(this.TAG + "Failed to find mod with hash:" + strSubstring);
                        } else {
                            randomUUID = Utility.joinPath(modByUuid.getSourceFolder(), randomUUID);
                            GameEngine.log(this.TAG + "Path changed to mod path:" + randomUUID);
                            z = true;
                            break;
                        }
                    }
                    randomUUID = str2 + File.separator + randomUUID;
                    length--;
                }
                if (z) {
                    str = randomUUID;
                }
            }
            if (str.contains("MOD|")) {
                GameEngine.log(this.TAG + "Path still contains prefix: " + str);
            }
        }
        if (str.contains("NEW_PATH|")) {
            String[] strArrSplit2 = str.split("/");
            if (strArrSplit2.length >= 2) {
                String str3 = strArrSplit2[strArrSplit2.length - 1];
                boolean z2 = false;
                int length2 = strArrSplit2.length - 2;
                while (true) {
                    if (length2 < 0) {
                        break;
                    }
                    String str4 = strArrSplit2[length2];
                    if (str4.startsWith("NEW_PATH|") && str4.substring("NEW_PATH|".length()).equals("maps2")) {
                        str3 = "/SD/rustedWarfare/maps" + File.separator + str3;
                        GameEngine.log(this.TAG + "Path changed to maps2 path:" + str3);
                        z2 = true;
                        break;
                    }
                    str3 = str4 + File.separator + str3;
                    length2--;
                }
                if (z2) {
                    str = str3;
                }
            }
        }
        return str;
    }

    /* JADX INFO: renamed from: e */
    public String getFileName(String str) {
        if (str == null) {
            return "<null>";
        }
        String strConvertAbstractPath = convertAbstractPath(str);
        IFileLoader zipFileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(strConvertAbstractPath);
        if (zipFileLoaderForPath != null) {
            return zipFileLoaderForPath.getLastModified(strConvertAbstractPath);
        }
        return strConvertAbstractPath;
    }

    /* JADX INFO: renamed from: f */
    public String convertAbstractPath(String str) {
        String strApplyModPath = applyModPath(str);
        if (GameEngine.isNonAndroidVersion) {
            if (strApplyModPath.startsWith("/SD/rusted_warfare_maps")) {
                strApplyModPath = "/SD/mods/maps" + strApplyModPath.substring("/SD/rusted_warfare_maps".length());
                GameEngine.log(this.TAG + "convertAbstractPath: Changing to:" + strApplyModPath);
            }
            if (strApplyModPath.startsWith("/SD/rustedWarfare/maps")) {
                strApplyModPath = "/SD/mods/maps" + strApplyModPath.substring("/SD/rustedWarfare/maps".length());
                GameEngine.log(this.TAG + "convertAbstractPath2: Changing to:" + strApplyModPath);
            }
            if (strApplyModPath.startsWith("/SD/") || strApplyModPath.startsWith("\\SD\\")) {
                String strSubstring = strApplyModPath.substring("/SD/".length());
                if (strSubstring.startsWith("rustedWarfare/")) {
                    strSubstring = strSubstring.substring("rustedWarfare/".length());
                }
                return b() + strSubstring;
            }
            if (isAbsolutePath(strApplyModPath)) {
                return strApplyModPath;
            }
            return "assets/" + strApplyModPath;
        }
        if (strApplyModPath.startsWith("/SD/")) {
            String strSubstring2 = strApplyModPath.substring("/SD/".length());
            if (strSubstring2.startsWith("rustedWarfare/")) {
                strSubstring2 = strSubstring2.substring("rustedWarfare/".length());
            }
            return b() + strSubstring2;
        }
        return strApplyModPath;
    }

    private boolean isAssetPath(String str) {
        String strReplace = str.replace('\\', '/');
        return strReplace.equals("assets") || strReplace.startsWith("assets/");
    }

    private String stripAssetPathPrefix(String str) {
        String strReplace = str.replace('\\', '/');
        if (strReplace.equals("assets")) {
            return VariableScope.nullOrMissingString;
        }
        if (strReplace.startsWith("assets/")) {
            return strReplace.substring("assets/".length());
        }
        return strReplace;
    }

    private boolean assetPathExists(String str) {
        String strStripAssetPathPrefix = stripAssetPathPrefix(str);
        return LegacyAssetBridge.assetExists(strStripAssetPathPrefix) ||
                LegacyAssetBridge.listAssets(strStripAssetPathPrefix).length > 0;
    }

    /* JADX INFO: renamed from: f */
    private String getExternalStoragePath() {
        if (this.externalStoragePath == null) {
            this.externalStoragePath = CoreFileSystem.externalStorageDirectory() + VariableScope.nullOrMissingString;
        }
        return this.externalStoragePath;
    }

    /* JADX INFO: renamed from: a */
    public boolean isDirectory(String str, boolean z) {
        IFileLoader fileLoaderForPath;
        String strConvertAbstractPath = convertAbstractPath(str);
        if (z) {
            fileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(strConvertAbstractPath);
        } else {
            fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        }
        if (fileLoaderForPath != null) {
            return fileLoaderForPath.move(strConvertAbstractPath);
        }
        if (isAssetPath(strConvertAbstractPath)) {
            if (!assetPathExists(strConvertAbstractPath)) {
                GameEngine.log(this.TAG + "isDirectory: asset file doesn't exist:" + strConvertAbstractPath);
                return false;
            }
            if (Utility.getFileName(str).contains(".")) {
                return false;
            }
            return true;
        }
        if (isAbstractPath(str)) {
            if (this.disableAssets) {
                return false;
            }
            if (!GameEngine.getInstance().assetIndex.exists(strConvertAbstractPath)) {
                GameEngine.log(this.TAG + "isDirectory: asset file doesn't exist:" + strConvertAbstractPath);
                return false;
            }
            if (Utility.getFileName(str).contains(".")) {
                return false;
            }
            return true;
        }
        File file = new File(strConvertAbstractPath);
        if (!file.exists()) {
            GameEngine.log(this.TAG + "isDirectory: file doesn't exist:" + strConvertAbstractPath);
            return false;
        }
        return file.isDirectory();
    }

    /* JADX INFO: renamed from: g */
    public boolean fileExists(String str) {
        String strConvertAbstractPath = convertAbstractPath(str);
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        if (fileLoaderForPath != null) {
            boolean zExists = fileLoaderForPath.exists(strConvertAbstractPath);
            if (this.trace) {
                GameEngine.log("fileExists: " + zExists + " with reader: " + fileLoaderForPath + " convertedDir:" + strConvertAbstractPath);
            }
            return zExists;
        }
        if (isAssetPath(strConvertAbstractPath)) {
            boolean zExists2 = assetPathExists(strConvertAbstractPath);
            if (this.trace) {
                GameEngine.log("fileExists: " + zExists2 + " with asset path convertedDir:" + strConvertAbstractPath);
            }
            return zExists2;
        }
        if (isAbstractPath(str)) {
            if (this.disableAssets) {
                if (this.trace) {
                    GameEngine.log("fileExists: false with disableAssets");
                    return false;
                }
                return false;
            }
            boolean zExists2 = GameEngine.getInstance().assetIndex.exists(strConvertAbstractPath);
            if (this.trace) {
                GameEngine.log("fileExists: " + zExists2 + " with abstractPathAsset convertedDir:" + strConvertAbstractPath);
            }
            return zExists2;
        }
        File file = new File(strConvertAbstractPath);
        if (file == null || !file.exists()) {
            if (this.trace) {
                GameEngine.log("fileExists: false with normal file convertedDir:" + strConvertAbstractPath);
                return false;
            }
            return false;
        }
        return true;
    }

    public String[] b(String str, boolean z) {
        String[] list;
        try {
            String strConvertAbstractPath = convertAbstractPath(str);
            IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
            if (fileLoaderForPath != null) {
                list = fileLoaderForPath.listDir(strConvertAbstractPath);
            } else if (isAssetPath(strConvertAbstractPath)) {
                list = LegacyAssetBridge.listAssets(stripAssetPathPrefix(strConvertAbstractPath));
            } else if (isAbstractPath(str)) {
                if (this.disableAssets) {
                    return null;
                }
                list = GameEngine.getInstance().assetIndex.listDir(strConvertAbstractPath);
            } else {
                File file = new File(strConvertAbstractPath);
                if (file == null || !file.exists()) {
                    String str2 = "listDir: path doesn't exist:" + strConvertAbstractPath;
                    GameEngine.logColored(str2);
                    FileHelper.setWritePath(str2);
                    return null;
                }
                list = file.list();
                if (list == null) {
                    if (file != null && !file.isDirectory()) {
                        FileHelper.setWritePath("path is not a directory, .rwmod or .zip");
                        return null;
                    }
                    return null;
                }
            }
            if (list == null) {
                GameEngine.log(this.TAG + "listDir baseList==null:" + str + " (non folder?)");
                return null;
            }
            ArrayList arrayList = new ArrayList();
            if (z) {
                for (String str3 : list) {
                    if (str3.toLowerCase(Locale.ENGLISH).endsWith(".tmx")) {
                        arrayList.add(str3);
                    }
                }
            } else {
                for (String str4 : list) {
                    arrayList.add(str4);
                }
            }
            Collections.sort(arrayList);
            return (String[]) arrayList.toArray(new String[0]);
        } catch (OutOfMemoryError e) {
            FileHelper.setWritePath(e.getMessage());
            return null;
        }
    }

    /* JADX INFO: renamed from: h */
    public File createFileCaseInsensitive(String str) {
        if (str.contains("\\")) {
            str = str.replace('\\', '/');
        }
        File file = new File(str);
        if (file.exists()) {
            return file;
        }
        File parentFile = file.getParentFile();
        if (parentFile == null || !parentFile.isDirectory()) {
            parentFile = createFileCaseInsensitive(parentFile.getAbsolutePath());
            if (parentFile == null || !parentFile.isDirectory()) {
                GameEngine.log(this.TAG + "createFileCaseInsensitive: did not find parent for: " + str);
                return null;
            }
        }
        File[] fileArrListFiles = parentFile.listFiles();
        if (fileArrListFiles == null) {
            GameEngine.log(this.TAG + "createFileCaseInsensitive: Failed to list files for: " + str + " in " + parentFile);
            return null;
        }
        for (File file2 : fileArrListFiles) {
            if (file2.getName().equalsIgnoreCase(file.getName())) {
                return file2;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: i */
    public AssetInputStream openAsset(String str) {
        if (str.startsWith("assets/") || str.startsWith("assets\\")) {
            str = str.substring("assets/".length());
        }
        String str2 = str;
        String str3 = "assets/" + str;
        try {
            return LegacyAssetBridge.openAsset(str2);
        } catch (Exception e2) {
            GameEngine.log(this.TAG + "Could not find asset:" + str3);
            return null;
        }
    }

    /* JADX INFO: renamed from: j */
    public AssetInputStream openAssetSteam(String str) {
        AssetInputStream assetInputStream;
        String strConvertAbstractPath = convertAbstractPath(str);
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        if (fileLoaderForPath != null && !strConvertAbstractPath.endsWith(".rwmod")) {
            return fileLoaderForPath.openAssetInputStream(strConvertAbstractPath, true);
        }
        if (str.startsWith("/SD/") || str.startsWith("\\SD\\")) {
            String strSubstring = str.substring("/SD/".length());
            String strSubstring2 = strSubstring;
            if (strSubstring2.startsWith("rustedWarfare/")) {
                strSubstring2 = strSubstring2.substring("rustedWarfare/".length());
            }
            String str2 = b() + strSubstring2;
            if (this.debug) {
                GameEngine.log(this.TAG + "openAssetSteam converted:" + strSubstring + " to: " + str2);
            }
            try {
                File fileCreateFileCaseInsensitive = createFileCaseInsensitive(str2);
                if (fileCreateFileCaseInsensitive == null) {
                    return null;
                }
                assetInputStream = new AssetInputStream(new FileInputStream(fileCreateFileCaseInsensitive), fileCreateFileCaseInsensitive.getAbsolutePath());
            } catch (FileNotFoundException e) {
                return null;
            }
        } else if (isAbsolutePath(str)) {
            try {
                File fileCreateFileCaseInsensitive2 = createFileCaseInsensitive(str);
                if (fileCreateFileCaseInsensitive2 == null) {
                    return null;
                }
                assetInputStream = new AssetInputStream(new FileInputStream(fileCreateFileCaseInsensitive2), fileCreateFileCaseInsensitive2.getAbsolutePath());
            } catch (FileNotFoundException e2) {
                return null;
            }
        } else {
            assetInputStream = openAsset(str);
        }
        return assetInputStream;
    }

    /* JADX INFO: renamed from: c */
    public OutputStream openOutputStream(String str, boolean z) throws FileNotFoundException {
        String strConvertAbstractPath = convertAbstractPath(str);
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        if (fileLoaderForPath != null && !strConvertAbstractPath.endsWith(".rwmod")) {
            return fileLoaderForPath.openOutputStream(strConvertAbstractPath, z);
        }
        return new FileOutputStream(strConvertAbstractPath, z);
    }

    /* JADX INFO: renamed from: k */
    public boolean createDirectory(String str) {
        String strConvertAbstractPath = convertAbstractPath(str);
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        if (fileLoaderForPath != null && !strConvertAbstractPath.endsWith(".rwmod")) {
            boolean zCreateDirectory = fileLoaderForPath.createDirectory(strConvertAbstractPath);
            if (!zCreateDirectory) {
                GameEngine.log("Failed to create directory: " + strConvertAbstractPath + " using reader:" + fileLoaderForPath);
            }
            return zCreateDirectory;
        }
        boolean zMkdirs = new File(strConvertAbstractPath).mkdirs();
        if (!zMkdirs) {
            GameEngine.log("Failed to create directory: " + strConvertAbstractPath);
        }
        return zMkdirs;
    }

    public String b() {
        if (GameEngine.isNonAndroidVersion) {
            return VariableScope.nullOrMissingString;
        }
        return getExternalStoragePath() + "/rustedWarfare/";
    }

    /* JADX INFO: renamed from: c */
    public String getCachePath() {
        if (GameEngine.isAndroidPlatform()) {
            String absolutePath = LegacyAssetBridge.cacheDir().getAbsolutePath();
            if (!absolutePath.endsWith("/")) {
                absolutePath = absolutePath + "/";
            }
            return absolutePath;
        }
        String strB = b();
        if (strB.equals(VariableScope.nullOrMissingString)) {
            return "cache/";
        }
        return strB + "/cache/";
    }

    /* JADX INFO: renamed from: l */
    public long getLastModified(String str) {
        String strConvertAbstractPath = convertAbstractPath(str);
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        if (fileLoaderForPath != null) {
            return fileLoaderForPath.getFullPath(strConvertAbstractPath);
        }
        File file = new File(strConvertAbstractPath);
        if (!file.exists()) {
        }
        return file.lastModified();
    }

    /* JADX INFO: renamed from: a */
    public void scanFile(File file) {
        if (GameEngine.isAndroidPlatform()) {
        }
    }

    /* JADX INFO: renamed from: a */
    public File getRWFile(String str, String str2, boolean z) {
        File file = new File(b() + str2 + str);
        if (z) {
            File parentFile = file.getParentFile();
            if (!FileHelper.fileExists(parentFile.getAbsolutePath())) {
                GameEngine.log("Making missing parent dir: " + parentFile.getAbsolutePath());
                if (!FileHelper.deleteFile(parentFile.getAbsolutePath())) {
                    GameEngine.logColored("getRWFile: Could not create parent directory");
                }
            }
            if (GameEngine.isAndroidPlatform()) {
            }
        }
        return file;
    }

    public String d() {
        return "external";
    }

    /* JADX INFO: renamed from: m */
    public String getStorageTypeForPath(String str) {
        return d();
    }

    public boolean e() {
        return true;
    }

    /* JADX INFO: renamed from: n */
    public String fixPath(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf("[INTERNAL-PATH]/");
        if (iIndexOf != -1) {
            String str2 = str.substring(0, iIndexOf) + str.substring(iIndexOf + "[INTERNAL-PATH]/".length());
            if (str2.contains("[INTERNAL-PATH]/") || str2.contains("[EXTERNAL-PATH]/")) {
                GameEngine.log("fixPath: double tag for: " + str);
            }
            return str2;
        }
        int iIndexOf2 = str.indexOf("[EXTERNAL-PATH]/");
        if (iIndexOf2 != -1) {
            String str3 = str.substring(0, iIndexOf2) + str.substring(iIndexOf2 + "[EXTERNAL-PATH]/".length());
            if (str3.contains("[INTERNAL-PATH]/") || str3.contains("[EXTERNAL-PATH]/")) {
                GameEngine.log("fixPath: double tag for: " + str);
            }
            return str3;
        }
        return str;
    }

    public String o(String str) {
        return str;
    }

    /* JADX INFO: renamed from: b */
    public boolean deleteFile(File file) {
        GameEngine.log("deleteFile: " + file.getAbsolutePath());
        IFileLoader zipFileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(file.getAbsolutePath());
        if (zipFileLoaderForPath != null) {
            GameEngine.log("Mapped delete");
            return zipFileLoaderForPath.delete(file.getAbsolutePath());
        }
        GameEngine.log("Native delete");
        return file.delete();
    }

    /* JADX INFO: renamed from: a */
    public boolean renameFile(File file, File file2) {
        GameEngine.log("renameFile: " + file.getAbsolutePath() + " to:" + file2.getAbsolutePath());
        IFileLoader zipFileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(file.getAbsolutePath());
        if (zipFileLoaderForPath != null) {
            try {
                boolean rWFile = zipFileLoaderForPath.getRWFile(file.getAbsolutePath(), file2.getAbsolutePath());
                FileLoaderFactory.closeModFile(file2.getAbsolutePath());
                return rWFile;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        boolean zRenameTo = file.renameTo(file2);
        FileLoaderFactory.closeModFile(file2.getAbsolutePath());
        return zRenameTo;
    }

    /* JADX INFO: renamed from: p */
    public boolean isSaf(String str) {
        IFileLoader zipFileLoaderForPath = FileLoaderFactory.getZipFileLoaderForPath(convertAbstractPath(str));
        if (zipFileLoaderForPath != null && (zipFileLoaderForPath instanceof SafFileLoader)) {
            return true;
        }
        return false;
    }
}
