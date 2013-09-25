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
        if (isFocusSucked()) {
            super.onLeft();
        } else {
            final Widget currentFocusedChild = getFocusedChild();
            Widget closest = findClosestLeftWidget(currentFocusedChild);
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
            Widget closest = findClosestRightWidget(currentFocusedChild);
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
            Widget closest = findClosestUpWidget(currentFocusedChild);
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
            Widget closest = findClosestDownWidget(currentFocusedChild);
            if (null != closest) {
                focusedChild = closest;
            }
        }
    }

    @Override
    public void onOK() {
        final Widget currentFocusedChild = getFocusedChild();
        if (null != currentFocusedChild) {
            if (currentFocusedChild.isInputWhore() && !currentFocusedChild.isSuckingFocus()) {
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
            GL11.glLineWidth(5.0f);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex2f(focused.getX(), focused.getY());
            GL11.glVertex2f(focused.getX() + focused.getWidth(), focused.getY());
            GL11.glVertex2f(focused.getX() + focused.getWidth(), focused.getY() + focused.getHeight());
            GL11.glVertex2f(focused.getX(), focused.getY() + focused.getHeight());
            GL11.glVertex2f(focused.getX(), focused.getY());
            GL11.glEnd();
            GL11.glLineWidth(1.0f);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

}