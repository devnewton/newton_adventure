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

import im.bci.newtonadv.platform.interfaces.ITexture;
import net.phys2d.math.ROVector2f;

import im.bci.newtonadv.game.AbstractDrawableStaticBody;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.ConvexPolygon;

/**
 *
 * @author devnewton
 */
public class DownLeftHalfPlatform extends AbstractDrawableStaticBody {

    static final float size = 2.0f * World.distanceUnit;
    static final ROVector2f[] vertices = new ROVector2f[] { new Vector2f(-size/2.0f, size/2.0f), new Vector2f(-size/2.0f, -size/2.0f), new Vector2f(size/2.0f, -size/2.0f) };
    private ITexture texture;
    private final World world;

    DownLeftHalfPlatform(World world) {
        super(new ConvexPolygon(vertices));
        setFriction(10.0f);
        addBit(World.STATIC_BODY_COLLIDE_BIT);
        this.world = world;
    }

    public void setTexture(ITexture texture) {
        this.texture = texture;
    }

    @Override
    public void draw() {
        world.getView().drawDownLeftHalfPlatform(this,texture);
    }
}
