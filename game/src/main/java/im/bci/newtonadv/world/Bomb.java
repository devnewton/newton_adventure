/*
 * Copyright (c) 2012 devnewton <devnewton@bci.im>
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

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Shape;

/**
 *
 * @author devnewton
 */
public strictfp class Bomb extends AbstractDrawableBody implements Updatable {

    static final float size = 2.0f * World.distanceUnit;
    private World world;
    private AnimationCollection texture;
	private Animation currentAnimation;
	private boolean triggered;
	private long explodeTime = -1;
	private static final float explosionForce = 10000.0f;
	private static final long triggerDuration = 1000000000L;

    Bomb(World world) {
        super(new Circle(size / 2.0f), 1.0f);
        this.world = world;
        setRotatable(false);
    }

    Bomb(World world, Shape shape) {
        super(shape, 1.0f);
        this.world = world;
        setRotatable(false);
	}

	@Override
    public void draw() {
        world.getView().drawBomb(this, currentAnimation.getCurrentFrame(), world);
    }

    void setTexture(AnimationCollection texture) {
        this.texture = texture;
        this.currentAnimation = texture.getAnimationByName("bomb_inactive");
        this.currentAnimation.start();
    }

    @Override
    public strictfp void collided(Body body) {
        if (body instanceof Hero) {
        	currentAnimation = texture.getAnimationByName("bomb_about_to_explode");
        	triggered = true;
        }
    }

	public AnimationCollection getTexture() {
		return texture;
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		currentAnimation.update(frameTimeInfos.elapsedTime / 1000000);
	       if(triggered) {
	           if( explodeTime < 0 )
	               explodeTime = frameTimeInfos.currentTime + triggerDuration;
	           else if( frameTimeInfos.currentTime  >= explodeTime ) {
	               explode();
	           }
	       }
	}

	private void explode() {
		world.remove(this);
		throwFireball(1, 1);
		throwFireball(-1, -1);
		throwFireball(-1, 1);
		throwFireball(1, -1);
	}
	
    private void throwFireball(float dx, float dy) {
        Vector2f pos = new Vector2f(getPosition());
        float w = this.getShape().getBounds().getWidth();
		FireBall fireBall = new FireBall(world, w / 2.0f);
        fireBall.setPosition(pos.x + dx * w, pos.y + dy * w);
        fireBall.setTexture(world.getFireBallTexture());
        world.add(fireBall);
        fireBall.addForce(new Vector2f(dx * explosionForce, dy * explosionForce));
    }
}