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

import im.bci.newtonadv.score.LevelScore;
import java.util.Iterator;
import im.bci.newtonadv.Game;

/**
 * 
 * @author devnewton
 */
public class LevelMenuSequence extends MenuSequence {

    private QuestSequence questSequence;
    static final int LEVEL_MINIATURE_ON_X = 2;
    static final int LEVEL_MINIATURE_ON_Y = 3;
    static final float LEVEL_MINIATURE_SPACING = 60;
    static final float LEVEL_MINIATURE_WIDTH = (ortho2DRight - LEVEL_MINIATURE_SPACING
            * (LEVEL_MINIATURE_ON_X + 1))
            / LEVEL_MINIATURE_ON_X;
    public static final float LEVEL_MINIATURE_HEIGHT = (ortho2DBottom - LEVEL_MINIATURE_SPACING
            * (LEVEL_MINIATURE_ON_Y + 1))
            / LEVEL_MINIATURE_ON_Y;

    public LevelMenuSequence(Game game, QuestSequence questSequence) {
        super(game);
        this.questSequence = questSequence;
        verticalIncrement = LEVEL_MINIATURE_ON_X;
    }

    @Override
    public void start() {
        loadLevels();
        Button returnToPreviousSequence = new Button() {

            @Override
            void activate() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
                game.gotoQuestMenu();
            }
        };
        returnToPreviousSequence.offTextureName = game.getData().getFile("quest_menu/bt-menu-off.png");
        returnToPreviousSequence.onTextureName = game.getData().getFile("quest_menu/bt-menu-on.png");
        returnToPreviousSequence.x = ortho2DRight - LEVEL_MINIATURE_WIDTH - LEVEL_MINIATURE_SPACING;
        returnToPreviousSequence.y = ortho2DBottom - LEVEL_MINIATURE_SPACING * 4.8f / 4.0f;
        returnToPreviousSequence.w = LEVEL_MINIATURE_WIDTH;
        returnToPreviousSequence.h = LEVEL_MINIATURE_SPACING;
        addButton(returnToPreviousSequence);
        super.start();
    }

    private void loadLevels() {

        clearButtons();

        Iterator<String> levelNamesIterator = game.getData().listQuestLevels(questSequence.getQuestName()).iterator();

        for (int j = 0; j < LEVEL_MINIATURE_ON_Y; ++j) {
            for (int i = 0; i < LEVEL_MINIATURE_ON_X; ++i) {
                if (!levelNamesIterator.hasNext()) {
                    return;
                }
                String levelName = levelNamesIterator.next();
                createLevelButton(i, j, levelName);
            }
        }
    }

    private void createLevelButton(int i, int j, final String levelName) {
        final boolean isBlocked = game.isLevelBlocked(questSequence.getQuestName(), levelName);
        Button questButton = new Button() {

            @Override
            void activate() throws NormalTransitionException {
                if (!isBlocked) {
                    questSequence.gotoLevel(levelName);
                }
            }

            @Override
            public void draw() {
                String scoreLabel = "";
                LevelScore levelScore = game.getScore().getQuestScore(questSequence.getQuestName()).get(levelName);
                if (null != levelScore) {
                    int score = levelScore.computeScore();
                    if (score > 0) {
                        scoreLabel += score;
                    }
                }
                game.getView().drawMenuButton(this, levelName, scoreLabel);
            }
        };
        questButton.offTextureName = game.getData().getLevelFilePath(questSequence.getQuestName(),
                levelName, "bt-level-off.png");
        if (!isBlocked) {
            questButton.onTextureName = game.getData().getLevelFilePath(questSequence.getQuestName(),
                    levelName, "bt-level-on.png");
        } else {
            questButton.onTextureName = game.getData().getFile("btn-blocked.png");
        }

        questButton.x = LEVEL_MINIATURE_SPACING + i
                * (LEVEL_MINIATURE_WIDTH + LEVEL_MINIATURE_SPACING);
        questButton.y = LEVEL_MINIATURE_SPACING / 4.0f + j
                * (LEVEL_MINIATURE_HEIGHT + LEVEL_MINIATURE_SPACING);
        questButton.w = LEVEL_MINIATURE_WIDTH;
        questButton.h = LEVEL_MINIATURE_HEIGHT;
        addButton(questButton);
    }

    @Override
    public void resume() {
    }
}
