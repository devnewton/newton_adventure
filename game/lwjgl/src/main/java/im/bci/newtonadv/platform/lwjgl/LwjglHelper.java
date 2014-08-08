/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.platform.lwjgl;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

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
            if (null == methods) {
                methods = new Methods();
            }
            if (null != methods.setResizableMethod) {
                methods.setResizableMethod.invoke(null, b);
            }
        } catch (Exception e) {
        }
    }

    public static boolean wasResized() {
        try {
            if (null == methods) {
                methods = new Methods();
            }
            if (null != methods.wasResizedMethod) {
                return Boolean.TRUE.equals(methods.wasResizedMethod.invoke(null));
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static int getWidth() {
        try {
            if (null == methods) {
                methods = new Methods();
            }
            if (null != methods.getWidthMethod) {
                return (int) (Integer) methods.getWidthMethod.invoke(null);
            }
        } catch (Exception e) {
        }
        return Display.getDisplayMode().getWidth();
    }

    public static int getHeight() {
        try {
            if (null == methods) {
                methods = new Methods();
            }
            if (null != methods.getHeightMethod) {
                return (int) (Integer)methods.getHeightMethod.invoke(null);
            }
        } catch (Exception e) {
        }
        return Display.getDisplayMode().getHeight();
    }

    public static void setupGLTextureParams() {
        if (GLContext.getCapabilities().OpenGL12) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        }
        setupGLTextureQualityParams();
        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
                GL11.GL_MODULATE);
    }

    public static void setupGLTextureQualityParams() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    }

    public static DisplayMode findBestDisplayMode(int width, int height) {
        DisplayMode desktopMode = Display.getDesktopDisplayMode();
        if (width == desktopMode.getWidth() && height == desktopMode.getHeight()) {
            return desktopMode;
        }
        DisplayMode bestMode = null;
        try {
            for (DisplayMode m : Display.getAvailableDisplayModes()) {
                if (width == m.getWidth() && height == m.getHeight()) {
                    if (null == bestMode
                            || (m.isFullscreenCapable() && !bestMode.isFullscreenCapable())
                            || (m.getBitsPerPixel() > bestMode.getBitsPerPixel())
                            || m.getFrequency() > bestMode.getFrequency()) {
                        bestMode = m;
                    }
                }
            }
            if (null != bestMode) {
                return bestMode;
            }
        } catch (LWJGLException ex) {
            Logger.getLogger(LwjglHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new DisplayMode(width, height);
    }
}
