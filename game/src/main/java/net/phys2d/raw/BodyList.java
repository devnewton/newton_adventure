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
 * A typed list of <code>Body</code>
 * 
 * @author Kevin Glass
 */
public class BodyList {
	/** The elements in the list */
	private ArrayList elements = new ArrayList();
	
	/**
	 * Create an empty list
	 */
	public BodyList() {
		
	}
	
	/**
	 * Create a new list containing the elements specified
	 * 
	 * @param list The list of elements to add to the new list
	 */
	BodyList(BodyList list) {
		elements.addAll(list.elements);
	}
	
	/**
	 * Add a body to the list
	 * 
	 * @param body The body to add
	 */
	public void add(Body body) {
		elements.add(body);
	}
	
	/**
	 * Get the number of elements in the list
	 * 
	 * @return The number of the element in the list
	 */
	public int size() {
		return elements.size();
	}
	
	/**
	 * Remove a body from the list
	 * 
	 * @param body The body to remove from the list 
	 */
	public void remove(Body body) {
		elements.remove(body);
	}
	
	/**
	 * Get a body at a specific index
	 * 
	 * @param i The index of the body to retrieve
	 * @return The body retrieved
	 */
	public Body get(int i) {
		return (Body) elements.get(i);
	}
	
	/**
	 * Clear all the elements out of the list
	 */
	public void clear() {
		elements.clear();
	}
	
	/**
	 * Check if this list contains the specified body
	 * 
	 * @param body The body to look for
	 * @return True if this list contains the specified body
	 */
	public boolean contains(Body body) {
		return elements.contains(body);
	}
	
	/**
	 * Get a list of bodies containing all of the bodies in this
	 * list except those specified
	 * 
	 * @param others The bodies that should be removed from the contents
	 * @return The list of bodies excluding those specified
	 */
	public BodyList getContentsExcluding(BodyList others) {
		BodyList list = new BodyList(this);
		list.elements.removeAll(others.elements);
		
		return list;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = "[BodyList ";
		for (int i=0;i<elements.size();i++) {
			str += get(i)+",";
		}
		str += "]";
		
		return str;
	}
}
