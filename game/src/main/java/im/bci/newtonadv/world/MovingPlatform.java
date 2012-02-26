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

import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.platform.interfaces.ITexture;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Box;

/**
 *
 * @author devnewton
 */
public strictfp class MovingPlatform extends AbstractDrawableBody implements Updatable {

    private final Destinations destinations;

    public static class Destinations {

        Vector2f a = new Vector2f(), b = new Vector2f();
    }
    static final float size = 2.0f * World.distanceUnit;
    private static final float weight = 10000.0f;
    final World world;
    final ITexture texture;
    final Vector2f f = new Vector2f();

    public MovingPlatform(World world, ITexture texture, Destinations destinations) {
        super(new Box(size, size), weight);
        this.world = world;
        this.texture = texture;
        this.destinations = destinations;
        this.setGravityEffected(false);
        this.setRotatable(false);
    }

    @Override
	public void draw() {
        world.getView().drawMovingPlatform(this, texture);
    }

    @Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        if (this.getPosition().distance(destinations.a) < 1f) {
            Vector2f swap = destinations.a;
            destinations.a = destinations.b;
            destinations.b = swap;
        }
        f.set(destinations.a);
        f.sub(this.getPosition());
        f.normalise();
        f.scale(world.getGravityForce());
        this.adjustBiasedVelocity(f);
    }
}
