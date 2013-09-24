package im.bci.newtonadv.platform.lwjgl.nuit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.esotericsoftware.tablelayout.Cell;

public class Table extends Widget {
    
    private final TableLayout layout;
    private Widget focusedChild;
    
    Table(NuitToolkit toolkit) {
        layout = new TableLayout(toolkit);
        layout.setTable(this);
    }
    
    @Override
    public Widget getFocusedChild() {
        if(null == focusedChild) {
            focusedChild = getTopLeftChild();

        }
        return focusedChild;
    }
    
    private Widget getTopLeftChild() {
        return Collections.min(getChildren(), new Comparator<Widget>() {

            @Override
            public int compare(Widget w1, Widget w2) {
                int result = Float.compare(w1.getY(), w2.getY());
                if(result == 0) {
                    result = Float.compare(w1.getX(), w2.getX());
                }
                return result;
            }
        });
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
        super.drawChildren();
    }
}
