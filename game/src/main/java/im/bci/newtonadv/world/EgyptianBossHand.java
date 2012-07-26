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

import net.phys2d.math.ROVector2f;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Circle;

import im.bci.newtonadv.util.Vector;

/**
 *
 * @author devnewton
 */
public strictfp class EgyptianBossHand extends AbstractDrawableBody implements Updatable {
    private final World world;

    enum Side {

        LEFT,
        RIGHT
    }

    enum State {

        FALLING,
        MOVING_TO_BOSS,
    }
    private static final float weight = 5.0f;
    private EgyptianBoss boss;
    private AnimationCollection texture;
    private Side side;
    private State state = State.MOVING_TO_BOSS;

    EgyptianBossHand(EgyptianBoss boss, Side side, World world) {
        super(new Circle(World.distanceUnit /* * 2.0f, World.distanceUnit * 2.0f*/), weight);
        this.boss = boss;
        this.side = side;
        this.world = world;
        setRotatable(false);
        setGravityEffected(false);
        addExcludedBody(boss);
    }

    public void setTexture(AnimationCollection t) {
        this.texture = t;
    }

    @Override
    public strictfp void collided(Body other) {
        if (other instanceof Hero) {
            ((Hero) other).hurtByEgyptianBoss();
            setGravityEffected(false);
            state = State.MOVING_TO_BOSS;
        } else if (other instanceof Platform) {
            setGravityEffected(false);
            state = State.MOVING_TO_BOSS;
        }
    }

    @Override
    public void draw() {
        world.getView().drawEgyptianBossHand(this, texture.getFirst().getCurrentFrame());
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        if (state == State.MOVING_TO_BOSS) {
            final Vector2f handPosition = boss.getHandPosition(side);
            Vector2f directionVelocity = new Vector2f(handPosition);
            final ROVector2f position = getPosition();
            directionVelocity.sub(position);
            directionVelocity.normalise();
            directionVelocity.scale(boss.world.getGravityForce() * boss.speed * 2.0f);
            adjustBiasedVelocity(directionVelocity);

            boolean canfall = handPosition.distanceSquared(position) < World.distanceUnit * World.distanceUnit;

            if (canfall) {
                Hero hero = boss.world.getHero();
                Vector2f dir = new Vector2f(hero.getPosition());
                dir.sub(getPosition());
                if (Vector.angle(dir, boss.world.getGravityVector()) < Math.PI / 8.0f) {
                    setGravityEffected(true);
                    state = State.FALLING;
                }
            }
        }
    }
}
