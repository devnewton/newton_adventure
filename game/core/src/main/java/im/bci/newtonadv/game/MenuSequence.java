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
import im.bci.newtonadv.Game;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.anim.Play;
import im.bci.newtonadv.anim.PlayMode;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import java.io.IOException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 *
 * @author devnewton
 */
public abstract class MenuSequence implements Sequence {

    public static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    public static final float ortho2DLeft = 0;
    public static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    public static final float ortho2DTop = 0;
    private final ArrayList<Button> buttons = new ArrayList<Button>();
    private int currentButtonIndex;
    protected boolean redraw;
    protected int horizontalIncrement = 1;
    protected int verticalIncrement = 1;
    protected Game game;
    private String backgroundTexturePath;
    protected Play backgroundTexture;
    private float backgroundX1 = MenuSequence.ortho2DLeft;
    private float backgroundX2 = MenuSequence.ortho2DRight;
    private float backgroundY1 = MenuSequence.ortho2DBottom;
    private float backgroundY2 = MenuSequence.ortho2DTop;
    private final Vector2f oldMousePos = new Vector2f();
    private Boolean mouseActivateCurrentButton;
    private Button defaultButton;
    private PlayMode backgroundPlayMode = PlayMode.LOOP;

    public MenuSequence(Game game) {
        this.game = game;
    }

    public void setBackgroundPlayMode(PlayMode backgroundPlayMode) {
        this.backgroundPlayMode = backgroundPlayMode;
    }

    public float getBackgroundX1() {
        return backgroundX1;
    }

    public void setBackgroundX1(float backgroundX1) {
        this.backgroundX1 = backgroundX1;
    }

    public float getBackgroundX2() {
        return backgroundX2;
    }

    public void setBackgroundX2(float backgroundX2) {
        this.backgroundX2 = backgroundX2;
    }

    public float getBackgroundY1() {
        return backgroundY1;
    }

    public void setBackgroundY1(float backgroundY1) {
        this.backgroundY1 = backgroundY1;
    }

    public float getBackgroundY2() {
        return backgroundY2;
    }

    public void setBackgroundY2(float backgroundY2) {
        this.backgroundY2 = backgroundY2;
    }

    protected void setDefaultButton(Button b) {
        this.defaultButton = b;
    }

