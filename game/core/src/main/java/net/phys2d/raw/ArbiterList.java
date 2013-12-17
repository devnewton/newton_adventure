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
 * A typed list of <code>Arbiter</code>
 * 
 * @author Kevin Glass
 */
public class ArbiterList {
	/** The elements in the list */
	private ArrayList elements = new ArrayList();
	
	/**
	 * Create an empty list 
	 */
	ArbiterList() {
		
	}
	
	/**
	 * Add an arbiter to the list
	 * 
	 * @param arbiter The arbiter to add
	 */
	void add(Arbiter arbiter) {
		elements.add(arbiter);
	}
	
	/**
	 * Get the size of the list
	 * 
	 * @return The number of elements in the list
	 */
	public int size() {
		return elements.size();
	}
	
	/**
	 * Return the index of a particular arbiter in the list
	 * 
	 * @param arbiter The arbiter to search for
	 * @return The index of -1 if not found
	 */
	public int indexOf(Arbiter arbiter) {
		return elements.indexOf(arbiter);
	}
	
	/**
	 * Remove an abiter from the list
	 * 
	 * @param arbiter The arbiter ot remove from the list
	 */
	void remove(Arbiter arbiter) {
		if (!elements.contains(arbiter)) {
			return;
		}
		elements.set(elements.indexOf(arbiter), elements.get(elements.size()-1));
		elements.remove(elements.size()-1);
	}
	
	/**
	 * Get an arbiter at a specified index
	 * 
	 * @param i The index of arbiter to retrieve
	 * @return The arbiter at the specified index
	 */
	public Arbiter get(int i) {
		return (Arbiter) elements.get(i);
	}
	
	/**
	 * Remove all the elements from the list
	 */
	public void clear() {
		elements.clear();
	}

	/**
	 * Check if an arbiter is contained within a list
	 * 
	 * @param arb The arbiter to check for
	 * @return True if the arbiter is in the list
	 */
	public boolean contains(Arbiter arb) {
		return elements.contains(arb);
	}
}
