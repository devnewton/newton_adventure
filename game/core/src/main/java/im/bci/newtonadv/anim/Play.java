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

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class Play {

    private int currentFrameIndex;
    private long currentTime;
    private PlayMode mode = PlayMode.LOOP;
    private State state = State.STOPPED;
    private final IAnimation animation;

    public Play(final IAnimation animation) {
        this.animation = animation;
    }

    public String getName() {
        return animation.getName();
    }

    public AnimationFrame getCurrentFrame() {
        return animation.getFrame(currentFrameIndex);
    }

    public void start() {
        start(PlayMode.LOOP);
    }

    public void start(PlayMode mode) {
        this.state = State.STARTED;
        this.mode = mode;
        this.currentTime = 0;
        this.currentFrameIndex = 0;
    }

    public void stop() {
        state = State.STOPPED;
        currentTime = 0;
        currentFrameIndex = 0;
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    public void update(long elapsedTime) {
        if (state == State.STOPPED) {
            return;
        }
        if (animation.getFrameCount() > 0) {
            this.currentTime += elapsedTime;
            final long totalDuration = animation.getTotalDuration();
            if (currentTime >= totalDuration) {
                switch (mode) {
                    case ONCE:
                        currentFrameIndex = animation.getFrameCount() - 1;
                        state = State.STOPPED;
                        return;
                    case LOOP:
                        if (totalDuration > 0) {
                            currentTime %= totalDuration;
                        } else {
                            currentTime = 0;
                        }
                        currentFrameIndex = 0;
                        break;
                }
            }
            while (currentTime > animation.getFrame(currentFrameIndex).getEndTime()) {
                ++this.currentFrameIndex;
            }
        }
    }

}
