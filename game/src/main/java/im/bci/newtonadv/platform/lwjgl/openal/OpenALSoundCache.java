/*
 * Copyright (c) 2012 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.lwjgl.openal;

import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author devnewton
 */
public class OpenALSoundCache implements ISoundCache {

    private static final Logger logger = Logger.getLogger(OpenALSoundCache.class.getName());
    private boolean soundEnabled;
    private boolean musicEnabled;
    private final IGameData gameData;
    private SimpleSoundEngine engine;
    private final ExecutorService executor;
    private final Runnable poll = new AbstractOpenALTask() {

        @Override
        public void doRun() {
            engine.poll();
            Thread.yield();
            executor.submit(poll);
        }
    };
    
    private abstract class AbstractOpenALTask implements Runnable {

        @Override
        public final void run() {
            try {
                doRun();
            } catch (Exception ex) {
                logger.log(Level.WARNING, "OpenAL error", ex);
            }
        }
        
        protected abstract void doRun() throws Exception;
    }

    public OpenALSoundCache(IGameData gd, Properties config) {
        this.gameData = gd;
        this.soundEnabled = config.getProperty("sound.enabled").equals("true");
        this.musicEnabled = config.getProperty("music.enabled").equals("true");
        executor = Executors.newSingleThreadExecutor();
        executor.submit(new AbstractOpenALTask() {

            @Override
            public void doRun() {
                engine = new SimpleSoundEngine(gameData);
                engine.init();
                executor.submit(poll);
            }
        });
    }

    @Override
    public void close() {
        executor.submit(new AbstractOpenALTask() {

            @Override
            public void doRun() {
                engine.destroy();
                executor.shutdownNow();
            }
        });
    }

    @Override
    public void update() {
    }

    @Override
    public void clearAll() {
        executor.submit(new AbstractOpenALTask() {

            @Override
            public void doRun() {
                engine.unloadAllSounds();
            }
        });
    }

    @Override
    public void clearUseless() {
        clearAll();
    }

    @Override
    public Playable getSound(final String name) {
        return new Playable() {

            @Override
            public void play() {
                if (soundEnabled) {
                    executor.submit(new AbstractOpenALTask() {

                        @Override
                        public void doRun() {
                            engine.playSound(name);
                        }
                    });
                }
            }

            @Override
            public void stop() {
            }
        };
    }

    @Override
    public void playMusicIfEnabled(final String name) {
        if (musicEnabled) {
            executor.submit(new AbstractOpenALTask() {

                @Override
                public void doRun() {
                    try {
                        engine.playMusic(name, true);
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "Cannot play music " + name, ex);
                    }
                }
            });
        }
    }

    @Override
    public void stopMusic() {
        executor.submit(new AbstractOpenALTask() {

            @Override
            public void doRun() {
                engine.stopMusic();
            }
        });

    }

    @Override
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    @Override
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    @Override
    public void setSoundEnabled(boolean e) {
        this.soundEnabled = e;
    }

    @Override
    public void setMusicEnabled(boolean e) {
        this.musicEnabled = e;
        if (!e) {
            stopMusic();
        }
    }
}
