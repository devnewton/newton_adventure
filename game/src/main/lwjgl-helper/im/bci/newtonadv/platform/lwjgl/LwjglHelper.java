package im.bci.newtonadv.platform.lwjgl;

import org.lwjgl.opengl.Display;

public class LwjglHelper {
    
    public static void setResizable(boolean b) {
        Display.setResizable(b);
    }
    
    public static boolean wasResized() {
        return false;
    }

    public static int getWidth() {
        return Display.getWidth();
    }

    public static int getHeight() {
        return Display.getHeight();
    }
}
