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

import net.phys2d.math.Matrix2f;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.Play;
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
public strictfp class Mummy extends AbstractDrawableBody implements Updatable {

	private Play play;
	private static final float weight = 1.0f;
	private static final float horizontalSpeed = 4.0f;
	private static final long moveStraightDuration = 4000000000L;
	private long nextChangeDirectionTime = 0;
	private boolean isDead = false;
	private static final long dyingDuration = 1000000000L;
	private long beginOfDyingDuration = -1;
	private float scale = 1;

	public Movement getCurrentMovement() {
		return currentMovement;
	}

	public boolean isDead() {
		return isDead;
	}

	private void setCurrentMovement(Movement currentMovement) {
		if (this.currentMovement != currentMovement) {
			this.previousMovement = this.currentMovement;
			this.currentMovement = currentMovement;
			if (this.currentMovement == Movement.NOT_GOING_ANYWHERE) {
				play.stop();
			} else {
				play.start();
			}
		}
	}

	public enum Movement {

		GOING_LEFT, GOING_RIGHT, NOT_GOING_ANYWHERE
	}

	private Movement currentMovement = Movement.NOT_GOING_ANYWHERE;
	Movement previousMovement = Movement.NOT_GOING_ANYWHERE;
	private World world;

	public Mummy(World world, Shape shape, AnimationCollection animation) {
		super(shape, weight);
		this.world = world;
		this.play = animation.getFirst().start();
		setRotatable(false);
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
							hero.hurtByMummy();
						} else {
							isDead = true;
							hero.killedMummy();
						}
					} else if (event.getBodyA() == hero) {
						Vector2f normal = new Vector2f(event.getNormal());
						float angle = Vector.angle(normal,
								world.getGravityVector());
						if (angle > Math.PI / 4.0f) {
							hero.hurtByMummy();
						} else {
							isDead = true;
							hero.killedMummy();
						}
					}
				}
			}
			if (isDead) {
				setMoveable(false);
			}
		}
	}

	public boolean isLookingLeft() {
		return isMovingLeft()
				|| (isNotGoingAnywhere() && previousMovement == Movement.GOING_LEFT);
	}

	public boolean isMovingLeft() {
		return getCurrentMovement() == Movement.GOING_LEFT;
	}

	public boolean isNotGoingAnywhere() {
		return getCurrentMovement() == Movement.NOT_GOING_ANYWHERE;
	}

	public void step() {
		if (isResting()) {
			setCurrentMovement(Movement.NOT_GOING_ANYWHERE);
		}
	}

	@Override
	public void draw() {
		world.getView().drawMummy(this, world,
				play.getCurrentFrame(), scale);
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
					+ moveStraightDuration;
			if (isMovingLeft()) {
				moveRight(1.0f);
			} else {
				moveLeft(1.0f);
			}
		} else {
			if (isMovingLeft()) {
				moveLeft(1.0f);
			} else {
				moveRight(1.0f);
			}
		}
		if (!isNotGoingAnywhere()) {
			play.update(frameTimeInfos.elapsedTime / 1000000);
		}
	}

	private void moveLeft(float step) {
		Matrix2f rot = new Matrix2f(world.getGravityAngle() /*
															 * + (float) Math.PI
															 * / 4.0f
															 */);
		Vector2f velocity = net.phys2d.math.MathUtil.mul(rot, new Vector2f(step
				* -world.getGravityForce() * horizontalSpeed, 0.0f));
		adjustBiasedVelocity(velocity);
		setCurrentMovement(Mummy.Movement.GOING_LEFT);
	}

	private void moveRight(float step) {
		Matrix2f rot = new Matrix2f(world.getGravityAngle() /*
															 * + (float) Math.PI
															 * / 4.0f
															 */);
		Vector2f velocity = net.phys2d.math.MathUtil.mul(rot, new Vector2f(step
				* world.getGravityForce() * horizontalSpeed, 0.0f));
		adjustBiasedVelocity(velocity);
		setCurrentMovement(Mummy.Movement.GOING_RIGHT);
	}
}
