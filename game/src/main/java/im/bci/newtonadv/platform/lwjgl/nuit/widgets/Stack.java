package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import java.util.List;

public class Stack extends Widget {
    
    public void show(Widget w) {
        add(w);
        w.onShow();
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        for (Widget child : getChildren()) {
            child.setX(x);
        }
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        for (Widget child : getChildren()) {
            child.setY(y);
        }
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        for (Widget child : getChildren()) {
            child.setWidth(width);
        }
    }

    @Override
    public void add(Widget child) {
        super.add(child);
        child.setX(getX());
        child.setY(getY());
        child.setWidth(getWidth());
        child.setHeight(getHeight());
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        for (Widget child : getChildren()) {
            child.setHeight(height);
        }
    }

    @Override
    public Widget getFocusedChild() {
        List<Widget> children = getChildren();
        int size = children.size();
        if (size > 0) {
            return children.get(size - 1);
        } else {
            return null;
        }
    }

    @Override
    public void draw() {
        Widget child = getFocusedChild();
        if (null != child) {
            child.draw();
        }
    }
    
    @Override
	public void onMouseMove(float mouseX, float mouseY) {
		Widget child = getFocusedChild();
        if (null != child) {
            child.onMouseMove(mouseX, mouseY);
        }
	}
    
    @Override
	public void onMouseClick(float mouseX, float mouseY) {
		Widget child = getFocusedChild();
        if (null != child) {
            child.onMouseClick(mouseX, mouseY);
        }
	}

}