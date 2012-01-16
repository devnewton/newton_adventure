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
import im.bci.newtonadv.platform.lwjgl.TrueTypeFont;
import im.bci.newtonadv.world.GameOverException;
import im.bci.newtonadv.world.World;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author devnewton
 */
strictfp public class LevelSequence implements Sequence {

    static final int nbWorldStepByFrame = 1;
    static final int maxWorldStepByFrame = 5;
    private FrameTimeInfos frameTimeInfos;
    private World world;
    public long stepTime = 0;
    private TrueTypeFont appleFont;
    private Sequence nextSequence;
    private Game game;
    private String levelPath;
    private boolean cheatCodeGotoNextLevel = false;

    public LevelSequence(Game game, String levelPath) {
        this.game = game;
        this.levelPath = levelPath;
    }

    @Override
    public void start() {
        try {
            cheatCodeGotoNextLevel = false;
            appleFont = new TrueTypeFont();
            world = new World(game);
            frameTimeInfos = game.getFrameTimeInfos();
            world.loadLevel(levelPath);
        } catch (Exception ex) {
            Logger.getLogger(LevelSequence.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void stop() {
        appleFont.destroy();
        appleFont = null;
        world = null;
    }

    @Override
    public void update() throws TransitionException {
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
                game.getScore().setLevelScore(levelPath, world.getLevelScore());
                throw new TransitionException(nextSequence);
            }
        } catch (GameOverException ex) {
            GameOverSequence gameOverSequence = new GameOverSequence(game, this, null);
            throw new TransitionException(gameOverSequence);
        }
    }

    @Override
    public void processInputs() {
        if (world.getHero().isDead()) {
            return;
        }

        float stepRate = 1.0f;//frameTimeInfos.elapsedTime / (1000000000.0f / Game.FPSf);
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

        if (game.getInput().isKeyCheatActivateAllDown()) {
            world.cheatActivateAll();
        }
        if (game.getInput().isKeyCheatGotoNextLevelDown()) {
            cheatCodeGotoNextLevel = true;
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
    }

    private void drawIndicators() {
        game.getView().drawLevelIndicators(world, appleFont);
    }

    public void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }
}
