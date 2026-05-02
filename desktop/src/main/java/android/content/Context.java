package android.content;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/* JADX INFO: loaded from: game-lib.jar:android/content/Context.class */
public abstract class Context {
    public abstract AssetManager d();

    public abstract Resources e();

    public abstract PackageManager f();

    public abstract Context g();

    public abstract String h();

    public abstract SharedPreferences a(String str, int i);

    public abstract FileInputStream a(String str);

    public abstract FileOutputStream b(String str, int i);

    public abstract File b(String str);

    public abstract File i();

    public abstract File j();

    public abstract void a(Intent intent);

    public abstract Object c(String str);
}
