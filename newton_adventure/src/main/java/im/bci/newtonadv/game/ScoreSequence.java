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

import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.score.ScoreServer;
import im.bci.newtonadv.util.TrueTypeFont;
import java.awt.Font;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author devnewton
 */
public class ScoreSequence implements Sequence {

    static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    static final float ortho2DLeft = 0;
    static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    static final float ortho2DTop = 0;
    TrueTypeFont font;
    private final QuestScore questScore;
    private boolean redraw;
    private boolean mustSendScoreQuit;
    private boolean mustQuitWithoutSendingScore;
    private Sequence nextSequence;
    private FrameTimeInfos timeInfos;
    private long scorePerCentToShow;
    private final ScoreServer scoreServer;

    public ScoreSequence(Game game, String questName, Sequence nextSequence) {
        this.questScore = game.getScore().getQuestScore(questName);
        this.nextSequence = nextSequence;
        this.scoreServer = new ScoreServer(game.getConfig());
    }

    @Override
    public void start() {
        font = new TrueTypeFont(new Font("monospaced", Font.BOLD, 32), false);
        redraw = true;
        mustSendScoreQuit = false;
        mustQuitWithoutSendingScore = false;
        timeInfos = new FrameTimeInfos();
        scorePerCentToShow = 0;
    }

    @Override
    public void stop() {
        font.destroy();
    }

    public void draw() {
        if (Display.isVisible() || Display.isDirty() || Display.wasResized() || redraw) {
            redraw = false;
            GL11.glPushMatrix();
            GLU.gluOrtho2D(ortho2DLeft, ortho2DRight, ortho2DBottom, ortho2DTop);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            int i = 1;
            font.drawString((ortho2DLeft + ortho2DRight) / 2.0f, i++ * font.getHeight(), "SCORES", 1, -1, TrueTypeFont.ALIGN_CENTER);
            font.drawString(0, i++ * font.getHeight(), questScore.getQuestName(), 1, -1, TrueTypeFont.ALIGN_LEFT);
            for (Entry<String, LevelScore> levelEntry : questScore.entrySet()) {
                String levelScoreStr = levelEntry.getKey() + ": " + (scorePerCentToShow * levelEntry.getValue().computeScore() / 100);
                font.drawString((ortho2DLeft + ortho2DRight) / 2.0f, i++ * font.getHeight(), levelScoreStr, 1, -1, TrueTypeFont.ALIGN_CENTER);
            }
            String questScoreStr = "Quest total: " + (scorePerCentToShow * questScore.computeScore() / 100);
            font.drawString(0, i++ * font.getHeight(), questScoreStr, 1, -1, TrueTypeFont.ALIGN_LEFT);
            font.drawString(ortho2DRight, ortho2DBottom - font.getHeight() * 2, "Press space to send score to server ", 1, -1, TrueTypeFont.ALIGN_RIGHT);
            font.drawString(ortho2DRight, ortho2DBottom - font.getHeight(), "Press right to skip ", 1, -1, TrueTypeFont.ALIGN_RIGHT);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    public void update() throws TransitionException {
        timeInfos.update();
        long newScorePercentToShow = Math.min(100, timeInfos.currentTime * 100 / (10 * 1000000000));
        if (newScorePercentToShow != scorePerCentToShow) {
            scorePerCentToShow = newScorePercentToShow;
            redraw = true;
        }
    }

    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            mustSendScoreQuit = true;
        } else if (mustSendScoreQuit) {
            scoreServer.sendScore(questScore.getQuestName(), questScore.computeScore());
            throw new Sequence.TransitionException(nextSequence);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            mustQuitWithoutSendingScore = true;
        } else if (mustQuitWithoutSendingScore) {
            throw new Sequence.TransitionException(nextSequence);
        }
    }

    void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }
}
