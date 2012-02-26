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
import net.phys2d.math.ROVector2f;

import im.bci.newtonadv.game.AbstractEntity;
import im.bci.newtonadv.game.FrameTimeInfos;

public strictfp class UsedKey extends AbstractEntity {

    private World world;
    private ITexture texture;
    private ROVector2f position;
    private float size = Key.size;
    private long endTime = -1;
    private static final long waitingDuration = 300000000L;
    private boolean isDead = false;

    UsedKey(World world, ITexture texture, ROVector2f position) {
        this.world = world;
        this.texture = texture;
        this.position = position;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    @Override
    public void draw() {
        world.getView().drawUsedKey(this,texture,world);
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
        if (endTime < 0) {
            endTime = frameTimeInfos.currentTime + waitingDuration;
        } else {
            //size = Key.size * 1.1f;//* ( 0.5f + 0.5f * (endTime -  frameTimeInfos.currentTime) / (float)waitingDuration);
            size = Key.size * ( 2.0f - 1.0f * (endTime -  frameTimeInfos.currentTime) / waitingDuration);
            isDead = frameTimeInfos.currentTime > endTime;
        }
    }

    public ROVector2f getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }
}
