package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import im.bci.newtonadv.platform.lwjgl.nuit.NuitToolkit;
import im.bci.newtonadv.platform.lwjgl.nuit.utils.LwjglHelper;

import org.lwjgl.opengl.GL11;

public class Root extends Stack {
    
    private NuitToolkit toolkit;

    public Root(NuitToolkit tk) {
        this.toolkit = tk;
        setWidth(1280);
        setHeight(800);
    }

    @Override
    public void draw() {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_TRANSFORM_BIT | GL11.GL_HINT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_SCISSOR_BIT | GL11.GL_LINE_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glViewport(0, 0, LwjglHelper.getWidth(), LwjglHelper.getHeight());
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

    public void update() {
        toolkit.update(this);
        for(Widget child : getChildren()) {
            child.update();
        }
    }
}
