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

import im.bci.jnuit.animation.IAnimationCollection;
import im.bci.jnuit.animation.IPlay;
import im.bci.jnuit.animation.PlayMode;
import net.phys2d.math.Matrix2f;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.Shape;
import im.bci.newtonadv.util.Vector;

/**
 * 
 * @author devnewton
 */
public strictfp class Bat extends AbstractDrawableBody implements Updatable {

	private IPlay play;
	private static final float weight = 0.1f;
	private static final float speed = 4.0f;
	private static final long maxMoveStraightDuration = 1000000000L;
	private long nextChangeDirectionTime = 0;
	private boolean isDead = false;
	private static final long dyingDuration = 1000000000L;
	private long beginOfDyingDuration = -1;
	private float scale = 1;
	private World world;
	private Vector2f directionVelocity;

	public Bat(World world, Shape shape, IAnimationCollection animation) {
		super(shape,
				weight);
		this.world = world;
		setRotatable(false);
		setGravityEffected(false);
		play = animation.getFirst().start(PlayMode.LOOP);
	}

	public boolean isDead() {
		return isDead;
	}

	@Override
	public strictfp void collided(Body other) {
		if (!isDead) {
			if (other instanceof Hero) {
				CollisionEvent[] events = world.getContacts(this);
				Hero hero = (Hero) other;
				for (int i = 0; i < events.length; i++) {
					CollisionEvent event = events[i];
					if (event.getBodyB() == hero) {
						Vector2f normal = new Vector2f(event.getNormal());
						float angle = Vector.angle(normal,
								world.getGravityVector());
						if (angle < Math.PI / 4.0f) {
							hero.hurtByBat();
						} else {
							hero.killedBat();
							isDead = true;
						}
					} else if (event.getBodyA() == hero) {
						Vector2f normal = new Vector2f(event.getNormal());
						float angle = Vector.angle(normal,
								world.getGravityVector());
						if (angle > Math.PI / 4.0f) {
							hero.hurtByBat();
						} else {
							hero.killedBat();
							isDead = true;
						}
					}
				}
			}
			if (isDead) {
				setMoveable(false);
			}
		}
	}

	@Override
	public void draw() {
		world.getView().drawBat(this, scale, play.getCurrentFrame(),
				world);
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		if (isDead) {
			if (beginOfDyingDuration < 0) {
				beginOfDyingDuration = frameTimeInfos.currentTime;
			} else {
				scale = 1.0f
						- (frameTimeInfos.currentTime - beginOfDyingDuration)
						/ (float) dyingDuration;
				if (scale <= 0) {
					world.remove(this);
				}
			}
			return;
		}

		if (frameTimeInfos.currentTime > nextChangeDirectionTime) {
			nextChangeDirectionTime = frameTimeInfos.currentTime
					+ (long) (Math.random() * maxMoveStraightDuration);

			Matrix2f rot = new Matrix2f(
					(float) (Math.random() * 2.0f * Math.PI));
			directionVelocity = net.phys2d.math.MathUtil.mul(rot, new Vector2f(
					world.getGravityForce() * speed, 0));
		} else {
			adjustBiasedVelocity(directionVelocity);
		}
		play.update(frameTimeInfos.elapsedTime / 1000000);
	}
}
