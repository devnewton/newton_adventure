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
import im.bci.newtonadv.game.FadeSequence.FadeSequenceTransition;

/**
 *
 * @author devnewton
 */
public strictfp class BonusSequence extends LevelSequence {

    private long endTime;
    private String currentQuestName;

    public BonusSequence(Game game, String levelName) {
        super(game, "bonus", levelName);
    }

    public void setCurrentQuestName(String currentQuestName) {
        this.currentQuestName = currentQuestName;
    }

    @Override
    protected void processRotateInputs() {
    }

    @Override
    public strictfp void start() {
        super.start();
        endTime = game.getFrameTimeInfos().currentTime + 60000000000L;
    }

    @Override
    public strictfp void update() throws ResumeTransitionException, NormalTransitionException, ResumableTransitionException {
        super.update();

        if (game.getFrameTimeInfos().currentTime > endTime) {
            game.getScore().setLevelScore(currentQuestName, levelName, world.getLevelScore());
            throw new NormalTransitionException(new FadeSequence(game, nextSequence, 1, 1, 1, 1000000000L,FadeSequenceTransition.RESUME));
        }
    }

    @Override
    protected strictfp void drawIndicators() {
        StringBuilder b = new StringBuilder();
        b.append(world.getHero().getNbApple());
        b.append("$ ");
        long seconds = (endTime - game.getFrameTimeInfos().currentTime) / 1000000000L;
        b.append(seconds / 60);
        b.append(":");
        b.append(String.format("%02d", seconds % 60));
        game.getView().drawLevelIndicators(b.toString(), indicatorsFont);
    }
}
