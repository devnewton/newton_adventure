/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tuxfamily.newtonadv.world;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.shapes.Box;

/**
 *
 * @author bci
 */
public strictfp class Pikes extends Platform {

    private World world;

    public enum DangerousSide {

        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    DangerousSide dangerousSide;

    public Pikes(World world, DangerousSide dangerousSide) {
        this.world = world;
        this.dangerousSide = dangerousSide;
    }

    @Override
    public void collided(Body other) {
        if (other instanceof Hero) {
            Hero hero = (Hero) other;
            if (hero.isInvincible()) {
                return;
            }
            CollisionEvent[] events = world.getContacts(this);

            for (int i = 0; i < events.length; i++) {
                CollisionEvent event = events[i];
                ROVector2f point = event.getPoint();
                if (isPointOnDangerousSide(point)) {

                    /*switch (dangerousSide) {
                        case UP:
                            hero.hurtByPike(new Vector2f(0, 1));
                            break;
                        case DOWN:
                            hero.hurtByPike(new Vector2f(0, -1));
                            break;
                        case RIGHT:
                            hero.hurtByPike(new Vector2f(1, 0));
                            break;
                        case LEFT:
                            hero.hurtByPike(new Vector2f(-1, 0));
                            break;
                    }*/
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
    }

    private boolean isPointOnDangerousSide(ROVector2f point) {
        switch (dangerousSide) {
            case UP:
                return point.getY() >= (this.getPosition().getY() + size * 0.45f);
            case DOWN:
                return point.getY() <= (this.getPosition().getY() - size * 0.45);
            case RIGHT:
                return point.getX() >= (this.getPosition().getX() + size * 0.45f);
            case LEFT:
                return point.getX() <= (this.getPosition().getX() - size * 0.45f);
        }
        return false;
    }
}
