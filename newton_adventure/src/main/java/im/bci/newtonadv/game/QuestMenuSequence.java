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
import java.util.List;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;

/**
 *
 * @author devnewton
 */
public class QuestMenuSequence extends MenuSequence {

    List<QuestSequence> quests;
    ITrueTypeFont questNameFont;
    static final int QUEST_MINIATURE_BY_ROW = 2;
    static final int QUEST_MINIATURE_BY_COLUMN = 2;
    static final float QUEST_MINIATURE_SPACING = 60;
    static final float QUEST_MINIATURE_WIDTH = (ortho2DRight - QUEST_MINIATURE_SPACING * (QUEST_MINIATURE_BY_ROW + 1)) / QUEST_MINIATURE_BY_ROW;
    public static final float QUEST_MINIATURE_HEIGHT = (ortho2DBottom - QUEST_MINIATURE_SPACING * (QUEST_MINIATURE_BY_COLUMN + 1)) / QUEST_MINIATURE_BY_COLUMN;

    public QuestMenuSequence(Game game) {
        super(game);
        verticalIncrement = QUEST_MINIATURE_BY_ROW;
        loadQuests();
    }

    @Override
    public void start() {
        super.start();
        questNameFont = game.getView().createQuestNameFont();
    }

    @Override
    public void stop() {
        super.stop();
        questNameFont.destroy();
    }

    private void loadQuests() {
        quests = new ArrayList<QuestSequence>();

        List<String> questNames = game.getData().listQuests();
        for (int i = 0; i < questNames.size(); ++i) {
            String questName = questNames.get(i);
            QuestSequence questSequence = new QuestSequence(game, questName);
            quests.add(questSequence);
            createQuestButton(i, questName, questSequence);
        }
    }

    public void setNextSequence(Sequence sequence) {
        for (QuestSequence quest : quests) {
            quest.setNextSequence(sequence);
        }
    }

    private void createQuestButton(int i, final String questName, final QuestSequence questSequence) {
        Button questButton = new Button() {

            @Override
            void activate() throws TransitionException {
                throw new Sequence.TransitionException(questSequence);
            }

            @Override
            public void draw() {
                game.getView().drawQuestMenuButton(this, questNameFont, questName);
            }
        };
        questButton.offTexture = game.getData().getQuestFile(questName, "bt-quest-off.jpg");
        questButton.onTexture = game.getData().getQuestFile(questName, "bt-quest-on.jpg");

        questButton.x = QUEST_MINIATURE_SPACING + (i % QUEST_MINIATURE_BY_COLUMN) * (QUEST_MINIATURE_WIDTH + QUEST_MINIATURE_SPACING);
        questButton.y = QUEST_MINIATURE_SPACING + (i / QUEST_MINIATURE_BY_ROW) * (QUEST_MINIATURE_HEIGHT + QUEST_MINIATURE_SPACING);
        questButton.w = QUEST_MINIATURE_WIDTH;
        questButton.h = QUEST_MINIATURE_HEIGHT;
        addButton(questButton);
    }
}
