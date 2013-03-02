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
 * An axis oriented used for shape bounds
 * 
 * @author Kevin Glass
 */
public strictfp class AABox {
	/** The width of the box */
	private float width;
	/** The height of the box */
	private float height;
	/** The x offset to the body's position of the bounds */
	private float offsetx;
	/** The y offset to the body's position of the bounds */
	private float offsety;
	
	/**
	 * Create a new bounding box
	 * 
	 * @param width The width of the box
	 * @param height The hieght of the box
	 */
	public AABox(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Create a new AABox
	 * 
	 * @param offsetx The x offset to the body's position
	 * @param offsety The y offset to the body's position
	 * @param width The width of the box
	 * @param height The hieght of the box
	 */
	public AABox(float offsetx, float offsety, float width, float height) {
		this.width = width;
		this.height = height;
		this.offsetx = offsetx;
		this.offsety = offsety;
	}
	
	/**
	 * Get the width of the box
	 * 
	 * @return The width of the box
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the box
	 * 
	 * @return The height of the box
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * Get the x offset to the body's position of this bounds
	 * 
	 * @return The x offset to the body's position of this bounds
	 */ 
	public float getOffsetX() {
		return offsetx;
	}
	
	/**
	 * Get the y offset to the body's position of this bounds
	 * 
	 * @return The y offset to this body's position of this bounds
	 */
	public float getOffsetY() {
		return offsety;
	}
	
	/**
	 * Check if this box touches another
	 * 
	 * @param x The x position of this box
	 * @param y The y position of this box
	 * @param other The other box to check against  
	 * @param otherx The other box's x position
	 * @param othery The other box's y position
	 * @return True if the boxes touches
	 */
	public boolean touches(float x, float y, AABox other, float otherx, float othery) {
		float totalWidth = (other.width + width) / 2;
		float totalHeight = (other.height + height) / 2;
		
		float dx = Math.abs((x + offsetx) - (otherx + other.offsetx));
		float dy = Math.abs((y + offsety) - (othery + other.offsety));
		
		return (totalWidth > dx) && (totalHeight > dy);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[AABox "+width+"x"+height+"]";
	}
}
