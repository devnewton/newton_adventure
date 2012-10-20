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

import java.util.EnumSet;

import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.Animation.Play;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.util.NewtonColor;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Shape;

/**
 * 
 * @author devnewton
 */
public strictfp class Hero extends AbstractDrawableBody implements Updatable {

	private ISoundCache.Playable jumpSound;
	private AnimationCollection animations;
	private int nbApple = 10;
	private static final float jumpForce = 180.0f;
	private static final float weight = 1.0f;
	private static final float horizontalSpeed = 4.0f;
	private static final int nbStepToWaitBetweenJump = 1;
	private int nbStepSinceLastJump;
	private static final long invincibleAfterHurtDuration = 3000000000L;
	private long endOfInvincibilityDuration = -1;
	private boolean isHurt = false;
	private boolean isHurtBlinkState = false;
	private boolean isDead = false;
	private static final long dyingDuration = 2000000000L;
	private long beginOfDyingDuration = -1;
	private float scale = 1;
	private LevelScore levelScore = new LevelScore();
	private Play play;

	public void setJumpSound(ISoundCache.Playable jumpSound) {
		this.jumpSound = jumpSound;
	}

	public Movement getCurrentMovement() {
		return currentMovement;
	}

	public int getNbApple() {
		return nbApple;
	}

	public boolean isDead() {
		return isDead;
	}

	boolean isInvincible() {
		return isHurt;
	}

	private void hurt(int nbAppleLose) {
		if (nbApple == 0) {
			this.isDead = true;
		}

		if (nbAppleLose > nbApple) {
			nbAppleLose = nbApple;
		}

		this.nbApple -= nbAppleLose;
		if(nbAppleLose>0) {
			levelScore.addLosedApple(nbAppleLose);
		}
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this.getPosition(), nbAppleLose * levelScore.getLosedAppleValue()));
		final float losedAppleSize = getShape().getBounds().getWidth() / 4.0f;
		for (int i = 0; i < nbAppleLose; ++i) {
			world.addTopLevelEntities(new LosedApple(world, this.getPosition(), losedAppleSize));
		}

		this.isHurt = true;
		this.endOfInvincibilityDuration = -1;
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

	void killedMummy() {
		levelScore.addKilledMummy();
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this.getPosition(), levelScore.getKilledMummyValue()));
	}

	void killedBat() {
		levelScore.addKilledBat();
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this.getPosition(), levelScore.getKilledBatValue()));
	}

	void killedEgyptianBoss() {
		levelScore.addKilledEgyptianBoss();
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this.getPosition(), levelScore.getKilledEgyptianBossValue()));
	}

	LevelScore getLevelScore() {
		return levelScore;
	}

	public enum Movement {

		GOING_LEFT, GOING_RIGHT, NOT_GOING_ANYWHERE
	}

	boolean isOnPlatform = false;
	private Movement currentMovement = Movement.NOT_GOING_ANYWHERE;
	Movement previousMovement = Movement.NOT_GOING_ANYWHERE;
	private World world;
	private boolean hasMap;
	private boolean hasCompass;
	private NewtonColor color = NewtonColor.white;

	public Hero(World world) {
		this(world, new Circle(World.distanceUnit));
	}

	public Hero(World world, Shape shape) {
		super(shape, weight);
		this.world = world;
		setRotatable(false);
		levelScore.addApple(this.nbApple);
	}

	@Override
	public strictfp void collided(Body body) {
		if (body instanceof Apple) {
			++nbApple;
			levelScore.addApple(1);
			world.addTopLevelEntities(new ScoreVisualIndicator(world, body.getPosition(), levelScore.getAppleValue()));
		} else if (body instanceof Coin) {
			levelScore.addCoin(1);
			world.addTopLevelEntities(new ScoreVisualIndicator(world, body.getPosition(), levelScore.getCoinValue()));
		} else if (body instanceof WorldMap) {
			hasMap = true;
		} else if (body instanceof Compass) {
			hasCompass = true;
		}
	}

	public boolean hasMap() {
		return hasMap;
	}

	public boolean hasCompass() {
		return hasCompass;
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
		isOnPlatform = false;
		if (isResting()) {
			setCurrentMovement(Movement.NOT_GOING_ANYWHERE);
		}
	}

	public Animation.Play getAnimation() {
		return play;
	}

	public void setAnimation(AnimationCollection heroAnimation) {
		this.animations = heroAnimation;
		play = animations.getFirst().start();
		play.stop();
	}

	@Override
	public void draw() {
		if (isHurt) {
			isHurtBlinkState = !isHurtBlinkState;
			if (isHurtBlinkState) {
				return;
			}
		}
		world.getView().drawHero(this, getAnimation().getCurrentFrame(),
				world, scale);
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		getAnimation().update(frameTimeInfos.elapsedTime / 1000000);
		if (isHurt) {
			if (endOfInvincibilityDuration < 0) {
				endOfInvincibilityDuration = frameTimeInfos.currentTime
						+ invincibleAfterHurtDuration;
			} else if (frameTimeInfos.currentTime > endOfInvincibilityDuration) {
				isHurt = false;
			}
		}
		if (isDead) {
			if (beginOfDyingDuration < 0) {
				beginOfDyingDuration = frameTimeInfos.currentTime;
			} else {
				scale = 1.0f
						- (frameTimeInfos.currentTime - beginOfDyingDuration)
						/ (float) dyingDuration;
				if (scale <= 0) {
					throw new GameOverException("Newton is dead");
				}
			}
		}
	}

	private boolean canJump() {

		if (nbStepSinceLastJump++ < nbStepToWaitBetweenJump) {
			return false;
		}

		nbStepSinceLastJump = 0;

		CollisionEvent[] events = world.getContacts(this);
		// return events.length > 0;

		for (int i = 0; i < events.length; i++) {
			float angle = im.bci.newtonadv.util.Vector.angle(
					world.getGravityVector(), events[i].getNormal());
			if (angle > Math.PI / 1.35 && events[i].getBodyB() == this) {
				return true;
			}
			if (angle < Math.PI / 1.35 && events[i].getBodyA() == this) {
				return true;
			}
		}
		return false;
		/*
		 * if (events[i].getNormal().getY() > 0) { if (events[i].getBodyB() ==
		 * hero) { return true; } } if (events[i].getNormal().getY() < 0) { if
		 * (events[i].getBodyA() == hero) { return true; } }
		 */
		// }

		/*
		 * BodyList connected = hero.getTouching(); for (int i = 0; i <
		 * connected.size(); ++i) { Body body = connected.get(i); if (body
		 * instanceof Platform) { Vector2f normal = new
		 * Vector2f(hero.getPosition()); normal.sub(body.getPosition()); float
		 * angle = im.bci.newtonadv.util.Vector.angle(gravityVector, normal); if
		 * (angle >= Math.PI / 1.35) { return true; } } }
		 */
		// return false;
	}

	public void jump(float step) {
		if (/* hero.isOnPlatform */canJump()) {
			Matrix2f rot = new Matrix2f(world.getGravityAngle());
			Vector2f jump = net.phys2d.math.MathUtil.mul(rot, new Vector2f(
					0.0f, /* stepRate * */world.getGravityForce() * jumpForce));
			addForce(jump);

			if (jumpSound != null) {
				jumpSound.play();
			}

			// Vector2f jump = net.phys2d.math.MathUtil.mul(rot, new Vector2f(0,
			// /*stepRate **/ world.getGravityForce() * 1.0f));
			// adjustVelocity(jump);
		}
	}

	public void moveLeft(float step) {
		Matrix2f rot = new Matrix2f(world.getGravityAngle() /*
															 * + (float) Math.PI
															 * / 4.0f
															 */);
		Vector2f velocity = net.phys2d.math.MathUtil.mul(rot, new Vector2f(step
				* -world.getGravityForce() * horizontalSpeed, 0.0f));
		adjustBiasedVelocity(velocity);
		setCurrentMovement(Hero.Movement.GOING_LEFT);
	}

	public void moveRight(float step) {
		Matrix2f rot = new Matrix2f(world.getGravityAngle() /*
															 * + (float) Math.PI
															 * / 4.0f
															 */);
		Vector2f velocity = net.phys2d.math.MathUtil.mul(rot, new Vector2f(step
				* world.getGravityForce() * horizontalSpeed, 0.0f));
		adjustBiasedVelocity(velocity);
		setCurrentMovement(Hero.Movement.GOING_RIGHT);
	}

	public void dontMove() {
		setCurrentMovement(Movement.NOT_GOING_ANYWHERE);
	}

	public void hurtByFireBall() {
		if (!isHurt) {
			hurt(2);
		}
	}

	public void hurtByEgyptianBoss() {
		if (!isHurt) {
			hurt(1);
		}
	}

	public void hurtByMummy() {
		if (!isHurt) {
			hurt(1);
		}
	}

	public void hurtByBat() {
		if (!isHurt) {
			hurt(1);
		}
	}

	public void hurtByPike(ROVector2f normal) {
		if (!isHurt) {
			hurt(5);
			float reactionForce = world.getGravityForce() * jumpForce * 0.8f;
			Vector2f force = new Vector2f(normal.getX() * reactionForce,
					normal.getY() * reactionForce);
			addForce(force);
		}
	}

	public void collisionWithBouncePlatform(Vector2f normal) {
		float reactionForce = world.getGravityForce() * jumpForce * 1.1f;
		Vector2f force = new Vector2f(normal.getX() * reactionForce,
				normal.getY() * reactionForce);
		addForce(force);
	}

	public void setHasMap(boolean b) {
		hasMap = b;
	}

	public void setHasCompass(boolean b) {
		hasCompass = b;
	}
	
	public NewtonColor getColor() {
		return color;
	}

	public void setColor(NewtonColor color) {
		this.color = color;
		for(NewtonColor c : EnumSet.complementOf(EnumSet.of(color))) {
			this.removeBit(c.collisionBitmask);
		}
		this.addBit(color.collisionBitmask);
	}
}
