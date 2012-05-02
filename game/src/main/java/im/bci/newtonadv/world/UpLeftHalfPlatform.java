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
import im.bci.newtonadv.game.AbstractDrawableStaticBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.ConvexPolygon;

/**
 *
 * @author devnewton
 */
public class UpLeftHalfPlatform extends AbstractDrawableStaticBody implements Updatable {

    static final float size = 2.0f * World.distanceUnit;
    private AnimationCollection texture;
    private final World world;

    UpLeftHalfPlatform(World world, float w, float h) {
        super(new ConvexPolygon(computeVertices(w, h)));
        setFriction(10.0f);
        addBit(World.STATIC_BODY_COLLIDE_BIT);
        this.world = world;
    }

    private static ROVector2f[] computeVertices(float w, float h) {
		return new ROVector2f[] { new Vector2f(-w/2.0f, h/2.0f), new Vector2f(-w/2.0f, -h/2.0f), new Vector2f(w/2.0f, h/2.0f) };
	}

	public void setTexture(AnimationCollection texture) {
        this.texture = texture;
        texture.getFirst().start();
    }

    @Override
    public void draw() {
        world.getView().drawUpLeftHalfPlatform(this, texture.getFirst().getCurrentFrame());
    }

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		texture.getFirst().update(frameTimeInfos.elapsedTime / 1000000);		
	}
}
