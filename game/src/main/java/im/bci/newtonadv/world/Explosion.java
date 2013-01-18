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

import im.bci.newtonadv.game.AbstractEntity;
import im.bci.newtonadv.game.FrameTimeInfos;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;

/**
 *
 * @author devnewton
 */
public strictfp class Explosion extends AbstractEntity {
    
    float size = World.distanceUnit * 2.0f;
    private World world;
    private Animation.Play play;
    private Vector2f position;

    Explosion(World world, ROVector2f position, AnimationCollection explosionAnimation) {
        this.world = world;
        this.position = new Vector2f(position);
        this.play = explosionAnimation.getFirst().start(Animation.PlayMode.ONCE);
    }

    @Override
    public void draw() {
        world.getView().drawExplosion(this,play.getCurrentFrame(),world);
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
    	play.update(frameTimeInfos.elapsedTime / 1000000);
    }

    @Override
    public boolean isDead() {
        return play.isStopped();
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }
}
