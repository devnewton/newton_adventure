/*
 * Copyright (c) 2009 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv;

import im.bci.newtonadv.game.BonusSequence;
import im.bci.newtonadv.game.FadeSequence;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.IGameInput;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.game.FrameTimeInfos;
import java.io.IOException;
import java.util.Properties;

import im.bci.newtonadv.game.CustomTickSequence;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.QuestMenuSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.Sequence.ResumableTransitionException;
import im.bci.newtonadv.game.StoryboardSequence;
import im.bci.newtonadv.score.GameScore;
import im.bci.newtonadv.score.ScoreServer;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author devnewton
 */
public strictfp class Game {

    private final IGameView view;
    private final IGameInput input;
    private final IGameData data;
    private boolean running = true;
    static public final int FPS = 60;
    static public final float FPSf = FPS;
    static public final int DEFAULT_SCREEN_WIDTH = 1280;
    static public final int DEFAULT_SCREEN_HEIGHT = 800;
    private FrameTimeInfos frameTimeInfos = new FrameTimeInfos();
    private Properties config;
    private ISoundCache soundCache = null;
    private MainMenuSequence mainMenuSequence;
    private GameScore score;
    private Sequence currentSequence;
    private List<BonusSequence> bonusSequences;
    private BonusSequence lastBonusSequence;
    private IOptionsSequence optionsSequence;
    private QuestMenuSequence questMenuSequence;
    private IPlatformSpecific platform;
    private GameProgression progression;

    public Properties getConfig() {
        return config;
    }

    public FrameTimeInfos getFrameTimeInfos() {
        return frameTimeInfos;
    }

    public IGameView getView() {
        return view;
    }

    public ISoundCache getSoundCache() {
        return soundCache;
    }

    public Game(IPlatformSpecific platform) throws Exception {
        this.config = platform.getConfig();
        this.data = platform.getGameData();
        this.score = platform.loadScore();
        this.progression = platform.loadProgression();
        this.soundCache = platform.getSoundCache();
        this.view = platform.getGameView();
        this.input = platform.getGameInput();
        this.scoreServer = platform.getScoreServer();
        this.optionsSequence = platform.getOptionsSequence();

        this.platform = platform;
    }

    public void tick() {
        try {
            if (bShowMainMenu) {
                bShowMainMenu = false;
                if (currentSequence != mainMenuSequence) {
                    mainMenuSequence.setResumeSequence(currentSequence);
                    currentSequence = mainMenuSequence;
                    mainMenuSequence.start();
                }
            }
            frameTimeInfos.update();
            soundCache.update();
            if (currentSequence instanceof CustomTickSequence) {
                ((CustomTickSequence) currentSequence).tick();
            } else {
                view.draw(currentSequence);
                for (input.beginPoll(); input.poll();) {
                    processInputs();
                    if (!frameTimeInfos.paused) {
                        currentSequence.processInputs();
                    }
                }
                if (!frameTimeInfos.paused) {
                    currentSequence.update();
                }
            }
        } catch (Sequence.NormalTransitionException ex) {
            currentSequence.stop();
            currentSequence = ex.getNextSequence();
            collectGarbage();
            if (currentSequence == null) {
                stopGame();
            } else {
                currentSequence.start();
                collectGarbage();
            }
        } catch (Sequence.ResumeTransitionException ex) {
            currentSequence.stop();
            currentSequence = ex.getNextSequence();
            collectGarbage();
            currentSequence.resume();
            collectGarbage();
        } catch (ResumableTransitionException e) {
            currentSequence = e.getNextSequence();
            collectGarbage();
            currentSequence.start();
            collectGarbage();
        }
    }

    private void collectGarbage() {
        System.gc();
        getView().getTextureCache().clearUseless();
        getSoundCache().clearUseless();
    }

    void stopGame() {
        running = false;
        getView().getTextureCache().clearAll();
        getSoundCache().stopMusic();
    }

    Sequence setupSequences() {
        Sequence outroSequence = new StoryboardSequence(this,
                data.getFile("outro.jpg"), data.getFile("The_End.ogg"), new Sequence.NormalTransitionException(null));
        this.questMenuSequence = new QuestMenuSequence(this);
        mainMenuSequence = new MainMenuSequence(this, questMenuSequence,
                outroSequence, optionsSequence);
        questMenuSequence.setNextSequence(mainMenuSequence);
        if (null != optionsSequence) {
            optionsSequence.setNextSequence(mainMenuSequence);
        }
        loadBonusSequences();
        return mainMenuSequence;
    }

    public void start() throws IOException {

        currentSequence = setupSequences();
        currentSequence.start();
    }
    private boolean bToggleFullscreen = false;
    private boolean bTogglePause = false;
    private boolean bShowMainMenu = false;
    private ScoreServer scoreServer;

    private void processInputs() {
        if (input.isKeyReturnToMenuDown()) {
            bShowMainMenu = true;
        }
        if (input.isKeyToggleFullscreenDown()) {
            bToggleFullscreen = true;
        } else if (bToggleFullscreen) {
            bToggleFullscreen = false;
            view.toggleFullscreen();
        }
        if (input.isKeyPauseDown()) {
            bTogglePause = true;
        } else if (bTogglePause) {
            bTogglePause = false;
            frameTimeInfos.togglePause();
        }
    }

    public GameScore getScore() {
        return score;
    }

    final public IGameInput getInput() {
        return input;
    }

    public boolean isRunning() {
        return running;
    }

    public IGameData getData() {
        return data;
    }

    public void goToRandomBonusLevel(String currentQuestName)
            throws ResumableTransitionException {
        if (!bonusSequences.isEmpty()) {
            BonusSequence bonusSequence = bonusSequences.get(frameTimeInfos.random.nextInt(bonusSequences.size()));
            bonusSequence.setCurrentQuestName(currentQuestName);
            bonusSequence.setNextSequence(currentSequence);
            throw new Sequence.ResumableTransitionException(new FadeSequence(
                    this, new Sequence.NormalTransitionException(bonusSequence), 1, 1, 1, 1000000000L));
        }
    }

    public void goToNextBonusLevel(String currentQuestName)
            throws ResumableTransitionException {
        if (!bonusSequences.isEmpty()) {
            int i = bonusSequences.indexOf(lastBonusSequence) + 1;
            if (i < 0 || i >= bonusSequences.size()) {
                i = 0;
            }
            lastBonusSequence = bonusSequences.get(i);
            lastBonusSequence.setCurrentQuestName(currentQuestName);
            lastBonusSequence.setNextSequence(currentSequence);
            throw new Sequence.ResumableTransitionException(new FadeSequence(
                    this, new Sequence.NormalTransitionException(lastBonusSequence), 1, 1, 1, 1000000000L));
        }
    }

    private void loadBonusSequences() {
        bonusSequences = new ArrayList<BonusSequence>();
        List<String> levelNames = getData().listQuestLevels("bonus");

        for (String levelName : levelNames) {
            BonusSequence levelSequence = new BonusSequence(this, levelName);
            bonusSequences.add(levelSequence);
        }
    }

    public Sequence getMainMenuSequence() {
        return mainMenuSequence;
    }

    public ScoreServer getScoreServer() {
        return scoreServer;
    }

    public void gotoLevel(String newQuestName, String newLevelName)
            throws Sequence.NormalTransitionException {
        this.questMenuSequence.gotoLevel(newQuestName, newLevelName);
    }

    public boolean isQuestBlocked(String questName) {
        for (String questToComplete : data.listQuestsToCompleteToUnlockQuest(questName)) {
            if (!isQuestCompleted(questToComplete)) {
                return true;
            }
        }
        return false;
    }

    private boolean isQuestCompleted(String questName) {
        for (String levelName : data.listQuestLevels(questName)) {
            if (isLevelBlocked(questName, levelName)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLevelBlocked(String questName, String levelName) {
        String previousLevelName = null;
        for (String l : data.listQuestLevels(questName)) {
            if (l.equals(levelName)) {
                if(null != previousLevelName) {
                    return !progression.getQuest(questName).getLevel(previousLevelName).isCompleted();
                } else {
                    return false;
                }
            }
            previousLevelName = l;
        }
        return false;
    }

    public void setLevelCompleted(String questName, String completedLevelName) {
        progression.getQuest(questName).getLevel(completedLevelName).setCompleted(true);
        platform.saveProgression(progression);
        platform.saveConfig();
    }

    public void showHelp() throws ResumableTransitionException {
        throw new ResumableTransitionException(new StoryboardSequence(this, this.getData().getFile("help.jpg"), null, new Sequence.ResumeTransitionException(currentSequence)));
    }

    public void insertCoins() {
        platform.openUrl("http://devnewton.bci.im/projects/newton_adventure/wiki?name=Donation");
    }

    public void saveScore() {
        platform.saveScore(score);
    }
}
