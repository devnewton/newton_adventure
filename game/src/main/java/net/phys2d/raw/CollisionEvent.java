package net.phys2d.raw;

import net.phys2d.math.ROVector2f;

/**
 * An event describing a collision between two bodies
 * 
 * @author Kevin Glass
 */
public class CollisionEvent {
	/** The time of the collision detection */
	private float time;
	/** The first body in the collision */
	private Body body1;
	/** The second body in the collision */
	private Body body2;
	/** The point of the collision */
	private ROVector2f point;
	/** The normal at the collision */
	private ROVector2f normal;
	/** The penetration caused by the collision */
	private float depth;
	
	/**
	 * Create a new event describing a contact 
	 * 
	 * @param time The time of the collision
	 * @param body1 The first body in the collision
	 * @param body2 The second body in the collision
	 * @param point The point of collision (not always perfect - accepts penetration)
	 * @param normal The normal of collision
	 * @param depth The penetration of of the contact
	 */
	public CollisionEvent(float time, Body body1, Body body2, ROVector2f point, ROVector2f normal, float depth) {
		this.time = time;
		this.body1 = body1;
		this.body2 = body2;
		this.point = point;
		this.normal = normal;
		this.depth = depth;
	}
	
	/**
	 * Get the time of the collision
	 * 
	 * @return The time of the collision
	 */
	public float getTime() {
		return time;
	}
	
	/**
	 * Get the first body in the collision
	 * 
	 * @return The first body in the collision
	 */
	public Body getBodyA() {
		return body1;
	}

	/**
	 * Get the second body in the collision
	 * 
	 * @return The second body in the collision
	 */
	public Body getBodyB() {
		return body2;
	}
	
	/**
	 * Get the normal at the collision point
	 * 
	 * @return The normal at the collision point
	 */
	public ROVector2f getNormal() {
		return normal;
	}
	
	/**
	 * Get the point where the collision occured
	 * 
	 * @return The point where the collision occured
	 */
	public ROVector2f getPoint() {
		return point;
	}
	
	/**
	 * Get the penetration depth caused by the collision
	 * 
	 * @return The penetration depth caused by the collision
	 */
	public float getPenetrationDepth() {
		return depth;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[Collision \r\n"+
			   " body A: "+body1+"\r\n"+
			   " body B: "+body2+"\r\n"+
			   " contact: "+point+"\r\n"+
			   " normal: "+normal+"\r\n"+
			   " penetration: "+depth+"\r\n";
	}
}
