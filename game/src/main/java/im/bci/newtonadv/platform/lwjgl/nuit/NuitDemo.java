package im.bci.newtonadv.platform.lwjgl.nuit;

import im.bci.newtonadv.platform.lwjgl.LwjglHelper;
import im.bci.newtonadv.platform.lwjgl.RuntimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class NuitDemo {
    static void setupLibraryPath() {
        if (System.getProperty("javawebstart.version") != null) {
            return;
        }

        String libraryPath = System.getProperty("java.library.path");
        if (libraryPath != null && libraryPath.contains("natives")) {
            return;
        }

        try {
            File nativeDir = new File(RuntimeUtils.getApplicationDir() + File.separator
                    + "natives");
            if (nativeDir.exists()) {
                String nativePath = nativeDir.getCanonicalPath();
                System.setProperty("org.lwjgl.librarypath", nativePath);
                System.setProperty("net.java.games.input.librarypath", nativePath);
                return;
            } else {
                System.out.println("Cannot find 'natives' library folder, try system libraries");
            }
        } catch (IOException e) {
            Logger.getLogger(NuitDemo.class.getName()).log(Level.WARNING, "error", e);
        }
        Logger.getLogger(NuitDemo.class.getName()).log(Level.WARNING,
                "Cannot find 'natives' library folder, try system libraries");
    }
    
    enum Difficulty {
        EASY,
        NORMAL,
        HARD
    }

    public static void main(String[] args) throws IOException,
            ClassNotFoundException, Exception {

            try {
                setupLibraryPath();
            } catch (Throwable e) {
                handleError(e, "Unexpected error during newton adventure startup. Check your java version and your opengl driver.\n");
                return;
            }

            try {
                Display.setDisplayMode(new DisplayMode(800, 600));
                Display.setFullscreen(false);
                Display.create();
                LwjglHelper.setResizable(true);
                NuitBasic nuit = new NuitBasic();

                while(!Display.isCloseRequested()) {
                	nuit.update();
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                    nuit.draw();
                    Display.update(false);
                    Display.sync(60);
                    Display.processMessages();
                    Mouse.poll();
                    Keyboard.poll();
                    Controllers.poll();
                }

            } catch (Throwable e) {
                handleError(e, "Unexpected error during newton adventure execution.\n");
            }
    }

    public static void handleError(Throwable e, final String defaultMessage) {
        Logger.getLogger(NuitDemo.class.getName()).log(Level.SEVERE,
                defaultMessage, e);
        JOptionPane.showMessageDialog(null,
                defaultMessage
                + "\n"
                + e.getMessage()
                + (e.getCause() != null ? "\nCause: "
                + e.getCause().getMessage() : ""), "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
