package im.bci.lwjgl.nuit.widgets;

import im.bci.lwjgl.nuit.NuitToolkit;

import com.esotericsoftware.tablelayout.BaseTableLayout;

public class TableLayout extends BaseTableLayout<Widget, Table> {

    public TableLayout(NuitToolkit toolkit) {
        super(toolkit);
    }

    @Override
    public void invalidateHierarchy() {
    }
}
