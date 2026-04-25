package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import java.awt.Toolkit;
import java.io.IOException;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/j.class */
class GameStartupRunnable implements Runnable {
    final /* synthetic */ Main a;

    GameStartupRunnable(Main main) {
        this.a = main;
    }

    @Override // java.lang.Runnable
    public void run() {
        GameEngine.setupUncaughtExceptionHandler();
        try {
            this.a.k.start();
        } catch (SlickException e) {
            if (!"Failed to initialise the LWJGL display".equals(e.getMessage())) {
                throw new RuntimeException((Throwable) e);
            }
            GameEngine.log("Error starting display", (Throwable) e);
            String str = "\nFailed to get opengl version";
            try {
                System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
                Display.setDisplayMode(new DisplayMode(100, 100));
                Display.create();
                String strGlGetString = GL11.glGetString(7938);
                Display.destroy();
                GameEngine.isInSpace("OpenGL version: " + strGlGetString);
                str = "\nOpenGL version detected: " + strGlGetString;
                if (strGlGetString.startsWith("1.0") || strGlGetString.startsWith("1.1")) {
                    str = str + "\n---\nOpenGL 1.1 is over 20 years old you might be using a fallback microsoft driver. Try updating your graphics drivers from the manufacturer.";
                }
            } catch (Exception e2) {
                GameEngine.log("Failed to get opengl info", (Throwable) e2);
            }
            Toolkit.getDefaultToolkit();
            Sys.alert("Error", "Failed to create display." + str);
            System.exit(1);
        }
        GameEngine.isInSpace("Game stopped running shutting down");
        try {
            GameEngine.getInstance().gameSaver.updateAutosave("lastgame", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.a.a(true);
    }
}
