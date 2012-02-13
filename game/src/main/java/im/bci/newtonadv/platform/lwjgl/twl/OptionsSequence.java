package im.bci.newtonadv.platform.lwjgl.twl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;
import im.bci.newtonadv.platform.lwjgl.PlatformFactory;
import im.bci.newtonadv.platform.lwjgl.SoundCache;
import im.bci.newtonadv.score.ScoreServer;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class OptionsSequence implements IOptionsSequence {

	private GUI gui;
	private OptionsGUI optionsGui;
	private Sequence nextSequence;
	private final GameView view;
	private final GameInput input;
	private final Properties config;
	private final ScoreServer scoreServer;
	private final SoundCache soundCache;

	public OptionsSequence(GameView view, GameInput input,
			ScoreServer scoreServer, SoundCache soundCache, Properties config) {
		this.view = view;
		this.input = input;
		this.scoreServer = scoreServer;
		this.soundCache = soundCache;
		this.config = config;
	}

	@Override
	public void start() {
		LWJGLRenderer renderer;
		try {
			renderer = new LWJGLRenderer();
			optionsGui = new OptionsGUI(view, input, scoreServer, soundCache);
			gui = new GUI(optionsGui, renderer);
			ThemeManager themeManager = ThemeManager.createThemeManager(
					getClass().getClassLoader().getResource("twl/theme.xml"),
					renderer);
			gui.applyTheme(themeManager);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		gui.destroy();
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.update();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void processInputs() throws TransitionException {
	}

	@Override
	public void update() throws TransitionException {
		if (optionsGui.okPressed) {
			try {
				applyOptions();
			} catch (LWJGLException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"cannot apply options", e);
			}
			try {
				updateConfig();
				saveConfig();
			} catch (IOException ex) {
				Logger.getLogger(OptionsSequence.class.getName()).log(
						Level.SEVERE, "cannot save options", ex);
			}
			throw new TransitionException(nextSequence);
		}
		if (optionsGui.cancelPressed) {
			throw new TransitionException(nextSequence);
		}
	}

	private void applyOptions() throws LWJGLException {
		view.setDisplayMode(optionsGui.fullscreen.isActive(),
				getSelectedQuality(), getSelectedMode());
		input.keyJump = Keyboard.getKeyIndex(optionsGui.keyJump.getModel()
				.getEntry(optionsGui.keyJump.getSelected()));
		input.keyLeft = Keyboard.getKeyIndex(optionsGui.keyLeft.getModel()
				.getEntry(optionsGui.keyLeft.getSelected()));
		input.keyRight = Keyboard.getKeyIndex(optionsGui.keyRight.getModel()
				.getEntry(optionsGui.keyRight.getSelected()));
		input.keyRotate90Clockwise = Keyboard
				.getKeyIndex(optionsGui.keyRotate90Clockwise
						.getModel()
						.getEntry(optionsGui.keyRotate90Clockwise.getSelected()));
		input.keyRotate90CounterClockwise = Keyboard
				.getKeyIndex(optionsGui.keyRotate90CounterClockwise.getModel()
						.getEntry(
								optionsGui.keyRotate90CounterClockwise
										.getSelected()));
		input.keyRotateClockwise = Keyboard
				.getKeyIndex(optionsGui.keyRotateClockwise.getModel().getEntry(
						optionsGui.keyRotateClockwise.getSelected()));
		input.keyRotateCounterClockwise = Keyboard
				.getKeyIndex(optionsGui.keyRotateCounterClockwise.getModel()
						.getEntry(
								optionsGui.keyRotateCounterClockwise
										.getSelected()));
		scoreServer.setPlayer(optionsGui.scorePlayer.getText());
		scoreServer.setSecret(optionsGui.scoreSecret.getText());
		scoreServer.setServerUrl(optionsGui.scoreServerUrl.getText());
		soundCache.setSoundEnabled(optionsGui.soundEnabled.isActive());
		soundCache.setMusicEnabled(optionsGui.musicEnabled.isActive());
	}

	DisplayMode getSelectedMode() {
		int selectedModeIndex = optionsGui.mode.getSelected();
		if (selectedModeIndex >= 0
				&& selectedModeIndex < optionsGui.mode.getModel()
						.getNumEntries()) {
			return optionsGui.mode.getModel().getEntry(selectedModeIndex);
		} else {
			return Display.getDisplayMode();
		}
	}

	GameViewQuality getSelectedQuality() {
		int selected = optionsGui.quality.getSelected();
		if (selected >= 0
				&& selected < optionsGui.quality.getModel().getNumEntries()) {
			return optionsGui.quality.getModel().getEntry(selected);
		} else {
			return view.getQuality();
		}
	}

	@Override
	public void setNextSequence(Sequence mainMenuSequence) {
		this.nextSequence = mainMenuSequence;
	}

	private void writeConfig(String path) throws FileNotFoundException,
			IOException {
		FileOutputStream os = new FileOutputStream(path);
		try {
			config.store(os, "Newton adventure configuration, see "
					+ PlatformFactory.getDefaultConfigFilePath()
					+ " for example and documentation");
		} finally {
			os.close();
		}

	}

	private void saveConfig() throws IOException {
		File userConfigFile = new File(PlatformFactory.getUserConfigFilePath());

		if (!userConfigFile.exists()) {
			(new File(PlatformFactory.getUserConfigDirPath())).mkdirs();
		}
		writeConfig(userConfigFile.getAbsolutePath());
	}

	private void updateConfig() {
		config.setProperty("view.width", "" + Display.getWidth());
		config.setProperty("view.height", "" + Display.getHeight());
		config.setProperty("view.bpp", ""
				+ Display.getDisplayMode().getBitsPerPixel());
		config.getProperty("view.fullscreen", "" + Display.isFullscreen());
		config.setProperty("view.quality", view.getQuality().toString());

		config.setProperty("key.jump", getKeyFieldName(input.keyJump));
		config.setProperty("key.left", getKeyFieldName(input.keyLeft));
		config.setProperty("key.right", getKeyFieldName(input.keyRight));
		config.setProperty("key.rotate_clockwise",
				getKeyFieldName(input.keyRotateClockwise));
		config.setProperty("key.rotate_counter_clockwise",
				getKeyFieldName(input.keyRotateCounterClockwise));
		config.setProperty("key.rotate_90_clockwise",
				getKeyFieldName(input.keyRotate90Clockwise));
		config.setProperty("key.rotate_90_counter_clockwise",
				getKeyFieldName(input.keyRotate90CounterClockwise));

		config.setProperty("scoreserver.url", scoreServer.getServerUrl());
		config.setProperty("scoreserver.player", scoreServer.getPlayer());
		config.setProperty("scoreserver.secret", scoreServer.getSecret());
		config.setProperty("sound.enabled", "" + soundCache.isSoundEnabled());
		config.setProperty("music.enabled", "" + soundCache.isMusicEnabled());
	}

	private String getKeyFieldName(int key) {
		for (Field field : Keyboard.class.getFields()) {
			try {
				if (field.getName().startsWith("KEY_")
						&& field.getInt(null) == key) {
					return field.getName();
				}
			} catch (Exception ex) {
				Logger.getLogger(OptionsSequence.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
		Logger.getLogger(OptionsSequence.class.getName()).log(Level.SEVERE,
				"Unknow lwjgl key value {0}", key);
		return "";
	}
}
