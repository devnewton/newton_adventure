package im.bci.newtonadv.platform.lwjgl.nuit;

import com.esotericsoftware.tablelayout.BaseTableLayout;

public class TableLayout extends BaseTableLayout<Widget, Table, TableLayout, NuitToolkit> {

    public TableLayout(NuitToolkit toolkit) {
        super(toolkit);
    }

    @Override
    public void invalidateHierarchy() {
    }

}