    public void setBackgroundTexturePath(String backgroundImage) {
        this.backgroundTexturePath = backgroundImage;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    protected void clearButtons() {
        buttons.clear();
    }

    @Override
    public void draw() {
        game.getView().drawMenuSequence(this);
    }

    @Override
    public void processInputs() throws Sequence.NormalTransitionException,
            ResumeTransitionException,
            ResumableTransitionException {
        if (!buttons.isEmpty()) {
            int oldButtonIndex = currentButtonIndex;
            if (game.getInput().getMenuRight().isActivated()) {
                buttons.get(currentButtonIndex).setOff();
                currentButtonIndex += horizontalIncrement;
                if (currentButtonIndex >= buttons.size()) {
                    currentButtonIndex = 0;
                }
                buttons.get(currentButtonIndex).setOn();
                redraw = true;
            }
            if (game.getInput().getMenuLeft().isActivated()) {
                buttons.get(currentButtonIndex).setOff();
                currentButtonIndex -= horizontalIncrement;
                if (currentButtonIndex < 0) {
                    currentButtonIndex = buttons.size() - 1;
                }
                buttons.get(currentButtonIndex).setOn();
                redraw = true;
            }
            if (game.getInput().getMenuDown().isActivated()) {
                buttons.get(currentButtonIndex).setOff();
                currentButtonIndex += verticalIncrement;
                if (currentButtonIndex >= buttons.size()) {
                    currentButtonIndex = 0;
                }
                buttons.get(currentButtonIndex).setOn();
                redraw = true;
            }
            if (game.getInput().getMenuUp().isActivated()) {
                buttons.get(currentButtonIndex).setOff();
                currentButtonIndex -= verticalIncrement;
                if (currentButtonIndex < 0) {
                    currentButtonIndex = buttons.size() - 1;
                }
                buttons.get(currentButtonIndex).setOn();
                redraw = true;
            }
            if (game.getInput().getMenuOk().isActivated()) {
                game.getNuitToolkit().getAudio().getSound(game.getData().getFile("select.wav")).play();
                buttons.get(currentButtonIndex).activate();
            }

            ROVector2f mousePos = game.getInput().getMousePos();
            if (null != mousePos) {
                float viewWidth = game.getView().getWidth();
                float viewHeight = game.getView().getHeight();
                if (viewWidth != 0.0f && viewHeight != 0.0f) {
                    float mouseX = mousePos.getX() * ortho2DRight / viewWidth;
                    float mouseY = ortho2DBottom
                            - (mousePos.getY() * ortho2DBottom / viewHeight);
                    for (Button button : buttons) {
                        if (mouseX > button.x && mouseX < (button.x + button.w)
                                && mouseY > button.y
                                && mouseY < (button.y + button.h)) {
                            if ((oldMousePos.getX() != mousePos.getX() || oldMousePos
                                    .getY() != mousePos.getY())
                                    || game.getInput().isMouseButtonDown()) {
                                buttons.get(currentButtonIndex).setOff();
                                currentButtonIndex = buttons.indexOf(button);
                                button.setOn();
                            }
                            if (null == mouseActivateCurrentButton) {
                                if(!game.getInput().isMouseButtonDown()) {
                                    mouseActivateCurrentButton = false;
                                }
                            } else {
                                if (game.getInput().isMouseButtonDown()) {
                                    mouseActivateCurrentButton = true;
                                } else if (mouseActivateCurrentButton) {
                                    mouseActivateCurrentButton = false;
                                    game.getNuitToolkit().getAudio().getSound(game.getData().getFile("select.wav")).play();
                                    button.activate();
                                }
                            }
                            break;
                        }
                    }
                }
                oldMousePos.set(mousePos);

            }
            if (oldButtonIndex != currentButtonIndex) {
                game.getNuitToolkit().getAudio().getSound(game.getData().getFile("select.wav")).play();
            }
        }
    }

    @Override
    public void start() {
        if (null != backgroundTexturePath) {
            try {
                backgroundTexture = game.getView().loadFromAnimation(backgroundTexturePath).getFirst().start(backgroundPlayMode);
            } catch (IOException ex) {
                Logger.getLogger(MenuSequence.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        currentButtonIndex = 0;
        mouseActivateCurrentButton = null;
        redraw = true;
        if (buttons.contains(defaultButton)) {
            setCurrentButton(defaultButton);
        } else {
            setCurrentButton(buttons.isEmpty() ? null : buttons.get(0));
        }
        final ITextureCache textureCache = game.getView().getTextureCache();
        for (Button button : buttons) {
            button.start();
            button.onTexture = textureCache.getTexture(button.onTextureName);
            button.offTexture = game.getView().getTextureCache().getTexture(button.offTextureName);
        }
        final ROVector2f mousePos = game.getInput().getMousePos();
        if (null != mousePos) {
            this.oldMousePos.set(mousePos);
        }
    }

    @Override
    public void stop() {
        // ensure that textures can be deleted
        backgroundTexture = null;
        for (Button button : buttons) {
            button.onTexture = null;
            button.offTexture = null;
        }
    }

    @Override
    public void resume() {
        mouseActivateCurrentButton = null;
    }

    @Override
    public void update() throws Sequence.NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        if (null != backgroundTexture) {
            backgroundTexture.update(game.getFrameTimeInfos().elapsedTime / 1000000);
        }
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

    public ITexture getBackgroundImage() {
        if (null != backgroundTexture) {
            AnimationFrame frame = backgroundTexture.getCurrentFrame();
            if (null != frame) {
                return frame.getImage();
            }
        }
        return null;
    }

    public abstract class Button {

        public String onTextureName;
        public String offTextureName;
        public float x = 273, y;
        public float w = -1.0f;
        public float h = -1.0f;

        private ITexture onTexture, offTexture;
        private boolean on;

        public ITexture getTexture() {
            return on ? onTexture : offTexture;
        }

        public void draw() {
            game.getView().drawButton(this);
        }

        void setOn() {
            on = true;
        }

        void setOff() {
            on = false;
        }

        abstract void activate() throws Sequence.NormalTransitionException,
                Sequence.ResumeTransitionException, Sequence.ResumableTransitionException;

        void start() {
        }
    }
}
