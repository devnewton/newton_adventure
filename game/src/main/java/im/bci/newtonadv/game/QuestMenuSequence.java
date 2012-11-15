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
import java.util.Iterator;
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
	private List<QuestMenuSequence> questMenuSequences = new ArrayList<QuestMenuSequence>();
	private static final int NB_BUTTONS_ON_X = 2;
	private static final int NB_BUTTONS_ON_Y = 3;
	private static final float QUEST_MINIATURE_SPACING = 60;
	private static final float QUEST_MINIATURE_WIDTH = (ortho2DRight - QUEST_MINIATURE_SPACING
			* (NB_BUTTONS_ON_X + 1))
			/ NB_BUTTONS_ON_X;
	public static final float QUEST_MINIATURE_HEIGHT = (ortho2DBottom - QUEST_MINIATURE_SPACING
			* (NB_BUTTONS_ON_Y + 1))
			/ NB_BUTTONS_ON_Y;

	public QuestMenuSequence(Game game) {
		super(game);
		verticalIncrement = NB_BUTTONS_ON_X;
		loadQuests(game.getData().listQuests()
				.iterator(), null);
	}
	
	public QuestMenuSequence(Game game, QuestMenuSequence previousSequence, Iterator<String> questNamesIterator) {
		super(game);
		verticalIncrement = NB_BUTTONS_ON_X;		
		loadQuests(questNamesIterator,previousSequence);
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

	private void loadQuests(Iterator<String> questNamesIterator, QuestMenuSequence previousSequence) {
		quests = new ArrayList<QuestSequence>();
		
		int nbQuestButtonCreated = 0;
		final int maxQuestButtons = NB_BUTTONS_ON_X * NB_BUTTONS_ON_Y - (null == previousSequence ? 1 : 2);
		for (int j = 0; j < NB_BUTTONS_ON_Y; ++j) {
			for (int i = 0; i < NB_BUTTONS_ON_X; ++i) {
				if (questNamesIterator.hasNext() && nbQuestButtonCreated < maxQuestButtons) {
					String questName = questNamesIterator.next();
					QuestSequence questSequence = new QuestSequence(game, questName);
					quests.add(questSequence);
					createQuestButton(i, j, questName, questSequence);
					++nbQuestButtonCreated;
				} else if(null != previousSequence){
					createNavigateButton(i, j, previousSequence, "bt-previous");
					previousSequence = null;
				} else if(questNamesIterator.hasNext()) {
					QuestMenuSequence nextQuestMenu = new QuestMenuSequence(game, this, questNamesIterator);
					questMenuSequences .add(nextQuestMenu);
					createNavigateButton(i, j, nextQuestMenu, "bt-next");
					return;
				} else {
					createReturnToMenuButton(i, j);
					return;
				}
			}
		}
	}

	private void createNavigateButton(int i, int j,
			final QuestMenuSequence sequence, String textureBaseName) {
		Button button = new Button() {

			@Override
			void activate() throws NormalTransitionException,
					ResumeTransitionException {
				throw new NormalTransitionException(sequence);				
			}
			
		};
		button.offTextureName = game.getData().getFile("quest_menu/" + textureBaseName + "-off.png");
		button.onTextureName = game.getData().getFile("quest_menu/" + textureBaseName + "-on.png");
		
		button.x = QUEST_MINIATURE_SPACING + i
				* (QUEST_MINIATURE_WIDTH + QUEST_MINIATURE_SPACING);
		button.y = QUEST_MINIATURE_SPACING + j
				* (QUEST_MINIATURE_HEIGHT + QUEST_MINIATURE_SPACING);
		button.w = QUEST_MINIATURE_WIDTH;
		button.h = QUEST_MINIATURE_HEIGHT;
		
		addButton(button);
	}

	private void createReturnToMenuButton(int i, int j) {
		Button returnToMenuButton = new Button() {

			@Override
			void activate() throws NormalTransitionException,
					ResumeTransitionException {
				throw new NormalTransitionException(game.getMainMenuSequence());				
			}
			
		};
		returnToMenuButton.offTextureName = game.getData().getFile("quest_menu/bt-menu-off.png");
		returnToMenuButton.onTextureName = game.getData().getFile("quest_menu/bt-menu-on.png");
		
		returnToMenuButton.x = QUEST_MINIATURE_SPACING + i
				* (QUEST_MINIATURE_WIDTH + QUEST_MINIATURE_SPACING);
		returnToMenuButton.y = QUEST_MINIATURE_SPACING + j
				* (QUEST_MINIATURE_HEIGHT + QUEST_MINIATURE_SPACING);
		returnToMenuButton.w = QUEST_MINIATURE_WIDTH;
		returnToMenuButton.h = QUEST_MINIATURE_HEIGHT;
		
		addButton(returnToMenuButton);
		
	}

	public void setNextSequence(Sequence sequence) {
		for (QuestSequence quest : quests) {
			quest.setNextSequence(sequence);
		}
		for(QuestMenuSequence questMenu : questMenuSequences) {
			questMenu.setNextSequence(sequence);
		}
	}

	private void createQuestButton(int i, int j, final String questName,
			QuestSequence questSequence) {
		final LevelMenuSequence levelMenuSequence = new LevelMenuSequence(game, questSequence);
		Button questButton = new Button() {

			@Override
			void activate() throws Sequence.NormalTransitionException {
				if(!game.isQuestBlocked(questName)) {
					throw new Sequence.NormalTransitionException(levelMenuSequence);
				}
			}

			@Override
			public void draw() {
				game.getView().drawMenuButton(this, questNameFont,
						questName);
			}
			
			@Override
			public void start() {
				if(game.isQuestBlocked(questName)) {
					onTextureName = game.getData().getFile("btn-blocked.png");
				} else {
					onTextureName = game.getData().getQuestFile(questName,
							"bt-quest-on.jpg");
				}
			}
		};
		questButton.offTextureName = game.getData().getQuestFile(questName,
				"bt-quest-off.jpg");
		questButton.onTextureName = game.getData().getQuestFile(questName,
				"bt-quest-on.jpg");

		questButton.x = QUEST_MINIATURE_SPACING + i
				* (QUEST_MINIATURE_WIDTH + QUEST_MINIATURE_SPACING);
		questButton.y = QUEST_MINIATURE_SPACING + j
				* (QUEST_MINIATURE_HEIGHT + QUEST_MINIATURE_SPACING);
		questButton.w = QUEST_MINIATURE_WIDTH;
		questButton.h = QUEST_MINIATURE_HEIGHT;
		addButton(questButton);
	}

	public void gotoLevel(String newQuestName, String newLevelName) throws Sequence.NormalTransitionException {
		for(QuestSequence quest: quests) {
			if(quest.getQuestName().equals(newLevelName)) {
				quest.gotoLevel(newLevelName);
			}
		}
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
