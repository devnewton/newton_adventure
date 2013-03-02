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
 * A collider for circles hitting boxes, Circle = BodyA, Box = BodyB
 * 
 * The create() method is used as a factory incase this class should
 * ever become stateful.
 * 
 * @author Kevin Glass
 */
public strictfp class CircleBoxCollider extends BoxCircleCollider {
	/** The single instance of this collider to exist */
	private static CircleBoxCollider single = new CircleBoxCollider();
	
	/**
	 * Get an instance of this collider
	 * 
	 * @return The instance of this collider
	 */
	public static CircleBoxCollider createCircleBoxCollider() {
		return single;
	}

	/**
	 * Prevent construction
	 */
	private CircleBoxCollider() {
	}
	
	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body circleBody, Body boxBody) {
		int count = super.collide(contacts, boxBody, circleBody);
		
		// reverse the collision results by inverting normals
		// and projecting the results onto the circle
		for (int i=0;i<count;i++) {
			Vector2f vec = MathUtil.scale(contacts[i].getNormal(),-1);
			contacts[i].setNormal(vec);
			
			Vector2f pt = MathUtil.sub(contacts[i].getPosition(), circleBody.getPosition());
			pt.normalise();
			pt.scale(((Circle) circleBody.getShape()).getRadius());
			pt.add(circleBody.getPosition());
			contacts[i].setPosition(pt);
		}
		
		return count;
	}
}
