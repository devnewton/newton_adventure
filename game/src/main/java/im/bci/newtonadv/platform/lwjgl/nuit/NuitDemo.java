package im.bci.newtonadv.platform.lwjgl.nuit;

import im.bci.newtonadv.platform.lwjgl.RuntimeUtils;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Button;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.ColoredRectangle;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Container;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Root;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Select;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Table;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Toggle;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
                NuitToolkit tk = new NuitToolkit();
                final Root root = new Root(tk);
                final Table table = new Table(tk);
                final Container container = new Container();
                
                table.cell(new ColoredRectangle(1, 0, 0)).expand().fill();
                table.cell(new ColoredRectangle(0, 1, 0)).expand().fill();                
                Button toFreeLayoutButton = new Button(tk, "free layout") { 
                    @Override
                    public void onOK() {
                        root.show(container);
                    }
                };
                table.cell(toFreeLayoutButton).expand().fill();                
                table.row().expand().fill();
                table.cell(new ColoredRectangle(1, 1, 0)).expand().fill();
                table.cell(new ColoredRectangle(0.5f, 0.5f, 0.5f)).expand().fill();
                table.cell(new ColoredRectangle(0.5f, 0.4f, 0)).expand().fill();
                table.row().expand().fill();
                table.cell(new Toggle(tk)).expand().fill();
                table.cell(new ColoredRectangle(0, 0, 1)).expand().fill();
                table.cell(new Select(tk, Arrays.asList(Difficulty.values()))).expand().fill();
                table.row().expand().fill();
                root.add(table);
                
                ColoredRectangle r = new ColoredRectangle(1, 0, 0);
                r.setX(40);
                r.setY(40);
                r.setWidth(40);
                r.setHeight(50);
                container.add(r);
                ColoredRectangle g = new ColoredRectangle(0, 1, 0);
                g.setX(100);
                g.setY(40);
                g.setWidth(40);
                g.setHeight(50);
                container.add(g);
                ColoredRectangle b = new ColoredRectangle(0, 0, 1);
                b.setX(40);
                b.setY(100);
                b.setWidth(40);
                b.setHeight(50);
                container.add(b);
                ColoredRectangle j = new ColoredRectangle(0, 1, 1);
                j.setX(100);
                j.setY(100);
                j.setWidth(40);
                j.setHeight(50);
                container.add(j);
                Button toTableLayoutButton = new Button(tk, "table layout"){ 
                    @Override
                    public void onOK() {
                        root.show(table);
                    }
                };
                toTableLayoutButton.setX(300);
                toTableLayoutButton.setY(300);
                toTableLayoutButton.setWidth(200);
                toTableLayoutButton.setHeight(50);
                container.add(toTableLayoutButton);     
                root.add(container);

                while(!Display.isCloseRequested()) {
                    root.update();
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
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
