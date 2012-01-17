package im.bci.newtonadv.platform.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class AndroidActivity extends Activity {
	 
    private GLSurfaceView mGLView;
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new AndroidGameView(this);
        setContentView(mGLView);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }

}