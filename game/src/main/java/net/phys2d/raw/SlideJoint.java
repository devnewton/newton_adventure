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

import net.phys2d.math.MathUtil;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.Vector2f;

/**
 * Create a joint that keeps the distance between two bodies in a given range
 * 
 * @author guRuQu
 */
public class SlideJoint implements Joint {
	/** The marker to indicate minimum collision */
	private static final int COLLIDE_MIN=-1;
	/** The marker to indicate no collision */
	private static final int COLLIDE_NONE=0;
	/** The marker to indicate maximum collision */
	private static final int COLLIDE_MAX=1;
	
	/**The first body in the constraint*/
	private Body body1;
	/**The second body in the constraint*/
	private Body body2;
	/** Anchor point for first body, on which impulse is going to apply*/
	private Vector2f anchor1;
	/** Anchor point for second body, on which impulse is going to apply*/
	private Vector2f anchor2;
	/** The rotation of the first body */
	protected Vector2f r1;
	/** The rotation of the second body */
	protected Vector2f r2;
	/** The minimum distance between the two bodies */
	protected float minDistance;
	/** The maximum distance between the two bodies */
	protected float maxDistance;
	/** The restitution on hitting the end of the joint */
	protected float restitutionConstant;
	
	/** The collision side of the end of the joint if any */
	private int collideSide;
	/** Normalised distance vector*/
	private Vector2f ndp;
	/** The cached impulse through the calculation to yield correct impulse faster */
	private float accumulateImpulse;
	/** The minimum distance between the two bodies squared */
	private float minDistance2;
	/** The maximum distance between the two bodies squared */
	private float maxDistance2;
	/** Used to calculate the relation ship between impulse and velocity change between body*/
	private float K;
	/** The restitution constant when angle bounce on either side*/
	private float restitute;
	
	/**
	 * Create a new joint
	 * 
	 * @param body1	The first body to be attached on constraint
	 * @param body2 The second body to be attached on constraint
	 * @param anchor1 The anchor point on first body
	 * @param anchor2 The anchor point on second body
	 * @param minDistance The maximum distance limit of the slide
	 * @param maxDistance The minimum distance limit of the slide
	 * @param restitution The restitution body is going to be effected when bounce off the distance limit
	 */
	public SlideJoint(Body body1,Body body2,Vector2f anchor1,Vector2f anchor2,float minDistance,float maxDistance,float restitution){
		this.restitutionConstant=restitution;
		this.minDistance2=minDistance;
		this.maxDistance2=maxDistance;
		this.minDistance=minDistance*minDistance;
		this.maxDistance=maxDistance*maxDistance;
		this.body1=body1;
		this.body2=body2;
		this.anchor1=anchor1;
		this.anchor2=anchor2;
		
	}
	
	/**
	 * @see net.phys2d.raw.Joint#applyImpulse()
	 */
	public void applyImpulse() {
		if(collideSide==COLLIDE_NONE)
			return;
		
		Vector2f dv = new Vector2f(body2.getVelocity());
		dv.add(MathUtil.cross(body2.getAngularVelocity(),r2));
		dv.sub(body1.getVelocity());
		dv.sub(MathUtil.cross(body1.getAngularVelocity(),r1));
		float reln = dv.dot(ndp);
		reln = restitute-reln;
		float P = reln/K;
		float newImpulse;
		if(collideSide==COLLIDE_MIN){
			newImpulse = accumulateImpulse+P<0.0f?accumulateImpulse+P:0.0f;
		}else{
			newImpulse = accumulateImpulse+P>0.0f?accumulateImpulse+P:0.0f;
		}
		P = newImpulse-accumulateImpulse;
		accumulateImpulse = newImpulse;
		
		Vector2f impulse = new Vector2f(ndp);
		impulse.scale(P);
		
		if (!body1.isStatic()) {
			Vector2f accum1 = new Vector2f(impulse);
			accum1.scale(body1.getInvMass());
			body1.adjustVelocity(accum1);
			body1.adjustAngularVelocity((body1.getInvI() * MathUtil.cross(r1, impulse)));
		}
		if (!body2.isStatic()) {
			Vector2f accum2 = new Vector2f(impulse);
			accum2.scale(-body2.getInvMass());
			body2.adjustVelocity(accum2);
			body2.adjustAngularVelocity(-(body2.getInvI() * MathUtil.cross(r2, impulse)));
		}
	}

	/**
	 * @see net.phys2d.raw.Joint#getBody1()
	 */
	public Body getBody1() {
		return body1;
	}

	/**
	 * @see net.phys2d.raw.Joint#getBody2()
	 */
	public Body getBody2() {
		return body2;
	}

