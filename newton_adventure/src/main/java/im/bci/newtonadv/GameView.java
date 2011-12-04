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
package im.bci.newtonadv;

import java.awt.Point;
import java.util.Comparator;
import im.bci.newtonadv.game.Sequence;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import im.bci.newtonadv.util.TrueTypeFont;

/**
 *
 * @author devnewton
 */
public strictfp class GameView {

    private TextureCache textureCache;
    private Game game;
    private TrueTypeFont fpsFont;

    public GameView( Game game) {
        this.game = game;
        initDisplay();
    }

    void toggleFullscreen() {
        try {
            Display.setFullscreen(!Display.isFullscreen());
        } catch (LWJGLException ex) {
            Logger.getLogger(GameView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private DisplayMode findGoodDisplayMode(final int targetHeight, final int targetWidth) {
        try {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            java.util.Arrays.sort(modes, new Comparator< DisplayMode >() {

                @Override
                public int compare(DisplayMode a, DisplayMode b) {
                   if( a.getWidth() == targetWidth && a.getHeight() == targetHeight )
                       return -1;
                   else if(b.getWidth() == targetWidth && b.getHeight() == targetHeight)
                       return 1;
                   else {
                       Point pA = new Point(a.getWidth(), b.getHeight());
                       Point pB = new Point(b.getWidth(), b.getHeight());
                       Point pTarget = new Point(targetWidth, targetHeight);
                       if( pA.distance(pTarget) < pB.distance(pTarget) )
                           return -1;
                       else
                           return 1;
                   }
                }
            });

            if( modes.length > 0 )
                return modes[0];
            else
                return Display.getDesktopDisplayMode();
        } catch (LWJGLException e) {
            Sys.alert("Error", "Unable to determine display modes.");
        }
        return Display.getDesktopDisplayMode();
    }

    private void initDisplay() {
        int targetWidth = Integer.parseInt(game.getConfig().getProperty("view.width"));
        int targetHeight = Integer.parseInt(game.getConfig().getProperty("view.height"));
        GameViewQuality quality = GameViewQuality.valueOf(game.getConfig().getProperty("view.quality"));

        DisplayMode chosenMode = findGoodDisplayMode(targetHeight, targetWidth);

        if (chosenMode == null) {
            Sys.alert("Error", "Unable to find appropriate display mode.");
            System.exit(0);
        }
        try {
            if( chosenMode.getWidth() >= Display.getDesktopDisplayMode().getWidth() || chosenMode.getHeight() >= Display.getDesktopDisplayMode().getHeight())
                Display.setDisplayModeAndFullscreen(chosenMode);
            else
                Display.setDisplayMode(chosenMode);
            Display.setTitle("Newton adventure");
            Display.create();
            Display.setVSyncEnabled(true);
            System.out.println("adapter : " + Display.getAdapter());
            System.out.println("version : " + Display.getVersion());
        } catch (LWJGLException e) {
            Sys.alert("error", "Unable to create display.");
            System.exit(0);
        }

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, quality.toGL());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);       
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        
        textureCache = new TextureCache();
        fpsFont = new TrueTypeFont();
    }

    void draw(Sequence sequence ) {
        sequence.draw();

        // now tell the screen to update
        Display.update();
        Display.sync(Game.FPS);

        // finally check if the user has requested that the display be 
        // shutdown
        if (Display.isCloseRequested()) {
            close();
            System.exit(0);
        }
    }

    public void drawFPS() {
        String fps = game.getFrameTimeInfos().fps + " FPS";
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getDisplayMode().getWidth(), 0, Display.getDisplayMode().getHeight(), -1, 1);
        GL11.glTranslatef(Display.getDisplayMode().getWidth() - fpsFont.getWidth(fps), Display.getDisplayMode().getHeight() - 64, 0);
        fpsFont.drawString(fps);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void close() {
        textureCache.clearAll();
        Display.destroy();
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }
}

