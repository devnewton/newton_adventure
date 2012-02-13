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

import net.phys2d.math.Vector2f;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;

/**
 *
 * @author bci
 */
public strictfp class Cannon extends Platform implements Updatable {

    private static final long durationBetweenFireballGeneration = 2000000000;
    private long nextFireballTime = 0;
    private static final float shotForce = 10000.0f;

    public enum Orientation {

        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private Orientation orientation;

    public Cannon(World world, Orientation orientation) {
        super(world);
        this.orientation = orientation;
        setEnabled(false);
    }

    @Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        if( frameTimeInfos.currentTime > nextFireballTime) {
            throwFireball();
            nextFireballTime = frameTimeInfos.currentTime + durationBetweenFireballGeneration;
        }
    }
    
    private void throwFireball() {
        Vector2f pos = new Vector2f(getPosition());
        //pos.add( new Vector2f( size, size));
        FireBall fireBall = new FireBall(world);
        fireBall.setPosition(pos.x, pos.y);
        fireBall.setTexture(world.getFireBallTexture());
        world.add(fireBall);
        switch(orientation) {
            case UP:
                fireBall.addForce(new Vector2f(0, shotForce));
                break;
            case DOWN:
                fireBall.addForce(new Vector2f(0, -shotForce));
                break;
            case LEFT:
                fireBall.addForce(new Vector2f(-shotForce, 0));
                break;
            case RIGHT:
                fireBall.addForce(new Vector2f(shotForce, 0));
                break;
        }
        
    }
}
