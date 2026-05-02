package com.corrodinggames.rts.appFramework;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.local.Locale;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.rts.appFramework.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/appFramework/c.class */
public class AppFrameworkUtils {

    /* JADX INFO: renamed from: a */
    static Handler handler;

    /* JADX INFO: renamed from: b */
    static volatile Context applicationContext;

    /* JADX INFO: renamed from: c */
    public static final RenderMethod storageTypeDefault = RenderMethod.dynamicDefault;

    /* JADX INFO: renamed from: d */
    public static RenderMethod storageType = storageTypeDefault;

    /* JADX INFO: renamed from: a */
    public static int getNumberOfPlayersInMap(String str) {
        String fileNameWithoutExtension = null;
        if (str != null) {
            fileNameWithoutExtension = Utility.getFileNameWithoutExtension(str);
        }
        if (fileNameWithoutExtension != null) {
            Matcher matcher = Pattern.compile("^ *\\[([^\\]]*)\\].*").matcher(fileNameWithoutExtension);
            if (matcher.matches()) {
                for (String str2 : matcher.group(1).split(";")) {
                    if (str2.startsWith("p") && str2.length() >= 2) {
                        String strSubstring = str2.substring(1);
                        try {
                            return Integer.parseInt(strSubstring);
                        } catch (NumberFormatException e) {
                            GameEngine.log("getNumberOfPlayersInMap: NumberFormatException:" + strSubstring);
                            return -1;
                        }
                    }
                }
            }
        }
        GameEngine.log("getNumberOfPlayersInMap: fail to match:" + fileNameWithoutExtension);
        return -1;
    }

    /* JADX INFO: renamed from: b */
    public static String getMapName(String str) {
        if (str == null) {
            return null;
        }
        if (str.contains(File.separator)) {
            String[] strArrSplit = str.split(Pattern.quote(File.separator));
            str = strArrSplit[strArrSplit.length - 1];
        }
        if (str.contains("/")) {
            String[] strArrSplit2 = str.split("/");
            str = strArrSplit2[strArrSplit2.length - 1];
        }
        String strGroup = null;
        if (0 == 0) {
            Matcher matcher = Pattern.compile("^l\\d*;\\[.*\\](.+)\\.tmx").matcher(str);
            if (matcher.matches()) {
                strGroup = matcher.group(1);
                if (strGroup.length() >= 1) {
                    strGroup = strGroup.substring(0, 1).toUpperCase() + strGroup.substring(1);
                }
            }
        }
        if (strGroup == null) {
            Matcher matcher2 = Pattern.compile("^l\\d*;(.+)\\.tmx").matcher(str);
            if (matcher2.matches()) {
                strGroup = matcher2.group(1);
                if (strGroup.length() >= 1) {
                    strGroup = strGroup.substring(0, 1).toUpperCase() + strGroup.substring(1);
                }
            }
        }
        if (strGroup == null) {
            Matcher matcher3 = Pattern.compile("^ *\\[.*\\](.+)\\.tmx").matcher(str);
            if (matcher3.matches()) {
                strGroup = matcher3.group(1);
                if (strGroup.length() >= 1) {
                    strGroup = strGroup.substring(0, 1).toUpperCase() + strGroup.substring(1);
                }
            }
        }
        if (strGroup == null) {
            Matcher matcher4 = Pattern.compile("(.*)\\.tmx").matcher(str);
            if (matcher4.matches()) {
                strGroup = matcher4.group(1);
                if (strGroup.length() >= 1) {
                    strGroup = strGroup.substring(0, 1).toUpperCase() + strGroup.substring(1);
                }
            }
        }
        if (strGroup == null) {
            strGroup = str;
        }
        String strReplace = strGroup.replace('_', ' ');
        if (strReplace.endsWith(".rwsave")) {
            strReplace = strReplace.replace(".rwsave", VariableScope.nullOrMissingString);
        }
        return strReplace;
    }

    /* JADX INFO: renamed from: c */
    public static String getMapThumbnail(String str) {
        return str.replace(".tmx", VariableScope.nullOrMissingString) + "_map.png";
    }

