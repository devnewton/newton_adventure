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
package net.phys2d.raw.shapes;

import net.phys2d.math.MathUtil;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 * Class representing a convex and closed polygon as a list of vertices
 * in counterclockwise order. Convexity is maintained by a check in the
 * constructor after which the polygon becomes immutable.
 * 
 * @author Gideon Smeding
 *
 */
public class ConvexPolygon extends Polygon implements DynamicShape {
	
	/** Construct the convex polygon with a list of vertices
	 * sorted in counterclockwise order.
	 * Note that all the vector values will be copied.
	 * 
	 * Throws an exception when too few vertices are given (< 3)
	 * and when the supplied vertices are not convex.
	 * Polygons with area = 0, will be reported as non-convex too.
	 * 
	 * @param vertices Vertices sorted in counterclockwise order
	 */
	public ConvexPolygon(ROVector2f[] vertices) {	
		if ( vertices.length < 3 )
			throw new IllegalArgumentException("A polygon can not have fewer than 3 edges!");
		
		this.vertices = new Vector2f[vertices.length];
		
		for ( int i = 0; i < vertices.length; i++ ) {
			this.vertices[i] = new Vector2f(vertices[i]);
		}
		
		if ( !super.isConvex() )
			throw new IllegalArgumentException("The supplied vertices do not represent a convex polygon!");
		
		float r = computeBoundingCircleRadius();
		this.bounds = new AABox(r*2,r*2);
		this.area = computeArea();
		this.centroid = computeCentroid();
	}
	
	/**
	 * Because convexness is checked at construction
	 * we can always return true here.
	 * @see Polygon#isConvex()
	 */
	public boolean isConvex() {
		return true;
	}

	/**
	 * Test whether or not the point p is in this polygon in O(n),
	 * where n is the number of vertices in this polygon.
	 *  
	 * @param p The point to be tested for inclusion in this polygon
	 * @return true iff the p is in this polygon (not on a border)
	 */
	public boolean contains(Vector2f p) {
		// p is in the polygon if it is left of all the edges
		int l = vertices.length;
		for ( int i = 0; i < vertices.length; i++ ) {
			Vector2f x = vertices[i];
			Vector2f y = vertices[(i+1)%l];
			Vector2f z = p;
			
			// does the 3d cross product point up or down?
			if ( (z.x-x.x)*(y.y-x.y)-(y.x-x.x)*(z.y-x.y) >= 0 )
				return false;
		}
		
		return true;
	}
	
	/**
	 * Get point on this polygon's hull that is closest to p.
	 * 
	 * TODO: make this thing return a negative value when it is contained in the polygon
	 * 
	 * @param p The point to search the closest point for
	 * @return the nearest point on this vertex' hull
	 */
	public ROVector2f getNearestPoint(ROVector2f p) {
		// TODO: this can be done with a kind of binary search
		float r = Float.MAX_VALUE;
		float l;
		Vector2f v;
		int m = -1;
		
		for ( int i = 0; i < vertices.length; i++ ) {
			v = new Vector2f(vertices[i]);
			v.sub(p);
			l = v.x * v.x + v.y * v.y;
			
			if ( l < r ) {
				r = l;
				m = i;
			}
		}
		
		// the closest point could be on one of the closest point's edges
		// this happens when the angle between v[m-1]-v[m] and p-v[m] is
		// smaller than 90 degrees, same for v[m+1]-v[m]
		int length = vertices.length;
		Vector2f pm = new Vector2f(p);
		pm.sub(vertices[m]);
		Vector2f l1 = new Vector2f(vertices[(m-1+length)%length]);
		l1.sub(vertices[m]);
		Vector2f l2 = new Vector2f(vertices[(m+1)%length]);
		l2.sub(vertices[m]);
		
		Vector2f normal;
		if ( pm.dot(l1) > 0 ) {
			normal = MathUtil.getNormal(vertices[(m-1+length)%length], vertices[m]);
		} else if ( pm.dot(l2) > 0 ) {
			normal = MathUtil.getNormal(vertices[m], vertices[(m+1)%length]);
		} else {
			return vertices[m];
		}
		
		normal.scale(-pm.dot(normal));
		normal.add(p);
		return normal;
	}

	/**
	 * @see net.phys2d.raw.shapes.Shape#getSurfaceFactor()
	 */
	public float getSurfaceFactor() {
		// TODO: return the real surface factor
		return getArea();
	}


}
