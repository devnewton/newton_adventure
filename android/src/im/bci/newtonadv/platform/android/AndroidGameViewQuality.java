package im.bci.newtonadv.platform.android;

import javax.microedition.khronos.opengles.GL10;

enum AndroidGameViewQuality {

    DEFAULT,
    NICEST,
    FASTEST;

    int toGL() {
        switch(this) {
            case NICEST:
                return GL10.GL_NICEST;
            case FASTEST:
                return GL10.GL_FASTEST;
            default:
                return GL10.GL_DONT_CARE;
        }
    }
}
