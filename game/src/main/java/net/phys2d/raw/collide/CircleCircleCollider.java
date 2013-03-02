/*
 * Phys2D - a 2D physics engine based on the work of Erin Catto.
 * 
 * This source is provided under the terms of the BSD License.
 * 
 * Copyright (c) 2006, Phys2D
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 *  * Redistributions of source code must retain the above 
 *    copyright notice, this list of conditions and the 
 *    following disclaimer.
 *  * Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution.
 *  * Neither the name of the Phys2D/New Dawn Software nor the names of 
 *    its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 */
package net.phys2d.raw.collide;

import net.phys2d.math.MathUtil;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.shapes.Circle;

/**
 * A collider for circle 2 circle collisions
 * 
 * The create() method is used as a factory just in case this 
 * class becomes stateful eventually.
 * 
 * @author Kevin Glass
 */
public strictfp class CircleCircleCollider implements Collider {	
	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		float x1 = bodyA.getPosition().getX();
		float y1 = bodyA.getPosition().getY();
		float x2 = bodyB.getPosition().getX();
		float y2 = bodyB.getPosition().getY();
		
		boolean touches = bodyA.getShape().getBounds().touches(x1,y1,bodyB.getShape().getBounds(),x2,y2);
		if (!touches) {
			return 0;
		}
		
		Circle circleA = (Circle) bodyA.getShape();
		Circle circleB = (Circle) bodyB.getShape();
		
		touches = circleA.touches(x1,y1,circleB,x2,y2);
		if (!touches) {
			return 0;
		}
		
		Vector2f normal = MathUtil.sub(bodyB.getPosition(),bodyA.getPosition());
		float sep = (circleA.getRadius() + circleB.getRadius()) - normal.length();

		normal.normalise();
		Vector2f pt = MathUtil.scale(normal, circleA.getRadius());
		pt.add(bodyA.getPosition());

		contacts[0].setSeparation(-sep);
		contacts[0].setPosition(pt);
		contacts[0].setNormal(normal);
		
		FeaturePair fp = new FeaturePair();
		contacts[0].setFeature(fp);

		return 1;
	}
}
