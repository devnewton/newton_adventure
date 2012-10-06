package im.bci.newtonadv.platform.lwjgl.twl;

import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;
import im.bci.newtonadv.platform.lwjgl.SoundCache;
import im.bci.newtonadv.score.ScoreServer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ColumnLayout;
import de.matthiasmann.twl.ColumnLayout.Row;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.EnumListModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;

public class OptionsGUI extends Widget {

	boolean okPressed, cancelPressed;
	ToggleButton soundEnabled;
	ToggleButton fullscreen;
	ComboBox<DisplayMode> mode;
	ComboBox<GameViewQuality> quality;
	InputChoice keyJump;
	InputChoice keyLeft;
	InputChoice keyRight;
	InputChoice keyRotateClockwise;
	InputChoice keyRotateCounterClockwise;
	InputChoice keyRotate90Clockwise;
	InputChoice keyRotate90CounterClockwise;
	InputChoice keyPause;
	InputChoice keyReturn;
	InputChoice keyReturnToMenu;
	EditField scoreServerUrl, scorePlayer, scoreSecret;
	ToggleButton musicEnabled;
	ComboBox<ControllerItem> joypad;
	ComboBox<String> joypadXAxis;
	ComboBox<String> joypadYAxis;
	EditField dataDir;

	private final ColumnLayout layout;
	private static SimpleChangableListModel<String> keyModel = buildKeyListModel();
	private SimpleChangableListModel<ControllerItem> controllerModel = buildControllerListModel();
	private SimpleChangableListModel<String> joyAxisModel = new SimpleChangableListModel<String>();
	private SimpleChangableListModel<String> joyButtonModel = new SimpleChangableListModel<String>();

