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
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.score.ScoreServer;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;

/**
 *
 * @author devnewton
 */
public class ScoreSequence implements Sequence {

    public static final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    public static final float ortho2DLeft = 0;
    public static final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    public static final float ortho2DTop = 0;
    ITrueTypeFont font;
    private final QuestScore questScore;
    private boolean redraw;
    private boolean mustSendScoreQuit;
    private boolean mustQuitWithoutSendingScore;
    private Sequence nextSequence;
    private FrameTimeInfos timeInfos;
    private long scorePerCentToShow;
    private final ScoreServer scoreServer;
    private final Game game;

    public ScoreSequence(Game game, String questName, Sequence nextSequence) {
        this.questScore = game.getScore().getQuestScore(questName);
        this.nextSequence = nextSequence;
        this.scoreServer = game.getScoreServer();
        this.game = game;
    }

    @Override
    public void start() {
        font = game.getView().createScoreSequenceFont();
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

    @Override
	public void draw() {
        game.getView().drawScoreSequence(this,font,questScore,scorePerCentToShow);
    }

    @Override
	public void update() throws TransitionException {
        timeInfos.update();
        long newScorePercentToShow = Math.min(100, timeInfos.currentTime * 100 / (10 * 1000000000));
        if (newScorePercentToShow != scorePerCentToShow) {
            scorePerCentToShow = newScorePercentToShow;
            redraw = true;
        }
    }

    @Override
	public void processInputs() throws TransitionException {
        if (game.getInput().isKeyReturnDown()) {
            mustSendScoreQuit = true;
        } else if (mustSendScoreQuit) {
            scoreServer.sendScore(questScore.getQuestName(), questScore.computeScore());
            throw new Sequence.TransitionException(nextSequence);
        }
        if (game.getInput().isKeyRightDown()) {
            mustQuitWithoutSendingScore = true;
        } else if (mustQuitWithoutSendingScore) {
            throw new Sequence.TransitionException(nextSequence);
        }
    }

    void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }

    public boolean isDirty() {
        return redraw;
    }

    public void setDirty(boolean b) {
        redraw = b;
    }
}
