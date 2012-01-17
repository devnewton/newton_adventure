package im.bci.newtonadv.platform.android;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class AndroidGameView extends GLSurfaceView {

	public AndroidGameView(Context context) {
		super(context);
		
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new AndroidGameRenderer());
	}
}
