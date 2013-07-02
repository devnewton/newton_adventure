/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.lwjgl.twl;

import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.platform.lwjgl.GameCloseException;
import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;
import im.bci.newtonadv.platform.lwjgl.twl.OptionsGUI.ControllerItem;
import im.bci.newtonadv.platform.lwjgl.twl.OptionsGUI.InputChoice;
import im.bci.newtonadv.platform.lwjgl.twl.OptionsGUI.JoyButtonItem;
import im.bci.newtonadv.score.ScoreServer;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class OptionsSequence implements IOptionsSequence {

	private GUI gui;
	private OptionsGUI optionsGui;
	private Sequence nextSequence;
	private final GameView view;
	private final GameInput input;
	private final Properties config;
	private final ScoreServer scoreServer;
	private final ISoundCache soundCache;
	private IPlatformSpecific platform;

	public OptionsSequence(IPlatformSpecific platform, GameView view,
			GameInput input, ScoreServer scoreServer, ISoundCache soundCache,
			Properties config) {
		this.platform = platform;
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
			optionsGui = new OptionsGUI(view, input, scoreServer, soundCache, platform);
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
	}

	@Override
	public void processInputs() {
	}

	@Override
	public void update() throws Sequence.NormalTransitionException {
	}

	private void applyOptions() throws LWJGLException, RestartGameException {
                view.setRotateViewWithGravity(optionsGui.rotateViewWithGravity.isActive());
                view.setMustDrawFPS(optionsGui.mustDrawFPS.isActive());
		view.setDisplayMode(optionsGui.fullscreen.isActive(),
				getSelectedQuality(), getSelectedMode());
		input.keyJump = findKeyIndex(optionsGui.keyJump);
		input.keyLeft = findKeyIndex(optionsGui.keyLeft);
		input.keyRight = findKeyIndex(optionsGui.keyRight);
		input.keyRotate90Clockwise = findKeyIndex(optionsGui.keyRotate90Clockwise);
		input.keyRotate90CounterClockwise = findKeyIndex(optionsGui.keyRotate90CounterClockwise);
		input.keyRotateClockwise = findKeyIndex(optionsGui.keyRotateClockwise);
		input.keyRotateCounterClockwise = findKeyIndex(optionsGui.keyRotateCounterClockwise);
		input.keyReturn = findKeyIndex(optionsGui.keyReturn);
		input.keyReturnToMenu = findKeyIndex(optionsGui.keyReturnToMenu);
		if (optionsGui.joypad.getSelected() >= 0
				&& optionsGui.joypad.getSelected() < optionsGui.joypad
						.getModel().getNumEntries()) {
			ControllerItem controllerItem = optionsGui.joypad.getModel()
					.getEntry(optionsGui.joypad.getSelected());
			input.joypad = null != controllerItem ? controllerItem
					.getController() : null;
			if (null != input.joypad) {
				input.joypadXAxis = findJoypadAxisIndex(input.joypad,
						optionsGui.joypadXAxis);
				input.joypadYAxis = findJoypadAxisIndex(input.joypad,
						optionsGui.joypadYAxis);
				input.joypadKeyJump = findJoypadButtonIndex(input.joypad,
						optionsGui.keyJump);
				input.joypadKeyLeft = findJoypadButtonIndex(input.joypad,
						optionsGui.keyLeft);
				input.joypadKeyRight = findJoypadButtonIndex(input.joypad,
						optionsGui.keyRight);
				input.joypadKeyReturn = findJoypadButtonIndex(input.joypad,
						optionsGui.keyReturn);
				input.joypadKeyReturnToMenu = findJoypadButtonIndex(
						input.joypad, optionsGui.keyReturnToMenu);
				input.joypadKeyRotate90Clockwise = findJoypadButtonIndex(
						input.joypad, optionsGui.keyRotate90Clockwise);
				input.joypadKeyRotate90CounterClockwise = findJoypadButtonIndex(
						input.joypad, optionsGui.keyRotate90CounterClockwise);
				input.joypadKeyRotateClockwise = findJoypadButtonIndex(
						input.joypad, optionsGui.keyRotateClockwise);
				input.joypadKeyRotateCounterClockwise = findJoypadButtonIndex(
						input.joypad, optionsGui.keyRotateCounterClockwise);
			}
		} else {
			input.joypad = null;
		}
		scoreServer.setPlayer(optionsGui.scorePlayer.getText());
		scoreServer.setSecret(optionsGui.scoreSecret.getText());
		scoreServer.setServerUrl(optionsGui.scoreServerUrl.getText());
                scoreServer.setScoreShareEnabled(optionsGui.scoreShareEnabled.isActive());
		soundCache.setSoundEnabled(optionsGui.soundEnabled.isActive());
		soundCache.setMusicEnabled(optionsGui.musicEnabled.isActive());
	}

	private int findJoypadAxisIndex(Controller controller,
			ComboBox<String> joypadAxis) {
		if (joypadAxis.getSelected() >= 0
				&& joypadAxis.getSelected() < joypadAxis.getModel()
						.getNumEntries()) {
			String axisName = joypadAxis.getModel().getEntry(
					joypadAxis.getSelected());
			return GameInput.findJoypadAxisByName(controller, axisName);
		} else {
			return -1;
		}
	}

	private static int findKeyIndex(InputChoice c) {
		return Keyboard.getKeyIndex(c.key.getModel().getEntry(
				c.key.getSelected()));
	}

	private static int findJoypadButtonIndex(Controller controller,
			InputChoice c) {
		ComboBox<JoyButtonItem> joyButton = c.joyButton;
		if (joyButton.getSelected() >= 0
				&& joyButton.getSelected() < joyButton.getModel()
						.getNumEntries()) {
			JoyButtonItem buttonItem = joyButton.getModel().getEntry(
					joyButton.getSelected());
			return buttonItem.getButtonIndex();
		} else {
			return -1;
		}
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
			return optionsGui.quality.getModel().getEntry(selected).getQuality();
		} else {
			return view.getQuality();
		}
	}

	@Override
	public void setNextSequence(Sequence mainMenuSequence) {
		this.nextSequence = mainMenuSequence;
	}

	private void updateConfig() {
		config.setProperty("view.width", "" + Display.getWidth());
		config.setProperty("view.height", "" + Display.getHeight());
		config.setProperty("view.bpp", ""
				+ Display.getDisplayMode().getBitsPerPixel());
		config.setProperty("view.fullscreen", "" + Display.isFullscreen());
                config.setProperty("view.rotate", "" + view.isRotateViewWithGravity());
                config.setProperty("view.draw.fps", "" + view.getMustDrawFPS());
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
		config.setProperty("key.return", getKeyFieldName(input.keyReturn));
		config.setProperty("key.return_to_menu",
				getKeyFieldName(input.keyReturnToMenu));

		if (Controllers.getControllerCount() > 0) {
			config.setProperty("joypad.name",
					null != input.joypad ? input.joypad.getName() : "");
			config.setProperty("joypad.axis.x",
					getJoypadAxisName(input.joypadXAxis));
			config.setProperty("joypad.axis.y",
					getJoypadAxisName(input.joypadYAxis));
			config.setProperty("joypad.button.left",
					getJoypadButtonName(input.joypadKeyLeft));
			config.setProperty("joypad.button.right",
					getJoypadButtonName(input.joypadKeyRight));
			config.setProperty("joypad.button.jump",
					getJoypadButtonName(input.joypadKeyJump));
			config.setProperty("joypad.button.return",
					getJoypadButtonName(input.joypadKeyReturn));
			config.setProperty("joypad.button.return_to_menu",
					getJoypadButtonName(input.joypadKeyReturnToMenu));
			config.setProperty("joypad.button.rotate_clockwise",
					getJoypadButtonName(input.joypadKeyRotateClockwise));
			config.setProperty("joypad.button.rotate_counter_clockwise",
					getJoypadButtonName(input.joypadKeyRotateCounterClockwise));
			config.setProperty("joypad.button.rotate_90_clockwise",
					getJoypadButtonName(input.joypadKeyRotate90Clockwise));
			config.setProperty(
					"joypad.button.rotate_90_counter_clockwise",
					getJoypadButtonName(input.joypadKeyRotate90CounterClockwise));
		}

		config.setProperty("scoreserver.url", scoreServer.getServerUrl());
		config.setProperty("scoreserver.player", scoreServer.getPlayer());
		config.setProperty("scoreserver.secret", scoreServer.getSecret());
                config.setProperty("scoreserver.share", "" + scoreServer.isScoreShareEnabled());
		config.setProperty("sound.enabled", "" + soundCache.isSoundEnabled());
		config.setProperty("music.enabled", "" + soundCache.isMusicEnabled());
                config.setProperty("newton_adventure.mod", optionsGui.getSelectedModName());
	}

	private String getJoypadAxisName(int joypadXAxis) {
		if (null != input.joypad && joypadXAxis >= 0
				&& joypadXAxis < input.joypad.getAxisCount()) {
			return input.joypad.getAxisName(joypadXAxis);
		}
		return "";
	}

	private String getJoypadButtonName(int joypadButton) {
		if (null != input.joypad && joypadButton >= 0
				&& joypadButton < input.joypad.getButtonCount()) {
			return input.joypad.getButtonName(joypadButton);
		}
		return "";
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

	@Override
	public void resume() {

	}

	@Override
	public void tick() throws NormalTransitionException, RestartGameException {
		if (Display.isVisible() || Display.isDirty() || Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.update();
		GL11.glPopAttrib();
		GL11.glPopMatrix();

		// now tell the screen to update
		Display.update(false);
		Display.sync(Game.FPS);
		Display.processMessages();

		// finally check if the user has requested that the display be
		// shutdown
		if (Display.isCloseRequested()) {
                    throw new GameCloseException();
		}

		if (optionsGui.okPressed) {
			try {
				applyOptions();
			} catch (LWJGLException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"cannot apply options", e);
			}
			updateConfig();
                        platform.loadModIfNeeded(optionsGui.getSelectedModName());
			platform.saveConfig();
			throw new Sequence.NormalTransitionException(nextSequence);
		}
		if (optionsGui.cancelPressed) {
			throw new Sequence.NormalTransitionException(nextSequence);
		}
	}
}
