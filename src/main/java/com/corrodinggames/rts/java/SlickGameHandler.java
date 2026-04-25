package com.corrodinggames.rts.java;

import com.corrodinggames.librocket.GameMainManager;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.appFramework.MultiTouchPointerState;
import com.corrodinggames.rts.debug.DebugSocketServer;
import com.corrodinggames.rts.game.FrameBufferHelper;
import com.corrodinggames.rts.game.GameLogic;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.NullGraphicsRenderer;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.OpenGLException;
import org.newdawn.slick.*;
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.u */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/u.class */
public class SlickGameHandler extends BasicGame {
    GameContainer a;
    Main b;
    SlickGameContainer c;
    NullGraphicsRenderer d;
    GameEngine e;
    HeadlessGameView f;
    boolean g;
    Object h;
    boolean i;
    boolean j;
    boolean k;
    boolean l;
    SlickTexture m;
    SlickTexture n;
    boolean o;
    boolean p;
    boolean q;
    boolean r;
    boolean s;
    int t;
    boolean u;
    boolean v;
    float w;
    float x;
    boolean y;
    private boolean[] Z;
    int z;
    float A;
    float B;
    int C;
    int D;
    int E;
    int F;
    int G;
    int H;
    boolean I;
    boolean J;
    float K;
    int L;
    String M;
    String N;
    long O;
    float P;
    float Q;
    float R;
    int S;
    int T;
    long U;
    float V;
    float W;
    int X;
    FrameBufferHelper Y;

    public SlickGameHandler(String str) {
        super(str);
        this.g = false;
        this.h = new Object();
        this.i = false;
        this.j = false;
        this.k = false;
        this.l = false;
        this.p = false;
        this.q = false;
        this.r = false;
        this.s = false;
        this.u = false;
        this.y = false;
        this.Z = new boolean[SlickToAndroidKeycodes.AndroidCodes.KEYCODE_WAKEUP];
        this.z = 0;
        this.A = 0.0f;
        this.B = 0.0f;
        this.C = 0;
        this.D = 0;
        this.K = 0.0f;
        this.L = 0;
        this.M = VariableScope.nullOrMissingString;
        this.N = VariableScope.nullOrMissingString;
        this.O = -9999L;
        this.P = 1.0f;
        this.Q = 1.0f;
        this.R = 1.0f;
        this.S = 100;
        this.T = 100;
        this.V = 0.0f;
        this.W = 0.0f;
    }

    public boolean closeRequested() {
        if (!this.i && !DebugSocketServer.isEnabled() && this.c != null && !this.c.isFullscreen()) {
            ScriptEngine.getInstance().addScriptToQueue("askQuitGame();");
            return false;
        }
        return true;
    }

    public void init(GameContainer gameContainer) throws SlickException {
        this.a = gameContainer;
        gameContainer.setAlwaysRender(true);
        gameContainer.setForceExit(true);
        gameContainer.setShowFPS(false);
        gameContainer.setTargetFrameRate(300);
        if (GameEngine.isAndroidVersionStatic) {
            gameContainer.setShowFPS(true);
            gameContainer.setTargetFrameRate(30);
        }
        gameContainer.setIcons(new String[]{"res/drawable/icon_window.png", "res/drawable/icon_window128.png", "res/drawable/icon_window24.png", "res/drawable/icon_window16.png"});
        gameContainer.setUpdateOnlyWhenVisible(false);
        gameContainer.getInput().enableKeyRepeat();
        this.m = SlickGraphicsEngine.b(com.corrodinggames.rts.R.drawable.logo, true);
        this.n = SlickGraphicsEngine.b(com.corrodinggames.rts.R.drawable.pointer, true);
        gameContainer.setMouseCursor(this.n.C(), 0, 0);
        this.U = System.currentTimeMillis();
    }

