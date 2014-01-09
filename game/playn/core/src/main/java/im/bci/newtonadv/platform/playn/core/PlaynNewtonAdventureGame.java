/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.playn.core;

import im.bci.newtonadv.game.BonusSequence;
import im.bci.newtonadv.game.LevelSequence;
import im.bci.newtonadv.game.RestartGameException;

import playn.core.Game;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.ImmediateLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Surface;

public class PlaynNewtonAdventureGame extends Game.Default {

    im.bci.newtonadv.Game game;
    private PlaynPlatformSpecific platform;
    private boolean useVirtualPad = true;
    private ImageLayer virtualPadUp;
    private Layer virtualPadRight;
    private ImageLayer virtualPadLeft;
    private ImageLayer virtualPadRotateClockwise;
    private ImageLayer virtualPadRotateCounterClockwise;

    public PlaynNewtonAdventureGame() {
        super(1000 / 60);
    }

    @Override
    public void init() {
        try {
            platform = new PlaynPlatformSpecific();
            platform.getAssets().getImage("images/virtualpad/rotate_counter_clockwise.png");
            platform.getAssets().getImage("images/virtualpad/rotate_clockwise.png");
            platform.getAssets().getImage("images/virtualpad/left.png");
            platform.getAssets().getImage("images/virtualpad/right.png");
            platform.getAssets().getImage("images/virtualpad/up.png");

            ImmediateLayer immediateLayer = PlayN.graphics().createImmediateLayer(new ImmediateLayer.Renderer() {

                @Override
                public void render(Surface surface) {
                    try {
                        if (platform.getAssets().isDone()) {
                            if (null == game) {
                                if (((PlaynGameData) platform.getGameData()).isReady()) {
                                    game = new im.bci.newtonadv.Game(platform);
                                    game.start();
                                    if (useVirtualPad) {
                                        createVirtualPadLayer();
                                    }
                                }
                            } else if (game.isRunning()) {
                                try {
                                    if (useVirtualPad) {
                                        final boolean isLevel = game.getCurrentSequence() instanceof LevelSequence;
                                        virtualPadUp.setVisible(isLevel);
                                        virtualPadLeft.setVisible(isLevel);
                                        virtualPadRight.setVisible(isLevel);
                                        final boolean isBonusLevel = game.getCurrentSequence() instanceof BonusSequence;
                                        virtualPadRotateClockwise.setVisible(isLevel && !isBonusLevel);
                                        virtualPadRotateCounterClockwise.setVisible(isLevel && !isBonusLevel);
                                    }
                                    ((PlaynGameView) game.getView()).setCurrentSurface(surface);
                                    game.tick();
                                } catch (RestartGameException e) {
                                    game = new im.bci.newtonadv.Game(platform);
                                    game.start();
                                    game.tick();
                                    platform.saveConfig();//only save config if everything seems ok
                                }
                            }
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("Error during update", ex);
                    }
                }
            });
            PlayN.graphics().rootLayer().add(immediateLayer);
        } catch (Exception ex) {
            throw new RuntimeException("Error during init", ex);
        }
    }

    private void createVirtualPadLayer() {
        final Image upImage = platform.getAssets().getImage("images/virtualpad/up.png");
        virtualPadUp = PlayN.graphics().createImageLayer(upImage);
        virtualPadUp.setAlpha(0.4f);
        virtualPadUp.setTranslation(0.0f, PlayN.graphics().height() * 1.5f / 4.0f);
        virtualPadUp.setScale(PlayN.graphics().width() / 4.0f / upImage.width(), PlayN.graphics().height()/ 4.0f / upImage.height());
        PlayN.graphics().rootLayer().add(virtualPadUp);
        
        final Image rotateCounterClockwiseImage = platform.getAssets().getImage("images/virtualpad/rotate_counter_clockwise.png");
        virtualPadRotateCounterClockwise = PlayN.graphics().createImageLayer(rotateCounterClockwiseImage);
        virtualPadRotateCounterClockwise.setAlpha(0.4f);
        virtualPadRotateCounterClockwise.setTranslation(0.0f, 0.0f);
        virtualPadRotateCounterClockwise.setScale(PlayN.graphics().width() / 4.0f / rotateCounterClockwiseImage.width(), PlayN.graphics().height()/ 4.0f / rotateCounterClockwiseImage.height());
        PlayN.graphics().rootLayer().add(virtualPadRotateCounterClockwise);

        final Image leftImage = platform.getAssets().getImage("images/virtualpad/left.png");
        virtualPadLeft = PlayN.graphics().createImageLayer(leftImage);
        virtualPadLeft.setAlpha(0.4f);
        virtualPadLeft.setTranslation(0.0f, PlayN.graphics().height() * 3.0f / 4.0f);
        virtualPadLeft.setScale(PlayN.graphics().width() / 4.0f / leftImage.width(), PlayN.graphics().height()/ 4.0f / leftImage.height());
        PlayN.graphics().rootLayer().add(virtualPadLeft);
        
        final Image rightImage = platform.getAssets().getImage("images/virtualpad/right.png");
        virtualPadRight = PlayN.graphics().createImageLayer(rightImage);
        virtualPadRight.setAlpha(0.4f);
        virtualPadRight.setTranslation(PlayN.graphics().width()* 3.0f / 4.0f, PlayN.graphics().height() * 3.0f / 4.0f);
        virtualPadRight.setScale(PlayN.graphics().width() / 4.0f / rightImage.width(), PlayN.graphics().height()/ 4.0f / rightImage.height());
        PlayN.graphics().rootLayer().add(virtualPadRight);

        final Image rotateClockwiseImage = platform.getAssets().getImage("images/virtualpad/rotate_clockwise.png");
        virtualPadRotateClockwise = PlayN.graphics().createImageLayer(rotateClockwiseImage);
        virtualPadRotateClockwise.setAlpha(0.4f);
        virtualPadRotateClockwise.setTranslation(PlayN.graphics().width()* 3.0f / 4.0f, 0.0f);
        virtualPadRotateClockwise.setScale(PlayN.graphics().width() / 4.0f / rotateClockwiseImage.width(), PlayN.graphics().height()/ 4.0f / rotateClockwiseImage.height());
        PlayN.graphics().rootLayer().add(virtualPadRotateClockwise);
    }

    @Override
    public void update(int delta) {
    }

    @Override
    public void paint(float alpha) {
        // the background automatically paints itself, so no need to do anything here!
    }

}
