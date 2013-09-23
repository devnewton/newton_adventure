package im.bci.newtonadv.platform.lwjgl.nuit;

import java.util.List;

import com.esotericsoftware.tablelayout.Cell;

public class Table extends Widget {
    
    final TableLayout layout;
    
    Table(NuitToolkit toolkit) {
        layout = new TableLayout(toolkit);
        layout.setTable(this);
    }
    
    @Override
    public void setX(float x) {
        super.setX(x);
        layout();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        layout();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        layout();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        for (Widget child : getChildren()) {
            child.setHeight(height);
        }
    }

    public void layout() {
        layout.layout(getX(), getY(), getWidth(), getHeight());
        List<Cell> cells = layout.getCells();
        for (int i = 0, n = cells.size(); i < n; i++) {
            Cell c = cells.get(i);
            if (c.getIgnore()) {
                continue;
            }
            Widget cellWidget = (Widget)c.getWidget();
            cellWidget.setX(c.getWidgetX());
            cellWidget.setY(c.getWidgetY());
            cellWidget.setWidth(c.getWidgetWidth());
            cellWidget.setHeight(c.getWidgetHeight());
        }
    }

    @Override
    public void draw() {       
    }
}
