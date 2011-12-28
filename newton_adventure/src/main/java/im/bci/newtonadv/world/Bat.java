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

import net.phys2d.math.Matrix2f;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import org.lwjgl.opengl.GL11;
import im.bci.newtonadv.util.Vector;

/**
 *
 * @author devnewton
 */
public strictfp class Bat extends Body implements Drawable, Updatable {

    private Animation animation;
    private static final float weight = 0.1f;
    private static final float speed = 4.0f;
    private static final long maxMoveStraightDuration = 1000000000L;
    private long nextChangeDirectionTime = 0;
    private boolean isDead = false;
    private static final long dyingDuration = 1000000000L;
    private long beginOfDyingDuration = -1;
    private float scale = 1;
    private World world;
    private Vector2f directionVelocity;

    public Bat(World world) {
        super(new Box(World.distanceUnit * 1.0f, World.distanceUnit * 0.5f), weight);
        this.world = world;
        this.animation = world.getBatAnimation();
        setRotatable(false);
        setGravityEffected(false);
        this.animation.start();
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public strictfp void collided(Body other) {
        if (other instanceof Hero) {
            CollisionEvent[] events = world.getContacts(this);
            Hero hero = (Hero) other;
            for (int i = 0; i < events.length; i++) {
                CollisionEvent event = events[i];
                if (event.getBodyB() == hero) {
                    Vector2f normal = new Vector2f(event.getNormal());
                    float angle = Vector.angle(normal, world.getGravityVector());
                    if (angle < Math.PI / 4.0f) {
                        hero.hurtByBat();
                    } else {
                        hero.killedBat();
                        isDead = true;
                    }
                } else if (event.getBodyA() == hero) {
                    Vector2f normal = new Vector2f(event.getNormal());
                    float angle = Vector.angle(normal, world.getGravityVector());
                    if (angle > Math.PI / 4.0f) {
                        hero.hurtByBat();
                    } else {
                        hero.killedBat();
                        isDead = true;
                    }
                }
            }
        }
        if (isDead) {
            setMoveable(false);
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void draw() {
        AABox bounds = getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(getPosition().getX(), getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        GL11.glScalef(scale, scale, 1);
        float x1 = -bounds.getWidth() / 2.0f;
        float x2 = bounds.getWidth() / 2.0f;
        float y1 = -bounds.getHeight() / 2.0f;
        float y2 = bounds.getHeight() / 2.0f;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

        getAnimation().getCurrentTexture().bind();

        final float u1 = 1,  u2 = 0;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, 0.0f);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, 0.0f);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, 1.0f);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, 1.0f);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        if (isDead) {
            if (beginOfDyingDuration < 0) {
                beginOfDyingDuration = frameTimeInfos.currentTime;
            } else {
                scale = 1.0f - (frameTimeInfos.currentTime - beginOfDyingDuration) / (float) dyingDuration;
                if (scale <= 0) {
                    world.remove(this);
                }
            }
            return;
        }

        if (frameTimeInfos.currentTime > nextChangeDirectionTime) {
            nextChangeDirectionTime = frameTimeInfos.currentTime + (long)(Math.random() * maxMoveStraightDuration);

            Matrix2f rot = new Matrix2f((float) (Math.random() * 2.0f * Math.PI));
            directionVelocity = net.phys2d.math.MathUtil.mul(rot, new Vector2f(world.getGravityForce() * speed, 0));
        } else {
            adjustBiasedVelocity(directionVelocity);
        }
        getAnimation().update(frameTimeInfos.elapsedTime / 1000000);
    }
}
