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

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.collide.FeaturePair;

/**
 * A description of a single contact point between two bodies
 * 
 * @author Kevin Glass
 */
public strictfp class Contact {
	// TODO: the positions are absolute, right? if not make them so
	/** The position of the contact */
	Vector2f position = new Vector2f();
	/** The normal at the contact point which, for convex bodies,
	 * points away from the first body. */
	Vector2f normal = new Vector2f();
	/** ? */
	float separation;
	/** The impulse accumlated in the direction of the normal */
	float accumulatedNormalImpulse;
	/** The impulse accumlated in the direction of the tangent */
	float accumulatedTangentImpulse;
	/** The mass applied throught the normal at this contact point */
	float massNormal;
	/** The mass applied through the tangent at this contact point */
	float massTangent;
	/** The correction factor penetration */
	float bias;
	/** The pair of edges this contact is between */
	FeaturePair feature = new FeaturePair();
	/** The restitution at this point of contact */
	float restitution;
	/** The bias impulse accumulated */
	float biasImpulse;
	
	/**
	 * Create a new contact point
	 */
	public Contact() {
		accumulatedNormalImpulse = 0.0f;
		accumulatedTangentImpulse = 0.0f;
	}
	
	/** 
	 * Get the position of this contact 
	 * 
	 * @return The position of this contact
	 */
	public ROVector2f getPosition() {
		return position;
	}
	
	/**
	 * Set the contact information based on another contact
	 * 
	 * @param contact The contact information
	 */
	void set(Contact contact) {
		position.set(contact.position);
		normal.set(contact.normal);
		separation = contact.separation;
		accumulatedNormalImpulse = contact.accumulatedNormalImpulse;
		accumulatedTangentImpulse = contact.accumulatedTangentImpulse;
		massNormal = contact.massNormal;
		massTangent = contact.massTangent;
		bias = contact.bias;
		restitution = contact.restitution;
		feature.set(contact.feature);
	}
	
	/**
	 * Get the seperation between bodies
	 * 
	 * @return The seperation between bodies
	 */
	public float getSeparation() {
		return separation;
	}
	
	/**
	 * Get the normal at the point of contact
	 * 
	 * @return The normal at the point of contact
	 */
	public ROVector2f getNormal() {
		return normal;
	}
	
	/**
	 * Set the normal at the point of contact.
	 * 
	 * @param normal The normal at the point of contact
	 */
	public void setNormal(ROVector2f normal) {
		this.normal.set(normal);
	}
	
	/**
	 * Set the position of the contact
	 * 
	 * @param position The position of the contact
	 */
	public void setPosition(ROVector2f position) {
		this.position.set(position);
	}
	
	/**
	 * Get the pairing identifing the location of the contact
	 * 
	 * @return The feature painting identifing the contact
	 */
	public FeaturePair getFeature() {
		return feature;
	}
	
	/**
	 * Set the feature identifying the location of the contact
	 * 
	 * @param pair The pair identifying the location of the contact
	 */
	public void setFeature(FeaturePair pair) {
		this.feature = pair;
	}

	/**
	 * Set the separation between bodies
	 * 
	 * @param separation The separation between bodies at this contact
	 */
	public void setSeparation(float separation) {
		this.separation = separation;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return feature.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other.getClass() == getClass()) {
			return ((Contact) other).feature.equals(feature);
		}
		
		return false;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Contact "+position+" n: "+normal+" sep: "+separation+"]";
	}
}
