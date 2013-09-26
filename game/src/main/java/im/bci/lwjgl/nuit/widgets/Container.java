package im.bci.lwjgl.nuit.widgets;

import org.lwjgl.opengl.GL11;

public class Container extends Widget {

    private Widget focusedChild;

    @Override
    public Widget getFocusedChild() {
        if (null == focusedChild) {
            focusedChild = getTopLeftFocusableChild();
        }
        return focusedChild;
    }

    @Override
    public void onLeft() {
        if (isFocusSucked()) {
            super.onLeft();
        } else {
            final Widget currentFocusedChild = getFocusedChild();
            Widget closest = findClosestLeftFocusableWidget(currentFocusedChild);
            if (null != closest) {
                focusedChild = closest;
            }
        }
    }

    private boolean isFocusSucked() {
        final Widget currentFocusedChild = getFocusedChild();
        return null == currentFocusedChild || currentFocusedChild.isSuckingFocus();
    }

    @Override
    public void onRight() {
        if (isFocusSucked()) {
            super.onRight();
        } else {
            final Widget currentFocusedChild = getFocusedChild();
            Widget closest = findClosestRightFocusableWidget(currentFocusedChild);
            if (null != closest) {
                focusedChild = closest;
            }
        }
    }

    @Override
    public void onUp() {
        if (isFocusSucked()) {
            super.onUp();
        } else {
            final Widget currentFocusedChild = getFocusedChild();
            Widget closest = findClosestUpFocusableWidget(currentFocusedChild);
            if (null != closest) {
                focusedChild = closest;
            }
        }
    }

    @Override
    public void onDown() {
        if (isFocusSucked()) {
            super.onDown();
        } else {
            final Widget currentFocusedChild = getFocusedChild();
            Widget closest = findClosestDownFocusableWidget(currentFocusedChild);
            if (null != closest) {
                focusedChild = closest;
            }
        }
    }

    @Override
    public void onOK() {
        final Widget currentFocusedChild = getFocusedChild();
        if (null != currentFocusedChild) {
            if (currentFocusedChild.isFocusWhore() && !currentFocusedChild.isSuckingFocus()) {
                currentFocusedChild.suckFocus();
                return;
            }
        }
        super.onOK();
    }

    @Override
    public void onCancel() {
        if (isFocusSucked()) {
            super.onCancel();
        }
    }

    @Override
    public void draw() {
        drawChildren();
        Widget focused = getFocusedChild();
        if (null != focused) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(2.0f);
            if(isFocusSucked()) {
                GL11.glColor3f(0.5f, 0.5f, 0.5f);
            }
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex2f(focused.getX(), focused.getY());
            GL11.glVertex2f(focused.getX() + focused.getWidth(), focused.getY());
            GL11.glVertex2f(focused.getX() + focused.getWidth(), focused.getY() + focused.getHeight());
            GL11.glVertex2f(focused.getX(), focused.getY() + focused.getHeight());
            GL11.glVertex2f(focused.getX(), focused.getY());
            GL11.glEnd();
            GL11.glColor3f(1, 1, 1);
            GL11.glLineWidth(1.0f);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }
    
    @Override
	public void onMouseMove(float mouseX, float mouseY) {
        for(Widget child : getChildren()) {
            if(mouseX >= child.getX() && mouseX <= (child.getX() + child.getWidth()) && mouseY >= child.getY() && mouseY <= (child.getY() + child.getHeight())) {
            	if(child.isFocusable() && !isFocusSucked()) {
            		focusedChild = child;
            	}
            	child.onMouseMove(mouseX, mouseY);
            }
        }
	}
}