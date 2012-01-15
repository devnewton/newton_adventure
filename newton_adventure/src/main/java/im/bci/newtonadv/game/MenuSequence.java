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
import im.bci.newtonadv.Game;
import im.bci.newtonadv.platform.lwjgl.Texture;
import java.util.List;

/**
 *
 * @author devnewton
 */
public abstract class MenuSequence implements Sequence {

    public static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    public static final float ortho2DLeft = 0;
    public static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    public static final float ortho2DTop = 0;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private int currentButtonIndex;
    private boolean redraw;
    private boolean horizontalSelectNextButton = false, horizontalSelectPreviousButton = false;
    private boolean verticalSelectNextButton = false, verticalSelectPreviousButton = false;
    private boolean activateCurrentButton = false;
    protected int horizontalIncrement = 1;
    protected int verticalIncrement = 1;
    protected Game game;
    private String backgroundImage;

    public MenuSequence(Game game) {
        this.game = game;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
    public List<Button> getButtons()
    {
    return buttons;
    }

    @Override
    public void draw() {
        game.getView().drawMenuSequence(this);
    }

    @Override
    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            horizontalSelectNextButton = true;
        } else if (horizontalSelectNextButton) {
            horizontalSelectNextButton = false;
            buttons.get(currentButtonIndex).setOff();
            currentButtonIndex += horizontalIncrement;
            if (currentButtonIndex >= buttons.size()) {
                currentButtonIndex = 0;
            }
            buttons.get(currentButtonIndex).setOn();
            redraw = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            horizontalSelectPreviousButton = true;
        } else if (horizontalSelectPreviousButton) {
            horizontalSelectPreviousButton = false;
            buttons.get(currentButtonIndex).setOff();
            currentButtonIndex -= horizontalIncrement;
            if (currentButtonIndex < 0) {
                currentButtonIndex = buttons.size() - 1;
            }
            buttons.get(currentButtonIndex).setOn();
            redraw = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            verticalSelectNextButton = true;
        } else if (verticalSelectNextButton) {
            verticalSelectNextButton = false;
            buttons.get(currentButtonIndex).setOff();
            currentButtonIndex += verticalIncrement;
            if (currentButtonIndex >= buttons.size()) {
                currentButtonIndex = 0;
            }
            buttons.get(currentButtonIndex).setOn();
            redraw = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            verticalSelectPreviousButton = true;
        } else if (verticalSelectPreviousButton) {
            verticalSelectPreviousButton = false;
            buttons.get(currentButtonIndex).setOff();
            currentButtonIndex -= verticalIncrement;
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
        for (int i = 0; i < buttons.size(); ++i) {
            Button b = buttons.get(i);
            if (b == button) {
                currentButtonIndex = i;
                b.setOn();
            } else {
                b.setOff();
            }

        }
        redraw = true;
    }

    public boolean isDirty() {
        return redraw;
    }

    public void setDirty(boolean b) {
        redraw = b;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public abstract class Button {

        public String onTexture, offTexture, currentTexture = null;
        public float x = 273, y;
        public float w = -1.0f;
        public float h = -1.0f;

        public void draw() {
            game.getView().drawButton(this);
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
