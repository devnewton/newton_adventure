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
import im.bci.newtonadv.game.time.OneShotTimedAction;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.world.GameOverException;
import im.bci.newtonadv.world.Hero;
import im.bci.newtonadv.world.TmxLoader;
import im.bci.newtonadv.world.World;

/**
 *
 * @author devnewton
 */
strictfp public class LevelSequence implements PreloadableSequence {

    static final int nbWorldStepByFrame = 1;
    static final int maxWorldStepByFrame = 5;
    private FrameTimeInfos frameTimeInfos;
    protected World world;
    public long stepTime = 0;
    protected Sequence nextSequence;
    protected Game game;
    protected String questName, levelName;
    private boolean cheatCodeGotoNextLevel = false;
    private boolean cheatCodeGotoNextBonusLevel = false;
    private ITexture minimapTexture;
    private TmxLoader worldLoader;

    public LevelSequence(Game game, String questName, String levelName) {
        this.game = game;
        this.questName = questName;
        this.levelName = levelName;
    }

    @Override
    public void startPreload() {
        try {
            world = null;
            worldLoader = new TmxLoader(game, questName, levelName);
            worldLoader.preloading();
        } catch (Exception ex) {
            throw new RuntimeException("Error during preloading", ex);
        }
    }

    @Override
    public boolean preloadSomeAndCheckIfTerminated() {
        try {
            if (null == world) {
                if (worldLoader.isReadyToLoad()) {
                    world = new World(game, questName, levelName);
                    worldLoader.startLoading(world);
                }
                return false;
            } else {
                long startLoadingTime = game.nanoTime();
                for (;;) {
                    if (worldLoader.hasMoreToLoad()) {
                        worldLoader.loadSome();
                        long currentLoadingTime = game.nanoTime();
                        long elapsedLoadingDuration = currentLoadingTime - startLoadingTime;
                        if (elapsedLoadingDuration > (1000000000L / 24L)) {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load level " + levelName + " of quest " + questName, ex);
        }
    }

    @Override
    public void finishPreload() {
        worldLoader.finishLoading();
        worldLoader = null;
        String minimapPath = game.getData().getLevelFilePath(questName, levelName, "minimap.png");
        if (game.getData().fileExists(minimapPath)) {
            minimapTexture = game.getView().getTextureCache().getTexture(minimapPath);
        } else {
            minimapTexture = null;
        }
    }

    @Override
    public void start() {
        cheatCodeGotoNextLevel = false;
        cheatCodeGotoNextBonusLevel = false;
        frameTimeInfos = game.getFrameTimeInfos();
    }

    @Override
    public void stop() {
        world = null;
    }

    @Override
    public void update() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
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
                if (nextSequence instanceof PreloadableSequence) {
                    throw new NormalTransitionException(new PreloaderFadeSequence(game,
                            (PreloadableSequence) nextSequence, 0, 0, 0, 1000000000L));
                } else {
                    throw new NormalTransitionException(new FadeSequence(game,
                            new Sequence.NormalTransitionException(nextSequence), 0, 0, 0, 1000000000L));
                }

            }
            if (cheatCodeGotoNextBonusLevel) {
                game.goToNextBonusLevel(questName);
            }
        } catch (GameOverException ex) {
            throw new NormalTransitionException(new GameOverSequence(game, this));
        }

    }

    @Override
    public void processInputs() {
        if (world.getHero().isDead()) {
            return;
        }
        processRotateInputs();
        processMovingInput();
        processCheatInput();

    }

    protected void processCheatInput() {
        if (game.getInput().getCheatActivateAll().isActivated()) {
            world.cheatActivateAll();
        }
        if (game.getInput().getCheatGotoNextLevel().isActivated()) {
            cheatCodeGotoNextLevel = true;
        }
        if (game.getInput().getCheatGotoNextBonusLevel().isActivated()) {
            cheatCodeGotoNextBonusLevel = true;
        }
        if (game.getInput().getCheatGetWorldMap().isActivated()) {
            world.getHero().setHasMap(true);
        }
        if (game.getInput().getCheatGetCompass().isActivated()) {
            world.getHero().setHasCompass(true);
        }
    }

    protected void processMovingInput() {
        final float stepRate = 1.0f;
        boolean heroIsMoving = false;
        if (game.getInput().getLeft().isPressed()) {
            world.getHero().moveLeft(stepRate);
            heroIsMoving = true;
        }
        if (game.getInput().getRight().isPressed()) {
            world.getHero().moveRight(stepRate);
            heroIsMoving = true;
        }
        if (game.getInput().getJump().isPressed()) {
            world.getHero().jump(stepRate);
            heroIsMoving = true;
        }
        if (!heroIsMoving) {
            world.getHero().dontMove();
        }
        world.getHero().setTryingToActivateThings(game.getInput().getActivate().isPressed());
    }

    protected void processRotateInputs() {
        final float stepRate = 1.0f;
        if (game.getInput().getRotateClockwise().isPressed()) {
            world.progressiveRotateGravity(0.05f * stepRate);
        }
        if (game.getInput().getRotateCounterClockwise().isPressed()) {
            world.progressiveRotateGravity(-0.05f * stepRate);
        }
        if (game.getInput().getRotate90Clockwise().isPressed()) {
            world.rotateGravity((float) (Math.PI / 4.0));
        }
        if (game.getInput().getRotate90CounterClockwise().isPressed()) {
            world.rotateGravity((float) (-Math.PI / 4.0));
        }
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void draw() {
        world.draw();
        game.getView().drawFPS(game.getFrameTimeInfos().fps);
        drawIndicators();
        drawMinimap();
    }

    private void drawMinimap() {
        if (null != minimapTexture) {
            game.getView().drawMinimap(world, minimapTexture);
        }
    }

    protected void drawIndicators() {
        StringBuilder b = new StringBuilder();
        final Hero hero = world.getHero();
        b.append(hero.getNbApple());
        b.append("$ Score: ");
        b.append(world.getLevelScore().computeScore());
        final OneShotTimedAction deadClock = hero.getDeadClock();
        if (null != deadClock) {
            long seconds = deadClock.getRemainingTime() / 1000000000L;
            b.append(' ');
            b.append(seconds / 60);
            b.append(":");
            final long remainingSeconds = seconds % 60;
            if (remainingSeconds < 10) {
                b.append('0');
            }
            b.append(remainingSeconds);
        }

        game.getView().drawLevelIndicators(b.toString());
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