	OptionsGUI(GameView gameView, GameInput gameInput, ScoreServer scoreServer,
			SoundCache soundCache, String dataDir) throws LWJGLException {
		setSize(Display.getWidth(), Display.getHeight());
		this.layout = new ColumnLayout();
		layout.setSize(Display.getWidth(), Display.getHeight());

		soundEnabled = new ToggleButton("Effect enabled");
		soundEnabled.setActive(soundCache.isSoundEnabled());
		musicEnabled = new ToggleButton("Music enabled");
		musicEnabled.setActive(soundCache.isMusicEnabled());
		Row soundRow = layout.addRow("label", "effect", "music");
		soundRow.addLabel("Sound");
		soundRow.add(soundEnabled);
		soundRow.add(musicEnabled);

		fullscreen = new ToggleButton("Fullscreen");
		fullscreen.setActive(Display.isFullscreen());
		mode = new ComboBox<DisplayMode>(
				new SimpleChangableListModel<DisplayMode>(getDisplayModes()));
		mode.setSelected(0);
		EnumListModel<GameViewQuality> gameViewQualityModel = new EnumListModel<GameViewQuality>(
				GameViewQuality.class);
		quality = new ComboBox<GameViewQuality>(gameViewQualityModel);
		quality.setSelected(gameViewQualityModel.findEntry(gameView
				.getQuality()));
		layout.addRow("label", "widget").addWithLabel("", fullscreen);
		layout.addRow("label", "widget").addWithLabel("Video mode", mode);
		layout.addRow("label", "widget").addWithLabel("Quality", quality);

		joypad = new ComboBox<ControllerItem>(controllerModel);
		joypad.setNoSelectionIsError(false);
		joypad.addCallback(new Runnable() {

			@Override
			public void run() {
				ControllerItem item = joypad.getModel().getEntry(
						joypad.getSelected());
				controllerSelected(item.getController());
			}
		});
		joypadXAxis = new ComboBox<String>(joyAxisModel);
		joypadYAxis = new ComboBox<String>(joyAxisModel);
		layout.addRow("label", "joypad").addWithLabel("Joypad", joypad);
		Row rowJoypadAxis = layout.addRow("label", "xaxis", "yaxis");
		rowJoypadAxis.addLabel("Joypad XY axis").add(joypadXAxis)
				.add(joypadYAxis);
		if (null != gameInput.joypad) {
			int joypadIndex = controllerModel.findElement(new ControllerItem(
					gameInput.joypad));
			joypad.setSelected(joypadIndex);
			controllerSelected(gameInput.joypad);
			if (gameInput.joypadXAxis >= 0
					&& gameInput.joypadXAxis < gameInput.joypad.getAxisCount()) {
				joypadXAxis.setSelected(joyAxisModel
						.findElement(gameInput.joypad
								.getAxisName(gameInput.joypadXAxis)));
			}
			if (gameInput.joypadYAxis >= 0
					&& gameInput.joypadYAxis < gameInput.joypad.getAxisCount()) {
				joypadYAxis.setSelected(joyAxisModel
						.findElement(gameInput.joypad
								.getAxisName(gameInput.joypadYAxis)));
			}
		}

		keyJump = addInputChoice(layout, "Jump", gameInput.keyJump,
				gameInput.joypadKeyJump);
		keyLeft = addInputChoice(layout, "Left", gameInput.keyLeft,
				gameInput.joypadKeyLeft);
		keyRight = addInputChoice(layout, "Right", gameInput.keyRight,
				gameInput.joypadKeyRight);
		keyRotateClockwise = addInputChoice(layout, "Rotate clockwise",
				gameInput.keyRotateClockwise,
				gameInput.joypadKeyRotateClockwise);
		keyRotateCounterClockwise = addInputChoice(layout,
				"Rotate counter clockwise",
				gameInput.keyRotateCounterClockwise,
				gameInput.joypadKeyRotateCounterClockwise);
		keyRotate90Clockwise = addInputChoice(layout, "Rotate 90 clockwise",
				gameInput.keyRotate90Clockwise,
				gameInput.joypadKeyRotate90Clockwise);
		keyRotate90CounterClockwise = addInputChoice(layout,
				"Rotate 90 counter clockwise",
				gameInput.keyRotate90CounterClockwise,
				gameInput.joypadKeyRotate90CounterClockwise);
		keyPause = addInputChoice(layout, "Pause", gameInput.keyPause,
				gameInput.joypadKeyPause);
		keyReturn = addInputChoice(layout, "Return", gameInput.keyReturn,
				gameInput.joypadKeyReturn);
		keyReturnToMenu = addInputChoice(layout, "Return to menu",
				gameInput.keyReturnToMenu, gameInput.joypadKeyReturnToMenu);

		scoreServerUrl = new EditField();
		scoreServerUrl.setText(scoreServer.getServerUrl());
		layout.addRow("label", "widget").addWithLabel("Score server",
				scoreServerUrl);

		scorePlayer = new EditField();
		scorePlayer.setText(scoreServer.getPlayer());
		layout.addRow("label", "widget").addWithLabel("Player name",
				scorePlayer);

		scoreSecret = new EditField();
		scoreSecret.setText(scoreServer.getSecret());
		layout.addRow("label", "widget").addWithLabel("Player password",
				scoreSecret);
		
		this.dataDir = new EditField();
		this.dataDir.setText(null == dataDir ? "" : dataDir);
		this.dataDir.setTooltipContent("Save and reload game to apply this option");
		layout.addRow("label", "widget").addWithLabel("Data dir",
				this.dataDir);		

		Button ok = new Button("OK");
		Button cancel = new Button("Cancel");
		Row okCancelRow = layout.addRow("Parameter", "Value");
		okCancelRow.add(ok);
		okCancelRow.add(cancel);

		add(layout);

		ok.addCallback(new Runnable() {

			@Override
			public void run() {
				okPressed = true;
			}
		});
		cancel.addCallback(new Runnable() {

			@Override
			public void run() {
				cancelPressed = true;
			}
		});
	}

