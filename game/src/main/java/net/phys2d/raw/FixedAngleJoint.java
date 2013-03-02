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
 * A joint that will maintain a fixed angle between two bodies
 * 
 * @author guRuQu
 */
public class FixedAngleJoint implements Joint {
	/** The angle to maintain */
	private float rotateA;
	/** The first body jointed */
	private Body body1;
	/** The second body jointed */
	private Body body2;
	/** Anchor point for first body, on which impulse is going to apply*/
	private Vector2f anchor1;
	/** Anchor point for second body, on which impulse is going to apply*/
	private Vector2f anchor2;
	/** The cached impulse through the calculation to yield correct impulse faster */
	private float accumulateImpulse;
	/** The squared distance of two body*/
	private float dlength2;
	/** Used to calculate the relation ship between impulse and velocity change between body*/
	private float K;
	/** Normalised distance vector*/
	private Vector2f ndp;
	/** Distance Vector*/
	private Vector2f dp;
	/** The normal vector of the impulse direction*/
	private Vector2f n;
	/** R = r1 + d */
	private Vector2f R;

	/**
	 * @param body1	The first body to be attached on constraint
	 * @param body2 The second body to be attached on constraint
	 * @param anchor1 The anchor point on first body
	 * @param anchor2 The anchor point on second body
	 * @param rotateA The fixed angle on body2 from body1
	 */
	public FixedAngleJoint(Body body1, Body body2, Vector2f anchor1,
			Vector2f anchor2, float rotateA) {
		this.body1 = body1;
		this.body2 = body2;
		this.rotateA = rotateA;
		this.anchor1 = anchor1;
		this.anchor2 = anchor2;
	}

	/**
	 * @see net.phys2d.raw.Joint#applyImpulse()
	 */
	public void applyImpulse() {
		Matrix2f rot1 = new Matrix2f(body1.getRotation());
		Matrix2f rot2 = new Matrix2f(body2.getRotation());
		Vector2f r1 = MathUtil.mul(rot1, anchor1);
		Vector2f r2 = MathUtil.mul(rot2, anchor2);

		Vector2f relativeVelocity = new Vector2f(body2.getVelocity());
		relativeVelocity.add(MathUtil.cross(r2, body2.getAngularVelocity()));
		relativeVelocity.sub(body1.getVelocity());
		relativeVelocity.sub(MathUtil.cross(r1, body1.getAngularVelocity()));

		/*
		 * Matrix2f tr1 = new Matrix2f(-body1.getRotation()); relativeVelocity =
		 * MathUtil.mul(tr1, relativeVelocity);
		 * relativeVelocity.add(MathUtil.mul(tr1, dp));
		 */

		float rv = MathUtil.cross(dp, relativeVelocity) / dlength2
				- body1.getAngularVelocity();
		rv = 0 - rv;

		float p = rv / K;
		float oldImpulse = accumulateImpulse;
		float newImpulse;

		newImpulse = accumulateImpulse + p;
		p = newImpulse - oldImpulse;

		accumulateImpulse = newImpulse;

		Vector2f impulse = new Vector2f(n);
		impulse.scale(p);
		if (!body1.isStatic()) {
			Vector2f accum1 = new Vector2f(impulse);
			accum1.scale(body1.getInvMass());
			body1.adjustVelocity(accum1);
			body1.adjustAngularVelocity((body1.getInvI() * MathUtil.cross(R,
					impulse)));
		}
		if (!body2.isStatic()) {
			Vector2f accum2 = new Vector2f(impulse);
			accum2.scale(-body2.getInvMass());
			body2.adjustVelocity(accum2);
			body2.adjustAngularVelocity(-(body2.getInvI() * MathUtil.cross(r2,
					impulse)));
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
		float biasFactor = 0.005f;
		float biasImpulse = 0.0f;
		float RA = body1.getRotation() + rotateA;

		Vector2f VA = new Vector2f((float) Math.cos(RA), (float) Math.sin(RA));

		Matrix2f rot1 = new Matrix2f(body1.getRotation());
		Matrix2f rot2 = new Matrix2f(body2.getRotation());
		Vector2f r1 = MathUtil.mul(rot1, anchor1);
		Vector2f r2 = MathUtil.mul(rot2, anchor2);

		Vector2f p1 = new Vector2f(body1.getPosition());
		p1.add(r1);
		Vector2f p2 = new Vector2f(body2.getPosition());
		p2.add(r2);
		dp = new Vector2f(p2);
		dp.sub(p1);
		dlength2 = dp.lengthSquared();
		ndp = new Vector2f(dp);
		ndp.normalise();

		R = new Vector2f(r1);
		R.add(dp);
		// System.out.println(accumulateImpulse);
		Vector2f relativeVelocity = new Vector2f(body2.getVelocity());
		relativeVelocity.add(MathUtil.cross(r2, body2.getAngularVelocity()));
		relativeVelocity.sub(body1.getVelocity());
		relativeVelocity.sub(MathUtil.cross(r1, body1.getAngularVelocity()));

		/*
		 * Matrix2f tr1 = new Matrix2f(-body1.getRotation()); relativeVelocity =
		 * MathUtil.mul(tr1, relativeVelocity);
		 * relativeVelocity.add(MathUtil.mul(tr1, dp));
		 */
		// relativeVelocity.add(MathUtil.cross(dp,body1.getAngularVelocity()));
		n = new Vector2f(-ndp.y, ndp.x);
		Vector2f v1 = new Vector2f(n);
		v1.scale(-body2.getInvMass() - body1.getInvMass());

		Vector2f v2 = MathUtil.cross(MathUtil.cross(r2, n), r2);
		v2.scale(-body2.getInvI());

		Vector2f v3 = MathUtil.cross(MathUtil.cross(R, n), r1);
		v3.scale(-body1.getInvI());

		Vector2f K1 = new Vector2f(v1);
		K1.add(v2);
		K1.add(v3);

		K = MathUtil.cross(dp, K1) / dlength2 - MathUtil.cross(R, n)
				* body1.getInvI();



			biasImpulse = biasFactor * MathUtil.cross(ndp, VA) * invDT;

		Vector2f impulse = new Vector2f(n);
		impulse.scale(accumulateImpulse+biasImpulse);
		if (!body1.isStatic()) {
			Vector2f accum1 = new Vector2f(impulse);
			accum1.scale(body1.getInvMass());
			body1.adjustVelocity(accum1);
			body1.adjustAngularVelocity((body1.getInvI() * MathUtil.cross(R,
					impulse)));
		}
		if (!body2.isStatic()) {
			Vector2f accum2 = new Vector2f(impulse);
			accum2.scale(-body2.getInvMass());
			body2.adjustVelocity(accum2);
			body2.adjustAngularVelocity(-(body2.getInvI() * MathUtil.cross(r2,
					impulse)));
		}
	}

	/**
	 * @see net.phys2d.raw.Joint#setRelaxation(float)
	 */
	public void setRelaxation(float relaxation) {
	}

	/**
	 * Get the angle maintained between the two bodies
	 * 
	 * @return The angle maintained between the two bodies
	 */
	public float getRotateA() {
		return rotateA;
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
