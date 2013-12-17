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
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.shapes.Line;

/**
 * Collider for a Line and a Convex Polygon.
 * 
 * @author Gideon Smeding
 *
 */
public class LinePolygonCollider  extends PolygonPolygonCollider {


	/** 
	 * @see net.phys2d.raw.collide.Collider#collide(net.phys2d.raw.Contact[], net.phys2d.raw.Body, net.phys2d.raw.Body)
	 */
	public int collide(Contact[] contacts, Body bodyA, Body bodyB) {
		Line line = (Line) bodyA.getShape();
		Polygon poly = (Polygon) bodyB.getShape();
		
		// TODO: this can be optimized using matrix multiplications and moving only one shape
		// specifically the line, because it has only two vertices
		Vector2f[] vertsA = line.getVertices(bodyA.getPosition(), bodyA.getRotation());
		Vector2f[] vertsB = poly.getVertices(bodyB.getPosition(), bodyB.getRotation());

		Vector2f pos = poly.getCentroid(bodyB.getPosition(), bodyB.getRotation());
		
		// using the z axis of a 3d cross product we determine on what side B is
		boolean isLeftOf = 0 > (pos.x - vertsA[0].x) * (vertsA[1].y - vertsA[0].y) - (vertsA[1].x - vertsA[0].x) * (pos.y - vertsA[0].y);
		
		// to get the proper intersection pairs we make sure 
		// the line's normal is pointing towards the polygon
		// TODO: verify that it's not actually pointing in the opposite direction
		if ( isLeftOf ) {
			Vector2f tmp = vertsA[0];
			vertsA[0] = vertsA[1];
			vertsA[1] = tmp;
		}
		
		// we use the line's normal for our sweepline projection
		Vector2f normal = new Vector2f(vertsA[1]);
		normal.sub(vertsA[0]);
		normal.set(normal.y, -normal.x);
		EdgeSweep sweep = new EdgeSweep(normal);
		sweep.insert(0, true, vertsA[0].dot(normal));
		sweep.insert(0, true, vertsA[1].dot(normal));
		sweep.addVerticesToSweep(false, vertsB);
		int[][] collEdgeCands = sweep.getOverlappingEdges(); 
		
		IntersectionGatherer intGath = new IntersectionGatherer(vertsA, vertsB);
		for ( int i = 0; i < collEdgeCands.length; i++ )
			intGath.intersect(collEdgeCands[i][0], collEdgeCands[i][1]);
		
		Intersection[] intersections = intGath.getIntersections();
		
		return populateContacts(contacts, vertsA, vertsB, intersections);
	}
		
	/**
	 * Given a list of intersections, calculate the collision information and
	 * set the contacts with that information.
	 * 
	 * @param contacts The array of contacts to fill
	 * @param vertsA The vertices of polygon A
	 * @param vertsB The vertices of polygon B
	 * @param intersections The array of intersection as returned by 
	 * {@link IntersectionGatherer#getIntersections()}
	 * @return The number of contacts that have been set in the contact array
	 */
	public int populateContacts(Contact[] contacts, Vector2f[] vertsA, Vector2f[] vertsB, Intersection[] intersections) {	
		if ( intersections.length == 0 )
			return 0;
		
		int noContacts = 0;
		
		// is the first intersection outgoing?
		if ( !intersections[0].isIngoing ) {
			setLineEndContact(contacts[noContacts], intersections[intersections.length-1], vertsA, vertsB);
			
			if (contacts[noContacts].getSeparation() < -10 )
				System.out.println("first " + contacts[noContacts].getSeparation());
			
			noContacts++;
		}

		
		int i = noContacts;
		while ( i < intersections.length-1 ) {
			if ( noContacts > contacts.length-2 )
				return noContacts;
			
			// check if we have an intersection pair
			if ( !intersections[i].isIngoing || intersections[i+1].isIngoing ) {
				setContact(contacts[noContacts], intersections[i], vertsA, vertsB);
				i++;
				noContacts++;
				continue;
			}

			setContactPair(
					contacts[noContacts],
					contacts[noContacts+1],
					intersections[i],
					intersections[i+1],
					vertsA, vertsB);
			
			if (contacts[noContacts].getSeparation() < -10 )
				System.out.println("m " + contacts[noContacts].getSeparation());
			
			noContacts += 2;
			i += 2;
		}
		
		// is there still an ingoing intersection left?
		if ( i < intersections.length && 
				intersections[intersections.length-1].isIngoing &&
				noContacts < contacts.length) {
			setLineEndContact(contacts[noContacts], intersections[intersections.length-1], vertsA, vertsB);
			
			if (contacts[noContacts].getSeparation() < -10 )
				System.out.println(" last " +contacts[noContacts].getSeparation());
			noContacts++;
		}

		
		return noContacts;
	}
	
	/**
	 * Set a contact for an intersection where the colliding line's start- or endpoint
	 * is contained in the colliding polygon.
	 * 
	 * TODO: The current implementation doesn't work properly: because lines are very
	 * thin, they can slide into a polygon sideways which gives a very deep penetration
	 *  |
	 *  |->
	 *  |      +-----+
	 *  |->    |     |
	 *  |      |     |
	 *         |     |
	 *         +-----+
	 *         
	 * A possible solution would be to use the velocity of the line relative to the 
	 * polygon to construct a collision normal and penetration depth.
	 * Another possibility is to use the line's normals (both directions) and calculate
	 * proper intersection distances for them.
	 * If one has multiple normals/penetration depths to choose from, the one with the
	 * minimum penetration depth will probably be the best bet.
	 * 
	 * @param contact The contact to set
	 * @param intersection The intersection where the line enters or exits the polygon
	 * @param vertsA The line's vertices
	 * @param vertsB The polygon's vertices
	 */
	public void setLineEndContact(Contact contact, Intersection intersection, Vector2f[] vertsA, Vector2f[] vertsB) {
		Vector2f separation = new Vector2f(intersection.position);
		if ( intersection.isIngoing )
			separation.sub(vertsA[1]);
		else
			separation.sub(vertsA[0]);
		
		float depthA = 0;//separation.length();
		
		contact.setSeparation(-depthA);
		contact.setNormal(MathUtil.getNormal(vertsB[(intersection.edgeB + 1) % vertsB.length], vertsB[intersection.edgeB]));
		contact.setPosition(intersection.position);
		contact.setFeature(new FeaturePair(0, 0, intersection.edgeA, intersection.edgeB));
	}
	
}
