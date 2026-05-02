package com.corrodinggames.rts.gameFramework.file;

import android.content.Context;
import android.os.Build;
import com.corrodinggames.rts.appFramework.AppFrameworkUtils;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.e.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/e/a.class */
public class FileHelper {

    /* JADX INFO: renamed from: a */
    public static final FileLoader defaultFileLoader = new FileLoader();

    /* JADX INFO: renamed from: b */
    public static FileLoader activeFileLoader = defaultFileLoader;

    /* JADX INFO: renamed from: c */
    public static Boolean useStorageApi;

    /* JADX INFO: renamed from: d */
    public static String overriddenExternalPath;

    /* JADX INFO: renamed from: e */
    public static String loadError;

    /* JADX INFO: renamed from: a */
    protected static String getInternalPath() {
        File fileB = AppFrameworkUtils.getContext().b(null);
        if (fileB != null) {
            return fileB.getAbsolutePath();
        }
        GameEngine.updatePaintTextSizeIfNeeded("Failed to get an internal path.");
        return null;
    }

    /* JADX WARN: Failed to analyze thrown exceptions
    java.util.ConcurrentModificationException
    	at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1095)
    	at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1049)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:130)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:68)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.checkInsn(MethodThrowsVisitor.java:178)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:131)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:68)
     */
    /* JADX INFO: renamed from: b */
    public static void initialize() {
        loadError = null;
        if (GameEngine.isDesktop()) {
            if (Build.VERSION.SDK_INT < 19) {
                loadError = "Android version too old for new file system support";
                GameEngine.log("FileLoader: SDK too old, not changing FileLoader");
                return;
            }
            int i = GameEngine.getInstance().settingsEngine.storageType;
            GameEngine.log("FileLoader: storageBehaviour:" + i);
            FileLoader fileLoaderCreateFileLoader = createFileLoader(i);
            GameEngine.log("Using file loader: " + fileLoaderCreateFileLoader.d());
            activeFileLoader = fileLoaderCreateFileLoader;
        }
    }

    /* JADX INFO: renamed from: a */
    public static boolean isManagedPath(String str) {
        return activeFileLoader.isSaf(str);
    }

    /* JADX INFO: renamed from: a */
    public static FileAccessFlags getFileAccessFlags(boolean z) {
        FileAccessFlags fileAccessFlags = new FileAccessFlags();
        if (!GameEngine.isDesktop()) {
            fileAccessFlags.useSaf = false;
            fileAccessFlags.useDirectAccess = true;
            return fileAccessFlags;
        }
        if (Build.VERSION.SDK_INT < 19) {
            fileAccessFlags.useSaf = false;
            fileAccessFlags.useDirectAccess = true;
            return fileAccessFlags;
        }
        fileAccessFlags.useSaf = true;
        fileAccessFlags.useOverriddenExternalPath = false;
        if (overriddenExternalPath != null) {
            fileAccessFlags.useOverriddenExternalPath = true;
        }
        if (useStorageApi != null && !useStorageApi.booleanValue()) {
            fileAccessFlags.useDirectAccess = true;
            fileAccessFlags.useSaf = false;
            fileAccessFlags.useOverriddenExternalPath = false;
        }
        if (Build.VERSION.SDK_INT <= 28 && useStorageApi == null) {
            GameEngine.updatePaintTextSizeIfNeeded("FileLoader using direct external access due to sdk: " + Build.VERSION.SDK_INT);
            fileAccessFlags.useDirectAccess = true;
            fileAccessFlags.useSaf = false;
            fileAccessFlags.useOverriddenExternalPath = false;
        }
        return fileAccessFlags;
    }

    /* JADX INFO: renamed from: a */
    public static FileLoader createFileLoader(int i) {
        FileLoader customPathFileLoader;
        MergedFileLoader mergedFileLoader;
        if (!GameEngine.isDesktop()) {
            return new FileLoader();
        }
        if (Build.VERSION.SDK_INT >= 19) {
            String internalPath = getInternalPath();
            CustomPathFileLoader customPathFileLoader2 = null;
            if (internalPath == null) {
                loadError = "Failed to get internal app path (is it unmounted?).";
                i = 3;
            } else {
                customPathFileLoader2 = new CustomPathFileLoader(internalPath, "internal");
                customPathFileLoader2.mapPath = "Internal: ";
            }
            FileAccessFlags fileAccessFlags = getFileAccessFlags(false);
            if (!fileAccessFlags.useOverriddenExternalPath) {
                if (!fileAccessFlags.useDirectAccess) {
                    GameEngine.updatePaintTextSizeIfNeeded("Not using direct external backend: As direct reads will cause problems");
                    customPathFileLoader = null;
                    i = 0;
                } else {
                    GameEngine.updatePaintTextSizeIfNeeded("FileLoader using direct external file access! SDK:" + Build.VERSION.SDK_INT);
                    customPathFileLoader = new FileLoader();
                }
            } else {
                GameEngine.log("FileLoader using overriddenExternalPath:" + overriddenExternalPath);
                customPathFileLoader = new CustomPathFileLoader(overriddenExternalPath, "external");
            }
            NullFileLoader nullFileLoader = new NullFileLoader();
            if (i != 3 && customPathFileLoader2 == null) {
                GameEngine.updatePaintTextSizeIfNeeded("No available file backends!!");
                return nullFileLoader;
            }
            if (i == 1) {
                mergedFileLoader = new MergedFileLoader(customPathFileLoader2, "[INTERNAL-PATH]/", customPathFileLoader, "[EXTERNAL-PATH]/");
            } else if (i == 2) {
                mergedFileLoader = new MergedFileLoader(customPathFileLoader, "[EXTERNAL-PATH]/", customPathFileLoader2, "[INTERNAL-PATH]/");
            } else if (i == 3) {
                mergedFileLoader = new MergedFileLoader(customPathFileLoader, "[EXTERNAL-PATH]/", nullFileLoader, "[NULL-PATH]/");
            } else {
                mergedFileLoader = new MergedFileLoader(customPathFileLoader2, "[INTERNAL-PATH]/", nullFileLoader, "[NULL-PATH]/");
            }
            mergedFileLoader.fileLoader2.disableAssets = true;
            return mergedFileLoader;
        }
        GameEngine.log("FileLoader: SDK too old, not changing FileLoader");
        return new FileLoader();
    }

