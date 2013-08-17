/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
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
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;

/**
 *
 * @author devnewton
 */
public class PikesComponent {

    public enum DangerousSide {

        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private DangerousSide dangerousSide;

    public void setDangerousSide(DangerousSide dangerousSide) {
        this.dangerousSide = dangerousSide;
    }

    void collided(World world, Body aThis, float aThisW, float aThisH, Body other) {
        if (other instanceof Hero) {
            Hero hero = (Hero) other;
            if (hero.isInvincible()) {
                return;
            }
            CollisionEvent[] events = world.getContacts(aThis);

            for (int i = 0; i < events.length; i++) {
                CollisionEvent event = events[i];
                ROVector2f point = event.getPoint();
                if (isPointOnDangerousSide(aThis.getPosition(), aThisW, aThisH, point)) {
                    if (event.getBodyB() == hero) {
                        Vector2f normal = new Vector2f(event.getNormal());
                        hero.hurtByPike(normal);
                    } else if (event.getBodyA() == hero) {
                        Vector2f normal = new Vector2f(event.getNormal());
                        hero.hurtByPike(normal.negate());
                    }

                    return;
                }

            }
        }
    }

    private boolean isPointOnDangerousSide(ROVector2f position, float w, float h, ROVector2f point) {
        switch (dangerousSide) {
            case UP:
                return point.getY() >= (position.getY() + h * 0.45f);
            case DOWN:
                return point.getY() <= (position.getY() - h * 0.45);
            case RIGHT:
                return point.getX() >= (position.getX() + w * 0.45f);
            case LEFT:
                return point.getX() <= (position.getX() - w * 0.45f);
        }
        return false;
    }
}
