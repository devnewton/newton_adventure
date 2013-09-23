package im.bci.newtonadv.platform.lwjgl.nuit;

import java.util.ArrayList;
import java.util.List;

public class Widget {
    
    public List<Widget> getChildren() {
        return children;
    }

    private float x, y, width, height;
    private List<Widget> children = new ArrayList<Widget>();

    public void draw() {
        drawChildren();
    }

    private void drawChildren() {
        for(Widget child : children) {
            child.draw();
        }        
    }

    public void add(Widget child) {
        children.add(child);        
    }

    public void removeChild(Widget child) {
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
