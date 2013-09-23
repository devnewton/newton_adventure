package im.bci.newtonadv.platform.lwjgl;

import java.lang.reflect.Method;

import org.lwjgl.opengl.Display;

public class LwjglHelper {

    private static class Methods {
        final Method setResizableMethod, wasResizedMethod, getWidthMethod, getHeightMethod;
        
        Methods() throws NoSuchMethodException, SecurityException {
           setResizableMethod = Display.class.getMethod("setResizable", boolean.class);
           wasResizedMethod = Display.class.getMethod("wasResized");
           getWidthMethod = Display.class.getMethod("getWidth");
           getHeightMethod = Display.class.getMethod("getHeight");
        }
        
    }
    private static Methods methods;

    public static void setResizable(boolean b) {
        try {
            if(null == methods) {
                methods = new Methods();
            }
            if(null != methods.setResizableMethod) {
                methods.setResizableMethod.invoke(null, b);
            }
        } catch (Exception e) {
        }
    }
    
    public static boolean wasResized() {
        try {
            if(null == methods) {
                methods = new Methods();
            }
            if(null != methods.wasResizedMethod) {
                return Boolean.TRUE.equals(methods.wasResizedMethod.invoke(null));
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static int getWidth() {
        try {
            if(null == methods) {
                methods = new Methods();
            }
            if(null != methods.getWidthMethod) {
                return (int) methods.getWidthMethod.invoke(null);
            }
        } catch (Exception e) {
        }
        return Display.getDisplayMode().getWidth();
    }

    public static int getHeight() {
        try {
            if(null == methods) {
                methods = new Methods();
            }
            if(null != methods.getHeightMethod) {
                return (int) methods.getWidthMethod.invoke(null);
            }
        } catch (Exception e) {
        }
        return Display.getDisplayMode().getHeight();
    }
}
