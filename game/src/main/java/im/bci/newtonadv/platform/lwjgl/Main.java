/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
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

import im.bci.newtonadv.Game;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 *
 * @author devnewton
 */
public class Main {

    static void setupLibraryPath() {
        if (System.getProperty("javawebstart.version") != null) {
            return;
        }

        String libraryPath = System.getProperty("java.library.path");
        if (libraryPath != null && libraryPath.contains("native")) {
            return;
        }

        String osName = System.getProperty("os.name");
        String osDir;

        if (osName.startsWith("Windows")) {
            osDir = "windows";
        } else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD")) {
            osDir = "linux";
        } else if (osName.startsWith("Mac OS X")) {
            osDir = "macosx";
        } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            osDir = "solaris";
        } else {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING,
                    "Unknown platform: {0}", osName);
            return;
        }
        try {
            File nativeDir = new File(getApplicationDir() + File.separator
                    + "native" + File.separator + osDir);
            if (!nativeDir.exists()) {
                nativeDir = new File("native" + File.separator + osDir);
            }

            String nativePath = nativeDir.getCanonicalPath();
            System.setProperty("org.lwjgl.librarypath", nativePath);
            System.setProperty("net.java.games.input.librarypath", nativePath);
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING,
                    "Cannot find 'native' library folder, try system libraries",
                    e);
        }
        fuckNetbeans();
    }

    public static void fuckNetbeans() {
        if ("true".equals(System.getProperty("netbeans.fuck", "false"))) {
            try {
                File libDir = new File(getApplicationDir() + File.separator
                        + "lib");
                if (!libDir.exists()) {
                    libDir = new File(getApplicationParentDir() + File.separator
                            + "lib");
                }
                File[] jars = libDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar");
                    }
                });
                for (File jar : jars) {
                    addJarToClassPath(jar);
                }
            } catch (Exception e) {
                Logger.getLogger(Main.class.getName()).log(Level.WARNING,
                        "Cannot fuck netbeans", e);
            }
        }
    }

    private static void addJarToClassPath(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }

    private static String getApplicationDir() throws IOException {
        try {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException uriEx) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING,
                    "Cannot find application directory, try current", uriEx);
            return new File(".").getCanonicalPath();
        }
    }

    private static String getApplicationParentDir() throws IOException {
        return (new File(getApplicationDir())).getParent();
    }

    public static void main(String[] args) throws IOException,
            ClassNotFoundException, Exception {

        Game game;
        PlatformSpecific platform = null;
        try {
            try {
                setupLibraryPath();

                platform = new PlatformSpecific();
                game = new Game(platform);
                game.start();
                game.tick();
            } catch (GameCloseException e) {
                return;
            } catch (Throwable e) {
                handleError(e, "Unexpected error during newton adventure startup. Check your java version and your opengl driver.\n");
                return;
            }

            try {
                while (game.isRunning()) {
                    game.tick();
                }
            } catch (GameCloseException e) {
                return;
            } catch (Throwable e) {
                handleError(e, "Unexpected error during newton adventure execution.\n");
                return;
            }
        } finally {
            if (null != platform) {
                platform.close();
            }
            System.exit(0);
        }
    }

    public static void handleError(Throwable e, final String defaultMessage) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
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
