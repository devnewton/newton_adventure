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

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;

/**
 * Collision routines betwene a circle and a line. The create method is
 * provided in case this collider becomes stateful at some point.
 * 
 * @author Kevin Glass
 */
public strictfp class LineCircleCollider implements Collider {

	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		Line line = (Line) bodyA.getShape();
		Circle circle = (Circle) bodyB.getShape();
		
		Vector2f[] vertsA = line.getVertices(bodyA.getPosition(), bodyA.getRotation());
		
		// compute intersection of the line A and a line parallel to 
		// the line A's normal passing through the origin of B
		Vector2f startA = vertsA[0];
		Vector2f endA = vertsA[1];
		ROVector2f startB = bodyB.getPosition();
		Vector2f endB = new Vector2f(endA);
		endB.sub(startA);
		endB.set(endB.y, -endB.x);
//		endB.add(startB);// TODO: inline endB into equations below, this last operation will be useless..
		
		//TODO: reuse mathutil.intersect
//		float d = (endB.y - startB.getY()) * (endA.x - startA.x);
//		d -= (endB.x - startB.getX()) * (endA.y - startA.y);
//		
//		float uA = (endB.x - startB.getX()) * (startA.y - startB.getY());
//		uA -= (endB.y - startB.getY()) * (startA.x - startB.getX());
//		uA /= d;
		float d = endB.y * (endA.x - startA.x);
		d -= endB.x * (endA.y - startA.y);
		
		float uA = endB.x * (startA.y - startB.getY());
		uA -= endB.y * (startA.x - startB.getX());
		uA /= d;
		
		Vector2f position = null;
		
		if ( uA < 0 ) { // the intersection is somewhere before startA
			position = startA;
		} else if ( uA > 1 ) { // the intersection is somewhere after endA
			position = endA;
		} else {
			position = new Vector2f(
					startA.x + uA * (endA.x - startA.x),
					startA.y + uA * (endA.y - startA.y));
		}
		
		Vector2f normal = endB; // reuse of vector object
		normal.set(startB);
		normal.sub(position);
		float distSquared = normal.lengthSquared();
		float radiusSquared = circle.getRadius() * circle.getRadius();
		
		if ( distSquared < radiusSquared ) {
			contacts[0].setPosition(position);
			contacts[0].setFeature(new FeaturePair());
			
			normal.normalise();
			contacts[0].setNormal(normal);
			
			float separation = (float) Math.sqrt(distSquared) - circle.getRadius();
			contacts[0].setSeparation(separation);
			
			return 1;
		}
		
		return 0;
	}

}
