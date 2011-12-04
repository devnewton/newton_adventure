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
package im.bci.newtonadv.game;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.util.TrueTypeFont;
import im.bci.newtonadv.world.GameOverException;
import im.bci.newtonadv.world.World;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author devnewton
 */
strictfp public class LevelSequence implements Sequence {

    static final int nbWorldStepByFrame = 1;
    static final int maxWorldStepByFrame = 5;
    private FrameTimeInfos frameTimeInfos;
    private World world;
    public long stepTime = 0;
    private TrueTypeFont appleFont;
    private Sequence nextSequence;
    private Game game;
    private String levelPath;
    private boolean cheatCodeGotoNextLevel = false;

    public LevelSequence(Game game, String levelPath) {
        this.game = game;
        this.levelPath = levelPath;
    }

    @Override
    public void start() {
        try {
            appleFont = new TrueTypeFont();
            world = new World(game);
            frameTimeInfos = game.getFrameTimeInfos();
            world.loadLevel(levelPath);
        } catch (IOException ex) {
            Logger.getLogger(LevelSequence.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void stop() {
        appleFont.destroy();
        appleFont = null;
        game.getSoundCache().stopMusic();
        world = null;
    }

    @Override
    public void update() throws TransitionException {
        try {
            if (world.getHero().isDead()) {
                world.getHero().update(frameTimeInfos);
                return;
            }

            stepTime += frameTimeInfos.elapsedTime;
            if (stepTime >= 1000000000 / Game.FPS) {
                stepTime -= 1000000000 / Game.FPS;
                //long step = nbWorldStepByFrame * (1 + stepTime / (1000000000 / Game.FPS));
                //step = step > 0 ? Math.min(step, maxWorldStepByFrame) : 0;
                //System.out.println("step = " + step + " stepTime = " + stepTime);
                final int step = 5;
                for (int i = 0; i < step; ++i) {
                    world.step();
                }
            }
            world.update();
            if (world.areObjectivesCompleted() || cheatCodeGotoNextLevel) {
                throw new TransitionException(nextSequence);
            }
        } catch (GameOverException ex) {
            GameOverSequence gameOverSequence = new GameOverSequence(game, this, null);
            throw new TransitionException(gameOverSequence);
        }
    }

    @Override
    public void processInputs() {
        if (world.getHero().isDead()) {
            return;
        }

        float stepRate = 1.0f;//frameTimeInfos.elapsedTime / (1000000000.0f / Game.FPSf);
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            world.progressiveRotateGravity(0.05f * stepRate);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            world.progressiveRotateGravity(-0.05f * stepRate);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            world.rotateGravity((float) (Math.PI / 4.0));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            world.rotateGravity((float) (-Math.PI / 4.0));
        }

        boolean heroIsMoving = false;
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            world.getHero().moveLeft(stepRate);
            heroIsMoving = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            world.getHero().moveRight(stepRate);
            heroIsMoving = true;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            world.getHero().jump(stepRate);
            heroIsMoving = true;
        }
        if (!heroIsMoving) {
            world.getHero().dontMove();
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_F12)) {
            cheatCodeGotoNextLevel = true;
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void draw() {
        world.draw();
        game.getView().drawFPS();
        drawIndicators();
    }

    private void drawAppleIndicatorIcon(float x, float y, float w, float h) {
        final float x1 = x;
        final float x2 = x + w;
        final float y1 = y;
        final float y2 = y + h;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
        world.getAppleIconTexture().bind();

        final float u1 = 0.0f,  u2 = 1.0f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, 0.0f);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, 0.0f);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, 1.0f);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, 1.0f);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopAttrib();
    }

    private void drawIndicators() {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getDisplayMode().getWidth(), 0, Display.getDisplayMode().getHeight(), -1, 1);
        GL11.glTranslatef(0, Display.getDisplayMode().getHeight() - 64, 0);
        String nbAppleStr = "" + world.getHero().getNbApple();
        appleFont.drawString(nbAppleStr);
        int iconWidth = appleFont.getWidth("O");
        drawAppleIndicatorIcon(appleFont.getWidth(nbAppleStr), appleFont.getWidth(nbAppleStr), iconWidth, iconWidth);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }
}
