package net.phys2d.raw;

/**
 * A joint connecting two bodies
 * 
 * @author Kevin Glass
 */
public interface Joint {
	
	/**
	 * Set the relaxtion value on this joint. This value determines
	 * how loose the joint will be
	 * 
	 * @param relaxation The relaxation value
	 */
	public abstract void setRelaxation(float relaxation);

	/**
	 * Get the first body attached to this joint
	 * 
	 * @return The first body attached to this joint
	 */
	public abstract Body getBody1();

	/**
	 * Get the second body attached to this joint
	 * 
	 * @return The second body attached to this joint
	 */
	public abstract Body getBody2();
	
	/**
	 * Apply the impulse caused by the joint to the bodies attached.
	 */
	void applyImpulse();
	
	/**
	 * Precaculate everything and apply initial impulse before the
	 * simulation step takes place
	 * 
	 * @param invDT The amount of time the simulation is being stepped by
	 */
	void preStep(float invDT);
	
}