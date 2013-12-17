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

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;

/**
 * Collides two lines with oneanother.
 * 
 * @author Gideon Smeding
 *
 */
public class LineLineCollider implements Collider {

	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		// TODO: function disabled until we can remember on what side of A,
		// B used to be, which is crucial to determine a proper collision normal
		return 0;
//		Line lineA = (Line) bodyA.getShape();
//		Line lineB = (Line) bodyB.getShape();
//		
//		Vector2f[] vertsA = lineA.getVertices(bodyA.getPosition(), bodyA.getRotation());
//		Vector2f[] vertsB = lineB.getVertices(bodyB.getPosition(), bodyB.getRotation());
//		
//		Vector2f startA = vertsA[0];
//		Vector2f endA = vertsA[1];
//		Vector2f startB = vertsB[0];
//		Vector2f endB = vertsB[1];
//		
//		//TODO: reuse mathutil.intersect?
//		float d = (endB.y - startB.y) * (endA.x - startA.x) - (endB.x - startB.x) * (endA.y - startA.y);
//		
//		if ( d == 0 ) // parallel lines
//			return 0;
//		
//		float uA = (endB.x - startB.x) * (startA.y - startB.y) - (endB.y - startB.y) * (startA.x - startB.x);
//		uA /= d;
//		float uB = (endA.x - startA.x) * (startA.y - startB.y) - (endA.y - startA.y) * (startA.x - startB.x);
//		uB /= d;
//		
//		if ( uA < 0 || uA > 1 || uB < 0 || uB > 1 ) 
//			return 0; // intersection point isn't between the start and endpoints
//		
//		// there must be a collision, let's determine our contact information
//		// we're searching for a contact with the smallest penetration depth
//		Vector2f[][] closestPoints = {
//				{startB, getClosestPoint(startA, endA, startB)},
//				{endB, getClosestPoint(startA, endA, endB)},
//				{startA, getClosestPoint(startB, endB, startA)},
//				{endA, getClosestPoint(startB, endB, endA)}
//		};
//		
//		float distSquared = Float.MAX_VALUE;
//		Vector2f position = null;
//		Vector2f normal = new Vector2f();
//		
//		for ( int i = 0; i < 4; i++ ) {
//			Vector2f l;
//			if ( i < 2 ) {
//				l = closestPoints[i][1];
//				l.sub(closestPoints[i][0]);
//			} else {
//				l = closestPoints[i][0];
//				l.sub(closestPoints[i][1]);
//			}
//			
//			float newDistSquared = l.lengthSquared();
//			if ( newDistSquared < distSquared ) {
//				distSquared = newDistSquared;
//				position = closestPoints[i][0];
//				normal.set(l);
//			}
//		}
//		
//		normal.normalise();
//		contacts[0].setNormal(normal);
//		contacts[0].setPosition(position);
//		if ( Math.sqrt(distSquared) > 10f )
//			System.out.println(Math.sqrt(distSquared));
//		contacts[0].setSeparation((float) -Math.sqrt(distSquared));
//		
//		return 1;
	}
	
	/**
	 * Gets the closest point to a given point on the indefinately extended line.
	 * TODO: move this somewhere in math package
	 * 
	 * @param startA Starting point of the line
	 * @param endA End point of the line
	 * @param point The point to get a closes point on the line for
	 * @return the closest point on the line or null if the lines are parallel
	 */
	public static Vector2f getClosestPoint(Vector2f startA, Vector2f endA, Vector2f point) {
		Vector2f startB = point;
		Vector2f endB = new Vector2f(endA);
		endB.sub(startA);
		endB.set(endB.y, -endB.x);

		float d = endB.y * (endA.x - startA.x);
		d -= endB.x * (endA.y - startA.y);
		
		if ( d == 0 )
			return null;
		
		float uA = endB.x * (startA.y - startB.getY());
		uA -= endB.y * (startA.x - startB.getX());
		uA /= d;
		
		return new Vector2f(
			startA.x + uA * (endA.x - startA.x),
			startA.y + uA * (endA.y - startA.y));
	}

}
