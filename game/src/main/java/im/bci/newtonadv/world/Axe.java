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

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.Box;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;

/**
 *
 * @author devnewton
 */
public strictfp class Axe extends AbstractDrawableBody implements Updatable {

    private static final float weight = 10.0f;
    static final float width = World.distanceUnit;
    static final float height = 3.9f * World.distanceUnit;
    private AnimationCollection texture;
    private World world;

    Axe(World world) {
        super(new Box(width, height), weight);
        this.world = world;
        //setDamping(0.02f);
        setGravityEffected(false);
    }

    public void setTexture(AnimationCollection texture) {
        this.texture = texture;
        texture.getFirst().start();
    }

    @Override
    public strictfp void collided(Body other) {
        if (other instanceof Hero) {
            Hero hero = (Hero) other;
            if (hero.isInvincible()) {
                return;
            }
            CollisionEvent[] events = world.getContacts(this);

            for (CollisionEvent event : events) {
                if (event.getBodyB() == hero) {
                    Vector2f normal = new Vector2f(event.getNormal());
                    hero.hurtByPike(normal/*.negate()*/);
                    return;
                } else if (event.getBodyA() == hero) {
                    Vector2f normal = new Vector2f(event.getNormal());
                    hero.hurtByPike(normal.negate());
                    return;
                }
            }
        }
    }

    @Override
    public void draw() {
        world.getView().drawAxe(this, texture.getFirst().getCurrentFrame());
    }

    @Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        float v = getAngularVelocity();
        if( v < 1.0f )
            adjustAngularVelocity(1.0f - v);
    }
}
