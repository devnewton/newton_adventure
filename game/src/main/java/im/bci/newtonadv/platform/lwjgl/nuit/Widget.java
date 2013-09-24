package im.bci.newtonadv.platform.lwjgl.nuit;

import java.util.ArrayList;
import java.util.List;

public class Widget {
    
    public List<Widget> getChildren() {
        return children;
    }

    private float x, y, width, height;
    private List<Widget> children = new ArrayList<Widget>();
    
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
}
