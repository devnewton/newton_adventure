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

/**
 * This class will, given an intersection pair (an ingoing and outgoing 
 * intersection), calculate the penetration depth. This is the minimum distance
 * that A and B need to be separated to get rid of any overlap.
 * 
 * <p>The penetration depth or separation is calculated by running a sweepline
 * between the two points of an intersection pair. We keep track of the upper
 * bound defined by edges of polygon A and the lower bound defined by B.
 * The maximum distance between these bounds is the value we are searching for.
 * </p>
 * 
 * <pre>
 *        -<----
 *       |B     |
 *       |      | 
 *    out|      |in
 *  -----+--.---+-------
 * |A    |  !  /        |
 * |      \ ! /         |
 * |       \!/          |
 * |        .           |
 *  ->------------------
 *  </pre>
 * 
 * <p>The sweepline always runs from the ingoing to the outgoing
 * intersection. Usually the normal is perpendicular to the sweepline.</p>
 * 
 * <p>We cannot always use the whole intersection area. Take a look at the 
 * following example. If we would allow the vertex marked with to be included
 * in the sweep, the penetration depth would be far too big. Therefore we
 * 'cut off' the intersection area with two lines through the intersection
 * points perpendicular to the sweep direction. Unfortunately this will break
 * the algorithm for collision normals other than those perpendicular to the
 * sweepline (it's should still be usable). The lines are called the borders
 * of the intersection area.
 * </p>
 * 
 * <pre>
 *   +--/---/---------------------*                                                             
 * +-|-/-+ /                    A |                                            
 *  \|/ B|/                       |                                          
 *   x   /                        |                                            
 *  /|\ /|                        |                                            
 *   +-x--------------------------+                                         
 *    / \|  
 * </p>
 * 
 * <h3>Convexity</h3>
 * <p>This algorithm should always work well for convex intersection areas.
 * When the intersection area is not convex, the resulting separation might
 * be erroneous.</p>
 * <p>Since colliding two convex polygons will always result in convex
 * intersection areas, they should be used if possible. Colliding non-convex
 * polygons seems to work pretty well in practice too, but more testing is
 * needed.</p>
 *  
 * @author Gideon Smeding
 *
 */
public class PenetrationSweep {
	
	/** The collision normal onto which the penetration depth is projected */
	private Vector2f normal;
	/** The direction of our sweep, pointing from the ingoing intersection to
	 * the outgoing intersection */
	private Vector2f sweepDir;
	/** The projection of the ingoing intersection onto the sweepDir, defines
	 * a border of the intersecting area. */
	private float startDist;
	/** The projection of the outgoing intersection onto the sweepDir, defines
	 * a border of the intersecting area. */
	private float endDist;
	
 	/**
 	 * Constructs a Penetration Sweep object, with all its attributes set.
 	 * This constructor is public only for testing purposes. The static method
 	 * {@link PenetrationSweep#getPenetrationDepth(Intersection, Intersection, Vector2f, Vector2f[], Vector2f[])}
 	 * should be called to get the penetration depth. 
 	 * 
 	 * @param normal The collision normal
 	 * @param sweepDir The sweep direction
 	 * @param intersectionStart The start bound of the intersection area
 	 * @param intersectionEnd The end bound of the intersection area.
 	 */
	public PenetrationSweep(Vector2f normal, Vector2f sweepDir, Vector2f intersectionStart, Vector2f intersectionEnd) {
		super();
		this.normal = normal;
		this.sweepDir = sweepDir;
		this.startDist = intersectionStart.dot(sweepDir);
		this.endDist = intersectionEnd.dot(sweepDir);
	}


