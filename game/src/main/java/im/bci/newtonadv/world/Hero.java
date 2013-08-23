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

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.Animation.Play;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.game.time.OneShotTimedAction;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.util.NewtonColor;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Shape;

/**
 * 
 * @author devnewton
 */
public strictfp class Hero extends AbstractDrawableBody implements Updatable {

	private ISoundCache.Playable jumpSound;
	private ISoundCache.Playable pickupSound;
	private ISoundCache.Playable hurtSound;
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
	private static final long dyingAnimationDuration = 2000000000L;
	private LevelScore levelScore = new LevelScore();
	private Play play;
	private OneShotTimedAction deadClock, dyingTimedAction;

	public OneShotTimedAction getDyingTimedAction() {
		return dyingTimedAction;
	}

	public void setJumpSound(ISoundCache.Playable jumpSound) {
		this.jumpSound = jumpSound;
	}

	public void setPickupSound(ISoundCache.Playable pickupSound) {
		this.pickupSound = pickupSound;
	}

	public void setHurtSound(ISoundCache.Playable hurtSound) {
		this.hurtSound = hurtSound;
	}

	public void setDeadClock(long duration) {
		deadClock = new OneShotTimedAction(duration);
	}

	public OneShotTimedAction getDeadClock() {
		return deadClock;
	}

	public Movement getCurrentMovement() {
		return currentMovement;
	}

	public int getNbApple() {
		return nbApple;
	}

	public boolean isDead() {
		return null != dyingTimedAction;
	}

	boolean isInvincible() {
		return isHurt;
	}

	private void hurt(int nbAppleLose) {
		hurtSound.play();
		if (nbApple == 0) {
			this.dyingTimedAction = new OneShotTimedAction(
					dyingAnimationDuration);
		}

		if (nbAppleLose > nbApple) {
			nbAppleLose = nbApple;
		}

		this.nbApple -= nbAppleLose;
		if (nbAppleLose > 0) {
			levelScore.addLosedApple(nbAppleLose);
		}
		int scoreDiff = nbAppleLose * levelScore.getLosedAppleValue();
		if (scoreDiff != 0) {
			world.addTopLevelEntities(new ScoreVisualIndicator(world, this
					.getPosition(), scoreDiff));
		}
		final float losedAppleSize = getShape().getBounds().getWidth() / 4.0f;
		for (int i = 0; i < nbAppleLose; ++i) {
			world.addTopLevelEntities(new LosedApple(world, this.getPosition(),
					losedAppleSize));
		}

		this.isHurt = true;
		this.endOfInvincibilityDuration = -1;
	}

	private void setCurrentMovement(Movement currentMovement) {
		if (isFalling()) {
			if (!"falling".equals(play.getName())) {
				play = animations.getAnimationByName("falling").start();
			}
		} else {
			if (!"walk".equals(play.getName())) {
				play = animations.getAnimationByName("walk").start();
				if (this.currentMovement == Movement.NOT_GOING_ANYWHERE) {
					play.stop();
				}
			}
		}
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

	private boolean isFalling() {
		Vector2f g = world.getGravityVector();
		float dp = g.dot(this.getVelocity());
		if (dp > 0.1f) {
			Vector2f v = new Vector2f();
			v.x = dp * g.getX();
			v.y = dp * g.getY();
			return v.length() > 20.0f;
		}
		return false;
	}

	void killedMummy() {
		levelScore.addKilledMummy();
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this
				.getPosition(), levelScore.getKilledMummyValue()));
	}

	void killedBat() {
		levelScore.addKilledBat();
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this
				.getPosition(), levelScore.getKilledBatValue()));
	}

	void killedEgyptianBoss() {
		levelScore.addKilledEgyptianBoss();
		world.addTopLevelEntities(new ScoreVisualIndicator(world, this
				.getPosition(), levelScore.getKilledEgyptianBossValue()));
	}

	public LevelScore getLevelScore() {
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
			pickupSound.play();
			levelScore.addApple(1);
			world.addTopLevelEntities(new ScoreVisualIndicator(world, body
					.getPosition(), levelScore.getAppleValue()));
		} else if (body instanceof Coin) {
			pickupSound.play();
			levelScore.addCoin(1);
			world.addTopLevelEntities(new ScoreVisualIndicator(world, body
					.getPosition(), levelScore.getCoinValue()));
		} else if (body instanceof WorldMap) {
			pickupSound.play();
			hasMap = true;
		} else if (body instanceof Compass) {
			pickupSound.play();
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
		play = animations.getAnimationByName("walk").start();
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
		world.getView().drawHero(this, getAnimation().getCurrentFrame(), world);
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
		if (null != dyingTimedAction) {
			dyingTimedAction.update(frameTimeInfos);
			isHurt = false;
			this.setZOrder(1000);
			if (dyingTimedAction.getProgress() >= 1.0f) {
				throw new GameOverException("Newton is dead");
			}
		} else if(null != deadClock) {
			deadClock.update(frameTimeInfos);
			if (deadClock.getProgress() >= 1.0f) {
				hurt(1);
				deadClock = new OneShotTimedAction(2000000000L);
			}
		}
	}

	private boolean canJump() {

		if (nbStepSinceLastJump++ < nbStepToWaitBetweenJump) {
			return false;
		}

		nbStepSinceLastJump = 0;

		CollisionEvent[] events = world.getContacts(this);

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
	}

	public void jump(float step) {
		if (canJump()) {
			Matrix2f rot = new Matrix2f(world.getGravityAngle());
			Vector2f jump = net.phys2d.math.MathUtil.mul(rot, new Vector2f(
					0.0f, /* stepRate * */world.getGravityForce() * jumpForce));
			addForce(jump);
			jumpSound.play();
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
		removeColoredExcludedBodies(this.color);
		this.color = color;
		addColoredExcludedBodies(color);
	}

	private void removeColoredExcludedBodies(NewtonColor oldColor) {
		BodyList bodies = world.getColoredStaticBodyList(oldColor);
		for (int i = 0, n = bodies.size(); i < n; ++i) {
			this.removeExcludedBody(bodies.get(i));
		}
	}

	private void addColoredExcludedBodies(NewtonColor newColor) {
		BodyList bodies = world.getColoredStaticBodyList(newColor);
		for (int i = 0, n = bodies.size(); i < n; ++i) {
			this.addExcludedBody(bodies.get(i));
		}
	}
}
