/*
 * Copyright (c) 2014 devnewton <devnewton@bci.im>
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

import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.BonusSequence;
import im.bci.newtonadv.game.LevelSequence;
import java.util.logging.Level;
import java.util.logging.Logger;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.util.Callback;

/**
 *
 * @author devnewton
 */
public class VirtualPad {

    private static final Logger LOGGER = Logger.getLogger(VirtualPad.class.getName());
    private final PlaynPlatformSpecific platform;
    private ImageLayer virtualPadUp;
    private Layer virtualPadRight;
    private ImageLayer virtualPadLeft;
    private ImageLayer virtualPadRotateClockwise;
    private ImageLayer virtualPadRotateCounterClockwise;

    public VirtualPad(PlaynPlatformSpecific platform) {
        this.platform = platform;
        createVirtualPadLayer();
    }

    private void createVirtualPadLayer() {
        final PlaynGameInput input = (PlaynGameInput) platform.getGameInput();
        final Image upImage = platform.getAssets().getImage("images/virtualpad/up.png");
        upImage.addCallback(new Callback<Image>() {

            @Override
            public void onSuccess(Image result) {
                virtualPadUp = PlayN.graphics().createImageLayer(upImage);
                virtualPadUp.setVisible(false);
                virtualPadUp.setAlpha(0.4f);
                virtualPadUp.setTranslation(0.0f, PlayN.graphics().height() * 1.5f / 4.0f);
                virtualPadUp.setScale(PlayN.graphics().width() / 4.0f / upImage.width(), PlayN.graphics().height() / 4.0f / upImage.height());
                virtualPadUp.addListener(new Pointer.Adapter() {

                    @Override
                    public void onPointerStart(Pointer.Event event) {
                        input.virtualPadUpDown = true;
                    }

                    @Override
                    public void onPointerEnd(Pointer.Event event) {
                        input.virtualPadUpDown = false;
                    }
                });
                PlayN.graphics().rootLayer().add(virtualPadUp);
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load images/virtualpad/up.png", cause);
            }

        });

        final Image rotateCounterClockwiseImage = platform.getAssets().getImage("images/virtualpad/rotate_counter_clockwise.png");
        rotateCounterClockwiseImage.addCallback(new Callback<Image>() {

            @Override
            public void onSuccess(Image result) {
                virtualPadRotateCounterClockwise = PlayN.graphics().createImageLayer(rotateCounterClockwiseImage);
                virtualPadRotateCounterClockwise.setVisible(false);
                virtualPadRotateCounterClockwise.setAlpha(0.4f);
                virtualPadRotateCounterClockwise.setTranslation(0.0f, 0.0f);
                virtualPadRotateCounterClockwise.setScale(PlayN.graphics().width() / 4.0f / rotateCounterClockwiseImage.width(), PlayN.graphics().height() / 4.0f / rotateCounterClockwiseImage.height());
                virtualPadRotateCounterClockwise.addListener(new Pointer.Adapter() {

                    @Override
                    public void onPointerStart(Pointer.Event event) {
                        input.virtualPadRotateCounterClockwiseDown = true;
                    }

                    @Override
                    public void onPointerEnd(Pointer.Event event) {
                        input.virtualPadRotateCounterClockwiseDown = false;
                    }
                });
                PlayN.graphics().rootLayer().add(virtualPadRotateCounterClockwise);
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load images/virtualpad/rotate_counter_clockwise.png", cause);
            }

        });

        final Image leftImage = platform.getAssets().getImage("images/virtualpad/left.png");
        leftImage.addCallback(new Callback<Image>() {

            @Override
            public void onSuccess(Image result) {
                virtualPadLeft = PlayN.graphics().createImageLayer(leftImage);
                virtualPadLeft.setVisible(false);
                virtualPadLeft.setAlpha(0.4f);
                virtualPadLeft.setTranslation(0.0f, PlayN.graphics().height() * 3.0f / 4.0f);
                virtualPadLeft.setScale(PlayN.graphics().width() / 4.0f / leftImage.width(), PlayN.graphics().height() / 4.0f / leftImage.height());
                virtualPadLeft.addListener(new Pointer.Adapter() {

                    @Override
                    public void onPointerStart(Pointer.Event event) {
                        input.virtualPadLeftDown = true;
                    }

                    @Override
                    public void onPointerEnd(Pointer.Event event) {
                        input.virtualPadLeftDown = false;
                    }
                });
                PlayN.graphics().rootLayer().add(virtualPadLeft);
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load images/virtualpad/left.png", cause);
            }
        });

        final Image rightImage = platform.getAssets().getImage("images/virtualpad/right.png");
        rightImage.addCallback(new Callback<Image>() {

            @Override
            public void onSuccess(Image result) {
                virtualPadRight = PlayN.graphics().createImageLayer(rightImage);
                virtualPadRight.setVisible(false);
                virtualPadRight.setAlpha(0.4f);
                virtualPadRight.setTranslation(PlayN.graphics().width() * 3.0f / 4.0f, PlayN.graphics().height() * 3.0f / 4.0f);
                virtualPadRight.setScale(PlayN.graphics().width() / 4.0f / rightImage.width(), PlayN.graphics().height() / 4.0f / rightImage.height());
                virtualPadRight.addListener(new Pointer.Adapter() {

                    @Override
                    public void onPointerStart(Pointer.Event event) {
                        input.virtualPadRightDown = true;
                    }

                    @Override
                    public void onPointerEnd(Pointer.Event event) {
                        input.virtualPadRightDown = false;
                    }
                });
                PlayN.graphics().rootLayer().add(virtualPadRight);
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load images/virtualpad/right.png", cause);
            }
        });

        final Image rotateClockwiseImage = platform.getAssets().getImage("images/virtualpad/rotate_clockwise.png");
        rotateClockwiseImage.addCallback(new Callback<Image>() {

            @Override
            public void onSuccess(Image result) {
                virtualPadRotateClockwise = PlayN.graphics().createImageLayer(rotateClockwiseImage);
                virtualPadRotateClockwise.setVisible(false);
                virtualPadRotateClockwise.setAlpha(0.4f);
                virtualPadRotateClockwise.setTranslation(PlayN.graphics().width() * 3.0f / 4.0f, 0.0f);
                virtualPadRotateClockwise.setScale(PlayN.graphics().width() / 4.0f / rotateClockwiseImage.width(), PlayN.graphics().height() / 4.0f / rotateClockwiseImage.height());
                virtualPadRotateClockwise.addListener(new Pointer.Adapter() {

                    @Override
                    public void onPointerStart(Pointer.Event event) {
                        input.virtualPadRotateClockwiseDown = true;
                    }

                    @Override
                    public void onPointerEnd(Pointer.Event event) {
                        input.virtualPadRotateClockwiseDown = false;
                    }
                });
                PlayN.graphics().rootLayer().add(virtualPadRotateClockwise);
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load images/virtualpad/rotate_clockwise.png", cause);
            }
        });
    }

    void update(Game game) {
        final boolean isLevel = game.getCurrentSequence() instanceof LevelSequence;
        if (null != virtualPadUp) {
            virtualPadUp.setVisible(isLevel);
        }
        if (null != virtualPadLeft) {
            virtualPadLeft.setVisible(isLevel);
        }
        if (null != virtualPadRight) {
            virtualPadRight.setVisible(isLevel);
        }
        final boolean isBonusLevel = game.getCurrentSequence() instanceof BonusSequence;
        if (null != virtualPadRotateClockwise) {
            virtualPadRotateClockwise.setVisible(isLevel && !isBonusLevel);
        }
        if (null != virtualPadRotateCounterClockwise) {
            virtualPadRotateCounterClockwise.setVisible(isLevel && !isBonusLevel);
        }
    }
}
