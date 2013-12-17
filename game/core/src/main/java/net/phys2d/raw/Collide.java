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

import net.phys2d.raw.collide.Collider;
import net.phys2d.raw.collide.ColliderFactory;
import net.phys2d.raw.collide.ColliderUnavailableException;

/**
 * A static utility for resolve the collision between shapes
 * 
 * TODO: make this nonstatic to allow a user to provide his/her own factory
 * 
 * @author Kevin Glass
 */
public strictfp class Collide {

	/** The factory that provides us with colliders */
	private static ColliderFactory collFactory = new ColliderFactory();

	/**
	 * Perform the collision between two bodies
	 * 
	 * @param contacts The points of contact that should be populated
	 * @param bodyA The first body
	 * @param bodyB The second body
	 * @param dt The amount of time that's passed since we last checked collision
	 * @return The number of points at which the two bodies contact
	 */
	public static int collide(Contact[] contacts, Body bodyA, Body bodyB, float dt)
	{
		Collider collider;
		try {
			collider = collFactory.createCollider(bodyA, bodyB);
		} catch (ColliderUnavailableException e) {
			System.out.println(e.getMessage()
					+ "\n Ignoring any possible collision between the bodies in question");
			return 0;
		}
		
		return collider.collide(contacts, bodyA, bodyB);
	}
}
