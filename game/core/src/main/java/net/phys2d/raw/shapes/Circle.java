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

/**
 * A simple Circle within the simulation, defined by its radius and the
 * position of the body to which it belongs
 * 
 * @author Kevin Glass
 */
public strictfp class Circle extends AbstractShape implements DynamicShape {
	/** The radius of the circle */
	private float radius;
	
	/**
	 * Create a new circle based on its radius
	 * 
	 * @param radius The radius of the circle
	 */
	public Circle(float radius) {
		super(new AABox(radius*2, radius*2));
		
		this.radius = radius;
	}

	/**
	 * Get the radius of the circle
	 * 
	 * @return The radius of the circle
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * @see net.phys2d.raw.shapes.Shape#getSurfaceFactor()
	 */
	public float getSurfaceFactor() {
		float circ = (float) (2 * Math.PI * radius);
		circ /= 2;
		
		return circ * circ;
	}
	
	/**
	 * Check if this circle touches another
	 * 
	 * @param x The x position of this circle
	 * @param y The y position of this circle
	 * @param other The other circle
	 * @param ox The other circle's x position
	 * @param oy The other circle's y position
	 * @return True if they touch
	 */
	public boolean touches(float x, float y, Circle other, float ox, float oy) {
		float totalRad2 = getRadius() + other.getRadius();
		
		if (Math.abs(ox - x) > totalRad2) {
			return false;
		}
		if (Math.abs(oy - y) > totalRad2) {
			return false;
		}
		
		totalRad2 *= totalRad2;
		
		float dx = Math.abs(ox - x);
		float dy = Math.abs(oy - y);
		
		return totalRad2 >= ((dx*dx) + (dy*dy));
	}
}
