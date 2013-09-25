package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import org.lwjgl.opengl.GL11;

public class ColoredRectangle extends Widget {
    
    private float r, g, b;

    public ColoredRectangle(float r, float g, float b) {
        super();
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void draw() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor3f(r, g, b);
        GL11.glRectf(getX(), getY(), getX() + getWidth(), getY() + getHeight());    
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
