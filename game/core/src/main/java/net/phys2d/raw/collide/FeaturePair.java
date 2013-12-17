/*
 * Phys2D - a 2D physics engine based on the work of Erin Catto. The
 * original source remains:
 * 
 * Copyright (c) 2006 Erin Catto http://www.gphysics.com
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



/**
 * An identifier for a pair of edges between two bodies. This identifies which
 * contact points we're using in any given situation
 * 
 * For polygons defined by counter clockwise lists of vertices, the following
 * will hold (vertsA is the first, and vertsB the second polygon):
 * vertsA[inEdge1] is outside the overlap area
 * vertsA[inEdge1+1] is inside the overlap area
 * vertsB[inEdge2] is inside the overlap area
 * vertsB[inEdge2+1] is outside the overlap area
 * vertsA[outEdge1] is inside the overlap area
 * vertsA[outEdge1+1] is outside the overlap area
 * vertsB[outEdge1] is outside the overlap area
 * vertsB[outEdge1+1] is inside the overlap area
 * 
 * Keep in mind that the edges outside of the overlap area could be inside
 * another overlapping area that has another feature set.
 * 
 * TODO: check if this also holds for boxes
 * 
 * @author Kevin Glass
 */
public class FeaturePair {


	/** The edge of the first polygon entering the second polygon */
	int inEdge1;
	/** The first edge in the collision */
	int outEdge1;
	/** The second edge in the collision */
	int inEdge2;
	/** The second edge in the collision */
	int outEdge2;
	
	/** 
	 * Public constructor since something in the raw port want to access
	 * it. Should not be constructed by a user.
	 */
	public FeaturePair() {
	}
	
	/**
	 * Construct a feature pair and set edges.
	 * 
	 * @param inEdge1
	 * @param outEdge1
	 * @param inEdge2
	 * @param outEdge2
	 */
	public FeaturePair(int inEdge1, int inEdge2, int outEdge1, int outEdge2) {
		this.inEdge1 = inEdge1;
		this.inEdge2 = inEdge2;
		this.outEdge1 = outEdge1;
		this.outEdge2 = outEdge2;
	}
	
	/**
	 * Create a new feature pair
	 * 
	 * @param index The index identifing the feature pair that collided
	 */
	FeaturePair(int index) {
		inEdge1 = index;
	}
	
	/**
	 * Get this feature pair as a key value used for hashing
	 * 
	 * @return The key value
	 */
    int getKey() {
		return inEdge1 + (outEdge1 << 8) + (inEdge2 << 16) + (outEdge2 << 24);
	}
	
    /**
     * @see java.lang.Object#hashCode()
     */
	public int hashCode() {
		return getKey();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other instanceof FeaturePair) {
			return ((FeaturePair) other).getKey() == getKey();
		}
		
		return false;
	}
	
	/**
	 * Set the contents of this pair from another
	 * 
	 * @param other The other pair to populate this pair from
	 */
	public void set(FeaturePair other) {
		inEdge1 = other.inEdge1;
		inEdge2 = other.inEdge2;
		outEdge1 = other.outEdge1;
		outEdge2 = other.outEdge2;
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "((" + inEdge1 + "," + inEdge2 + "),(" + outEdge1 + "," + outEdge2 + "))"; 
	}
}
