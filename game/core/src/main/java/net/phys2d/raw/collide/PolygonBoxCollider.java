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
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Polygon;

/**
 * Collide a Convex Polygon with a Box.
 * 
 * @author Gideon Smeding
 *
 */
public class PolygonBoxCollider extends PolygonPolygonCollider {

	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		Polygon poly = (Polygon) bodyA.getShape();
		Box box = (Box) bodyB.getShape();
		
		// TODO: this can be optimized using matrix multiplications and moving only one shape
		// specifically the box, because it has fewer vertices.
		Vector2f[] vertsA = poly.getVertices(bodyA.getPosition(), bodyA.getRotation());
		Vector2f[] vertsB = box.getPoints(bodyB.getPosition(), bodyB.getRotation());
		
		// TODO: use a sweepline that has the smallest projection of the box
		// now we use just an arbitrary one
		Vector2f sweepline = new Vector2f(vertsB[1]);
		sweepline.sub(vertsB[2]);
		
		EdgeSweep sweep = new EdgeSweep(sweepline);
		
		sweep.addVerticesToSweep(true, vertsA);
		sweep.addVerticesToSweep(false, vertsB);

		int[][] collEdgeCands = sweep.getOverlappingEdges();
//		FeaturePair[] featurePairs = getFeaturePairs(contacts.length, vertsA, vertsB, collEdgeCands);
//		return populateContacts(contacts, vertsA, vertsB, featurePairs);
		
		Intersection[][] intersections = getIntersectionPairs(vertsA, vertsB, collEdgeCands);		
		return populateContacts(contacts, vertsA, vertsB, intersections);
	}

}
