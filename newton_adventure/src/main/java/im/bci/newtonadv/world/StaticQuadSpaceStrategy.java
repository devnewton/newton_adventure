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

import im.bci.newtonadv.util.AbsoluteAABox;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.BroadCollisionStrategy;
import net.phys2d.raw.CollisionContext;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.AABox;

/**
 *
 * @author devnewton
 */
class StaticQuadSpaceStrategy implements BroadCollisionStrategy {

    private int maxInSpace;
    private int maxLevels;
    private boolean needToBeRebuild = true;
    private ArrayList<Body> staticBodies = new ArrayList();
    private BodyList dynamicBodies = new BodyList();
    private Space rootSpace;
    private ArrayList<Space> spaceWithPotentialCollision = new ArrayList();
    private ArrayList<Body> removedStaticBodies = new ArrayList();

    /**
     * Create a new strategy
     *
     * @param maxInSpace The maximum number of bodies in a given space acceptable
     * @param maxLevels The number of sub divisions allows
     */
    public StaticQuadSpaceStrategy(int maxInSpace, int maxLevels) {
        this.maxInSpace = maxInSpace;
        this.maxLevels = maxLevels;
    }

    public AbsoluteAABox getStaticBounds() {
        if (rootSpace != null) {
            return new AbsoluteAABox(rootSpace);
        } else {
            return new AbsoluteAABox();
        }
    }

    public void addBody(Body body) {
        if (body instanceof StaticBody) {
            needToBeRebuild = true;
        } else {
            dynamicBodies.add(body);
        }    
    }
    
    public void removeBody(Body body) {
        if (body instanceof StaticBody) {
            removedStaticBodies.add(body);
        } else {
            dynamicBodies.remove(body);
        }
    }

    @Override
    public void collideBodies(CollisionContext context, BodyList bodies, float dt) {
        if (needToBeRebuild) {
            rebuild(bodies);
            needToBeRebuild = false;
        } else if (mustBeUpdated()) {
            update();
        }

        findSpaceWithPotentialCollision();
        for (Space space : spaceWithPotentialCollision) {
            context.resolve(space.bodiesForCollides, dt);
        }
    }

    public BodyList findVisibleBodies(float camera_x1, float camera_y1, float camera_x2, float camera_y2) {
        BodyList visibleBodies = new BodyList();
        if (needToBeRebuild) {
            return visibleBodies;
        }
        HashSet<Body> bodiesSet = new HashSet();
        recursiveFindVisibleBodies(rootSpace, camera_x1, camera_y1, camera_x2, camera_y2, bodiesSet);

        for (Body b : bodiesSet) {
            visibleBodies.add(b);
        }
        for (int i = 0; i < dynamicBodies.size(); ++i) {
            visibleBodies.add(dynamicBodies.get(i));
        }

        return visibleBodies;
    }

    private boolean mustBeUpdated() {
        return !removedStaticBodies.isEmpty();
    }

    private void recursiveFindVisibleBodies(Space space, float camera_x1, float camera_y1, float camera_x2, float camera_y2, Collection visibleBodies) {
        if (space.touches(camera_x1, camera_y1, camera_x2, camera_y2)) {
            if (space.isLeaf()) {
                for (int i = 0; i < space.staticBodiesInSpace.size(); ++i) {
                    visibleBodies.add(space.staticBodiesInSpace.get(i));
                }
            } else {
                for (Space s : space.subSpaces) {
                    recursiveFindVisibleBodies(s, camera_x1, camera_y1, camera_x2, camera_y2, visibleBodies);
                }
            }

        }
    }

    private void buildQuadTree() {
        rootSpace = new Space(staticBodies);
        recursiveSplit(rootSpace, 0);
    }

    private void recursiveSplit(Space space, int level) {
        if (level > maxLevels) {
            return;
        }
        if (space.staticBodiesInSpace.size() <= maxInSpace) {
            return;
        }

        space.split();
        for (Space s : space.subSpaces) {
            recursiveSplit(s, level + 1);
        }
    }

