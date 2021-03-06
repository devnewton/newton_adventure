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

/**
 *
 * @author devnewton
 */
public class QuestSequence implements Sequence {

    private final Game game;
    List<LevelSequence> levels;
    private Sequence nextSequence;
    private ScoreSequence scoreSequence;
	private final String questName;

    public QuestSequence(Game game, String questName) {
        this.game = game;
        this.questName = questName;
        loadLevels(questName);
    }

    @Override
	public void start() {
    }

    @Override
	public void draw() {
    }

    @Override
	public void stop() {
    }

    @Override
	public void update() throws NormalTransitionException {
        throw new NormalTransitionException(levels.isEmpty() ? nextSequence : levels.get(0));
    }

    @Override
	public void processInputs() {
    }

    private void loadLevels(String questName) {
        levels = new ArrayList<LevelSequence>();
        List<String> levelNames = game.getData().listQuestLevels(questName);
        LevelSequence lastSequence = null;
        
        for (String levelName : levelNames) {
                LevelSequence levelSequence = new LevelSequence(game, questName, levelName);
                levels.add(levelSequence);
                if (lastSequence != null) {
                    lastSequence.setNextSequence(levelSequence);
                }
                lastSequence = levelSequence;
        }

        if(lastSequence!=null) {
            scoreSequence = new ScoreSequence(game, questName, nextSequence);
            StoryboardSequence completedSequence = new StoryboardSequence(game, game.getData().getQuestFile(questName,"completed.png"), game.getData().getFile("story_time.ogg"), new NormalTransitionException(scoreSequence));
            lastSequence.setNextSequence(completedSequence);
        }
    }

    public void setNextSequence(Sequence s) {
        nextSequence = s;
        if (null != scoreSequence) {
            scoreSequence.setNextSequence(nextSequence);
        }
    }

	public String getQuestName() {
		return this.questName;
	}

	public void gotoLevel(String newLevelName) throws NormalTransitionException {
		for(LevelSequence level: levels) {
			if(level.getLevelName().equals(newLevelName)) {
				throw new NormalTransitionException(new PreloaderFadeSequence(this.game, level, 0, 0, 0, 1000000000L));
			}
		}
		
	}

	@Override
	public void resume() {
	}
}
