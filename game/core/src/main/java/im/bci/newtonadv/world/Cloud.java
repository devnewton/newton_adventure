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
package im.bci.newtonadv.world;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Shape;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.util.NewtonColor;

public strictfp class Cloud extends AnimatedPlatform implements Updatable {

    private boolean touched = false;
    private long disappearEndTime = -1;
    private float alpha = 1.0f;
    private NewtonColor color = NewtonColor.white;
    private static final long disappearDuration = 1000000000L;

    public Cloud(World world, float w, float h) {
        super(world, w, h);
    }

    public Cloud(World world, Shape shape) {
        super(world, shape);
    }

    @Override
    public void collided(Body body) {
        if (body instanceof Hero && checkColor((Hero) body)) {
            touched = true;
        }
    }

    private boolean checkColor(Hero hero) {
        return NewtonColor.white == color || hero.getColor() == color;
    }

    @Override
    public void draw() {
        world.getView().drawCloud(this, alpha);
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
        if (touched) {
            if (disappearEndTime < 0) {
                disappearEndTime = frameTimeInfos.currentTime + disappearDuration;
            } else if (frameTimeInfos.currentTime < disappearEndTime) {
                alpha = (disappearEndTime - frameTimeInfos.currentTime) / (float) disappearDuration;
            } else {
                world.remove(this);
            }
        }
    }

    public void setColor(NewtonColor color) {
        this.color = color;
    }

}
