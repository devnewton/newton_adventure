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

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import im.bci.newtonadv.Texture;
import im.bci.newtonadv.game.Drawable;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Box;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author devnewton
 */
strictfp class Door extends StaticBody implements Drawable, CollisionDetectionOnly {

    static final float width = 2.0f * World.distanceUnit;
    static final float height = 4.0f * World.distanceUnit;
    private World world;
    private Texture closedTexture;
    private Texture openTexture;
    private boolean isClose = true;

    Door(World world) {
        super(new Box(width, height));
        this.world = world;
        addBit(World.STATIC_BODY_COLLIDE_BIT);
    }

    @Override
    public void draw() {
        Box box = (Box) getShape();
        Vector2f[] pts = box.getPoints(getPosition(), getRotation());

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
        if (isClose) {
            closedTexture.bind();
        } else {
            openTexture.bind();
        }
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(pts[0].x, pts[0].y);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(pts[1].x, pts[1].y);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(pts[2].x, pts[2].y);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(pts[3].x, pts[3].y);
        GL11.glEnd();
        GL11.glPopAttrib();
    }

    void setOpenTexture(Texture texture) {
        this.openTexture = texture;
    }

    void setClosedTexture(Texture texture) {
        this.closedTexture = texture;
    }

    @Override
    public strictfp void collided(Body body) {
        if (body instanceof Key) {
            isClose = false;
        } else if( body instanceof Hero && !isClose )
            world.setObjectivesCompleted(true);
    }

    void open() {
        isClose = false;
    }
}
