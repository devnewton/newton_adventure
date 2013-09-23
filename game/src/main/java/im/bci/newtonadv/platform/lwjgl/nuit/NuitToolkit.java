package im.bci.newtonadv.platform.lwjgl.nuit;

import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;

public class NuitToolkit extends Toolkit<Widget, Table, TableLayout>{
    
    private Root root = new Root();

    public Widget getRoot() {
        return root;
    }  

    @Override
    public Cell obtainCell(TableLayout layout) {
        Cell<Widget> cell = new Cell<Widget>();
        cell.setLayout(layout);
        return cell;
    }

    @Override
    public void freeCell(Cell cell) {
    }

    @Override
    public void addChild(Widget parent, Widget child) {
        parent.add(child);
    }

    @Override
    public void removeChild(Widget parent, Widget child) {
        parent.removeChild(child);
    }

    @Override
    public float getMinWidth(Widget widget) {
        return widget.getMinWidth();
    }

    @Override
    public float getMinHeight(Widget widget) {
        return widget.getMinHeight();
    }

    @Override
    public float getPrefWidth(Widget widget) {
        return widget.getPreferredWidth();
    }

    @Override
    public float getPrefHeight(Widget widget) {
        return widget.getPreferredHeight();
    }

    @Override
    public float getMaxWidth(Widget widget) {
        return widget.getMaxWidth();
    }

    @Override
    public float getMaxHeight(Widget widget) {
        return widget.getMaxHeight();
    }

    @Override
    public float getWidth(Widget widget) {
        return widget.getWidth();
    }

    @Override
    public float getHeight(Widget widget) {
        return widget.getHeight();
    }

    @Override
    public void clearDebugRectangles(TableLayout layout) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addDebugRectangle(TableLayout layout, Debug type, float x, float y, float w, float h) {
        // TODO Auto-generated method stub
        
    }
}
