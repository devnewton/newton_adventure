/*
 * Copyright (c) 2009-2010 devnewton <devnewton@tuxfamily.org>
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
 * * Neither the name of 'devnewton <devnewton@tuxfamily.org>' nor the names of
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
package org.tuxfamily.newtonadv.world;

import net.phys2d.raw.Body;
import org.lwjgl.opengl.GL11;
import org.tuxfamily.newtonadv.game.FrameTimeInfos;
import org.tuxfamily.newtonadv.game.Updatable;

public class Cloud extends Platform implements Updatable {

    private boolean touched = false;
    private long disappearEndTime = -1;
    private float alpha = 1.0f;
    private static final long disappearDuration = 1000000000L;
    private World world;

    public Cloud(World world) {
        this.world = world;
    }
    
    @Override
    public strictfp void collided(Body body) {
        if( body instanceof Hero )
            touched = true;
    }

    @Override
    public void draw() {
         GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        GL11.glEnable (GL11.GL_BLEND); 
        GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        super.draw();
        GL11.glPopAttrib();
        
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
       if( touched) {
           if( disappearEndTime < 0 )
               disappearEndTime = frameTimeInfos.currentTime + disappearDuration;
           else if( frameTimeInfos.currentTime  < disappearEndTime ) {
               alpha =  (disappearEndTime -  frameTimeInfos.currentTime) / (float)disappearDuration;
           } else {
               world.remove(this);
           }
       }
    }
    
}