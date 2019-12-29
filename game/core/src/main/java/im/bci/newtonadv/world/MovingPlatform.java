/*
 *  Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  *  Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.world;

import im.bci.jnuit.animation.IAnimationCollection;
import im.bci.jnuit.animation.IPlay;
import im.bci.jnuit.animation.PlayMode;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Shape;

/**
 * 
 * @author devnewton
 */
public strictfp class MovingPlatform extends AbstractDrawableBody implements
		Updatable {

	private final Vector2f[] destinations;
	private int currentDestination = 0;
	private static final float weight = 10000.0f;
	final World world;
	final IPlay play;
	final Vector2f f = new Vector2f();

	public MovingPlatform(World world, IAnimationCollection texture,
			Vector2f[] destinations, float w, float h) {
		this(world, texture, destinations, new Box(w, h));
	}

	public MovingPlatform(World world, IAnimationCollection texture,
			Vector2f[] destinations, Shape shape) {
		super(shape, weight);
		this.world = world;
		play = texture.getFirst().start(PlayMode.LOOP);
		this.destinations = destinations;
		this.setGravityEffected(false);
		this.setRotatable(false);
		texture.getFirst().start(PlayMode.LOOP);
	}

	@Override
	public void draw() {
		world.getView().drawMovingPlatform(this, play.getCurrentFrame());
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		if (destinations.length > 0) {
			Vector2f destinationPos = destinations[currentDestination];
			if (this.getPosition().distance(destinationPos) < 1f) {
				++currentDestination;
				if (currentDestination >= destinations.length) {
					currentDestination = 0;
				}
			}
			f.set(destinationPos);
			f.sub(this.getPosition());
			f.normalise();
			f.scale(world.getGravityForce());
			this.adjustBiasedVelocity(f);
		}
	}
}
