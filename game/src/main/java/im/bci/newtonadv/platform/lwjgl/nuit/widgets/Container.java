package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import org.lwjgl.opengl.GL11;

public class Container extends Widget {

    private Widget focusedChild;

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
        Widget closest = findClosestLeftWidget(currentFocusedChild);
        if (null != closest) {
            focusedChild = closest;
        }
    }

    @Override
    public void onRight() {
        final Widget currentFocusedChild = getFocusedChild();
        Widget closest = findClosestRightWidget(currentFocusedChild);
        if (null != closest) {
            focusedChild = closest;
        }
    }

    @Override
    public void onUp() {
        final Widget currentFocusedChild = getFocusedChild();
        Widget closest = findClosestUpWidget(currentFocusedChild);
        if (null != closest) {
            focusedChild = closest;
        }
    }

    @Override
    public void onDown() {
        final Widget currentFocusedChild = getFocusedChild();
        Widget closest = findClosestDownWidget(currentFocusedChild);
        if (null != closest) {
            focusedChild = closest;
        }
    }

    @Override
    public void draw() {
        drawChildren();
        Widget focused = getFocusedChild();
        if (null != focused) {
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

}