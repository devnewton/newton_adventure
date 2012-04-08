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
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformFactory;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.score.ScoreServer;

public class AndroidPlatformFactory implements IPlatformFactory {

	private AssetManager assets;
	private Properties config;
	private AndroidGameInput input;
	private AndroidGameView view;
	private AndroidSoundCache soundCache;
	private AndroidGameData data;
	private ScoreServer scoreServer;

	public AndroidPlatformFactory(AssetManager assets) throws Exception {
		this.assets = assets;
        loadConfig();

        createGameData();
        createSoundCache();
        createGameView();
        createGameInput();
        createScoreServer();
	}

	private void createGameInput() throws Exception {
		input = new AndroidGameInput(config);
	}

	private void createGameView() {
		view = new AndroidGameView(assets,config);
	}

	private void createSoundCache() {
		soundCache = new AndroidSoundCache(assets,config);
	}

	private void loadConfig() {
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

	private void createGameData() {
		data = new AndroidGameData(assets);
	}


	private void createScoreServer() {
		scoreServer = new ScoreServer(config);
	}

	@Override
	public Properties getConfig() {
		return config;
	}

	@Override
	public IGameInput getGameInput() {
		return input;
	}

	@Override
	public IGameView getGameView() {
		return view;
	}

	@Override
	public ISoundCache getSoundCache() {
		return soundCache;
	}

	@Override
	public IGameData getGameData() {
		return data;
	}

	@Override
	public IOptionsSequence getOptionsSequence() {
		return null;
	}

	@Override
	public ScoreServer getScoreServer() {
		return scoreServer;
	}

	@Override
	public void saveConfig() {
		// TODO Auto-generated method stub
	}
}
