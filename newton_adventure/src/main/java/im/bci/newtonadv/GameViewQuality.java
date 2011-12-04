package im.bci.newtonadv;

import org.lwjgl.opengl.GL11;

public enum GameViewQuality {

    DEFAULT,
    NICEST,
    FASTEST;

    public int toGL() {
        switch(this) {
            case NICEST:
                return GL11.GL_NICEST;
            case FASTEST:
                return GL11.GL_FASTEST;
            default:
                return GL11.GL_DONT_CARE;
        }
    }
}
