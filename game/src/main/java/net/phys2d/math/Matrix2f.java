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

import net.phys2d.math.Vector2f;

/**
 * A two dimensional vector
 * 
 * @author Kevin Glass
 */
public strictfp class Matrix2f {
	/** The first column of the matrix */
	public Vector2f col1 = new Vector2f();
	/** The second column of the matrix */
	public Vector2f col2 = new Vector2f();
	
	/**
	 * Create an empty matrix
	 */
	public Matrix2f() {
	}
	
	/**
	 * Create a matrix with a rotation
	 * 
	 * @param angle The angle of the rotation decribed by the matrix
	 */
	public Matrix2f(float angle)
	{
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		col1.x = c; col2.x = -s;
		col1.y = s; col2.y = c;
	}

	/**
	 * Create a matrix
	 * 
	 * @param col1 The first column
	 * @param col2 The second column
	 */
	public Matrix2f(Vector2f col1, Vector2f col2) {
		this.col1.set(col1);
		this.col2.set(col2);
	}

	/**
	 * Transpose the matrix
	 * 
	 * @return A newly created matrix containing the transpose of this matrix
	 */
	public Matrix2f transpose() 
	{
		return new Matrix2f(new Vector2f(col1.x, col2.x), 
							new Vector2f(col1.y, col2.y));
	}

	/**
	 * Transpose the invert
	 * 
	 * @return A newly created matrix containing the invert of this matrix
	 */
	public Matrix2f invert() 
	{
		float a = col1.x, b = col2.x, c = col1.y, d = col2.y;
		Matrix2f B = new Matrix2f();
		
		float det = a * d - b * c;
		if (det == 0.0f) {
			throw new RuntimeException("Matrix2f: invert() - determinate is zero!");
		}
		
		det = 1.0f / det;
		B.col1.x =  det * d;	B.col2.x = -det * b;
		B.col1.y = -det * c;	B.col2.y =  det * a;
		return B;
	}
}
