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


/** Class representing a single intersection.
 * TODO: this should be merged with the 'Feature' class, it represents the same thing
 * 
 * @author Gideon Smeding
 */
public class Intersection {
	/** The edge of polygon A that intersects */
	public int edgeA;
	/** The edge of polygon B that intersects */
	public int edgeB;
	
	/** The position of the intersection in world (absolute) coordinates */
	public Vector2f position;
	
	/** True iff this is an intersection where polygon A enters B */
	public boolean isIngoing;

	/**
	 * Construct an intersection with all its attributes set.
	 * 
	 * @param edgeA The edge of polygon A that intersects
	 * @param edgeB The edge of polygon B that intersects
	 * @param position The position of the intersection in world (absolute) coordinates
	 * @param isIngoing True iff this is an intersection where polygon A enters B 
	 */
	public Intersection(int edgeA, int edgeB, Vector2f position, boolean isIngoing) {
		super();
		this.edgeA = edgeA;
		this.edgeB = edgeB;
		this.position = position;
		this.isIngoing = isIngoing;
	}
}
