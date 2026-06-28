package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.utility.GameObjectArrayList;
import com.corrodinggames.rts.gameFramework.utility.TransactionalArrayList;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.w */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/w.class */
public abstract class GameObject extends Serializable {

    /* JADX INFO: renamed from: eh */
    public long objectId;

    /* JADX INFO: renamed from: ej */
    public boolean isDestroyed;

    /* JADX INFO: renamed from: ek */
    public boolean isStatic;

    /* JADX INFO: renamed from: el */
    public boolean shouldDraw;

    /* JADX INFO: renamed from: em */
    public int drawLayer;

    /* JADX INFO: renamed from: en */
    public int drawOrder;

    /* JADX INFO: renamed from: eo */
    public float posX;

    /* JADX INFO: renamed from: ep */
    public float posY;

    /* JADX INFO: renamed from: eq */
    public float posZ;
    public static GameObjectComparator ei = new GameObjectComparator();
    private static final TransactionalArrayList a = new TransactionalArrayList();

    /* JADX INFO: renamed from: er */
    public static final GameObjectArrayList fastGameObjectList = new GameObjectArrayList("fastGameObjectList");

    /* JADX INFO: renamed from: a */
    public abstract void update(float delta);

    public abstract void a(float f, boolean z);

    public abstract void d(float f);

    public abstract void e(float f);

    public abstract boolean c(float f);

    public abstract boolean f(float f);

    public void S(int i) {
        this.drawLayer = i;
    }

    @Override // com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(this.isDestroyed);
        gameOutputStream.writeBoolean(this.isStatic);
        gameOutputStream.writeInt(this.drawLayer);
    }

    public void a(GameInputStream gameInputStream) throws IOException {
        this.isDestroyed = gameInputStream.readBoolean();
        this.isStatic = gameInputStream.readBoolean();
        this.drawLayer = gameInputStream.readInt();
    }

    public GameObject() {
        this(false);
    }

    public GameObject(boolean z) {
        this.isDestroyed = false;
        this.isStatic = false;
        this.drawLayer = 2;
        this.drawOrder = 0;
        this.posZ = 0.0f;
        if (!z) {
            GameEngine gameEngine = GameEngine.getInstance();
            if (this.objectId != 0) {
                throw new RuntimeException("ID for GameObject is already set at:" + this.objectId);
            }
            this.objectId = gameEngine.networkEngine.y();
            if (this.objectId == 0) {
                throw new RuntimeException("Adding object with id:0 class:" + getClass().getSimpleName());
            }
            traceAllocation(gameEngine);
            a.a(this);
            fastGameObjectList.add(this);
            return;
        }
        this.objectId = 0L;
    }

    private void traceAllocation(GameEngine gameEngine) {
        if (!"1".equals(System.getenv("RWX_PROBE_OBJECT_ALLOC_TRACE"))) {
            return;
        }
        int tick = gameEngine.currentTick;
        String startValue = System.getenv("RWX_PROBE_OBJECT_ALLOC_START_TICK");
        String endValue = System.getenv("RWX_PROBE_OBJECT_ALLOC_END_TICK");
        if (startValue != null && endValue != null) {
            try {
                if (tick < Integer.parseInt(startValue) || tick > Integer.parseInt(endValue)) {
                    return;
                }
            } catch (NumberFormatException ignored) {
                return;
            }
        }
        StringBuilder stack = new StringBuilder();
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 3; i < trace.length && i < 15; i++) {
            if (stack.length() > 0) {
                stack.append("|");
            }
            stack.append(trace[i]);
        }
        System.out.println("OBJECT_ALLOC tick=" + tick + " id=" + this.objectId +
                " class=" + getClass().getName() + " stack=" + stack);
    }

    public void p(float f) {
    }

    public boolean a(GameEngine gameEngine) {
        return true;
    }

    /* JADX INFO: renamed from: a */
    public void remove() {
        if (this.objectId != 0) {
            a.b(this);
            fastGameObjectList.remove(this);
        }
        this.isDestroyed = true;
    }

    public static GameObject a(long j, Class cls, boolean z) {
        if (j == -1) {
            return null;
        }
        GameObject[] gameObjectArrA = fastGameObjectList.a();
        int size = fastGameObjectList.size();
        for (int i = 0; i < size; i++) {
            GameObject gameObject = gameObjectArrA[i];
            if (gameObject.objectId == j) {
                if (cls.isInstance(gameObject)) {
                    return gameObject;
                }
                String name = gameObject.getClass().getName();
                String name2 = cls.getName();
                NetworkEngine.reportDesync("object id:" + j + " was found, but with type " + name.replace("com.corrodinggames.rts.", VariableScope.nullOrMissingString) + " instead of " + name2.replace("com.corrodinggames.rts.", VariableScope.nullOrMissingString));
            }
        }
        if (!z) {
            NetworkEngine.reportDesync("getFromId:" + j + " was not found");
            return null;
        }
        return null;
    }

    public static BaseUnit a(long j, boolean z) {
        return (BaseUnit) a(j, BaseUnit.class, z);
    }

    public static OrderableUnit b(long j, boolean z) {
        return (OrderableUnit) a(j, OrderableUnit.class, z);
    }

    public static TransactionalArrayList<GameObject> dK() {
        a.a();
        return a;
    }

    public static void dL() {
        a.a();
        BaseUnit.bG();
    }
}
