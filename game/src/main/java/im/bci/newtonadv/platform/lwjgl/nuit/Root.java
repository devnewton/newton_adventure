package im.bci.newtonadv.platform.lwjgl.nuit;

import org.lwjgl.opengl.GL11;

public class Root extends Widget {

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
    public void setHeight(float height) {
        super.setHeight(height);
        for (Widget child : getChildren()) {
            child.setHeight(height);
        }
    }

    @Override
    public void draw() {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_TRANSFORM_BIT | GL11.GL_HINT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_SCISSOR_BIT | GL11.GL_LINE_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(getX(), getWidth(), getHeight(), getY(), -1.0, 1.0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        super.draw();

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
