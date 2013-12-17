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

import java.util.ArrayList;

/**
 * A typed list of type <code>Joint</code>
 * 
 * @author Kevin Glass
 */
public class JointList {
	/** The elements in the list */
	private ArrayList elements = new ArrayList();
	
	/**
	 * Create an empty list
	 */
	public JointList() {
		
	}
	
	/**
	 * Check if a given joint in container within this list
	 * 
	 * @param joint The joint to check for
	 * @return True if the joint is contained in this list
	 */
	public boolean contains(Joint joint) {
		return elements.contains(joint);
	}
	
	/**
	 * Add a joint to the list
	 * 
	 * @param joint The joint to add
	 */
	public void add(Joint joint) {
		elements.add(joint);
	}
	
	/**
	 * Get the size of the list
	 * 
	 * @return The size of the list
	 */
	public int size() {
		return elements.size();
	}
	
	/**
	 * Remove a joint from the list
	 * 
	 * @param joint The joint to remove
	 */
	public void remove(Joint joint) {
		elements.remove(joint);
	}
	
	/**
	 * Get a joint from the list
	 * 
	 * @param i The index of the joint to retrieve
	 * @return The joint requested
	 */
	public Joint get(int i) {
		return (Joint) elements.get(i);
	}
	
	/**
	 * Empty the list
	 */
	public void clear() {
		elements.clear();
	}
}
