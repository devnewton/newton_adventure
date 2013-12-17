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
package im.bci.newtonadv.world;

import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.game.time.PingPongTimedAction;
import im.bci.newtonadv.game.time.TimedAction;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Shape;

/**
 *
 * @author devnewton
 */
public strictfp class Clue extends AnimatedPlatform implements CollisionDetectionOnly, Updatable {
    
    private static final float CLUE_VISIBLE_AT_DISTANCE = World.distanceUnit * 10f * World.distanceUnit * 10f;
    private Vector2f anchor;
    private TimedAction move = new PingPongTimedAction(500000000L);

    Clue(World world, float w, float h) {
        super(world, w, h);
    }

    public Clue(World world, Shape shape) {
        super(world, shape);
    }
    
    public void setAnchor(float x, float y) {
        this.anchor = new Vector2f(x, y);
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
        super.update(frameTimeInfos);
        move.update(frameTimeInfos);
        
        setPosition(anchor.x, anchor.y + this.h/4.0f * move.getProgress());
        
    }

    @Override
    public strictfp void draw() {
        if(world.getHero().getPosition().distanceSquared(this.getPosition()) < CLUE_VISIBLE_AT_DISTANCE) {
            super.draw();
        }
    }
    
}
