/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.IGameInput;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.platform.lwjgl.twl.OptionsSequence;
import im.bci.newtonadv.score.ScoreServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author devnewton
 */
public class PlatformSpecific implements IPlatformSpecific {

	private GameView view;
	private GameInput input;
	private Properties config;
	private ScoreServer scoreServer;
	private IGameData data;
	private SoundCache soundCache;
	private IOptionsSequence options;
	
	public PlatformSpecific() throws Exception {
        loadConfig();

        createGameData();
        createSoundCache();
        createGameView();
        createGameInput();
        createScoreServer();
        createOptionsSequence();

	}

	private SoundCache createSoundCache() {
		if (null == data)
			throw new RuntimeException("create IGameData before  SoundCache");
		if (null == soundCache)
			soundCache = new SoundCache(data, config);
		return soundCache;
	}

	private GameView createGameView() {
		if (null == data)
			throw new RuntimeException("create IGameData before IGameView");
		if (view == null) {
			view = new GameView(data, config);
		}
		return view;
	}

	private GameInput createGameInput() throws Exception {
		if (null == input) {
			input = new GameInput(config);
		}
		return input;
	}

	private void loadConfig() {
		try {
			URL configFilePath = getUserOrDefaultConfigFilePath();
			Logger.getLogger(PlatformSpecific.class.getName()).log(Level.INFO,
					"Load config from file {0}", configFilePath);

			InputStream f = configFilePath.openStream();
			try {
				config = new Properties();
				config.load(f);
			} finally {
				f.close();
			}
		} catch (IOException e) {
			Logger.getLogger(PlatformSpecific.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	private IGameData createGameData() {
		if (data == null) {
			String dataDir = config.getProperty("data.dir");
			if(null != dataDir) {
				data = new FileGameData(dataDir);
			} else {
				data = new EmbeddedGameData();
			}
		}
		return data;
	}

	private IOptionsSequence createOptionsSequence() {
		if (null == view) {
			throw new RuntimeException(
					"create IGameView before IOptionsSequence");
		}
		if (null == input) {
			throw new RuntimeException(
					"create IGameInput before IOptionsSequence");
		}
		if (null == scoreServer) {
			throw new RuntimeException(
					"create ScoreServer before creating IOptionsSequence");
		}
		if (null == config) {
			throw new RuntimeException(
					"load config before creating IOptionsSequence");
		}
		options = new OptionsSequence(this, view, input, scoreServer, soundCache, config);
		return options;
	}

	public static URL getDefaultConfigFilePath() {
		return PlatformSpecific.class.getClassLoader().getResource(
				"config.properties");
	}

	public static String getUserConfigDirPath() {
		String configDirPath = System.getenv("XDG_CONFIG_HOME");
		if (null == configDirPath) {
			configDirPath = System.getProperty("user.home") + File.separator
					+ ".config";
		}
		return configDirPath + File.separator + "newton_adventure";
	}

	public static String getUserConfigFilePath() {
		return getUserConfigDirPath() + File.separator + "config.properties";
	}

	public static URL getUserOrDefaultConfigFilePath() {
		File f = new File(getUserConfigFilePath());
		if (f.exists() && f.canRead()) {
			try {
				return f.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid user config file path" + f);
			}
		}
		return getDefaultConfigFilePath();
	}

	private ScoreServer createScoreServer() {
		scoreServer = new ScoreServer(config);
		return scoreServer;
	}

	private void writeConfig(String path) throws FileNotFoundException,
			IOException {
		FileOutputStream os = new FileOutputStream(path);
		try {
			config.store(os, "Newton adventure configuration, see "
					+ PlatformSpecific.getDefaultConfigFilePath()
					+ " for example and documentation");
		} finally {
			os.close();
		}

	}

	@Override
	public void saveConfig() {
		File userConfigFile = new File(PlatformSpecific.getUserConfigFilePath());

		if (!userConfigFile.exists()) {
			(new File(PlatformSpecific.getUserConfigDirPath())).mkdirs();
		}
		try {
			writeConfig(userConfigFile.getAbsolutePath());
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot save config", e);
		}
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
		return options;
	}

	@Override
	public ScoreServer getScoreServer() {
		return scoreServer;
	}
}