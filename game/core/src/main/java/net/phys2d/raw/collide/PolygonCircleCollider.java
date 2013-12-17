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
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.shapes.Line;

/**
 * Collide a circle with a convex polygon
 * 
 * @author Gideon Smeding
 *
 */
public class PolygonCircleCollider extends PolygonPolygonCollider {
	
	/**
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		Polygon polyA = (Polygon) bodyA.getShape();
		Circle circle = (Circle) bodyB.getShape();
		
		// TODO: this can be optimized using matrix multiplications and moving only the circle
		Vector2f[] vertsA = polyA.getVertices(bodyA.getPosition(), bodyA.getRotation());
		
		Vector2f centroidA = new Vector2f(polyA.getCentroid());
		centroidA.add(bodyA.getPosition());

		
		int[][] collPairs = getCollisionCandidates(vertsA, centroidA, circle.getRadius(), bodyB.getPosition());

		int noContacts = 0;
		for ( int i = 0; i < collPairs.length; i++ ) {
			if ( noContacts >= contacts.length )
				return contacts.length;
			
			Vector2f lineStartA = vertsA[collPairs[i][0]];
			Vector2f lineEndA = vertsA[(collPairs[i][0]+1) % vertsA.length ];
			Line line = new Line(lineStartA, lineEndA);
						
			float dis2 = line.distanceSquared(bodyB.getPosition());
			float r2 = circle.getRadius() * circle.getRadius();

			if ( dis2 < r2 ) {
				Vector2f pt = new Vector2f();
				
				line.getClosestPoint(bodyB.getPosition(), pt);
				Vector2f normal = new Vector2f(bodyB.getPosition());
				normal.sub(pt);
				float sep = circle.getRadius() - normal.length();
				normal.normalise();
				
				contacts[noContacts].setSeparation(-sep);
				contacts[noContacts].setPosition(pt);
				contacts[noContacts].setNormal(normal);
				contacts[noContacts].setFeature(new FeaturePair());
				noContacts++;
			}
		}
		
		return noContacts;
	}
	
	/**
	 * Get the edges from a list of vertices that can collide with the given circle.
	 * This uses a sweepline algorithm which is only efficient if some assumptions
	 * are indeed true. See CPolygonCPolygonCollider for more information.
	 *  
	 * @param vertsA The vertices of a polygon that is collided with a circle
	 * @param centroid The center of the polygon
	 * @param radius The radius of the circle
	 * @param circlePos The position (center) of the circle
	 * @return The list of edges that can collide with the circle
	 */
	protected int[][] getCollisionCandidates(Vector2f[] vertsA, ROVector2f centroid, float radius, ROVector2f circlePos) {
		Vector2f sweepDir = new Vector2f(centroid);
		sweepDir.sub(circlePos);
		sweepDir.normalise(); //TODO: this normalization might not be necessary
		
		EdgeSweep sweep = new EdgeSweep(sweepDir);//vertsA[0], true, true, dist);
		
		sweep.addVerticesToSweep(true, vertsA);
		
		float circProj = circlePos.dot(sweepDir);
		
		sweep.insert(0, false, -radius + circProj);
		sweep.insert(0, false, radius + circProj);

		return sweep.getOverlappingEdges();
	}

}
