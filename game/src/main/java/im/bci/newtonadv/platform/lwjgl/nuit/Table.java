package im.bci.newtonadv.platform.lwjgl.nuit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Value;

public class Table extends Widget {

    private final TableLayout layout;

    private Widget focusedChild;
    
    public Cell<Widget> cell(Widget widget) {
        add(widget);
        return layout.add(widget);
    }
    
    public Cell<Widget> row() {
        return layout.row();
    }

    public Cell<Widget> columnDefaults(int column) {
        return layout.columnDefaults(column);
    }

    public void clear() {
        layout.clear();
    }

    public Cell defaults() {
        return layout.defaults();
    }

    public TableLayout pad(Value top, Value left, Value bottom, Value right) {
        return layout.pad(top, left, bottom, right);
    }

    public TableLayout pad(float pad) {
        return layout.pad(pad);
    }

    public TableLayout pad(float top, float left, float bottom, float right) {
        return layout.pad(top, left, bottom, right);
    }

    public TableLayout padTop(float padTop) {
        return layout.padTop(padTop);
    }

    public TableLayout padLeft(float padLeft) {
        return layout.padLeft(padLeft);
    }

    public TableLayout padBottom(float padBottom) {
        return layout.padBottom(padBottom);
    }

    public TableLayout padRight(float padRight) {
        return layout.padRight(padRight);
    }

    public TableLayout align(int align) {
        return layout.align(align);
    }

    public TableLayout center() {
        return layout.center();
    }

    public TableLayout top() {
        return layout.top();
    }

    public TableLayout left() {
        return layout.left();
    }

    public TableLayout bottom() {
        return layout.bottom();
    }

    public TableLayout right() {
        return layout.right();
    }

    Table(NuitToolkit toolkit) {
        layout = new TableLayout(toolkit);
        layout.setTable(this);
    }

    @Override
    public Widget getFocusedChild() {
        if (null == focusedChild) {
            focusedChild = getTopLeftChild();

        }
        return focusedChild;
    }

    @Override
    public void onLeft() {
        final Widget currentFocusedChild = getFocusedChild();
        if (null != currentFocusedChild) {
            Widget closestLeftChild = null;
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getX() < currentFocusedChild.getX()) {
                    float lenghtSquared = new Vector2f(w.getX() - currentFocusedChild.getX(), w.getY() - currentFocusedChild.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
            if (null != closestLeftChild) {
                focusedChild = closestLeftChild;
            }
        }
    }

    @Override
    public void onRight() {
        final Widget currentFocusedChild = getFocusedChild();
        if (null != currentFocusedChild) {
            Widget closestLeftChild = null;
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getX() > currentFocusedChild.getX()) {
                    float lenghtSquared = new Vector2f(w.getX() - currentFocusedChild.getX(), w.getY() - currentFocusedChild.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
            if (null != closestLeftChild) {
                focusedChild = closestLeftChild;
            }
        }
    }

    @Override
    public void onUp() {
        final Widget currentFocusedChild = getFocusedChild();
        if (null != currentFocusedChild) {
            Widget closestLeftChild = null;
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getY() < currentFocusedChild.getY()) {
                    float lenghtSquared = new Vector2f(w.getX() - currentFocusedChild.getX(), w.getY() - currentFocusedChild.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
            if (null != closestLeftChild) {
                focusedChild = closestLeftChild;
            }
        }
    }

    @Override
    public void onDown() {
        final Widget currentFocusedChild = getFocusedChild();
        if (null != currentFocusedChild) {
            Widget closestLeftChild = null;
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getY() > currentFocusedChild.getY()) {
                    float lenghtSquared = new Vector2f(w.getX() - currentFocusedChild.getX(), w.getY() - currentFocusedChild.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
            if (null != closestLeftChild) {
                focusedChild = closestLeftChild;
            }
        }
    }

    private Widget getTopLeftChild() {
        return Collections.min(getChildren(), new Comparator<Widget>() {

            @Override
            public int compare(Widget w1, Widget w2) {
                int result = Float.compare(w1.getY(), w2.getY());
                if (result == 0) {
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
        layout();
    }

    public void layout() {
        layout.layout(getX(), getY(), getWidth(), getHeight());
        List<Cell> cells = layout.getCells();
        for (int i = 0, n = cells.size(); i < n; i++) {
            Cell c = cells.get(i);
            if (c.getIgnore()) {
                continue;
            }
            Widget cellWidget = (Widget) c.getWidget();
            cellWidget.setX(c.getWidgetX());
            cellWidget.setY(c.getWidgetY());
            cellWidget.setWidth(c.getWidgetWidth());
            cellWidget.setHeight(c.getWidgetHeight());
        }
    }

    @Override
    public void draw() {
        drawChildren();
        Widget focused = getFocusedChild();
        GL11.glLineWidth(5.0f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(focused.getX(), focused.getY());
        GL11.glVertex2f(focused.getX() + focused.getWidth(), focused.getY());
        GL11.glVertex2f(focused.getX() + focused.getWidth(), focused.getY() + focused.getHeight());
        GL11.glVertex2f(focused.getX(), focused.getY() + focused.getHeight());
        GL11.glVertex2f(focused.getX(), focused.getY());
        GL11.glEnd();
        GL11.glLineWidth(1.0f);
    }
}
