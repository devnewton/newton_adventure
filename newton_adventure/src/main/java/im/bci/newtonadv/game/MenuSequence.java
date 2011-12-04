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

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.Texture;

/**
 *
 * @author devnewton
 */
public abstract class MenuSequence implements Sequence {

    static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    static final float ortho2DLeft = 0;
    static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    static final float ortho2DTop = 0;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private int currentButtonIndex;
    private boolean redraw;
    private boolean selectNextButton = false, selectPreviousButton = false, activateCurrentButton = false;
    protected Game game;
    private String backgroundImage;

    public MenuSequence(Game game) {
        this.game = game;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    public void draw() {
        if (Display.isDirty() || redraw) {
            redraw = false;
            GL11.glPushMatrix();
            GLU.gluOrtho2D(MainMenuSequence.ortho2DLeft, MainMenuSequence.ortho2DRight, MainMenuSequence.ortho2DBottom, MainMenuSequence.ortho2DTop);

            if (backgroundImage != null) {
                game.getView().getTextureCache().getTexture(backgroundImage).bind();
                final float x1 = MainMenuSequence.ortho2DLeft;
                final float x2 = MainMenuSequence.ortho2DRight;
                final float y1 = MainMenuSequence.ortho2DBottom;
                final float y2 = MainMenuSequence.ortho2DTop;
                final float u1 = 0.0F;
                final float u2 = 1.0F;
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glTexCoord2f(u1, 0.0F);
                GL11.glVertex2f(x1, y2);
                GL11.glTexCoord2f(u2, 0.0F);
                GL11.glVertex2f(x2, y2);
                GL11.glTexCoord2f(u2, 1.0F);
                GL11.glVertex2f(x2, y1);
                GL11.glTexCoord2f(u1, 1.0F);
                GL11.glVertex2f(x1, y1);
                GL11.glEnd();
            } else {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            }

            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F); // sets aplha function
            for (Button b : buttons) {
                b.draw();
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    @Override
    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            selectNextButton = true;
        } else if (selectNextButton) {
            selectNextButton = false;
            buttons.get(currentButtonIndex).setOff();
            ++currentButtonIndex;
            if (currentButtonIndex >= buttons.size()) {
                currentButtonIndex = 0;
            }
            buttons.get(currentButtonIndex).setOn();
            redraw = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            selectPreviousButton = true;
        } else if (selectPreviousButton) {
            selectPreviousButton = false;
            buttons.get(currentButtonIndex).setOff();
            --currentButtonIndex;
            if (currentButtonIndex < 0) {
                currentButtonIndex = buttons.size() - 1;
            }
            buttons.get(currentButtonIndex).setOn();
            redraw = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
            activateCurrentButton = true;
        } else if (activateCurrentButton) {
            activateCurrentButton = false;
            buttons.get(currentButtonIndex).activate();
        }
    }

    @Override
    public void start() {
        currentButtonIndex = 0;
        redraw = true;
        setCurrentButton(buttons.isEmpty() ? null : buttons.get(0));
    }

    @Override
    public void stop() {
    }

    @Override
    public void update() throws TransitionException {
        //NOTHING
    }

    protected void addButton(Button b) {
        buttons.add(b);
    }

    protected void setCurrentButton(Button button) {
        for(int i=0; i<buttons.size(); ++i) {
            Button b = buttons.get(i);
            if(b == button) {
                currentButtonIndex = i;
                b.setOn();
            } else {
                b.setOff();
            }

        }
        redraw = true;
    }

    protected abstract class Button {

        String onTexture, offTexture, currentTexture = null;
        float x = 273, y;
        float w = -1.0f;
        float h = -1.0f;

        void draw() {
            if (currentTexture != null) {
                Texture texture = game.getView().getTextureCache().getTexture(currentTexture);
                texture.bind();
                final float x1 = x;
                final float x2 = x + (w > 0 ? w : texture.getWidth());
                final float y1 = y + (h > 0 ? h : texture.getHeight());
                final float y2 = y;
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
            }
        }

        void setOn() {
            currentTexture = onTexture;
        }

        void setOff() {
            currentTexture = offTexture;
        }

        abstract void activate() throws Sequence.TransitionException;
    }
}
