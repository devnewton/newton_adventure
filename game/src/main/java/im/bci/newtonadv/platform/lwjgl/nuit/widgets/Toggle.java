package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import org.lwjgl.opengl.GL11;

import im.bci.newtonadv.platform.lwjgl.TrueTypeFont;
import im.bci.newtonadv.platform.lwjgl.nuit.NuitToolkit;

public class Toggle extends Widget {
    private NuitToolkit toolkit;
    private String enabledText = "Yes", disabledText = "No";
    private boolean enabled;

    public Toggle(NuitToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public String getEnabledText() {
        return enabledText;
    }

    public void setEnabledText(String enabledText) {
        this.enabledText = enabledText;
    }

    public String getDisabledText() {
        return disabledText;
    }

    public void setDisabledText(String disabledText) {
        this.disabledText = disabledText;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onOK() {
        this.enabled = !this.enabled;
    }
    
    @Override
    public void onMouseClick(float mouseX, float mouseY) {
    	onOK();
    }

    @Override
    public void draw() {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        TrueTypeFont font = toolkit.getFont();
        String text = enabled ? enabledText : disabledText;
        GL11.glTranslatef(getX() + getWidth() / 2.0f - font.getWidth(text) / 4.0f, getY() + getHeight() / 2.0f + font.getHeight(text) / 2.0f, 0.0f);
        GL11.glScalef(1, -1, 1);
        font.drawString(text);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
