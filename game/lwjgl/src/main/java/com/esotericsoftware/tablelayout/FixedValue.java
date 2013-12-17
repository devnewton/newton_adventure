package com.esotericsoftware.tablelayout;

/** A fixed value that is not computed each time it is used.
 * @author Nathan Sweet */
public class FixedValue<C, T extends C> extends Value<C, T> {
	private float value;

	public FixedValue (Toolkit<C,T>toolkit, float value) {
	    super(toolkit);
		this.value = value;
	}

	public void set (float value) {
		this.value = value;
	}

	public float get (T table) {
		return value;
	}

	public float get (Cell<C,T> cell) {
		return value;
	}
}