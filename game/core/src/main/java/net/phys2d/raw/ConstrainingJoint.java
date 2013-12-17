package net.phys2d.raw;

import net.phys2d.math.Vector2f;

/**
 * A joint which only applys forces when the bodie's attempt to get too far apart, as 
 * suggested by Jesse on:
 * 
 * http://slick.javaunlimited.net/viewtopic.php?t=333
 *
 * @author Jesse
 * @author kevin
 */
public class ConstrainingJoint implements Joint {
	/** The joint that is used to keep the bodies together if they are seperating */
	private BasicJoint realJoint;
	/** The distance the bodies must be apart before force is applied */
	private float distance;
	/** The first body jointed */
	private Body body1;
	/** The second body jointed */
	private Body body2;
	/** True if the joint is active this update */
	private boolean active;
	
	/**
	 * Create a new joint
	 * 
	 * @param body1 The first body jointed
	 * @param body2 The second body jointed
	 * @param anchor The anchor point for the underlying basic joint
	 * @param distance The distance the bodies must be apart before force is applied
	 */
	public ConstrainingJoint(Body body1, Body body2, Vector2f anchor, float distance) {
		this.distance = distance;
		this.body1 = body1;
		this.body2 = body2;
		realJoint = new BasicJoint(body1, body2, anchor);
	}
	
	/**
	 * Check if this contraining joint is currently pulling the bodies back together
	 * 
	 * @return True if the joint is holding the bodies together
	 */
	public boolean isActive() {
		if (body1.getPosition().distanceSquared(body2.getPosition()) < distance) {
			Vector2f to2 = new Vector2f(body2.getPosition());
			to2.sub(body1.getPosition());
			to2.normalise();
			Vector2f vel = new Vector2f(body1.getVelocity());
			vel.normalise();
			if (body1.getVelocity().dot(to2) < 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @see net.phys2d.raw.Joint#applyImpulse()
	 */
	public void applyImpulse() {
		if (active) {
			realJoint.applyImpulse();
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
		if (isActive()) {
			active = true;
			realJoint.preStep(invDT);
		} else {
			active = false;
		}
	}

	/**
	 * @see net.phys2d.raw.Joint#setRelaxation(float)
	 */
	public void setRelaxation(float relaxation) {
	}

}
