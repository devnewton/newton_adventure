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
public class ScoreSequence extends MenuSequence {

    ITrueTypeFont font;
    private final QuestScore questScore;
    private Sequence nextSequence;
    private FrameTimeInfos timeInfos;
    private long scorePerCentToShow;
    private final ScoreServer scoreServer;

    public ScoreServer getScoreServer() {
        return scoreServer;
    }

    public ScoreSequence(Game game, String questName, Sequence nextSequence) {
        super(game);
        this.questScore = game.getScore().getQuestScore(questName);
        this.nextSequence = nextSequence;
        this.scoreServer = game.getScoreServer();
               
       Button shareButton = new Button() {

            @Override
            void activate() throws NormalTransitionException,
                    ResumeTransitionException, ResumableTransitionException {
                scoreServer.sendScore(questScore.getQuestName(), questScore.computeScore());
                throw new NormalTransitionException(ScoreSequence.this.nextSequence);
            }
        };
        shareButton.offTextureName = game.getData().getFile("bt-share-off.png");
        shareButton.onTextureName = game.getData().getFile("bt-share-on.png");
        shareButton.x = 640;
        shareButton.y = 700;
        shareButton.w = 312;
        shareButton.h = 90;
        addButton(shareButton);
        
        Button continueButton = new Button() {

            @Override
            void activate() throws NormalTransitionException,
                    ResumeTransitionException, ResumableTransitionException {
                throw new NormalTransitionException(ScoreSequence.this.nextSequence);
            }
        };
        continueButton.offTextureName = game.getData().getFile("bt-continue-off.png");
        continueButton.onTextureName = game.getData().getFile("bt-continue-on.png");
        continueButton.x = 960;
        continueButton.y = 700;
        continueButton.w = 312;
        continueButton.h = 90;
        addButton(continueButton);
        
        setDefaultButton(shareButton);
    }

    @Override
    public void start() {
        super.start();
        font = game.getView().createScoreSequenceFont();

        timeInfos = new FrameTimeInfos();
        scorePerCentToShow = 0;
    }

    @Override
    public void stop() {
        super.stop();
        font.destroy();
        game.saveScore();
    }

    @Override
    public void draw() {
        game.getView().drawScoreSequence(this,font,questScore,scorePerCentToShow);
    }

    @Override
	public void update() {
        super.update();
        timeInfos.update();
        long newScorePercentToShow = Math.min(100, timeInfos.currentTime * 100 / (10 * 1000000000));
        if (newScorePercentToShow != scorePerCentToShow) {
            scorePerCentToShow = newScorePercentToShow;
            redraw = true;
        }
    }

    void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }

    @Override
    public void resume() {
    }
}