    public void a() {
        this.e = GameEngine.getInstance();
        if (this.a.isVSyncRequested() != this.e.settingsEngine.renderVsync) {
            try {
                this.a.setVSync(this.e.settingsEngine.renderVsync);
            } catch (OpenGLException e) {
                GameEngine.log("Error while changing vsync setting", (Throwable) e);
            }
        }
        if (this.e.settingsEngine.highRefreshRate) {
            this.a.setTargetFrameRate(300);
        } else {
            this.a.setTargetFrameRate(120);
        }
        boolean z = false;
        if (this.e.settingsEngine.enableMouseCapture && ((!this.e.settingsEngine.slick2dFullScreen || this.e.isMenuOpen) && !this.e.isStopped && !this.e.shouldSkipUpdate(false) && this.e.isLoading)) {
            z = true;
        }
        if (z != this.v) {
            GameEngine.isInSpace("Grabbing mouse:" + z);
            this.v = z;
            if (!this.u) {
                this.a.setMouseGrabbed(this.v);
            }
            if (this.v) {
            }
            if (!this.v) {
                Mouse.setCursorPosition((int) (this.w * this.P), (int) (Display.getHeight() - (this.x * this.P)));
            }
            GameEngine.isNetworkConnectedStatic2 = this.v;
        }
        this.a.setSmoothDeltas(this.e.settingsEngine.renderSmoothDelta);
        if (this.o != Display.isActive()) {
            this.o = Display.isActive();
            if (this.o) {
                f();
            }
        }
    }

    public void b() {
        if (this.p) {
            GameEngine.logWarningAndStack("loadingStartedThreaded");
        } else {
            this.p = true;
            c();
        }
    }

    public void c() {
        if (this.q) {
            GameEngine.logWarningAndStack("loadingStartedNonThreaded");
            return;
        }
        this.q = true;
        if (this.b == null) {
            this.b = new Main();
        }
        this.b.h();
        if (GameEngine.isNetworkConnectedStatic) {
            GameEngine.isInSpace("switching to sandbox");
            ScriptEngine.getInstance().addScriptToQueue("open('sandboxOptions.rml', 'maps/skirmish/[z;p10]Crossing Large (10p).tmx'); loadConfigAndStartNewSandbox('maps/skirmish/[z;p10]Crossing Large (10p).tmx');");
        }
        this.r = true;
    }

    public void a(HeadlessGameView headlessGameView) {
        GameEngine.updatePaintTextSizeIfNeeded("SlickContainer:setup");
        this.e = GameEngine.getInstance();
        this.f = headlessGameView;
        this.f.d = new MultiTouchPointerState();
        this.f.a = this.a.getWidth();
        this.f.b = this.a.getHeight();
        this.e.updateWindowResolution(this.f.a, this.f.b);
        this.d = new NullGraphicsRenderer();
    }

    public void update(GameContainer gameContainer, int i) {
        this.t = i;
    }

