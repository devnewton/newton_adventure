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
package im.bci.newtonadv.anim;

import im.bci.newtonadv.platform.interfaces.ITexture;

import java.util.ArrayList;

/**
 *
 * @author devnewton
 */
public class Animation implements IAnimation {

    @Override
    public AnimationFrame getFrame(int i) {
        return frames.get(i);
    }

    @Override
    public int getFrameCount() {
        return frames.size();
    }

    @Override
    public long getTotalDuration() {
        return totalDuration;
    }

    private final ArrayList<AnimationFrame> frames = new ArrayList<>();
    private long totalDuration;// milliseconds
    private final String name;

    public Animation(String name) {
        this.name = name;
    }

    public AnimationFrame addFrame(ITexture image, long duration) {
        return addFrame(image, duration, 0f, 0f, 1f, 1f);
    }

    public AnimationFrame addFrame(ITexture image, long duration, float u1, float v1, float u2, float v2) {
        totalDuration += duration;
        final AnimationFrame frame = new AnimationFrame(image, totalDuration, u1, v1, u2, v2);
        frames.add(frame);
        return frame;
    }

    @Override
    public Play start() {
        return start(PlayMode.LOOP);
    }

    @Override
    public Play start(PlayMode mode) {
        if (!frames.isEmpty()) {
            Play play = new Play(this);
            play.start(mode);
            return play;
        } else {
            return null;
        }
    }

    /**
     * Call play.stop
     *
     * @param play
     */
    @Override
    public void stop(Play play) {
        if (null != play) {
            play.stop();
        }
    }

    public String getName() {
        return name;
    }
}
