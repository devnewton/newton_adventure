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

import im.bci.newtonadv.Texture;
import im.bci.newtonadv.game.Entity;
import im.bci.newtonadv.game.FrameTimeInfos;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author devnewton
 */
public strictfp class PickedUpApple implements Entity {
    
    float size = Apple.size;
    private World world;
    private Texture texture;
    private Vector2f position;
    private boolean canMove = false;
    private long canMoveTime = -1;
    private static final long waitingDuration = 300000000L;

    PickedUpApple(World world, Texture texture, ROVector2f position) {
        this.world = world;
        this.texture = texture;
        this.position = new Vector2f(position);
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        GL11.glTranslatef(position.getX(), position.getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -size / 2.0f;
        final float x2 = size / 2.0f;
        final float y1 = -size / 2.0f;
        final float y2 = size / 2.0f;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
        texture.bind();

        final float u1 = 0.0f,  u2 = 1.0f;
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
    public void update(FrameTimeInfos frameTimeInfos) {
        if( canMove ) {   
        Vector2f direction = new Vector2f( world.getHero().getPosition() );
        direction.sub(position);
        direction.normalise();
        position.add(direction);
        } else {
            if( canMoveTime < 0) {
                canMoveTime = frameTimeInfos.currentTime + waitingDuration;
            }
            else {
                canMove = frameTimeInfos.currentTime > canMoveTime;
                size = Apple.size * ( 0.5f + 0.5f * (canMoveTime -  frameTimeInfos.currentTime) / (float)waitingDuration);
            }
        }
    }

    @Override
    public boolean isDead() {
        return canMove && position.distance(world.getHero().getPosition()) < size;
    }
}