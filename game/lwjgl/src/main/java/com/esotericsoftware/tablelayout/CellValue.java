package com.esotericsoftware.tablelayout;

/** A value that is only valid for use with a cell.
 * @author Nathan Sweet */
abstract public class CellValue<C, T extends C> extends Value<C, T> {
	CellValue(Toolkit<C, T> toolkit) {
        super(toolkit);
    }

    public float get (Object table) {
		throw new UnsupportedOperationException("This value can only be used for a cell property.");
	}
}