	/**
	 * Given two intersecting polygons, the intersection points and a collision
	 * normal, get the maximum penetration distance along the normal.
	 * 
	 * @param in The ingoing intersection
	 * @param out The outgoing intersection
	 * @param normal The collision normal
	 * @param vertsA The vertices of polygon A
	 * @param vertsB The vertices of polygon B
	 * @return the maximum penetration depth along the given normal
	 */
	public static float getPenetrationDepth(Intersection in, Intersection out, Vector2f normal, Vector2f[] vertsA, Vector2f[] vertsB) {
		Vector2f sweepdir = new Vector2f(out.position);
		sweepdir.sub(in.position);
		
		PenetrationSweep ps = new PenetrationSweep(normal, sweepdir, in.position, out.position);

		//TODO: most penetrations are very simple, similar to:
		// \               +       |        
		//  \             / \      |          
		//   +-----------x---x-----+          
		//              /     \                    
		// these should be handled separately
		

		ContourWalker walkerA = ps.new ContourWalker(vertsA, in.edgeA, out.edgeA, false);
		ContourWalker walkerB = ps.new ContourWalker(vertsB, (out.edgeB+1) % vertsB.length, (in.edgeB+1) % vertsB.length, true);

		float penetration = 0;
		float lowerBound = in.position.dot(normal);
		float upperBound = lowerBound;
			
		while ( walkerA.hasNext() || walkerB.hasNext() ) {
			// if walker a has more and the next vertex comes before B's
			// or if walker a has more but walker b hasn't, go and take a step
			if ( walkerA.hasNext() && 
					(walkerA.getNextDistance() < walkerB.getNextDistance() ||
							!walkerB.hasNext() ) ) {
				walkerA.next();
				if ( walkerA.getDistance() < ps.startDist || walkerA.getDistance() > ps.endDist )
					continue; // we don't care for vertices outside of the intersecting borders
				
				upperBound = walkerA.getPenetration();
				lowerBound = walkerB.getPenetration(walkerA.getDistance());
			} else {
				walkerB.next();
				if ( walkerB.getDistance() < ps.startDist || walkerB.getDistance() > ps.endDist )
					continue;
				
				upperBound = walkerA.getPenetration(walkerB.getDistance());
				lowerBound = walkerB.getPenetration();
			}
			
			penetration = Math.max(penetration, upperBound - lowerBound);
		}

		return penetration;
	}


	
	
	/**
	 * The contour walker walks over the edges or vertices of a polygon.
	 * The class keeps track of two values: 
	 * <ul>
	 * <li>The penetration, which is the projection of the current vertex 
	 * onto the collision normal</li>
	 * <li>The distance, which is the projection of the current vertex onto
	 * the sweep direction</li>
	 * </ul>
	 *
	 * TODO: yes this use of nested classes is strange and possibly undersirable
	 * and no, it is not a misguided attempt to save memory, it simply evolved this way
	 */
	public class ContourWalker {
		
		/** The vertices of the polygon which's contour is being followed */
		private Vector2f[] verts;
		/** The index of the vertex we are currently at */
		private int currentVert;
		/** The index of the vertex where the contour's subsection which we 
		 * walk on starts */
		private int firstVert;
		/** The index of the vertex where the contour's subsection which we 
		 * walk on ends */
		private int lastVert;
		/** True if we are walking backwards, from lastVert to firstVert.
		 * False if we are walking forwards, from firstVert to lastVert. */
		private boolean isBackwards;
		
		/** The distance of the current vertex, which is the projection of the current vertex 
		 * onto the collision normal */
		private float distance;
		/** The distance of the next vertex */
		private float nextDistance;
		/** The penetration of the current vertex, which is the projection of the current vertex onto
		 * the sweep direction */
		private float penetration;
		/** The slope of the current edge with wich the penetration increases.
		 * The current edge is defined by the line from the current vertex to the next. */
		private float penetrationDelta;
		
		/**
		 * Construct a contourwalker.
		 * 
		 * @param verts The vertices of the polygon which's contour is being followed
		 * @param firstVert The index of the vertex where the contour's subsection which we 
		 * walk on starts
		 * @param lastVert The index of the vertex where the contour's subsection which we 
		 * walk on ends
		 * @param isBackwards True iff we're walking backwards over the contour
		 */
		public ContourWalker(Vector2f[] verts, int firstVert, int lastVert, boolean isBackwards) {
			if ( firstVert < 0 || lastVert < 0 )
				throw new IllegalArgumentException("Vertex numbers cannot be negative.");
			
			if ( firstVert > verts.length || lastVert > verts.length )
				throw new IllegalArgumentException("The given vertex array doesn't include the first or the last vertex.");
			
			this.isBackwards = isBackwards;
			this.verts = verts;
			this.firstVert = firstVert;
			this.lastVert = lastVert;
			this.currentVert = isBackwards ? lastVert : firstVert;
			
			this.distance = verts[currentVert].dot(sweepDir);
			this.penetration = verts[currentVert].dot(normal);
			calculateNextValues();
		}
		
