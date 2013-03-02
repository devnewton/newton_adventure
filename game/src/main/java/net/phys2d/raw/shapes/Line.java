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

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

/**
 * Implemenation of a bunch of maths functions to do with lines. Note
 * that lines can't be used as dynamic shapes right now - also collision 
 * with the end of a line is undefined.
 * 
 * @author Kevin Glass
 */
public strictfp class Line extends AbstractShape implements DynamicShape {
	/** The start point of the line */
	private ROVector2f start;
	/** The end point of the line */
	private ROVector2f end;
	/** The vector between the two points */
	private Vector2f vec;
	/** The length of the line squared */
	private float lenSquared;
	
	/** Temporary storage - declared globally to reduce GC */
	private Vector2f loc = new Vector2f(0,0);
	/** Temporary storage - declared globally to reduce GC */
	private Vector2f v = new Vector2f(0,0);
	/** Temporary storage - declared globally to reduce GC */
	private Vector2f v2 = new Vector2f(0,0);
	/** Temporary storage - declared globally to reduce GC */
	private Vector2f proj = new Vector2f(0,0);

	/** Temporary storage - declared globally to reduce GC */
	private Vector2f closest = new Vector2f(0,0);
	/** Temporary storage - declared globally to reduce GC */
	private Vector2f other = new Vector2f(0,0);

	/** True if this line blocks on the outer edge */
	private boolean outerEdge = true;
	/** True if this line blocks on the inner edge */
	private boolean innerEdge = true;
	
	/**
	 * Create a new line based on the origin and a single point
	 * 
	 * @param x The end point of the line
	 * @param y The end point of the line
	 * @param inner True if this line blocks on it's inner edge
	 * @param outer True if this line blocks on it's outer edge
	 */
	public Line(float x, float y, boolean inner, boolean outer) {
		this(0,0,x,y);
		
		setBlocksInnerEdge(inner);
		setBlocksOuterEdge(outer);
	}

	/**
	 * Create a new line based on the origin and a single point
	 * 
	 * @param x The end point of the line
	 * @param y The end point of the line
	 */
	public Line(float x, float y) {
		this(x,y,true,true);
	}
	
	/**
	 * Create a new line based on two points
	 * 
	 * @param x1 The x coordinate of the start point
	 * @param y1 The y coordinate of the start point
	 * @param x2 The x coordinate of the end point
	 * @param y2 The y coordinate of the end point
	 */
	public Line(float x1, float y1, float x2, float y2) {
		this(new Vector2f(x1,y1), new Vector2f(x2,y2));
	}
	
	/**
	 * Create a new line based on two points
	 * 
	 * @param start The start point
	 * @param end The end point
	 */
	public Line(ROVector2f start, ROVector2f end) {
		super();
		
//		float width = Math.abs(end.getX()-start.getX());
//		float height = Math.abs(end.getY()-start.getY());
//		float xoffset = width/2;
//		float yoffset = height/2;
//		if (width < 10) {
//			width = 10;
//		}
//		if (height < 10) {
//			height = 50;
//		}
//		if (end.getY() < start.getY()) {
//			yoffset = -yoffset;
//		}
//		if (end.getX() < start.getX()) {
//			xoffset = -xoffset;
//		}
		//TODO: do this properly!
		float radius = Math.max(start.length(), end.length());
		bounds = new AABox(0,0,radius*2,radius*2);
		
		set(start,end);
	}
	
	/**
	 * Check if this line blocks the inner side (for want of a better term)
	 * 
	 * @return True if this line blocks the inner side
	 */
	public boolean blocksInnerEdge() {
		return innerEdge;
	}
	
	/**
	 * Indicate if this line blocks on it's inner edge
	 * 
	 * @param innerEdge True if this line blocks on it's inner edge
	 */
	public void setBlocksInnerEdge(boolean innerEdge) {
		this.innerEdge = innerEdge;
	}

	/**
	 * Check if this line blocks the outer side (for want of a better term)
	 * 
	 * @return True if this line blocks the outer side
	 */
	public boolean blocksOuterEdge() {
		return outerEdge;
	}

	/**
	 * Indicate if this line blocks on it's outer edge
	 * 
	 * @param outerEdge True if this line blocks on it's outer edge
	 */
	public void setBlocksOuterEdge(boolean outerEdge) {
		this.outerEdge = outerEdge;
	}
	
	/**
	 * Get the start point of the line
	 * 
	 * @return The start point of the line
	 */
	public ROVector2f getStart() {
		return start;
	}

	/**
	 * Get the end point of the line
	 * 
	 * @return The end point of the line
	 */
	public ROVector2f getEnd() {
		return end;
	}
	
	/**
	 * Find the length of the line
	 * 
	 * @return The the length of the line
	 */
	public float length() {
		return vec.length();
	}
	
	/**
	 * Find the length of the line squared (cheaper and good for comparisons)
	 * 
	 * @return The length of the line squared
	 */
	public float lengthSquared() {
		return vec.lengthSquared();
	}
	
	/**
	 * Configure the line
	 * 
	 * @param start The start point of the line
	 * @param end The end point of the line
	 */
	public void set(ROVector2f start, ROVector2f end) {
		this.start = start;
		this.end = end;
		
		vec = new Vector2f(end);
		vec.sub(start);
		
		lenSquared = vec.length();
		lenSquared *= lenSquared;
	}
	
	/**
	 * Get the x direction of this line
	 * 
	 * @return The x direction of this line
	 */
	public float getDX() {
		return end.getX() - start.getX();
	}

	/**
	 * Get the y direction of this line
	 * 
	 * @return The y direction of this line
	 */
	public float getDY() {
		return end.getY() - start.getY();
	}
	
	/**
	 * Get the x coordinate of the start point
	 * 
	 * @return The x coordinate of the start point
	 */
	public float getX1() {
		return start.getX();
	}

	/**
	 * Get the y coordinate of the start point
	 * 
	 * @return The y coordinate of the start point
	 */
	public float getY1() {
		return start.getY();
	}

	/**
	 * Get the x coordinate of the end point
	 * 
	 * @return The x coordinate of the end point
	 */
	public float getX2() {
		return end.getX();
	}

	/**
	 * Get the y coordinate of the end point
	 * 
	 * @return The y coordinate of the end point
	 */
	public float getY2() {
		return end.getY();
	}
	
	/**
	 * Get the shortest distance from a point to this line
	 * 
	 * @param point The point from which we want the distance
	 * @return The distance from the line to the point
	 */
	public float distance(ROVector2f point) {
		return (float) Math.sqrt(distanceSquared(point));
	}
	
	/** 
	 * Get the shortest distance squared from a point to this line
	 * 
	 * @param point The point from which we want the distance
	 * @return The distance squared from the line to the point
	 */
	public float distanceSquared(ROVector2f point) {
		getClosestPoint(point, closest);
		closest.sub(point);
		
		float result = closest.lengthSquared();
		
		return result;
	}
	
	/**
	 * Get the closest point on the line to a given point
	 * 
	 * @param point The point which we want to project
	 * @param result The point on the line closest to the given point
	 */
	public void getClosestPoint(ROVector2f point, Vector2f result) {
		loc.set(point);
		loc.sub(start);
		
		v.set(vec);
		v2.set(vec);
		v2.scale(-1);
		
		v.normalise();
		loc.projectOntoUnit(v, proj);
		if (proj.lengthSquared() > vec.lengthSquared()) {
			result.set(end);
			return;
		}
		proj.add(start);
		
		other.set(proj);
		other.sub(end);
		if (other.lengthSquared() > vec.lengthSquared()) {
			result.set(start);
			return;
		}
		
		result.set(proj);
		return;
	}

	/**
	 * @see net.phys2d.raw.shapes.Shape#getSurfaceFactor()
	 */
	public float getSurfaceFactor() {
		return lengthSquared() / 2;
	}
	
	/**
	 * Get a line starting a x,y and ending offset from the current
	 * end point. Curious huh?
	 * 
	 * @param displacement The displacement of the line
	 * @param rotation The rotation of the line in radians
	 * @return The newly created line
	 */
	public Line getPositionedLine(ROVector2f displacement, float rotation) {
		Vector2f[] verts = getVertices(displacement, rotation);
		Line line = new Line(verts[0], verts[1]);
		
		return line;
	}
	
	/**
	 * Return a translated and rotated line.
	 * 
	 * @param displacement The displacement of the line
	 * @param rotation The rotation of the line in radians
	 * @return The two endpoints of this line
	 */
	public Vector2f[] getVertices(ROVector2f displacement, float rotation) {
		float cos = (float) Math.cos(rotation);
		float sin = (float) Math.sin(rotation);
		
		Vector2f[] endPoints = new Vector2f[2];
		endPoints[0] = new Vector2f(//getX1(), getY1());
				getX1() * cos - getY1() * sin,
				getY1() * cos + getX1() * sin);
		endPoints[0].add(displacement);
		endPoints[1] = new Vector2f(//getX2(), getY2());
				getX2() * cos - getY2() * sin,
				getY2() * cos + getX2() * sin);
		endPoints[1].add(displacement);
		
		return endPoints;
	}
	
	/**
	 * Move this line a certain amount
	 * 
	 * @param v The amount to move the line
	 */
	public void move(ROVector2f v) {
		Vector2f temp = new Vector2f(start);
		temp.add(v);
		start = temp;
		temp = new Vector2f(end);
		temp.add(v);
		end = temp;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Line "+start+","+end+"]";
	}
	
	/**
	 * Intersect this line with another
	 * 
	 * @param other The other line we should intersect with
	 * @return The intersection point or null if the lines are parallel
	 */
	public Vector2f intersect(Line other) {
		float dx1 = end.getX() - start.getX();
		float dx2 = other.end.getX() - other.start.getX();
		float dy1 = end.getY() - start.getY();
		float dy2 = other.end.getY() - other.start.getY();
		float denom = (dy2 * dx1) - (dx2 * dy1);
		
		if (denom == 0) {
			return null;
		}
		
		float ua = (dx2 * (start.getY() - other.start.getY())) - (dy2 * (start.getX() - other.start.getX()));
		ua /= denom;
		float ub = (dx1 * (start.getY() - other.start.getY())) - (dy1 * (start.getX() - other.start.getX()));
		ub /= denom;
		
		float u = ua;
		
		float ix = start.getX() + (u * (end.getX() - start.getX()));
		float iy = start.getY() + (u * (end.getY() - start.getY()));
		
		return new Vector2f(ix,iy);
	}
	
}
