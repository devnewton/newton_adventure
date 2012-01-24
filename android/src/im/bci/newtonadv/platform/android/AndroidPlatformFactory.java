package im.bci.newtonadv.platform.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.res.AssetManager;

import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.IGameInput;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IPlatformFactory;
import im.bci.newtonadv.platform.interfaces.ISoundCache;

public class AndroidPlatformFactory implements IPlatformFactory {

	private AssetManager assets;

	public AndroidPlatformFactory(AssetManager assets) {
		this.assets = assets;
	}

	@Override
	public IGameInput createGameInput(Properties config) throws Exception {
		return new AndroidGameInput(config);
	}

	@Override
	public IGameView createGameView(Properties config) {
		return new AndroidGameView(assets,config);
	}

	@Override
	public ISoundCache createSoundCache(Properties config) {
		return new AndroidSoundCache(assets,config);
	}

	@Override
	public void loadConfig(Properties config) {
		try {
			InputStream f;
			f = assets.open("config.properties");

			try {
				config.load(f);
			} finally {
				f.close();
			}
		} catch (IOException e) {
			Logger.getLogger(AndroidPlatformFactory.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	public IGameData createGameData(Properties config) {
		return new AndroidGameData(assets);
	}

}
