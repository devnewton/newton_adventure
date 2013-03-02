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
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;

/**
 * A collider for boxes hitting circles. Box = bodyA, Circle = bodyB
 * 
 * The create() method is used as a factor although since this collider
 * is currently stateless a single instance is returned.
 * 
 * @author Kevin Glass
 */
public strictfp class BoxCircleCollider implements Collider {	
	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body boxBody, Body circleBody) {
		float x1 = boxBody.getPosition().getX();
		float y1 = boxBody.getPosition().getY();
		float x2 = circleBody.getPosition().getX();
		float y2 = circleBody.getPosition().getY();
		
		boolean touches = boxBody.getShape().getBounds().touches(x1,y1,circleBody.getShape().getBounds(),x2,y2);
		if (!touches) {
			return 0;
		}
		
		Box box = (Box) boxBody.getShape();
		Circle circle = (Circle) circleBody.getShape();
		
		Vector2f[] pts = box.getPoints(boxBody.getPosition(), boxBody.getRotation());
		Line[] lines = new Line[4];
		lines[0] = new Line(pts[0],pts[1]);
		lines[1] = new Line(pts[1],pts[2]);
		lines[2] = new Line(pts[2],pts[3]);
		lines[3] = new Line(pts[3],pts[0]);
		
		float r2 = circle.getRadius() * circle.getRadius();
		int closest = -1;
		float closestDistance = Float.MAX_VALUE;
		
		for (int i=0;i<4;i++) {
			float dis = lines[i].distanceSquared(circleBody.getPosition());
			if (dis < r2) {
				if (closestDistance > dis) {
					closestDistance = dis;
					closest = i;
				}
			}
		}
		
		if (closest > -1) {
			float dis = (float) Math.sqrt(closestDistance);
			contacts[0].setSeparation(dis - circle.getRadius());
			
			// this should really be where the edge and the line
			// between the two elements cross?
			Vector2f contactPoint = new Vector2f();
			lines[closest].getClosestPoint(circleBody.getPosition(), contactPoint);
			
			Vector2f normal = MathUtil.sub(circleBody.getPosition(), contactPoint);
			normal.normalise();
			contacts[0].setNormal(normal);
			contacts[0].setPosition(contactPoint);
			contacts[0].setFeature(new FeaturePair());
			
			return 1;
		}
		
		return 0;
	}
	
}