		/**
		 * Get the distance of the current vertex
		 * @return the distance of the current vertex
		 */
		public float getDistance() {
			return distance;
		}
		
		/**
		 * Get the distance of the next vertex which can be a point on one of
		 * the borders or a vertex on the polygon's contour.
		 * 
		 * @return The next vertex's distance
		 */
		public float getNextDistance() {
			if ( distance < startDist )
				return Math.min(nextDistance, startDist);
			if ( distance < endDist )
				return Math.min(nextDistance, endDist);
			
			return nextDistance;
		}
		
		/**
		 * Get the penetration of the current vertex.
		 * @return the penetration of the current vertex
		 */
		public float getPenetration() {
			return penetration;
		}
		
		/**
		 * Get the penetration of a point on the current edge at the supplied
		 * distance.
		 * 
		 * @param distance The distance at which we want the penetration on the current edge
		 * @return The penetration at the supplied distance
		 */
		public float getPenetration(float distance) {
			return penetration + penetrationDelta * (distance - this.distance);
		}
		
		/**
		 * Let this walker take a step to the next vertex, which is either the
		 * next vertex in the contour or a point on the current edge that crosses
		 * one of the sweep area borders. 
		 */
		public void next() {
			if ( !hasNext() )
				return;
			
			// if the edge crosses the sweep area border, set our position on the border
			if ( distance < startDist && nextDistance > startDist ) {
				this.penetration = getPenetration(startDist);
				this.distance = startDist;
				return;
			}
			
			if  ( distance < endDist && nextDistance > endDist ) {
				this.penetration = getPenetration(endDist);
				this.distance = endDist;
				return;
			}
			
			if ( isBackwards ) {
				currentVert = (currentVert - 1 + verts.length) % verts.length;
			} else {
				currentVert = (currentVert + 1) % verts.length;
			}
			
			distance = verts[currentVert].dot(sweepDir);
			penetration = verts[currentVert].dot(normal);
			calculateNextValues();
		}
		
		/**
		 * Take a look at the next vertex and sets nextDistance and 
		 * penetrationDelta.
		 */
		private void calculateNextValues() {
			int nextVert = isBackwards ? currentVert - 1 : currentVert + 1;
			nextVert = (nextVert + verts.length) % verts.length;
			
			nextDistance = verts[nextVert].dot(sweepDir);
			
			penetrationDelta = verts[nextVert].dot(normal) - penetration;
			if ( nextDistance == distance ) {
				// the next vertex is straight up, since we're searching
				// for the maximum anyway, it's safe to set our penetration
				// to the next vertex's penetration.
				penetration += penetrationDelta;
				penetrationDelta = 0;
			} else {
				penetrationDelta /= nextDistance - distance;
			}
		}
		
		/**
		 * Check if there are still vertices to walk to.
		 * @return True iff a call to hasNext would succeed.
		 */
		public boolean hasNext() {
			// if the current edge crosses the boundaries defined by
			// start and end edge, we will definately have a next
			// vertex to report.
			if ( distance < startDist && nextDistance > startDist )
				return true;
			
			if  ( distance < endDist && nextDistance > endDist )
				return true;
			
			// first make x the distance from the 'start'
			// where the start depends on whether we're going backwards
			int x = isBackwards ? lastVert - currentVert : currentVert - firstVert;
			x = (x + verts.length) % verts.length;
				
			// now subtract the number of verts between the first and last vertex
			x = (lastVert - firstVert + verts.length) % verts.length - x;
			
			return x > 0; 
		}
		
		/**
		 * Reverse the direction of this walker.
		 */
		public void reverse() {
			isBackwards = !isBackwards;
			
			calculateNextValues();
		}
	}

}
