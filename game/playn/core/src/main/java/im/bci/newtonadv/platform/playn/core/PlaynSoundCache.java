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

import im.bci.newtonadv.platform.interfaces.ISoundCache;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynSoundCache implements ISoundCache {

    @Override
    public void update() {
        //TODO
    }

    @Override
    public void close() {
        //TODO
    }

    @Override
    public boolean isSoundEnabled() {
        return false;
    }

    @Override
    public boolean isMusicEnabled() {
        return false;
    }

    @Override
    public void setSoundEnabled(boolean active) {
        //TODO
    }

    @Override
    public void setMusicEnabled(boolean active) {
        //TODO
    }

    @Override
    public void clearAll() {
        //TODO
    }

    @Override
    public void clearUseless() {
        //TODO
    }

    @Override
    public Playable getSound(String name) {
        return null;
    }

    @Override
    public void playMusicIfEnabled(String name) {
        //TODO
    }

    @Override
    public void stopMusic() {
        //TODO
    }

}
