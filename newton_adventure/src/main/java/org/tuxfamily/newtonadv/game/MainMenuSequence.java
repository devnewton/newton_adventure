/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuxfamily.newtonadv.game;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.tuxfamily.newtonadv.Game;
import org.tuxfamily.newtonadv.Texture;

/**
 *
 * @author bob
 */
public strictfp class MainMenuSequence implements Sequence {

    static final float ortho2DLeft = 0;
    static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    static final float ortho2DTop = 0;
    private Game game;
    private Sequence playSequence, resumeSequence, optionsSequence, quitSequence;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private int currentButtonIndex;
    private boolean redraw;
    private final Button playButton;
    private final Button resumeButton;

    public MainMenuSequence(Game game, Sequence playSeq, Sequence quitSeq) {
        this.game = game;
        this.playSequence = playSeq;
        this.quitSequence = quitSeq;

        playButton = new Button() {

            @Override
            void activate() throws TransitionException {
                throw new Sequence.TransitionException(playSequence);
            }
        };
        playButton.offTexture = "data/main_menu/bt-play-off.png";
        playButton.currentTexture = playButton.onTexture = "data/main_menu/bt-play-on.png";
        playButton.y = 318;
        buttons.add(playButton);

        resumeButton = new Button() {

            @Override
            void activate() throws TransitionException {
                if (resumeSequence != null) {
                    throw new Sequence.TransitionException(resumeSequence);
                }
            }
        };
        resumeButton.currentTexture = resumeButton.offTexture = "data/main_menu/bt-resume-off.png";
        resumeButton.onTexture = "data/main_menu/bt-resume-on.png";
        resumeButton.y = 441;
        buttons.add(resumeButton);

        Button optionsButton = new Button() {

            @Override
            void activate() throws TransitionException {
                if (optionsSequence != null) {
                    throw new Sequence.TransitionException(optionsSequence);
                }
            }
        };
        optionsButton.currentTexture = optionsButton.offTexture = "data/main_menu/bt-options-off.png";
        optionsButton.onTexture = "data/main_menu/bt-options-on.png";
        optionsButton.y = 558;
        buttons.add(optionsButton);

        Button quitButton = new Button() {

            @Override
            void activate() throws TransitionException {
                throw new Sequence.TransitionException(quitSequence);
            }
        };
        quitButton.currentTexture = quitButton.offTexture = "data/main_menu/bt-quit-off.png";
        quitButton.onTexture = "data/main_menu/bt-quit-on.png";
        quitButton.y = 675;
        buttons.add(quitButton);
    }

    public void setResumeSequence(Sequence s) {
        this.resumeSequence = s;
    }

    public boolean isResumeSequence(Sequence s) {
        return s == resumeSequence && s != null;
    }

    private abstract class Button {

        String onTexture, offTexture, currentTexture = null;
        float x = 273, y;

        void draw() {
            if (currentTexture != null) {
                Texture texture = game.getView().getTextureCache().getTexture(currentTexture);
                texture.bind();
                final float x1 = x;
                final float x2 = x + texture.getWidth();
                final float y1 = y + texture.getHeight();
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

    @Override
    public void start() {
        currentButtonIndex = 0;
        redraw = true;
        for(Button b : buttons)
            b.setOff();

        if( resumeSequence == null ) {
            playButton.setOn();
            currentButtonIndex = buttons.indexOf(playButton);
        } else {
            resumeButton.setOn();
            currentButtonIndex = buttons.indexOf(resumeButton);
        }
    }

    @Override
    public void stop() {
        resumeSequence = null;
    }

    @Override
    public void draw() {
        if (Display.isDirty() || redraw) {
            redraw = false;

            GL11.glPushMatrix();
            GLU.gluOrtho2D(ortho2DLeft, ortho2DRight, ortho2DBottom, ortho2DTop);
            game.getView().getTextureCache().getTexture("data/main_menu/home.png").bind();
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

            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
            for (Button b : buttons) {
                b.draw();
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    @Override
    public void update() throws TransitionException {
        //NOTHING
    }
    private boolean selectNextButton = false, selectPreviousButton = false, activateCurrentButton = false;

    @Override
    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
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

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
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
}
