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
import im.bci.newtonadv.Texture;
import im.bci.newtonadv.game.Drawable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.ConvexPolygon;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author devnewton
 */
public class UpLeftHalfPlatform extends StaticBody implements Drawable {

    static final float size = 2.0f * World.distanceUnit;
    static final ROVector2f[] vertices = new ROVector2f[] { new Vector2f(-size/2.0f, size/2.0f), new Vector2f(-size/2.0f, -size/2.0f), new Vector2f(size/2.0f, size/2.0f) };
    private Texture texture;

    UpLeftHalfPlatform() {
        super(new ConvexPolygon(vertices));
        setFriction(10.0f);
        addBit(World.STATIC_BODY_COLLIDE_BIT);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void draw() {
        ConvexPolygon polygon = (ConvexPolygon) getShape();
        Vector2f[] pts = polygon.getVertices(getPosition(), getRotation());

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.9999f);
        texture.bind();
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(pts[0].x, pts[0].y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(pts[1].x, pts[1].y);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(pts[2].x, pts[2].y);
        GL11.glEnd();
        GL11.glPopAttrib();
    }
}
