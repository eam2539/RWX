package android.app;

import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import java.util.ArrayList;

/* JADX INFO: loaded from: game-lib.jar:android/app/Application.class */
public class Application extends ContextWrapper implements ComponentCallbacks2 {
    private ArrayList b;
    private ArrayList c;
    private ArrayList d;

    /* JADX INFO: loaded from: game-lib.jar:android/app/Application$ActivityLifecycleCallbacks.class */
    public interface ActivityLifecycleCallbacks {
    }

    /* JADX INFO: loaded from: game-lib.jar:android/app/Application$OnProvideAssistDataListener.class */
    public interface OnProvideAssistDataListener {
    }

    public Application() {
        super(null);
        this.b = new ArrayList();
        this.c = new ArrayList();
        this.d = null;
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        Object[] objArrA = a();
        if (objArrA != null) {
            for (Object obj : objArrA) {
                ((ComponentCallbacks) obj).onConfigurationChanged(configuration);
            }
        }
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        Object[] objArrA = a();
        if (objArrA != null) {
            for (Object obj : objArrA) {
                ((ComponentCallbacks) obj).onLowMemory();
            }
        }
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
        Object[] objArrA = a();
        if (objArrA != null) {
            for (Object obj : objArrA) {
                if (obj instanceof ComponentCallbacks2) {
                    ((ComponentCallbacks2) obj).onTrimMemory(i);
                }
            }
        }
    }

    private Object[] a() {
        Object[] array = null;
        synchronized (this.b) {
            if (this.b.size() > 0) {
                array = this.b.toArray();
            }
        }
        return array;
    }
}
