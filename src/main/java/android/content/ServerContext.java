package android.content;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/* JADX INFO: loaded from: game-lib.jar:android/content/ServerContext.class */
public class ServerContext extends Context {
    AssetManager a = new AssetManager();

    @Override // android.content.Context
    public AssetManager d() {
        return this.a;
    }

    @Override // android.content.Context
    public Resources e() {
        return null;
    }

    @Override // android.content.Context
    public PackageManager f() {
        return null;
    }

    @Override // android.content.Context
    public Context g() {
        return this;
    }

    @Override // android.content.Context
    public String h() {
        return null;
    }

    @Override // android.content.Context
    public SharedPreferences a(String str, int i) {
        return null;
    }

    @Override // android.content.Context
    public FileInputStream a(String str) {
        return null;
    }

    @Override // android.content.Context
    public FileOutputStream b(String str, int i) {
        return null;
    }

    @Override // android.content.Context
    public File b(String str) {
        return null;
    }

    @Override // android.content.Context
    public File i() {
        return null;
    }

    @Override // android.content.Context
    public File j() {
        return null;
    }

    @Override // android.content.Context
    public void a(Intent intent) {
    }

    @Override // android.content.Context
    public Object c(String str) {
        return null;
    }
}
