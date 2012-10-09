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

import net.phys2d.raw.Body;

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.game.AbstractDrawableBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Shape;

/**
 *
 * @author devnewton
 */
public strictfp class Key extends AbstractDrawableBody implements Updatable {

    static final float size = 2.0f * World.distanceUnit;
    private World world;
    private Animation.Play play;

    Key(World world) {
        super(new Circle(size / 2.0f), 1.0f);
        this.world = world;
        setRotatable(false);
    }

    Key(World world, Shape shape) {
        super(shape, 1.0f);
        this.world = world;
        setRotatable(false);
	}

	@Override
    public void draw() {
        world.getView().drawKey(this, play.getCurrentFrame(), world);
    }

    void setTexture(AnimationCollection texture) {
        play = texture.getFirst().start();
    }

    @Override
    public strictfp void collided(Body body) {
        if (body instanceof Door) {
        	Door door = (Door) body;
        	if(door.isOpenableWithKey())
        		use();
        } else if(body instanceof KeyLock) {
        	KeyLock keyLock = (KeyLock) body;
        	if(!keyLock.isOpened()) {
        		keyLock.open();
                use();
        	}
        }
    }

	private void use() {
		world.remove(this);
		world.addTopLevelEntities(new UsedKey(world, play.getCurrentFrame(), getPosition()));
	}

	public Animation.Play getAnimation() {
		return play;
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		play.update(frameTimeInfos.elapsedTime / 1000000);		
	}
}