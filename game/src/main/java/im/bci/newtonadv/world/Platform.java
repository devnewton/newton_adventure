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

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.Animation.PlayMode;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.game.AbstractDrawableStaticBody;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.util.ShapeUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Shape;

/**
 * 
 * @author devnewton
 */
public strictfp class Platform extends AbstractDrawableStaticBody implements
		Updatable {

	private AnimationCollection texture;
	protected Animation.Play play;
	protected final World world;
	protected float w;
	protected float h;
	public FloatBuffer vertices = ByteBuffer
			.allocateDirect(2 * 4 * Float.SIZE / 8)
			.order(ByteOrder.nativeOrder()).asFloatBuffer();
	public FloatBuffer texCoords = ByteBuffer
			.allocateDirect(2 * 4 * Float.SIZE / 8)
			.order(ByteOrder.nativeOrder()).asFloatBuffer();
	public AnimationFrame frame;

	Platform(World world, float w, float h) {
		super(new Box(w, h));
		setFriction(10.0f);
		addBit(World.STATIC_BODY_COLLIDE_BIT);
		this.world = world;
		this.w = w;
		this.h = h;
	}

	Platform(World world, Shape shp) {
		super(shp);
		setFriction(10.0f);
		addBit(World.STATIC_BODY_COLLIDE_BIT);
		this.world = world;
		this.w = shp.getBounds().getWidth();
		this.h = shp.getBounds().getHeight();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		
		Shape shape = getShape();
		Vector2f[] points = ShapeUtils.getVertices(shape, getPosition(), getRotation());
		int nbPoints = Math.min(points.length, vertices.capacity());
		for(int i = 0; i<nbPoints; ++i){
			Vector2f point = points[i];
			vertices.put(point.x);
			vertices.put(point.y);
		}
		vertices.flip();
	}

	public void setTexture(AnimationCollection texture) {
		this.texture = texture;
		this.play = texture.getFirst().start();
		setAnimationFrame(play.getCurrentFrame());
	}
	
	protected void changeAnimation(String animationName, PlayMode mode) {
		if(null != texture) {
			this.play = texture.getAnimationByName(animationName).start(mode);
			this.play.start(mode);
			setAnimationFrame(play.getCurrentFrame());
		}
	}

	private void setAnimationFrame(AnimationFrame currentFrame) {
		if (this.frame != currentFrame) {
			this.frame = currentFrame;
			texCoords.put(frame.getU1());
			texCoords.put(frame.getV2());
			texCoords.put(frame.getU2());
			texCoords.put(frame.getV2());
			texCoords.put(frame.getU2());
			texCoords.put(frame.getV1());
			texCoords.put(frame.getU1());
			texCoords.put(frame.getV1());
			texCoords.flip();
		}

	}

	@Override
	public void draw() {
		if (null != play) {
			setAnimationFrame(play.getCurrentFrame());
			world.getView().drawPlatform(this);
		}
	}

	@Override
	public void update(FrameTimeInfos frameTimeInfos) {
		play.update(frameTimeInfos.elapsedTime / 1000000);
	}
}
