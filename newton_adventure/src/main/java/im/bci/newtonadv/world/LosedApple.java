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
import im.bci.newtonadv.platform.lwjgl.Texture;
import im.bci.newtonadv.game.Entity;
import im.bci.newtonadv.game.FrameTimeInfos;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 *
 * @author devnewton
 */
public strictfp class LosedApple implements Entity {

    float size = Apple.size / 2.0f;
    private World world;
    private Texture texture;
    private Vector2f position;
    private Vector2f direction;
    private boolean isDead = false;
    private long deadTime = -1;
    private static final long livingDuration = 1000000000L;
    private float alpha = 1.0f;

    LosedApple(World world, ROVector2f position) {
        this.world = world;
        this.texture = world.getAppleIconTexture();
        this.position = new Vector2f(position);
        this.direction = net.phys2d.math.MathUtil.mul(new Matrix2f((float) (Math.random() * Math.PI * 2.0)), new Vector2f(1, 0));
    }

    @Override
    public void draw() {
        world.getView().drawLosedApple(this, world, texture, alpha);
    }

    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
        if (deadTime < 0) {
            deadTime = frameTimeInfos.currentTime + livingDuration;
        } else if (frameTimeInfos.currentTime > deadTime) {
            isDead = true;
        }

        position.add(direction);
        alpha = (deadTime - frameTimeInfos.currentTime) / (float) livingDuration;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getSize() {
        return size;
    }
}
