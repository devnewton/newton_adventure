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
 * A joint providing a springy resistance between bodies. Wiring a series together
 * gives an effective bopper spring. 
 * 
 * @see net.phys2d.raw.test.SpringyTest
 * @author guRuQu
 */
public class SpringyAngleJoint implements Joint {
	/** Anchor point for first body, on which impulse is going to apply*/
	private Vector2f anchor1;
	/** Anchor point for second body, on which impulse is going to apply*/
	private Vector2f anchor2;

	/** The first body jointed */
	private Body body1;
	/** The second body jointed */
	private Body body2;

	/** The constant that defines how springy the spring is */
	private float compressConstant;
	/** the original angle to attempt to maintain between the bodies - this may be varied by the spring */
	private float originalAngle;

	/**
	 * Create a new joint
	 * 
	 * @param body1	The first body to be attached on constraint
	 * @param body2 The second body to be attached on constraint
	 * @param anchor1 The anchor point on first body
	 * @param anchor2 The anchor point on second body
	 * @param compressConstant The constant k of hooke's law
	 * @param originalAngle The original angle of the spring
	 */
	public SpringyAngleJoint(Body body1, Body body2, Vector2f anchor1,
			Vector2f anchor2, float compressConstant, float originalAngle) {
		this.body1 = body1;
		this.body2 = body2;
		this.anchor1 = anchor1;
		this.anchor2 = anchor2;
		this.compressConstant = compressConstant;
		this.originalAngle = originalAngle;
	}

	/**
	 * @see net.phys2d.raw.Joint#applyImpulse()
	 */
	public void applyImpulse() {
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
		Matrix2f rot1 = new Matrix2f(body1.getRotation());
		Matrix2f rot2 = new Matrix2f(body2.getRotation());
		Vector2f r1 = MathUtil.mul(rot1, anchor1);
		Vector2f r2 = MathUtil.mul(rot2, anchor2);

		Vector2f p1 = new Vector2f(body1.getPosition());
		p1.add(r1);
		Vector2f p2 = new Vector2f(body2.getPosition());
		p2.add(r2);
		Vector2f dp = new Vector2f(p2);
		dp.sub(p1);
		float length = dp.length();
		// dp.scale(1.0f/length);
		Vector2f V = new Vector2f((float) Math.cos(originalAngle
				+ body1.getRotation()), (float) Math.sin(originalAngle
				+ body1.getRotation()));
		Vector2f ndp = new Vector2f(dp);
		ndp.normalise();
		float torq = (float) Math.asin(MathUtil.cross(ndp, V))
				* compressConstant / invDT;
		float P = torq / length;
		Vector2f n = new Vector2f(ndp.y, -ndp.x);
		Vector2f impulse = new Vector2f(n);
		impulse.scale(P);
		if (!body1.isStatic()) {
			Vector2f accum1 = new Vector2f(impulse);
			accum1.scale(body1.getInvMass());
			body1.adjustVelocity(accum1);
			body1.adjustAngularVelocity((body1.getInvI() * MathUtil.cross(dp,
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
}