    private void classifyBodies(BodyList bodies) {
        staticBodies.clear();
        dynamicBodies.clear();
        for (int i = 0; i < bodies.size(); ++i) {
            Body b = bodies.get(i);
            if (b.isStatic()) {
                staticBodies.add(b);
            } else {
                dynamicBodies.add(b);
            }
        }
    }

    private void rebuild(BodyList bodies) {
        classifyBodies(bodies);
        buildQuadTree();
    }

    private void findSpaceWithPotentialCollision() {
        spaceWithPotentialCollision.clear();
        recursiveFindSpaceWithPotentialCollision(rootSpace, dynamicBodies);
    }

    private void recursiveFindSpaceWithPotentialCollision(Space space, BodyList dynBodies) {
        space.bodiesForCollides.clear();
        for (int i = 0; i < dynBodies.size(); ++i) {
            Body b = dynBodies.get(i);
            if (space.touches(b)) {
                space.bodiesForCollides.add(b);
            }
        }

        if (space.bodiesForCollides.size() != 0) {

            if (space.isLeaf()) {
                spaceWithPotentialCollision.add(space);
                for (Body b : space.staticBodiesInSpace) {
                    space.bodiesForCollides.add(b);
                }
            } else {
                for (Space subSpace : space.subSpaces) {
                    recursiveFindSpaceWithPotentialCollision(subSpace, space.bodiesForCollides);
                }
            }
        }
    }

    private void update() {
        rootSpace.update();
        removedStaticBodies.clear();
    }

    private final class Space extends AbsoluteAABox {

        ArrayList<Body> staticBodiesInSpace = new ArrayList<Body>();
        ArrayList<Space> subSpaces = new ArrayList();
        BodyList bodiesForCollides = new BodyList();

        Space(Collection<Body> bodies) {
            for (Body body : bodies) {
                staticBodiesInSpace.add(body);

                AABox box = body.getShape().getBounds();
                float xp = body.getPosition().getX();
                float yp = body.getPosition().getY();

                float bx1 = xp - box.getWidth();
                float bx2 = xp + box.getWidth();
                float by1 = yp - box.getHeight();
                float by2 = yp + box.getHeight();

                x1 = Math.min(bx1, x1);
                y1 = Math.min(by1, y1);
                x2 = Math.max(bx2, x2);
                y2 = Math.max(by2, y2);
            }
        }

        Space(Space parent, float x1, float y1, float width, float height) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x1 + width;
            this.y2 = y1 + height;
            for (Body body : parent.staticBodiesInSpace) {
                if (touches(body)) {
                    staticBodiesInSpace.add(body);
                }
            }
        }

        final void split() {
            Space[] spaces = new Space[4];
            float width = (x2 - x1) / 2.0f;
            float height = (y2 - y1) / 2.0f;

            spaces[0] = new Space(this, x1, y1, width, height);
            spaces[1] = new Space(this, x1, y1 + height, width, height);
            spaces[2] = new Space(this, x1 + width, y1, width, height);
            spaces[3] = new Space(this, x1 + width, y1 + height, width, height);

            subSpaces.clear();
            for (Space s : spaces) {
                if (!s.staticBodiesInSpace.isEmpty()) {
                    subSpaces.add(s);
                }
            }
        }

        final boolean isLeaf() {
            return subSpaces.isEmpty();
        }

        final boolean touches(Body body) {
            AABox box = body.getShape().getBounds();
            float xp = body.getPosition().getX();
            float yp = body.getPosition().getY();
            final float w = box.getWidth();
            final float h = box.getHeight();

            return touches(xp - w, yp - h, xp + w, yp + h);
        }

        final boolean touches(float bx1, float by1, float bx2, float by2) {
            if (x2 < bx1 || y2 < by1 || x1 > bx2 || y1 > by2) {
                return false;
            } else {
                return true;
            }
        }

        private void update() {
            if (staticBodiesInSpace.removeAll(removedStaticBodies)) {
                Iterator<Space> it = subSpaces.iterator();
                while (it.hasNext()) {
                    Space s = it.next();
                    s.update();
                    if (staticBodiesInSpace.isEmpty()) {
                        it.remove();
                    }
                }
            }
        }
    }
}
