package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.MusicManager;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/b.class */
public class SlickGameContainer extends AppGameContainer {
    boolean a;
    Object b;
    DisplayMessageThread c;

    public SlickGameContainer(Game game, int i, int i2, boolean z) throws SlickException {
        super(game, i, i2, z);
        this.a = false;
        this.b = new Object();
    }

    public Graphics a() {
        Graphics graphics = getGraphics();
        this.input.poll(this.width, this.height);
        Music.poll(1);
        if (MusicManager.musicFactory != null) {
            MusicManager.musicFactory.poll(1);
        }
        GL.glClear(16640);
        GL.glLoadIdentity();
        graphics.resetTransform();
        graphics.resetFont();
        graphics.resetLineWidth();
        graphics.setAntiAlias(false);
        return graphics;
    }

    public void a(Graphics graphics) {
        graphics.resetTransform();
        GL.flush();
        Display.update();
    }

    protected void gameLoop() throws SlickException {
        int delta = getDelta();
        if (!Display.isVisible() && this.updateOnlyOnVisible) {
            try {
                Thread.sleep(100L);
            } catch (Exception e) {
            }
        } else {
            updateAndRender(delta);
        }
        updateFPS();
        Display.update(false);
        if (!this.a) {
            Display.processMessages();
        } else if (this.c == null) {
            this.c = new DisplayMessageThread(this);
            this.c.start();
        }
        if (Display.isCloseRequested() && this.game.closeRequested()) {
            this.running = false;
        }
    }

    protected void updateAndRender(int i) throws SlickException {
        if (this.smoothDeltas && getFPS() != 0) {
            i = 1000 / getFPS();
        }
        this.input.poll(this.width, this.height);
        Music.poll(i);
        if (MusicManager.musicFactory != null) {
            MusicManager.musicFactory.poll(i);
        }
        if (this.paused) {
            this.game.update(this, 0);
        } else {
            this.storedDelta += (long) i;
            if (this.storedDelta >= this.minimumLogicInterval) {
                if (this.maximumLogicInterval != 0) {
                    long j = this.storedDelta / this.maximumLogicInterval;
                    for (int i2 = 0; i2 < j; i2++) {
                        this.game.update(this, (int) this.maximumLogicInterval);
                    }
                    int i3 = (int) (this.storedDelta % this.maximumLogicInterval);
                    if (i3 > this.minimumLogicInterval) {
                        this.game.update(this, (int) (((long) i3) % this.maximumLogicInterval));
                        this.storedDelta = 0L;
                    } else {
                        this.storedDelta = i3;
                    }
                } else {
                    this.game.update(this, (int) this.storedDelta);
                    this.storedDelta = 0L;
                }
            }
        }
        if (hasFocus() || getAlwaysRender()) {
            if (this.clearEachFrame) {
                GL.glClear(16640);
            }
            GL.glLoadIdentity();
            Graphics graphics = getGraphics();
            graphics.resetTransform();
            graphics.resetFont();
            graphics.resetLineWidth();
            graphics.setAntiAlias(false);
            this.game.render(this, graphics);
            graphics.resetTransform();
            if (isShowingFPS()) {
                getDefaultFont().drawString(10.0f, 10.0f, "FPS: " + this.recordedFPS);
            }
            GL.flush();
        }
        if (this.targetFPS != -1) {
            Display.sync(this.targetFPS);
        }
    }

    public void destroy() {
        try {
            Display.destroy();
        } catch (Exception e) {
            GameEngine.log("Error on Display.destroy in destroy", (Throwable) e);
        }
    }
}
