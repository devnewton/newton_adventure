package im.bci.newtonadv.platform.android;

import java.util.Properties;

import android.content.res.AssetManager;
import im.bci.newtonadv.platform.interfaces.ISoundCache;

public class AndroidSoundCache implements ISoundCache {

	public AndroidSoundCache(AssetManager assets, Properties config) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void clearAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearUseless() {
		// TODO Auto-generated method stub

	}

	@Override
	public Playable getSoundIfEnabled(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playMusicIfEnabled(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopMusic() {
		// TODO Auto-generated method stub

	}

}
