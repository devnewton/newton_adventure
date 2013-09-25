package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class Widget {
    
    public List<Widget> getChildren() {
        return children;
    }

    private float x, y, width, height;
    private List<Widget> children = new ArrayList<Widget>();
    
    public boolean isInputWhore() {
        return false;
    }
    
    public void onLeft() {
        Widget child = getFocusedChild();
        if(null != child) {
            child.onLeft();
        }
    }
    
    public void onRight() {
        Widget child = getFocusedChild();
        if(null != child) {
            child.onRight();
        }
    }
    
    public void onUp() {
        Widget child = getFocusedChild();
        if(null != child) {
            child.onUp();
        }
    }
    
    public void onDown() {
        Widget child = getFocusedChild();
        if(null != child) {
            child.onDown();
        }
    }
    
    public void onOK() {
        Widget child = getFocusedChild();
        if(null != child) {
            child.onOK();
        }
    }
    
    public void onCancel() {
        Widget child = getFocusedChild();
        if(null != child) {
            child.onCancel();
        }
    }
    
    public Widget getFocusedChild() {
        return null;
    }

    public void draw() {
        drawChildren();
    }

    protected void drawChildren() {
        for(Widget child : children) {
            child.draw();
        }        
    }

    public void add(Widget child) {
        children.remove(child);
        children.add(child);
    }

    public void remove(Widget child) {
        children.remove(child);
    }

    public float getMinWidth() {
        return 0;
    }
    
    public float getMinHeight() {
        return 0;
    }

    public float getPreferredWidth() {
        return 0;
    }

    public float getPreferredHeight() {
        return 0;
    }
    
    public float getMaxWidth() {
        return 0;
    }

    public float getMaxHeight() {
        return 0;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    
    public Widget findClosestLeftWidget(Widget widget) {
        Widget closestLeftChild = null;
        if (null != widget) {
            
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getX() < widget.getX()) {
                    float lenghtSquared = new Vector2f(w.getX() - widget.getX(), w.getY() - widget.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
        }
        return closestLeftChild;
    }
    public Widget findClosestRightWidget(Widget widget) {
        Widget closestLeftChild = null;
        if (null != widget) {
            
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getX() > widget.getX()) {
                    float lenghtSquared = new Vector2f(w.getX() - widget.getX(), w.getY() - widget.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
        }
        return closestLeftChild;
    }
    
    public Widget findClosestUpWidget(Widget widget) {
        Widget closestLeftChild = null;
        if (null != widget) {
            
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getY() < widget.getY()) {
                    float lenghtSquared = new Vector2f(w.getX() - widget.getX(), w.getY() - widget.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
        }
        return closestLeftChild;
    }
    
    public Widget findClosestDownWidget(Widget widget) {
        Widget closestLeftChild = null;
        if (null != widget) {
            
            float closestLeftChildLengthSquared = Float.MAX_VALUE;
            for (Widget w : getChildren()) {
                if (w.getY() > widget.getY()) {
                    float lenghtSquared = new Vector2f(w.getX() - widget.getX(), w.getY() - widget.getY()).lengthSquared();
                    if (null == closestLeftChild || lenghtSquared < closestLeftChildLengthSquared) {
                        closestLeftChildLengthSquared = lenghtSquared;
                        closestLeftChild = w;
                    }
                }
            }
        }
        return closestLeftChild;
    }

    protected Widget getTopLeftChild() {
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

    public void suckFocus() {
    }

    public boolean isSuckingFocus() {
        return false;
    }
}
