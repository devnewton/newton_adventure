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
import im.bci.newtonadv.anim.Animation.Play;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;
import im.bci.newtonadv.world.GameOverException;
import im.bci.newtonadv.world.TmxLoader;
import im.bci.newtonadv.world.World;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author devnewton
 */
strictfp public class LevelSequence implements Sequence, PreloadableSequence {

    static final int nbWorldStepByFrame = 1;
    static final int maxWorldStepByFrame = 5;
    private FrameTimeInfos frameTimeInfos;
    protected World world;
    public long stepTime = 0;
    protected ITrueTypeFont indicatorsFont;
    protected Sequence nextSequence;
    protected Game game;
    protected String questName, levelName;
    private boolean cheatCodeGotoNextLevel = false;
    private boolean cheatCodeGotoNextBonusLevel = false;
    private ITexture minimapTexture;
    private ITrueTypeFont scoreIndicatorsFont;
    private TmxLoader worldLoader;
    private Play loadingPlay;

    public LevelSequence(Game game, String questName, String levelName) {
        this.game = game;
        this.questName = questName;
        this.levelName = levelName;
    }

    @Override
    public void preload() {
        try {
            world = new World(game, questName, levelName, scoreIndicatorsFont);
            worldLoader = new TmxLoader(game, world, questName, levelName);
            worldLoader.preloading();
        } catch (Exception ex) {
            Logger.getLogger(LevelSequence.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void start() {
        try {
            cheatCodeGotoNextLevel = false;
            cheatCodeGotoNextBonusLevel = false;
            indicatorsFont = game.getView().createAppleFont(questName, levelName);
            scoreIndicatorsFont = game.getView().createScoreIndicatorFont(questName, levelName);

            frameTimeInfos = game.getFrameTimeInfos();
            loadingPlay = game.getView().loadFromAnimation(game.getData().getFile("loading.nanim")).getFirst().start();
            worldLoader.startLoading();
            String minimapPath = game.getData().getLevelFilePath(questName, levelName, "minimap.png");
            if (game.getData().fileExists(minimapPath)) {
                minimapTexture = game.getView().getTextureCache().getTexture(minimapPath);
            } else {
                minimapTexture = null;
            }
        } catch (Exception ex) {
            Logger.getLogger(LevelSequence.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void stop() {
        indicatorsFont.destroy();
        scoreIndicatorsFont.destroy();
        scoreIndicatorsFont = null;
        indicatorsFont = null;
        world = null;
        worldLoader = null;
    }

    @Override
    public void update() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        if (worldLoader == null) {
            try {
                if (world.getHero().isDead()) {
                    world.getHero().update(frameTimeInfos);
                    return;
                }

                stepTime += frameTimeInfos.elapsedTime;
                if (stepTime >= 1000000000 / Game.FPS) {
                    stepTime -= 1000000000 / Game.FPS;
                    final int step = 5;
                    for (int i = 0; i < step; ++i) {
                        world.step();
                    }
                }
                world.update();
                if (world.areObjectivesCompleted() || cheatCodeGotoNextLevel) {
                    game.getScore().setLevelScore(questName, levelName,
                            world.getLevelScore());
                    game.saveScore();
                    game.setLevelCompleted(questName, levelName);
                    throw new NormalTransitionException(new FadeSequence(game,
                            new Sequence.NormalTransitionException(nextSequence), 0, 0, 0, 1000000000L));
                }
                if (cheatCodeGotoNextBonusLevel) {
                    game.goToNextBonusLevel(questName);
                }
            } catch (GameOverException ex) {
                throw new NormalTransitionException(new GameOverSequence(game, this));
            }
        } else {
            loadingPlay.update(frameTimeInfos.currentTime / 1000000);
            try {
                long startLoadingTime = System.nanoTime();
                for (;;) {
                    if (worldLoader.loadSome()) {
                        long currentLoadingTime = System.nanoTime();
                        long elapsedLoadingDuration = currentLoadingTime - startLoadingTime;
                        if (elapsedLoadingDuration > (1000000000L / 24L)) {
                            break;
                        }
                    } else {
                        worldLoader.finishLoading();
                        worldLoader = null;
                        break;
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException("Cannot load level " + levelName + " of quest " + questName, ex);
            }
        }
    }

    @Override
    public void processInputs() {
        if (null == worldLoader) {
            if (world.getHero().isDead()) {
                return;
            }
            processRotateInputs();
            processMovingInput();
            processCheatInput();
        }
    }

    protected void processCheatInput() {
        if (game.getInput().isKeyCheatActivateAllDown()) {
            world.cheatActivateAll();
        }
        if (game.getInput().isKeyCheatGotoNextLevelDown()) {
            cheatCodeGotoNextLevel = true;
        }
        if (game.getInput().isKeyCheatGotoNextBonusLevelDown()) {
            cheatCodeGotoNextBonusLevel = true;
        }
        if (game.getInput().isKeyCheatGetWorldMapDown()) {
            world.getHero().setHasMap(true);
        }
        if (game.getInput().isKeyCheatGetCompassDown()) {
            world.getHero().setHasCompass(true);
        }
    }

    protected void processMovingInput() {
        final float stepRate = 1.0f;
        boolean heroIsMoving = false;
        if (game.getInput().isKeyLeftDown()) {
            world.getHero().moveLeft(stepRate);
            heroIsMoving = true;
        }
        if (game.getInput().isKeyRightDown()) {
            world.getHero().moveRight(stepRate);
            heroIsMoving = true;
        }

        if (game.getInput().isKeyJumpDown()) {
            world.getHero().jump(stepRate);
            heroIsMoving = true;
        }
        if (!heroIsMoving) {
            world.getHero().dontMove();
        }
    }

    protected void processRotateInputs() {
        final float stepRate = 1.0f;
        if (game.getInput().isKeyRotateClockwiseDown()) {
            world.progressiveRotateGravity(0.05f * stepRate);
        }
        if (game.getInput().isKeyRotateCounterClockwiseDown()) {
            world.progressiveRotateGravity(-0.05f * stepRate);
        }
        if (game.getInput().isKeyRotate90ClockwiseDown()) {
            world.rotateGravity((float) (Math.PI / 4.0));
        }
        if (game.getInput().isKeyRotate90CounterClockwiseDown()) {
            world.rotateGravity((float) (-Math.PI / 4.0));
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void draw() {
        if (null == worldLoader) {
            world.draw();
            game.getView().drawFPS(game.getFrameTimeInfos().fps);
            drawIndicators();
            drawMinimap();
        } else {
            game.getView().drawLoading(loadingPlay);
        }
    }

    private void drawMinimap() {
        if (null != minimapTexture) {
            game.getView().drawMinimap(world, minimapTexture);
        }
    }

    protected void drawIndicators() {
        game.getView().drawLevelIndicators(world.getHero().getNbApple() + "$ score: " + world.getHero().getLevelScore().computeScore(),
                indicatorsFont);
    }

    public void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }

    public Object getLevelName() {
        return this.levelName;
    }

    @Override
    public void resume() {
        this.cheatCodeGotoNextBonusLevel = false;
        this.cheatCodeGotoNextLevel = false;
        world.resume();
    }
}
