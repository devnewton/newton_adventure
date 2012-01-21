package im.bci.newtonadv.platform.android;

import android.opengl.GLES10;

import im.bci.newtonadv.platform.interfaces.ITexture;

public class AndroidTexture implements ITexture {
	
    private int id;
    private int width, height;

    public AndroidTexture(int width, int height) {
        int[] ids = new int[1];
        GLES10.glGenTextures(1, ids, 0);
        id = ids[0];
        this.width = width;
        this.height = height;
    }

    int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void bind() {
    	GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, id);
    }
}
