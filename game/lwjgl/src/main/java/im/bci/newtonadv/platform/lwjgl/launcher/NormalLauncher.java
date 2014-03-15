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
package im.bci.newtonadv.platform.lwjgl.launcher;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.platform.lwjgl.GameCloseException;
import im.bci.newtonadv.platform.lwjgl.PlatformSpecific;
import im.bci.newtonadv.platform.lwjgl.RuntimeUtils;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author devnewton
 */
public class NormalLauncher {

    static void setupLibraryPath() {
        if (System.getProperty("javawebstart.version") != null) {
            return;
        }

        String libraryPath = System.getProperty("java.library.path");
        if (libraryPath != null && libraryPath.contains("natives")) {
            return;
        }

        try {
            File nativeDir = new File(RuntimeUtils.getApplicationDir(),"natives");
            if (nativeDir.exists()) {
                String nativePath = nativeDir.getCanonicalPath();
                System.setProperty("org.lwjgl.librarypath", nativePath);
                System.setProperty("net.java.games.input.librarypath", nativePath);
                return;
            } else {
                System.out.println("Cannot find 'natives' library folder, try system libraries");
            }
        } catch (IOException e) {
            Logger.getLogger(NormalLauncher.class.getName()).log(Level.WARNING, "error", e);
        }
        Logger.getLogger(NormalLauncher.class.getName()).log(Level.WARNING,
                "Cannot find 'natives' library folder, try system libraries");
    }

    public static void launch(String[] args) throws IOException,
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
                    try {
                        game.tick();
                    } catch (RestartGameException e) {
                        game = new Game(platform);
                        game.start();
                        game.tick();
                        platform.getConfig();//only save config if everything seems ok
                    }
                }
            } catch (GameCloseException e) {
            } catch (Throwable e) {
                handleError(e, "Unexpected error during newton adventure execution.\n");
            }
        } finally {
            if (null != platform) {
                platform.close();
            }
            System.exit(0);
        }
    }

    public static void handleError(Throwable e, final String defaultMessage) {
        Logger.getLogger(NormalLauncher.class.getName()).log(Level.SEVERE,
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