	/**
	 * @see net.phys2d.raw.Joint#preStep(float)
	 */
	public void preStep(float invDT) {
		float biasFactor=0.01f;
		float biasImpulse=0.0f;
		Matrix2f rot1 = new Matrix2f(body1.getRotation());
		Matrix2f rot2 = new Matrix2f(body2.getRotation());

		 r1 = MathUtil.mul(rot1,anchor1);
		 r2 = MathUtil.mul(rot2,anchor2);
		
		Vector2f p1 = new Vector2f(body1.getPosition());
		p1.add(r1);
		Vector2f p2 = new Vector2f(body2.getPosition());
		p2.add(r2);
		Vector2f dp = new Vector2f(p2);
		dp.sub(p1);
		
		Vector2f dv = new Vector2f(body2.getVelocity());
		dv.add(MathUtil.cross(body2.getAngularVelocity(),r2));
		dv.sub(body1.getVelocity());
		dv.sub(MathUtil.cross(body1.getAngularVelocity(),r1));
	    
		ndp = new Vector2f(dp);
		ndp.normalise();
		
		restitute = -restitutionConstant*dv.dot(ndp);
		
		Vector2f v1 = new Vector2f(ndp);
		v1.scale(-body2.getInvMass() - body1.getInvMass());

		Vector2f v2 = MathUtil.cross(MathUtil.cross(r2, ndp), r2);
		v2.scale(-body2.getInvI());
		
		Vector2f v3 = MathUtil.cross(MathUtil.cross(r1, ndp),r1);
		v3.scale(-body1.getInvI());
		
		Vector2f K1 = new Vector2f(v1);
		K1.add(v2);
		K1.add(v3);
		
		K = K1.dot(ndp);
		float length=dp.lengthSquared();
		if(length<minDistance){
			if(collideSide!=COLLIDE_MIN)
				accumulateImpulse=0;
			collideSide=COLLIDE_MIN;			
			biasImpulse=-biasFactor*(length-minDistance);
			if(restitute<0)
				restitute=0;
		}else if(length>maxDistance){
			if(collideSide!=COLLIDE_MAX)
				accumulateImpulse=0;
			collideSide=COLLIDE_MAX;
			biasImpulse=-biasFactor*(length-maxDistance);
			if(restitute>0)
				restitute=0;
		}else{
			collideSide=COLLIDE_NONE;
			accumulateImpulse=0;
		}
		restitute+=biasImpulse;
		Vector2f impulse = new Vector2f(ndp);
		impulse.scale(accumulateImpulse);
		
		if (!body1.isStatic()) {
			Vector2f accum1 = new Vector2f(impulse);
			accum1.scale(body1.getInvMass());
			body1.adjustVelocity(accum1);
			body1.adjustAngularVelocity((body1.getInvI() * MathUtil.cross(r1, impulse)));
		}
		if (!body2.isStatic()) {
			Vector2f accum2 = new Vector2f(impulse);
			accum2.scale(-body2.getInvMass());
			body2.adjustVelocity(accum2);
			body2.adjustAngularVelocity(-(body2.getInvI() * MathUtil.cross(r2, impulse)));
		}
	}

	/**
	 * Get the minimum distance between two bodies
	 * 
	 * @return The minimum distance between two bodies
	 */
	public float getMinDistance() {
		return minDistance2;
	}
	
	/**
	 * Set the minimum allowed distance between the two bodies
	 * 
	 * @param minDistance The minimum distance allowed between the two bodies
	 */
	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * Get the maximum distance between two bodies
	 * 
	 * @return The minimum distance between two bodies
	 */
	public float getMaxDistance() {
		return maxDistance2;
	}

	/**
	 * Set the maximum allowed distance between the two bodies
	 * 
	 * @param maxDistance The maximum distance allowed between the two bodies
	 */
	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
	}
	
	/**
	 * Get the restitution constant
	 * 
	 * @return The restitution constant
	 */
	public float getRestitutionConstant() {
		return restitutionConstant;
	}

	/**
	 * Set the restitution constant
	 * 
	 * @param restitutionConstant The restitution constant
	 */
	public void setRestitutionConstant(float restitutionConstant) {
		this.restitutionConstant = restitutionConstant;
	}
	
	/**
	 * @see net.phys2d.raw.Joint#setRelaxation(float)
	 */
	public void setRelaxation(float relaxation) {
	}

	/**
	 * Get the anchor of the joint on the first body
	 * 
	 * @return The anchor of the joint on the first body
	 */
	public Vector2f getAnchor1() {
		return anchor1;
	}

	/**
	 * Get the anchor of the joint on the second body
	 * 
	 * @return The anchor of the joint on the second body
	 */
	public Vector2f getAnchor2() {
		return anchor2;
	}


}
