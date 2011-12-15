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
package im.bci.newtonadv.game.special.occasion;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author devnewton
 */
public class SnowLayer implements SpecialOccasionLayer {

    static final float ortho2DBaseSize = 100.0f;
    static final float ortho2DLeft = -ortho2DBaseSize;
    static final float ortho2DBottom = -ortho2DBaseSize;
    static final float ortho2DRight = ortho2DBaseSize;
    static final float ortho2DTop = ortho2DBaseSize;
    static final int updateRate = 4;
    int lastUpdate;
    float aspectRatio = 1.0f;

    private class SnowFlake {

        float x = aspectRatio * ((float) Math.random() * (ortho2DRight - ortho2DLeft) + ortho2DLeft);
        float y = ortho2DTop;
    };
    final SnowFlake[] flakes = new SnowFlake[1000];

    @Override
    public void update() {
        ++lastUpdate;
        if (lastUpdate >= updateRate) {
            lastUpdate = 0;
            for (int i = 0; i < flakes.length; ++i) {
                if (null == flakes[i]) {
                    flakes[i] = new SnowFlake();
                    break;
                }
            }
        }
        for (int i = 0; i < flakes.length; ++i) {
            if (null != flakes[i]) {
                flakes[i].y -= 0.1f;
                if (flakes[i].y < ortho2DBottom) {
                    flakes[i] = null;
                }
            }
        }
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();

        aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        GLU.gluOrtho2D(ortho2DLeft * aspectRatio, ortho2DRight * aspectRatio, ortho2DBottom, ortho2DTop);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(GL11.GL_POINTS);
        for (int i = 0; i < flakes.length; ++i) {
            if (null != flakes[i]) {
                GL11.glVertex2f(flakes[i].x, flakes[i].y);
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
