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

import net.phys2d.raw.Body;

import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableStaticBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.raw.shapes.Box;

/**
 * 
 * @author devnewton
 */
public strictfp class Door extends AbstractDrawableStaticBody implements
		CollisionDetectionOnly, Updatable {

	protected final World world;
	private AnimationCollection closedTexture;
	private AnimationCollection openTexture;
	protected boolean isClose = true;

	Door(World world, float w, float h) {
		super(new Box(w, h));
		this.world = world;
		addBit(World.STATIC_BODY_COLLIDE_BIT);
	}

	@Override
	public void draw() {
		world.getView().drawDoor(this, isClose ? closedTexture.getFirst().getCurrentFrame() : openTexture.getFirst().getCurrentFrame());
	}

	void setOpenTexture(AnimationCollection texture) {
		this.openTexture = texture;
		openTexture.getFirst().start();
	}

	void setClosedTexture(AnimationCollection texture) {
		this.closedTexture = texture;
		closedTexture.getFirst().start();
	}

	@Override
	public strictfp void collided(Body body) {
		if (body instanceof Key) {
			isClose = false;
		} else if (body instanceof Hero && !isClose)
			world.setObjectivesCompleted(true);
	}

	void open() {
		isClose = false;
	}

	public boolean isOpenableWithKey() {
		return true;
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		if (isClose) {
			closedTexture.getFirst()
					.update(frameTimeInfos.elapsedTime / 1000000);
		} else {
			openTexture.getFirst().update(frameTimeInfos.elapsedTime / 1000000);
		}

	}
}
