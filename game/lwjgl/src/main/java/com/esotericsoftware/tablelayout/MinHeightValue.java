package com.esotericsoftware.tablelayout;

public class MinHeightValue<C, T extends C> extends CellValue<C, T> {
    
    MinHeightValue(Toolkit<C, T> toolkit) {
        super(toolkit);
    }

    public float get (Cell<C,T> cell) {
        if (cell == null) throw new RuntimeException("minHeight can only be set on a cell property.");
        C widget = cell.widget;
        if (widget == null) return 0;
        return toolkit.getMinHeight(widget);
    }
}
