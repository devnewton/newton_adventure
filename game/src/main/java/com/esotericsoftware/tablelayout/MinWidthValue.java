package com.esotericsoftware.tablelayout;

public class MinWidthValue<C, T extends C> extends CellValue<C, T> {
    
    MinWidthValue(Toolkit<C, T> toolkit) {
        super(toolkit);
    }

    public float get (Cell<C,T> cell) {
        if (cell == null) throw new RuntimeException("minWidth can only be set on a cell property.");
        C widget = cell.widget;
        if (widget == null) return 0;
        return toolkit.getMinWidth(widget);
    }
}
