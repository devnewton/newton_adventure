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
package net.phys2d.raw;

import net.phys2d.raw.shapes.Shape;

/**
 * A body that will not move
 * 
 * @author Kevin Glass
 */
public strictfp class StaticBody extends Body {

	/**
	 * Create a static body
	 * 
	 * @param shape The shape representing this body
	 */
	public StaticBody(Shape shape) {
		super(shape, Body.INFINITE_MASS);
	}

	/**
	 * Create a static body
	 * 
	 * @param name The name to assign to the body
	 * @param shape The shape representing this body
	 */
	public StaticBody(String name, Shape shape) {
		super(name, shape, Body.INFINITE_MASS);
	}

	/**
	 * @see net.phys2d.raw.Body#isRotatable()
	 */
	public boolean isRotatable() {
		return false;
	}
	
	/**
	 * @see net.phys2d.raw.Body#isMoveable()
	 */
	public boolean isMoveable() {
		return false;
	}
	
	/**
	 * Check if this body is static
	 * 
	 * @return True if this body is static
	 */
	public boolean isStatic() {
		return true;
	}
	
	/**
	 * @see net.phys2d.raw.Body#isResting()
	 */
	public boolean isResting() {
		return true;
	}
}
