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
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.Box;
import org.lwjgl.opengl.GL11;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;

/**
 *
 * @author devnewton
 */
public strictfp class Axe extends Body implements Drawable, Updatable {

    private static final float weight = 10.0f;
    static final float width = World.distanceUnit;
    static final float height = 3.9f * World.distanceUnit;
    private Texture texture;
    private World world;

    Axe(World world) {
        super(new Box(width, height), weight);
        this.world = world;
        //setDamping(0.02f);
        setGravityEffected(false);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public strictfp void collided(Body other) {
        if (other instanceof Hero) {
            Hero hero = (Hero) other;
            if (hero.isInvincible()) {
                return;
            }
            CollisionEvent[] events = world.getContacts(this);

            for (int i = 0; i < events.length; i++) {
                CollisionEvent event = events[i];
                ROVector2f point = event.getPoint();
                if (event.getBodyB() == hero) {
                    Vector2f normal = new Vector2f(event.getNormal());
                    hero.hurtByPike(normal/*.negate()*/);
                } else if (event.getBodyA() == hero) {
                    Vector2f normal = new Vector2f(event.getNormal());
                    hero.hurtByPike(normal.negate());
                }
                return;
            }
        }
    }

    @Override
    public void draw() {
        Box box = (Box) getShape();
        Vector2f[] pts = box.getPoints(getPosition(), getRotation());

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
        texture.bind();
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

    public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        float v = getAngularVelocity();
        if( v < 1.0f )
            adjustAngularVelocity(1.0f - v);
    }
}