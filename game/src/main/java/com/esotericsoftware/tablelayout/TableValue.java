package com.esotericsoftware.tablelayout;

/** A value that is valid for use with a table or a cell.
 * @author Nathan Sweet */
abstract public class TableValue<C, T extends C> extends Value<C, T> {
	TableValue(Toolkit<C, T> toolkit) {
        super(toolkit);
    }

    public float get (Cell<C,T> cell) {
		return get((T)cell.getLayout().getTable());
	}
}