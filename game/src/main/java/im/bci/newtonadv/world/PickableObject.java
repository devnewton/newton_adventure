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
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Shape;

/**
 * 
 * @author devnewton
 */
public abstract strictfp class PickableObject extends StaticBody implements
		Drawable, CollisionDetectionOnly, Updatable {

	protected Animation.Play play;
	protected World world;
	private int zOrder = 0;

	PickableObject(World world, Shape shape) {
		super(shape);
		this.world = world;
		addBit(World.STATIC_BODY_COLLIDE_BIT);
	}

	@Override
	public void collided(Body body) {
		if (body instanceof Hero) {
			removeFromWorld();
			world.addTopLevelEntities(new PickedUpObject(world, play
					.getCurrentFrame(), getPosition(), getShape().getBounds()
					.getWidth()));
		}
	}

	protected void removeFromWorld() {
		world.remove(this);
	}

	public void setTexture(AnimationCollection texture) {
		this.play = texture.getFirst().start();
	}

	@Override
	public void draw() {
		world.getView().drawPickableObject(this,
				play.getCurrentFrame(), world);
	}

	@Override
	public int getZOrder() {
		return zOrder;
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		play.update(frameTimeInfos.elapsedTime / 1000000);
	};

	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

}