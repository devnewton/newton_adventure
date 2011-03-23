/*
 * Copyright (c) 2009-2010 devnewton <devnewton@tuxfamily.org>
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
 * * Neither the name of 'devnewton <devnewton@tuxfamily.org>' nor the names of
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
package org.tuxfamily.newtonadv.game;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.tuxfamily.newtonadv.Game;

public class StoryboardSequence implements Sequence {

    Sequence nextSequence;
    String texture;
    static final float ortho2DBaseSize = 100.0f;
    static final float ortho2DLeft = -ortho2DBaseSize;
    static final float ortho2DBottom = -ortho2DBaseSize;
    static final float ortho2DRight = ortho2DBaseSize;
    static final float ortho2DTop = ortho2DBaseSize;
    private Game game;
    private final String music;
    private boolean redraw = true;

    public StoryboardSequence(Game game, String texture, String music, Sequence nextSequence) {
        this.game = game;
        this.texture = texture;
        this.nextSequence = nextSequence;
        this.music = music;
    }

    @Override
    public void draw() {
        if (Display.isDirty() || redraw) {
            redraw = false;

            GL11.glPushMatrix();
            GLU.gluOrtho2D(ortho2DLeft, ortho2DRight, ortho2DBottom, ortho2DTop);
            game.getView().getTextureCache().getTexture(texture).bind();
            final float x1 = ortho2DLeft;
            final float x2 = ortho2DRight;
            final float y1 = ortho2DBottom;
            final float y2 = ortho2DTop;
            final float u1 = 0.0f, u2 = 1.0f;
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
            GL11.glPopMatrix();
        }
    }

    @Override
    public void update() throws TransitionException {
        //NOTHING
    }
    private boolean mustQuit = false;

    @Override
    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            mustQuit = true;
        } else if (mustQuit) {
            throw new Sequence.TransitionException(nextSequence);
        }
    }

    public void start() {
        if (music != null) {
            game.getSoundCache().playMusicIfEnabled(music);
        }
        redraw = true;
    }

    public void stop() {
        if (music != null) {
            game.getSoundCache().stopMusic();
        }
    }
}
