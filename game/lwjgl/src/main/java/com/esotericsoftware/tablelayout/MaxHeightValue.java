package com.esotericsoftware.tablelayout;

public class MaxHeightValue<C, T extends C> extends CellValue<C, T> {
    
    MaxHeightValue(Toolkit<C, T> toolkit) {
        super(toolkit);
    }

    public float get (Cell<C,T> cell) {
        if (cell == null) throw new RuntimeException("maxHeight can only be set on a cell property.");
        C widget = cell.widget;
        if (widget == null) return 0;
        return toolkit.getMaxHeight(widget);
    }
}
