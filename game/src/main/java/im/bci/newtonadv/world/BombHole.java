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

import im.bci.newtonadv.anim.AnimationCollection;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Shape;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;

/**
 * 
 * @author bci
 */
public strictfp class BombHole extends Platform implements Updatable {
    
    private boolean canThrowBomb = true;
    private AnimationCollection bombTexture;
        private AnimationCollection fireBallTexture;
    private AnimationCollection explosionTexture;
    
    public BombHole(World world, float w, float h) {
        super(world, w, h);
        setEnabled(false);
    }
    
    public void setBombTexture(AnimationCollection bombTexture) {
        this.bombTexture = bombTexture;
    }
    
    
    public void setFireBallTexture(AnimationCollection fireBallTexture) {
        this.fireBallTexture = fireBallTexture;
    }

    public void setExplosionTexture(AnimationCollection explositionTexture) {
        this.explosionTexture = explositionTexture;
    }
    
    public BombHole(World world, Shape shape) {
        super(world, shape);
        setEnabled(false);
    }
    
    @Override
    public void update(FrameTimeInfos frameTimeInfos) {
        if (canThrowBomb) {
            canThrowBomb = false;
            throwBomb();
        }
    }
    
    private void throwBomb() {
        Vector2f pos = new Vector2f(getPosition());
        Bomb bomb = new Bomb(world);
        bomb.setTexture(bombTexture);
        bomb.setFireBallTexture(fireBallTexture);
        bomb.setExplosionTexture(explosionTexture);
        bomb.setParentHole(this);
        bomb.setPosition(pos.x, pos.y);
        world.add(bomb);
    }
    
    public void bombExploded() {
        canThrowBomb = true;
    }
}