	private void controllerSelected(Controller controller) {
		joyAxisModel.clear();
		joyButtonModel.clear();

		if (null != controller) {
			joyAxisModel.addElement("");
			for (int i = 0, n = controller.getAxisCount(); i < n; ++i) {
				joyAxisModel.addElement(controller.getAxisName(i));
			}
			joyButtonModel.addElement("");
			for (int i = 0, n = controller.getButtonCount(); i < n; ++i) {
				joyButtonModel.addElement(i + ". " + controller.getButtonName(i));
			}
		}
	}

	public static class ControllerItem {
		private Controller controller;

		ControllerItem(Controller controller) {
			this.controller = controller;
		}

		public Controller getController() {
			return controller;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ControllerItem) {
				return controller.equals(((ControllerItem) o).getController());
			} else {
				return super.equals(o);
			}
		}

		@Override
		public String toString() {
			return controller.getName();
		}
	}

	private SimpleChangableListModel<ControllerItem> buildControllerListModel() {
		SimpleChangableListModel<ControllerItem> model = new SimpleChangableListModel<ControllerItem>();
		for (int i = 0, n = Controllers.getControllerCount(); i < n; ++i) {
			model.addElement(new ControllerItem(Controllers.getController(i)));
		}
		return model;
	}

	@Override
	protected void layout() {
		layout.setPosition((getWidth() - layout.getPreferredWidth()) / 2,
				(getHeight() - layout.getPreferredHeight()) / 2);
	}

	public class InputChoice {
		ComboBox<String> key;
		ComboBox<String> joyButton;
	}

	private InputChoice addInputChoice(ColumnLayout layout, String label,
			int key, int button) {
		++button;
		InputChoice choice = new InputChoice();
		choice.key = new ComboBox<String>(keyModel);
		choice.joyButton = new ComboBox<String>(joyButtonModel);
		Row rowKeys = layout.addRow("label", "key", "joyButton");
		rowKeys.addLabel(label).add(choice.key).add(choice.joyButton);
		choice.key
				.setSelected(keyModel.findElement((Keyboard.getKeyName(key))));
		if (button >= 0 && button < choice.joyButton.getModel().getNumEntries()) {
			choice.joyButton.setSelected(button);
		}
		return choice;
	}

	private static SimpleChangableListModel<String> buildKeyListModel() {
		SimpleChangableListModel<String> model = new SimpleChangableListModel<String>();
		for (Field field : Keyboard.class.getFields()) {
			String name = field.getName();
			if (name.startsWith("KEY_"))
				try {
					model.addElement(Keyboard.getKeyName(field.getInt(null)));
				} catch (Exception e) {
					Logger.getLogger(OptionsGUI.class.getName()).log(
							Level.SEVERE, "error retrieving key name", e);
				}
		}
		return model;
	}

	private Collection<DisplayMode> getDisplayModes() throws LWJGLException {
		ArrayList<DisplayMode> modes = new ArrayList<DisplayMode>(
				java.util.Arrays.asList(Display.getAvailableDisplayModes()));
		java.util.Collections.sort(modes, new Comparator<DisplayMode>() {

			@Override
			public int compare(DisplayMode o1, DisplayMode o2) {
				int w1 = o1.getWidth(), w2 = o2.getWidth();
				if (w1 < w2)
					return -1;
				else if (w2 < w1)
					return 1;

				int h1 = o1.getHeight(), h2 = o2.getHeight();
				if (h1 < h2)
					return -1;
				else if (h2 < h1)
					return 1;

				int b1 = o1.getBitsPerPixel(), b2 = o2.getBitsPerPixel();
				if (b1 < b2)
					return -1;
				else if (b2 < b1)
					return 1;

				int f1 = o1.getFrequency(), f2 = o2.getFrequency();
				if (f1 < f2)
					return -1;
				else if (f2 < f1)
					return 1;
				else
					return 0;

			}
		});
		modes.add(0, Display.getDisplayMode());
		return modes;
	}

}