    /* JADX INFO: renamed from: c */
    public static String getReadPath() {
        return activeFileLoader.getLastError();
    }

    /* JADX INFO: renamed from: b */
    public static void setWritePath(String str) {
        activeFileLoader.setLastError(str);
    }

    /* JADX INFO: renamed from: a */
    public static String findFileWithExtension(String str, String str2) {
        return activeFileLoader.findFileExtension(str, str2);
    }

    /* JADX INFO: renamed from: c */
    public static boolean isAbstractPath(String str) {
        return activeFileLoader.isAbstractPath(str);
    }

    /* JADX INFO: renamed from: d */
    public static String getFileName(String str) {
        return activeFileLoader.getFileName(str);
    }

    /* JADX INFO: renamed from: e */
    public static String convertAbstractPath(String str) {
        return activeFileLoader.convertAbstractPath(str);
    }

    /* JADX INFO: renamed from: f */
    public static boolean isDirectoryNonZip(String str) {
        return activeFileLoader.isDirectory(str, false);
    }

    /* JADX INFO: renamed from: g */
    public static boolean isDirectory(String str) {
        return activeFileLoader.isDirectory(str, true);
    }

    /* JADX INFO: renamed from: h */
    public static String[] listFiles(String str) {
        return activeFileLoader.b(str, false);
    }

    /* JADX INFO: renamed from: a */
    public static String[] listFilesRecursive(String str, boolean z) {
        return activeFileLoader.b(str, z);
    }

    /* JADX INFO: renamed from: i */
    public static boolean fileExists(String str) {
        return activeFileLoader.fileExists(str);
    }

    /* JADX INFO: renamed from: j */
    public static AssetInputStream openAsset(String str) {
        return activeFileLoader.openAsset(str);
    }

    /* JADX INFO: renamed from: a */
    public static AssetInputStream openFile(File file) {
        return activeFileLoader.openAssetSteam(file.getAbsolutePath());
    }

    /* JADX INFO: renamed from: k */
    public static AssetInputStream openFileByPath(String str) {
        return activeFileLoader.openAssetSteam(str);
    }

    /* JADX INFO: renamed from: a */
    public static OutputStream openOutputStream(File file, boolean z) throws FileNotFoundException {
        return activeFileLoader.openOutputStream(file.getAbsolutePath(), z);
    }

    /* JADX INFO: renamed from: b */
    public static OutputStream openOutputStreamByPath(String str, boolean z) throws FileNotFoundException {
        return activeFileLoader.openOutputStream(str, z);
    }

    /* JADX INFO: renamed from: l */
    public static boolean deleteFile(String str) {
        return activeFileLoader.createDirectory(str);
    }

    /* JADX INFO: renamed from: d */
    public static String getExternalStoragePath() {
        return activeFileLoader.b();
    }

    /* JADX INFO: renamed from: e */
    public static String getWorkingDirectory() {
        return activeFileLoader.getCachePath();
    }

    /* JADX INFO: renamed from: m */
    public static long getFileSize(String str) {
        return activeFileLoader.getLastModified(str);
    }

    /* JADX INFO: renamed from: a */
    public static File createTempFile(String str, String str2, boolean z) {
        return activeFileLoader.getRWFile(str, str2, z);
    }

    /* JADX INFO: renamed from: a */
    public static boolean renameFile(File file, File file2) {
        if (GameEngine.isPC() && file2.exists()) {
            file2.delete();
        }
        if (!file.renameTo(file2)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: b */
    public static boolean renameFileInternal(File file, File file2) {
        return activeFileLoader.renameFile(file, file2);
    }

    /* JADX INFO: renamed from: b */
    public static boolean deleteDirectory(File file) {
        return activeFileLoader.deleteFile(file);
    }

    /* JADX INFO: renamed from: n */
    public static String getAbsolutePath(String str) {
        return activeFileLoader.getStorageTypeForPath(str);
    }

    /* JADX INFO: renamed from: f */
    public static boolean isZip() {
        return activeFileLoader.e();
    }

    /* JADX INFO: renamed from: o */
    public static String mapPath(String str) {
        return activeFileLoader.fixPath(str);
    }

    /* JADX INFO: renamed from: p */
    public static String getSourcePath(String str) {
        return activeFileLoader.o(str);
    }

    /* JADX INFO: renamed from: a */
    public static File createTempFileInContext(Context context, String str, String str2) throws IOException {
        try {
            return File.createTempFile(str, str2, context.i());
        } catch (IOException e) {
            try {
                return File.createTempFile(str, str2, context.j());
            } catch (IOException e2) {
                e.printStackTrace();
                throw e2;
            }
        }
    }

    /* JADX INFO: renamed from: c */
    public static void scanFile(File file) {
        activeFileLoader.scanFile(file);
    }
}
