package android.content;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/* JADX INFO: loaded from: game-lib.jar:android/content/ContextWrapper.class */
public class ContextWrapper extends Context {
    Context a;

    public ContextWrapper(Context context) {
        this.a = context;
    }

    public Context k() {
        return this.a;
    }

    @Override // android.content.Context
    public AssetManager d() {
        return this.a.d();
    }

    @Override // android.content.Context
    public Resources e() {
        return this.a.e();
    }

    @Override // android.content.Context
    public PackageManager f() {
        return this.a.f();
    }

    @Override // android.content.Context
    public Context g() {
        return this.a.g();
    }

    @Override // android.content.Context
    public String h() {
        return this.a.h();
    }

    @Override // android.content.Context
    public SharedPreferences a(String str, int i) {
        return this.a.a(str, i);
    }

    @Override // android.content.Context
    public FileInputStream a(String str) {
        return this.a.a(str);
    }

    @Override // android.content.Context
    public FileOutputStream b(String str, int i) {
        return this.a.b(str, i);
    }

    @Override // android.content.Context
    public File b(String str) {
        return this.a.b(str);
    }

    @Override // android.content.Context
    public File i() {
        return this.a.i();
    }

    @Override // android.content.Context
    public File j() {
        return this.a.j();
    }

    @Override // android.content.Context
    public void a(Intent intent) {
        this.a.a(intent);
    }

    @Override // android.content.Context
    public Object c(String str) {
        return this.a.c(str);
    }
}
