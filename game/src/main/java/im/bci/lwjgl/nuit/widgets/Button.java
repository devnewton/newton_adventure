package im.bci.lwjgl.nuit.widgets;

import org.lwjgl.opengl.GL11;

import im.bci.lwjgl.nuit.NuitToolkit;
import im.bci.lwjgl.nuit.utils.TrueTypeFont;

public class Button extends Widget {

    private String text;
    private NuitToolkit toolkit;

    public Button(NuitToolkit toolkit, String text) {
        this.toolkit = toolkit;
        this.text = text;
    }

    @Override
    public void draw() {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        TrueTypeFont font = toolkit.getFont();
        GL11.glTranslatef(getX() + getWidth()/2.0f - font.getWidth(text)/3.0f, getY() + getHeight()/2.0f + font.getHeight(text) / 2.0f, 0.0f);
        GL11.glScalef(1, -1, 1);
        font.drawString(text);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
    
    @Override
    public void onMouseClick(float mouseX, float mouseY) {
    	this.onOK();
    }
    
    @Override
    public float getMinWidth() {
        return toolkit.getFont().getWidth(text);
    }
    
    @Override
    public float getMinHeight() {
        return toolkit.getFont().getHeight(text);
    }
    
}