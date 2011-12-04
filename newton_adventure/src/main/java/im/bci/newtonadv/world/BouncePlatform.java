/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.world;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;

/**
 *
 * @author bci
 */
public strictfp class BouncePlatform extends Platform {

    private World world;

    public BouncePlatform(World world) {
        this.world = world;
    }

    @Override
    public void collided(Body other) {
        if (other instanceof Hero) {
            Hero hero = (Hero) other;
            CollisionEvent[] events = world.getContacts(this);

            for (int i = 0; i < events.length; i++) {
                CollisionEvent event = events[i];
                ROVector2f point = event.getPoint();
                    if (event.getBodyB() == hero) {
                        Vector2f normal = new Vector2f(event.getNormal());
                        hero.collisionWithBouncePlatform(normal/*.negate()*/);
                    } else if (event.getBodyA() == hero) {
                        Vector2f normal = new Vector2f(event.getNormal());
                        hero.collisionWithBouncePlatform(normal.negate());
                    }
            }
        }
    }
}
