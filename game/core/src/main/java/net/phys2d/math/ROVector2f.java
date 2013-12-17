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
package net.phys2d.math;

/**
 * A readonly two dimensional vector
 * 
 * @author Kevin Glass
 */
public interface ROVector2f {
	/**
	 * Get the X component of this vector
	 * 
	 * @return The X component of this vector
	 */
	public float getX();

	/**
	 * Get the Y component of this vector
	 * 
	 * @return The Y component of this vector
	 */
	public float getY();
	
	/**
	 * Get the length of this vector
	 * 
	 * @return The length of this vector
	 */
	public float length();
	
	/**
	 * Get the dot product of this vector and another
	 * 
	 * @param other The other vector to dot against
	 * @return The dot product of the two vectors
	 */
	public float dot(ROVector2f other);

	/**
	 * Project this vector onto another
	 * 
	 * @param b The vector to project onto
	 * @param result The projected vector
	 */
	public void projectOntoUnit(ROVector2f b, Vector2f result);
	
	/**
	 * The length of the vector squared
	 * 
	 * @return The length of the vector squared
	 */
	public float lengthSquared();

	/**
	 * Get the distance from this point to another
	 * 
	 * @param other The other point we're measuring to
	 * @return The distance to the other point
	 */
	public float distance(ROVector2f other);

	/**
	 * Get the distance squared from this point to another
	 * 
	 * @param other The other point we're measuring to
	 * @return The distance to the other point
	 */
	public float distanceSquared(ROVector2f other);
}