    /* JADX INFO: renamed from: c */
    private static void setImmersiveMode(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            activity.a().getDecorView().setSystemUiVisibility(5894);
        }
    }

    /* JADX INFO: renamed from: d */
    private static void unsetImmersiveMode(Activity activity) {
    }

    /* JADX INFO: renamed from: a */
    public static void postRunnable(Runnable runnable) {
        if (handler == null) {
            handler = new Handler(Looper.b());
        }
        handler.a(runnable);
    }

    /* JADX INFO: renamed from: a */
    public static Context getContext() {
        if (applicationContext == null) {
            throw new RuntimeException("ApplicationContext==null");
        }
        return applicationContext;
    }

    /* JADX INFO: renamed from: a */
    public static void setup(Activity activity) {
        if (applicationContext == null) {
            applicationContext = activity.g();
        }
    }

    /* JADX INFO: renamed from: a */
    public static void setup(Context context) {
        if (applicationContext == null) {
            applicationContext = context.g();
        }
    }

    /* JADX INFO: renamed from: a */
    public static void onActivityNewIntent(Activity activity, boolean z, boolean z2) {
        setup(activity);
        if (z2) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (gameEngine != null && gameEngine.settingsEngine.immersiveFullScreen) {
                setImmersiveMode(activity);
            }
        } else {
            unsetImmersiveMode(activity);
        }
        GameEngine gameEngine2 = GameEngine.getInstance();
        if (gameEngine2 != null) {
            gameEngine2.showPendingMessageBox();
        }
        if (z) {
            activity.a().setBackgroundDrawable(null);
        }
    }

    /* JADX INFO: renamed from: a */
    public static void onActivitySetContentView(Activity activity, boolean z) {
        if (z) {
            activity.a(0, 0);
        }
    }

    /* JADX INFO: renamed from: a */
    public static boolean askForStoragePermission(Activity activity, Runnable runnable) {
        GameEngine.getInstance();
        if (askForStoragePermission(activity, runnable, false)) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: a */
    public static boolean askForStoragePermission(final Activity activity, final Runnable runnable, boolean z) {
        final GameEngine gameEngine = GameEngine.getInstance();
        if ((!z && gameEngine.settingsEngine.hasSelectedAStorageType) || Build.VERSION.SDK_INT < 19) {
            return false;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.corrodinggames.rts.appFramework.c.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                gameEngine.settingsEngine.storageType = 1;
                gameEngine.settingsEngine.hasSelectedAStorageType = true;
                FileHelper.initialize();
                gameEngine.settingsEngine.save();
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        DialogInterface.OnClickListener onClickListener2 = new DialogInterface.OnClickListener() { // from class: com.corrodinggames.rts.appFramework.c.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!FileHelper.getFileAccessFlags(true).useSaf) {
                    GameEngine.log("Storage setup: Not using SAF, not showing setup folder popup");
                    if (AppFrameworkUtils.requestStoragePermission(activity)) {
                        gameEngine.settingsEngine.storageType = 2;
                        gameEngine.settingsEngine.hasSelectedAStorageType = true;
                        FileHelper.initialize();
                        gameEngine.settingsEngine.save();
                        return;
                    }
                    return;
                }
                if (activity instanceof SelectFolderActivity) {
                    GameEngine.log("Storage setup: Already on settings page");
                    ((SelectFolderActivity) activity).showFolderChooser();
                    return;
                }
                Intent intent = new Intent(activity, (Class<?>) SelectFolderActivity.class);
                intent.putExtra("mode", "setupExternalFolder");
                AppFrameworkUtils.applySelectMapIntentFlags(intent);
                activity.a(intent);
                if (activity instanceof TaskQueueActivity) {
                    if (runnable != null) {
                        ((TaskQueueActivity) activity).a(new Runnable() { // from class: com.corrodinggames.rts.appFramework.c.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (gameEngine.settingsEngine.hasSelectedAStorageType) {
                                    runnable.run();
                                }
                            }
                        });
                        return;
                    }
                    return;
                }
                GameEngine.updatePaintTextSizeIfNeeded("context not instance CommonActivity");
            }
        };
        String str = Locale.get("menus.mods.androidStorageSetupTitle", new Object[0]);
        String str2 = Locale.get("menus.mods.androidStorageSetupMessage", new Object[0]);
        String str3 = Locale.get("menus.mods.androidStorageSetupInternal", new Object[0]);
        new AlertDialog.Builder(activity).setIcon(R.drawable.ic_dialog_alert).setTitle(str).setMessage(str2).setPositiveButton(str3, onClickListener).setNeutralButton(Locale.get("menus.mods.androidStorageSetupExternal", new Object[0]), onClickListener2).show();
        GameEngine.log("Showing storage setup");
        return true;
    }

    /* JADX INFO: renamed from: b */
    public static boolean hasStoragePermission(Context context) {
        if (GameEngine.isPausedStatic2 || !FileHelper.isZip() || Build.VERSION.SDK_INT < 23 || ContextCompat.a(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: b */
    public static boolean requestStoragePermission(Activity activity) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!GameEngine.isPausedStatic2 && FileHelper.isZip() && Build.VERSION.SDK_INT >= 23) {
            if (gameEngine.clearGameState() == null) {
            }
            if (ContextCompat.a(activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                gameEngine.settingsEngine.hadStoragePermissionInPast = true;
                GameEngine.log("File Permission is granted");
                return true;
            }
            GameEngine.log("Permission is revoked");
            ActivityCompat.a(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public static void applySelectMapIntentFlags(Intent intent) {
        intent.addFlags(65536);
    }

    /* JADX INFO: renamed from: a */
    public static void showSAFDirectoryChooser(Activity activity, int i, boolean z, String str, Uri uri) {
        GameEngine.log("Show folder chooser. Write:" + z);
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        intent.addFlags(64);
        intent.addFlags(1);
        if (z) {
            intent.addFlags(2);
        }
        if (uri != null) {
            intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        }
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        try {
            activity.a(Intent.createChooser(intent, str), i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Failed to open file list. Please install a File Manager.", 0).show();
        }
    }
}