    public int a(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 3;
        }
        GameEngine.isInSpace("Unknown mouse button:" + i);
        return 0;
    }

    public void a(int i, int i2, boolean z) {
        int i3;
        int i4;
        if (this.y) {
            GameEngine.isInSpace("mouseGrab:m:" + z + ",newX:" + i + ",newY:" + i2);
        }
        if (!this.v) {
            this.w = (int) (i / this.P);
            this.x = (int) (i2 / this.P);
            return;
        }
        if (z) {
            if (this.u) {
                i3 = i - this.S;
                i4 = i2 - this.T;
            } else {
                i3 = i;
                i4 = i2;
            }
            this.w += i3 / this.P;
            this.x += i4 / this.P;
        } else {
            this.w = (int) (i / this.P);
            this.x = (int) (i2 / this.P);
        }
        if (this.w < 0.0f) {
            this.w = 0.0f;
        }
        if (this.x < 0.0f) {
            this.x = 0.0f;
        }
        if (this.f == null) {
            GameEngine.isInSpace("processMouseGrab gameView==null");
            return;
        }
        if (this.w > this.f.o() - 1) {
            this.w = this.f.o() - 1;
        }
        if (this.x > this.f.p() - 1) {
            this.x = this.f.p() - 1;
        }
    }

    public void mousePressed(int i, int i2, int i3) {
        int iA;
        a(i2, i3, false);
        if (d()) {
            a(this.w, this.x);
            this.b.p.processMouseButtonDown(0, 0);
        } else if (this.f != null && (iA = a(i)) != 0) {
            this.f.d.processEvent(this.w, this.x, true, iA);
        }
    }

    public void a(float f, float f2) {
        float f3 = f * this.P;
        float f4 = f2 * this.P;
        this.b.p.mouseMove((int) (f3 / this.R), (int) (f4 / this.R), 0);
    }

    public void mouseDragged(int i, int i2, int i3, int i4) {
        a(i3, i4, true);
        if (d()) {
            a(this.w, this.x);
        } else if (this.f != null) {
            this.f.d.setStart(this.w, this.x);
        }
    }

    public void mouseMoved(int i, int i2, int i3, int i4) {
        a(i3, i4, true);
        if (d()) {
            a(this.w, this.x);
        } else if (this.f != null) {
            this.f.d.setStart(this.w, this.x);
        }
    }

    public void mouseReleased(int i, int i2, int i3) {
        int iA;
        a(i2, i3, false);
        if (d()) {
            this.b.p.processMouseButtonUp(0, 0);
        } else if (this.f != null && (iA = a(i)) != 0) {
            this.f.d.processEvent(this.w, this.x, false, iA);
        }
    }

    public void mouseWheelMoved(int i) {
        if (d()) {
            this.b.p.processMouseWheel((-(i / 120)) * 2, 0);
        } else if (this.e != null) {
            this.e.addKeyEvent(i);
        }
    }

    boolean d() {
        if (this.b != null && this.b.p != null && !this.b.p.isGuiVisible()) {
            return true;
        }
        return false;
    }

    public boolean b(int i) {
        if (i >= this.Z.length || i < 0) {
            return false;
        }
        return this.Z[i];
    }

    public int e() {
        int i = 0;
        if (b(42) || b(54)) {
            i = 0 + 2;
        }
        if (b(29) || b(157)) {
            i++;
        }
        if (b(56) || b(184)) {
            i += 4;
        }
        return i;
    }

    public void a(int i, boolean z) {
        if (i >= this.Z.length || i < 0) {
            return;
        }
        this.Z[i] = z;
    }

    public void f() {
        for (int i = 0; i < this.Z.length; i++) {
            if (this.Z[i]) {
                c(i);
            }
        }
    }

    public void keyPressed(int i, char c) {
        a(i, true);
        if (this.b.i == null) {
            GameEngine.isInSpace("keyPressed: guiEngine==null");
        } else {
            this.b.i.onKeyDown(i, c);
        }
    }

    public void c(int i) {
        keyReleased(i, (char) 0);
    }

    public void keyReleased(int i, char c) {
        a(i, false);
        if (d()) {
            Integer numInitializeAndroidToGdxMapping = SlickToAndroidKeycodes.initializeAndroidToGdxMapping(i);
            if (numInitializeAndroidToGdxMapping != null) {
                this.b.p.processKeyUp(numInitializeAndroidToGdxMapping.intValue(), e());
            } else if (!Character.isISOControl(c)) {
            }
        }
        if (this.e != null) {
            this.e.setKeyState(SlickToAndroidKeycodes.initializeGdxToAndroidMapping(i), false);
        }
    }

    public void a(int i, int i2) {
        this.E = i;
        this.F = i2;
        g();
    }

    public void g() {
        if (this.l || this.i) {
            return;
        }
        try {
            boolean z = this.e.settingsEngine.slick2dFullScreen;
            boolean z2 = this.e.settingsEngine.slick2dBorderless;
            if (z2) {
                z = true;
            }
            int screenWidth = this.E;
            int screenHeight = this.F;
            if (z) {
                screenWidth = this.c.getScreenWidth();
                screenHeight = this.c.getScreenHeight();
                String str = this.e.settingsEngine.slick2dFullScreenResolution;
                if (str == null) {
                    GameEngine.isInSpace("fullScreenResolutionString is null");
                    str = "native";
                }
                if (!str.equals("native")) {
                    String[] strArrSplit = str.split("x");
                    if (strArrSplit.length != 2) {
                        GameEngine.updatePaintTextSizeIfNeeded("Unknown resolution format:" + str);
                    } else {
                        Integer intOrNull = Utility.parseIntOrNull(strArrSplit[0]);
                        Integer intOrNull2 = Utility.parseIntOrNull(strArrSplit[1]);
                        if (intOrNull == null || intOrNull2 == null) {
                            GameEngine.updatePaintTextSizeIfNeeded("Bad resolution int:" + str);
                        } else {
                            screenWidth = intOrNull.intValue();
                            screenHeight = intOrNull2.intValue();
                        }
                    }
                }
            }
            float f = 1.0f;
            if (screenWidth > 3360.0f || screenHeight > 1890.0f) {
                f = 2.0f;
            } else if (screenWidth > 2688.0f || screenHeight > 1512.0f) {
                f = 1.5f;
            }
            float f2 = f * this.e.settingsEngine.uiRenderScale;
            float f3 = this.e.settingsEngine.renderDensity;
            if (this.I == z && this.J == z2 && this.E == this.G && this.F == this.H && Utility.isClose(f2, this.P) && Utility.isClose(f3, this.Q)) {
                return;
            }
            int i = screenWidth;
            int i2 = screenHeight;
            boolean z3 = this.J != z2;
            this.P = f2;
            this.Q = f3;
            this.R = this.P;
            this.R *= ((this.Q - 1.0f) * 0.5f) + 1.0f;
            this.G = this.E;
            this.H = this.F;
            this.I = z;
            this.J = z2;
            boolean z4 = false;
            if (z2 && z) {
                z4 = true;
            }
            System.setProperty("org.lwjgl.opengl.Window.undecorated", z4 ? "true" : "false");
            if (z) {
                GameEngine.isInSpace("screen: " + this.c.getScreenWidth() + ", " + this.c.getScreenHeight());
                GameEngine.isInSpace("container: " + this.a.getWidth() + ", " + this.a.getHeight());
                if (z2) {
                    this.c.setDisplayMode(i, i2, false);
                } else {
                    this.c.setDisplayMode(i, i2, true);
                }
            } else {
                boolean z5 = false;
                if (this.c.isFullscreen()) {
                    z5 = true;
                }
                if (z3) {
                    z5 = true;
                    if (i > 2 && i2 > 2) {
                        this.c.setDisplayMode(i - 1, i2 - 1, false);
                    }
                }
                if (z5) {
                    this.c.setDisplayMode(i, i2, false);
                    Display.setResizable(false);
                    Display.setResizable(true);
                } else {
                    SGL sgl = Renderer.get();
                    sgl.initDisplay(i, i2);
                    sgl.enterOrtho(i, i2);
                    Field declaredField = GameContainer.class.getDeclaredField("width");
                    declaredField.setAccessible(true);
                    declaredField.set(this.c, Integer.valueOf(i));
                    Field declaredField2 = GameContainer.class.getDeclaredField("height");
                    declaredField2.setAccessible(true);
                    declaredField2.set(this.c, Integer.valueOf(i2));
                }
            }
            this.S = Display.getWidth() / 2;
            this.T = Display.getHeight() / 2;
            int i3 = (int) (i / this.P);
            int i4 = (int) (i2 / this.P);
            int i5 = (int) (i / this.R);
            int i6 = (int) (i2 / this.R);
            if (this.f != null) {
                this.f.a = i3;
                this.f.b = i4;
            } else {
                GameEngine.isInSpace("setResolution: gameView=null");
            }
            if (this.e != null) {
                this.e.updateWindowResolution(i3, i4);
                this.e.updateDensity();
            } else {
                GameEngine.isInSpace("setResolution: game=null");
            }
            if (this.b != null && this.b.p != null) {
                this.b.p.setDimensionsWrap(i5, i6);
            } else {
                GameEngine.isInSpace("setResolution: main.libRocket=null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a(String str, boolean z) {
        boolean z2 = true;
        if (!str.startsWith("Loading units ")) {
            GameEngine.isInSpace("--Now loading:" + str);
            z2 = false;
        }
        if (z) {
            this.N = this.M;
            this.M = str;
        }
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (z2 && jCurrentTimeMillis < this.O + 10) {
            return;
        }
        this.O = jCurrentTimeMillis;
        Graphics graphicsA = this.c.a();
        a((GameContainer) this.c, graphicsA);
        this.c.a(graphicsA);
    }

    public void a(GameContainer gameContainer, Graphics graphics) {
        this.K += this.t;
        this.L++;
        graphics.setColor(Color.black);
        graphics.clear();
        if (this.m != null) {
            graphics.drawImage(this.m.C(), (Display.getWidth() / 2) - (this.m.p / 2), (Display.getHeight() / 2) - (this.m.q / 2));
        }
        String str = "Loading";
        for (int i = 0; i <= this.L % 4; i++) {
            str = str + ".";
        }
        String strTruncate = Utility.truncate("    " + str, 17);
        int width = graphics.getFont().getWidth(strTruncate);
        int height = Display.getHeight() - 70;
        graphics.setColor(Color.white);
        graphics.drawString(strTruncate, (Display.getWidth() / 2) - (width / 2), height);
        graphics.setColor(new Color(1.0f, 1.0f, 1.0f, 0.6f));
        graphics.drawString(this.M, (Display.getWidth() / 2) - (graphics.getFont().getWidth(this.M) / 2), height + 20);
    }

    public void a(SlickGraphicsEngine slickGraphicsEngine) {
        slickGraphicsEngine.k();
        if (this.P != 1.0f) {
            slickGraphicsEngine.a(this.P, this.P);
        }
    }

    public void b(SlickGraphicsEngine slickGraphicsEngine) {
        slickGraphicsEngine.l();
    }

    public void a(Graphics graphics, float f) {
        graphics.pushTransform();
        graphics.scale(f, f);
    }

    public void a(Graphics graphics) {
        graphics.popTransform();
    }

    public void render(GameContainer gameContainer, Graphics graphics) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        float f = (jCurrentTimeMillis - this.U) * 0.060000002f;
        this.U = jCurrentTimeMillis;
        if (this.v && this.u && gameContainer.hasFocus()) {
            Mouse.setCursorPosition(this.S, this.T);
        }
        if (!this.r) {
            a(gameContainer, graphics);
            if (!this.s) {
                this.s = true;
                return;
            } else {
                if (!this.p) {
                    b();
                    return;
                }
                return;
            }
        }
        if (this.e == null) {
            GameEngine.updatePaintTextSizeIfNeeded("render: game==null");
            return;
        }
        a();
        float f2 = this.t * 0.060000002f;
        int width = Display.getWidth();
        int height = Display.getHeight();
        if (width != this.G || height != this.H) {
            if (this.C != width || this.D != height) {
                this.C = width;
                this.D = height;
                this.B = 0.0f;
            }
            this.B += f2;
            this.A += f2;
            if (this.A > 300.0f || this.B > 20.0f || this.A > 0.0f) {
                this.A = 0.0f;
                this.B = 0.0f;
                a(width, height);
            }
        }
        float f3 = this.R;
        this.b.a(f2);
        if (this.e.settingsEngine.liveReloading) {
            this.z++;
            if (this.z > 30) {
                this.z = 0;
                if (SlickTexture.y != null) {
                    Iterator it = SlickTexture.y.iterator();
                    while (it.hasNext()) {
                        ((SlickTexture) it.next()).t();
                    }
                }
                if (!this.e.isKeyPressed(48)) {
                    this.b.p.detectChangesAndReload();
                }
            }
        }
        boolean z = false;
        SlickGraphicsEngine slickGraphicsEngine = null;
        if (!this.e.loadNewGame) {
            graphics.setColor(Color.gray);
            graphics.resetTransform();
            graphics.clearClip();
            graphics.clear();
            if (!d() && !this.e.fullReload) {
                this.X++;
                if (this.X > 100) {
                    this.X = 0;
                    GameEngine.isInSpace("Fail safe menu");
                    GameMainManager.getInstance().showMainMenu();
                }
            }
        } else {
            this.X = 0;
        }
        boolean zA = this.e.inputController.ae.a();
        boolean z2 = this.e.isNetworkGameActive && this.e.inputController.af.a();
        if (z2) {
            zA = true;
        }
        if (this.e.loadNewGame) {
            graphics.resetTransform();
            if (!this.e.pinchDistance) {
                graphics.clearClip();
                graphics.clear();
            }
            graphics.setColor(Color.black);
            if (!GameEngine.isNetworkServerStatic) {
                slickGraphicsEngine = (SlickGraphicsEngine) this.e.graphicsEngine2;
                if (slickGraphicsEngine != null) {
                    slickGraphicsEngine.f = graphics;
                    slickGraphicsEngine.L = this.P;
                    z = true;
                    a(slickGraphicsEngine);
                }
            }
            boolean z3 = this.e.isPaused;
            if (zA) {
                this.e.isPaused = true;
            }
            try {
                this.e.gameLoop(f2, this.t);
            } catch (ConfigParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (zA) {
                this.e.isPaused = z3;
            }
            this.t = 0;
            if (!GameEngine.isNetworkServerStatic && z) {
                b(slickGraphicsEngine);
            }
        } else {
            this.e.networkEngine.b(f2);
            this.e.musicManager.update(f2);
        }
        a(graphics, f3);
        DisabledSteamEngine.a().a(0.0f);
        this.b.p.scriptEngine.update(f2);
        if (!this.b.p.isGuiVisible()) {
            this.b.p.a(graphics);
            this.b.p.update();
            this.b.p.render();
            this.b.p.scriptEngine.checkForErrors();
            this.b.p.debug = false;
        }
        this.b.p.postUpdate();
        a(graphics);
        if (this.i) {
            graphics.clear();
        }
        if (this.v && !zA && !this.e.isMenuOpen) {
            graphics.drawImage(this.n.C(), this.w * this.P, this.x * this.P);
        }
        if (zA) {
            a(graphics, z2);
        }
        if (this.b.u) {
            this.b.v++;
            if (this.b.v > 2) {
                this.b.u = false;
                this.b.v = 0;
                GameEngine.isInSpace("Saving settings (queued)");
                this.e.settingsEngine.save();
            }
        }
    }

    public void a(Graphics graphics, boolean z) {
        HeadlessGameView headlessGameView = this.f;
        int i = 10;
        int i2 = 10;
        if (headlessGameView != null) {
            i = headlessGameView.a;
            i2 = headlessGameView.b;
        }
        try {
            try {
                GameEngine.isInSpace("Saving screenshot");
                File file = new File("screenshots");
                if (!file.exists()) {
                    file.mkdir();
                }
                final String str = "screenshot_" + Utility.formatDate("d MMM yyyy HH.mm.ss") + ".png";
                final String str2 = "screenshots" + File.separator + str;
                if (!z) {
                    Image image = new Image(this.c.getWidth(), this.c.getHeight());
                    graphics.copyArea(image, 0, 0);
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageOut.write(image, "png", byteArrayOutputStream);
                    GameEngine.addUIMessage(null, "Saving screenshot: " + str);
                    new Thread(new Runnable() { // from class: com.corrodinggames.rts.java.u.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(str2);
                                try {
                                    fileOutputStream.write(byteArrayOutputStream.toByteArray());
                                    fileOutputStream.close();
                                    GameEngine.isInSpace("Screenshot saved: " + str);
                                } catch (Throwable th) {
                                    fileOutputStream.close();
                                    throw th;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                GameEngine.reportProblem("Failed to write screenshot:" + e.getMessage());
                            }
                        }
                    }).start();
                    if (headlessGameView != null) {
                        headlessGameView.a = i;
                        headlessGameView.b = i2;
                        return;
                    }
                    return;
                }
                if (this.Y == null) {
                    this.Y = new FrameBufferHelper();
                }
                int i3 = (int) (headlessGameView.a * 2.0f);
                int i4 = (int) (headlessGameView.b * 2.0f);
                GraphicsEngine graphicsEngine = this.e.graphicsEngine2;
                this.Y.a(graphicsEngine, i3, i4, 0);
                GameLogic gameLogic = (GameLogic) this.e;
                boolean z2 = this.e.isPaused;
                gameLogic.beginPostProcessing(this.Y);
                this.e.isPaused = true;
                try {
                    this.e.graphicsEngine2.b(android.graphics.Color.a(0, 0, 0));
                    headlessGameView.lockCanvas(true);
                    gameLogic.updateWindowResolution(i3, i4);
                    gameLogic.updateCameraSystem();
                    gameLogic.gameLoop(0.0f, 0);
                    gameLogic.updateWindowResolution(i, i2);
                    gameLogic.updateCameraSystem();
                    gameLogic.endPostProcessing(this.Y);
                    this.e.isPaused = z2;
                    graphicsEngine.a(this.Y.a, new File(str2));
                    GameEngine.addUIMessage(null, "Saving large screenshot: " + str);
                    if (headlessGameView != null) {
                        headlessGameView.a = i;
                        headlessGameView.b = i2;
                    }
                } catch (Throwable th) {
                    gameLogic.endPostProcessing(this.Y);
                    this.e.isPaused = z2;
                    throw th;
                }
            } catch (Throwable th2) {
                if (headlessGameView != null) {
                    headlessGameView.a = i;
                    headlessGameView.b = i2;
                }
                throw th2;
            }
        } catch (Exception e) {
            e.printStackTrace();
            GameEngine.reportProblem("Failed to take screenshot:" + e.getMessage());
            if (headlessGameView != null) {
                headlessGameView.a = i;
                headlessGameView.b = i2;
            }
        }
    }
}
