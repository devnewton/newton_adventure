package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import im.bci.newtonadv.platform.lwjgl.nuit.NuitToolkit;

import com.esotericsoftware.tablelayout.BaseTableLayout;

public class TableLayout extends BaseTableLayout<Widget, Table> {

    public TableLayout(NuitToolkit toolkit) {
        super(toolkit);
    }

    @Override
    public void invalidateHierarchy() {
    }
}
