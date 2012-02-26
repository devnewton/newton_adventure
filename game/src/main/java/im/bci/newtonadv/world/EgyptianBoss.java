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
import net.phys2d.raw.BodyList;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Circle;

import im.bci.newtonadv.util.Vector;

/**
 *
 * @author devnewton
 */
public strictfp class EgyptianBoss extends AbstractDrawableBody implements Updatable {

    public EgyptianBossHand getLeftHand() {
        return leftHand;
    }

    public EgyptianBossHand getRightHand() {
        return rightHand;
    }

    private ITexture bodyTexture;
    private static final float weight = 10.0f;
    static final float normalSpeed = 1.0f;
    float speed = normalSpeed;
    private static final long maxMoveStraightDuration = 1000000000L;
    private static final long minMoveStraightDuration = 500000000L;
    private static final long invincibleAfterHurtDuration = 10000000000L;
    private static final float size = World.distanceUnit * 8.0f;
    private long nextChangeDirectionTime = 0;
    private static final long dyingDuration = 10000000000L;
    private long endOfDyingDuration = -1;
    private long endOfInvincibilityDuration = -1;
    private boolean isHurt = false;
    World world;
    private Vector2f directionVelocity;
    private EgyptianBossHand leftHand, rightHand;
    private int lifePoints = 3;
    private boolean isHurtBlinkState = false;
    private long nextExplosionTime;
    private static final long timeBetweenExplosion = 300000000L;

    public EgyptianBoss(World world, float x, float y) {
        super(new Circle(size / 2.0f), weight);
        this.world = world;
        setRotatable(false);
        setGravityEffected(false);
        leftHand = new EgyptianBossHand(this, EgyptianBossHand.Side.LEFT,world);
        leftHand.setPosition(getHandPosition(EgyptianBossHand.Side.LEFT).x, getHandPosition(EgyptianBossHand.Side.LEFT).y);
        rightHand = new EgyptianBossHand(this, EgyptianBossHand.Side.RIGHT,world);
        rightHand.setPosition(getHandPosition(EgyptianBossHand.Side.RIGHT).x, getHandPosition(EgyptianBossHand.Side.RIGHT).y);
        setPosition(x, y);
    }

    final Vector2f getHandPosition(EgyptianBossHand.Side side) {
        Vector2f pos = new Vector2f(this.getPosition());
        pos.y -= 0.327005f * size;
        if (side == EgyptianBossHand.Side.LEFT) {
            pos.x -= 0.35f * size;
        } else {
            pos.x += 0.35f * size;
        }
        return pos;
    }

    public void setBodyTexture(ITexture bodyTexture) {
        this.bodyTexture = bodyTexture;
    }

    public void setHandTexture(ITexture handTexture) {
        leftHand.setTexture(handTexture);
        rightHand.setTexture(handTexture);
    }

    public boolean isDead() {
        return lifePoints <= 0;
    }

    @Override
    public strictfp void collided(Body other) {
        if (other instanceof Hero) {

            CollisionEvent[] events = world.getContacts(this);
            Hero hero = (Hero) other;
            boolean isBossHurt = false;
            boolean isHeroHurt = false;
            for (int i = 0; i < events.length; i++) {
                CollisionEvent event = events[i];
                Vector2f normal = new Vector2f(event.getNormal());
                float angle = Vector.angle(normal, world.getGravityVector());
                if (event.getBodyB() == hero) {
                    if (angle < Math.PI / 4.0f) {
                        isHeroHurt = true;
                    } else {
                        isBossHurt = true;
                    }
                    //normal.scale(world.getGravityForce() * 100.0f);
                    //hero.addForce(normal);
                } else if (event.getBodyA() == hero) {
                    if (angle > Math.PI / 4.0f) {
                        isHeroHurt = true;
                    } else {
                        isBossHurt = true;
                    }
                    //normal.scale(world.getGravityForce() * -100.0f);
                    //hero.addForce(normal);
                }
                if (isBossHurt) {
                    if (!isHurt) {
                        if (hero.getPosition().getY() > getPosition().getY()) {
                            --lifePoints;
                            isHurt = true;
                            endOfInvincibilityDuration = -1;
                            if(lifePoints == 0)
                                hero.killedEgyptianBoss();
                        }
                    }
                }

                if (!isBossHurt && isHeroHurt) {
                    hero.hurtByEgyptianBoss();
                }

            }

        }


        if (isDead()) {
            setMoveable(false);
        }
    }

    @Override
    public void draw() {
        
        if (isHurt) {
            isHurtBlinkState = !isHurtBlinkState;
        } else {
            isHurtBlinkState = false;
        }
        world.getView().drawEgyptianBoss(this,this.bodyTexture,isHurtBlinkState);
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        if (isDead()) {
            if (endOfDyingDuration < 0) {
                world.remove(leftHand);
                world.remove(rightHand);
                endOfDyingDuration = frameTimeInfos.currentTime + dyingDuration;
                nextExplosionTime = frameTimeInfos.currentTime + timeBetweenExplosion;
            } else {

                if (endOfDyingDuration > frameTimeInfos.currentTime) {
                    if (nextExplosionTime < frameTimeInfos.currentTime) {
                        AABox bounds = getShape().getBounds();
                        float x1 = getPosition().getX() + -bounds.getWidth() / 2.0f;
                        float y1 = getPosition().getY() + -bounds.getHeight() / 2.0f;
                        Vector2f pos = new Vector2f(x1 + frameTimeInfos.random.nextFloat() * bounds.getWidth(), y1 + frameTimeInfos.random.nextFloat() * bounds.getHeight() / 2.0f);
                        world.addTopLevelEntities(new Explosion(world, pos));
                        nextExplosionTime = frameTimeInfos.currentTime + timeBetweenExplosion;
                    }
                } else {
                    BodyList bodies = world.getBodies();
                    for (int i = 0; i < bodies.size(); ++i) {
                        Body body = bodies.get(i);
                        if (body instanceof Door) {
                            ((Door) body).open();
                        }
                    }
                    world.remove(this);
                }
            }
            return;
        }

        if (isHurt) {
            if (endOfInvincibilityDuration < 0) {
                speed = normalSpeed * 2.0f;
                endOfInvincibilityDuration = frameTimeInfos.currentTime + invincibleAfterHurtDuration;
            } else if (frameTimeInfos.currentTime > endOfInvincibilityDuration) {
                isHurt = false;
                speed = 1.0f;
            }
        }


        if (frameTimeInfos.currentTime > nextChangeDirectionTime) {
            nextChangeDirectionTime = frameTimeInfos.currentTime + minMoveStraightDuration + (long) (Math.random() * (maxMoveStraightDuration - minMoveStraightDuration));

            directionVelocity = new Vector2f(world.getHero().getPosition());
            directionVelocity.sub(this.getPosition());
            directionVelocity.normalise();
            directionVelocity.scale(world.getGravityForce() * speed);
        } else {
            adjustBiasedVelocity(directionVelocity);
        }
    }
}
