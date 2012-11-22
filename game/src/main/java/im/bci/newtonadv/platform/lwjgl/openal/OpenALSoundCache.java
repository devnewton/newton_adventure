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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author devnewton
 */
public class OpenALSoundCache implements ISoundCache {
    
    private static final Logger logger = Logger.getLogger(OpenALSoundCache.class.getName());
    private SimpleSoundEngine engine;
    
    OpenALSoundCache(IGameData gameData) {
        try {
            engine = new SimpleSoundEngine(gameData);
            engine.init();
        } catch(Exception e) {
            logger.log(Level.WARNING, "Cannot init openal sound system", e);
        }
    }
    
    @Override
    public void close() {
        try {
            if(null != engine) {
                engine.destroy();
            }
        } catch(Exception e) {
            logger.log(Level.WARNING, "Cannot close openal sound system", e);
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void clearAll() {
        try {
            if(null != engine) {
                engine.unloadAllSounds();
            }
        } catch(Exception e) {
            logger.log(Level.WARNING, "Cannot unload openal sounds", e);
        }
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
                try {
                    if(null != engine) {
                        engine.playSound(name);
                    }
                } catch(Exception e) {
                    logger.log(Level.WARNING, "Cannot play sound" + name, e);
                }
            }

            @Override
            public void stop() {
            }
        };
    }

    @Override
    public void playMusicIfEnabled(String name) {
        try {
            if(null != engine) {
                engine.playMusic(name, true);
            }
        } catch(Exception e) {
            logger.log(Level.WARNING, "Cannot play music" + name, e);
        }
    }

    @Override
    public void stopMusic() {
        try {
            if(null != engine) {
               engine.stopMusic();
            }
        } catch(Exception e) {
            logger.log(Level.WARNING, "Cannot stop music", e);
        }
    }
    
}
