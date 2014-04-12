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

import im.bci.jnuit.playn.PlaynNuitRenderer;
import im.bci.newtonadv.game.RestartGameException;

import playn.core.Game;
import playn.core.ImmediateLayer;
import playn.core.PlayN;
import playn.core.Surface;

public class PlaynNewtonAdventureGame extends Game.Default {

    im.bci.newtonadv.Game game;
    private PlaynPlatformSpecific platform;
    private VirtualPad virtualPad;

    public PlaynNewtonAdventureGame() {
        super(1000 / 60);
    }

    public PlaynNewtonAdventureGame useVirtualPad(final boolean useVirtualPad) {
        PlayN.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (useVirtualPad) {
                    virtualPad = new VirtualPad(platform);
                } else {
                    virtualPad = null;
                }
            }
        });
        return this;
    }

    @Override
    public void init() {
        try {
            platform = new PlaynPlatformSpecific();

            ImmediateLayer immediateLayer = PlayN.graphics().createImmediateLayer(new ImmediateLayer.Renderer() {

                @Override
                public void render(Surface surface) {
                    try {
                        if (platform.getAssets().isDone()) {
                            if (null == game) {
                                if (((PlaynGameData) platform.getGameData()).isReady()) {
                                    game = new im.bci.newtonadv.Game(platform);
                                    game.start();
                                }
                            } else if (game.isRunning()) {
                                try {
                                    if (null != virtualPad) {
                                        virtualPad.update(game);
                                    }
                                    ((PlaynNuitRenderer)platform.getNuitToolkit().getRenderer()).setSurface(surface);
                                    ((PlaynGameView) game.getView()).setCurrentSurface(surface);
                                    game.tick();
                                } catch (RestartGameException e) {
                                    game = new im.bci.newtonadv.Game(platform);
                                    game.start();
                                    game.tick();
                                    platform.getConfig().saveConfig();//only save config if everything seems ok
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

    @Override
    public void update(int delta) {
    }

    @Override
    public void paint(float alpha) {
        // the background automatically paints itself, so no need to do anything here!
    }

    public void onBackPressed() {
        ((PlaynGameInput) platform.getGameInput()).onBackPressed();
    }

}
