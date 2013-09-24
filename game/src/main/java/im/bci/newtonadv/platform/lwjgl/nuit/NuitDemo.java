package im.bci.newtonadv.platform.lwjgl.nuit;

import im.bci.newtonadv.platform.lwjgl.RuntimeUtils;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.ColoredRectangle;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Root;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Table;

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
                NuitToolkit tk = new NuitToolkit();
                Table table = new Table(tk);
                table.cell(new ColoredRectangle(1, 0, 0)).expand().fill();
                table.cell(new ColoredRectangle(0, 1, 0)).expand().fill();
                table.row().expand().fill();
                table.cell(new ColoredRectangle(0, 0, 1)).expand().fill();
                table.cell(new ColoredRectangle(1, 1, 0)).expand().fill();
                table.row().expand().fill();
                Root root = new Root();
                root.setWidth(Display.getDisplayMode().getWidth());
                root.setHeight(Display.getDisplayMode().getHeight());
                root.add(table);
                
                while(!Display.isCloseRequested()) {
                    root.draw();
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
