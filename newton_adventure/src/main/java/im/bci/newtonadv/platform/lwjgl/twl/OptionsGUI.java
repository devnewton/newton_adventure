package im.bci.newtonadv.platform.lwjgl.twl;

import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ColumnLayout;
import de.matthiasmann.twl.ColumnLayout.Row;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.EnumListModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;

public class OptionsGUI extends Widget {

	boolean okPressed, cancelPressed;
	ToggleButton fullscreen;
	ComboBox<DisplayMode> mode;
	ComboBox<GameViewQuality> quality;
	ComboBox<String> keyJump;
	ComboBox<String> keyLeft;
	ComboBox<String> keyRight;
	ComboBox<String> keyRotateClockwise;
	ComboBox<String> keyRotateCounterClockwise;
	ComboBox<String> keyRotate90Clockwise;
	ComboBox<String> keyRotate90CounterClockwise;
	private static SimpleChangableListModel<String> keyModel = buildKeyListModel();
        private final ColumnLayout layout;

	OptionsGUI(GameView gameView, GameInput gameInput) throws LWJGLException {
		setSize(Display.getWidth(), Display.getHeight());
		this.layout = new ColumnLayout();
		layout.setSize(Display.getWidth(), Display.getHeight());

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

		keyJump = addKeyCombo(layout, "Jump", gameInput.keyJump);
		keyLeft = addKeyCombo(layout, "Left", gameInput.keyLeft);
		keyRight = addKeyCombo(layout, "Right", gameInput.keyRight);
		keyRotateClockwise = addKeyCombo(layout, "Rotate clockwise",
				gameInput.keyRotateClockwise);
		keyRotateCounterClockwise = addKeyCombo(layout,
				"Rotate counter clockwise", gameInput.keyRotateCounterClockwise);
		keyRotate90Clockwise = addKeyCombo(layout, "Rotate 90 clockwise",
				gameInput.keyRotate90Clockwise);
		keyRotate90CounterClockwise = addKeyCombo(layout,
				"Rotate 90 counter clockwise",
				gameInput.keyRotate90CounterClockwise);

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

    @Override
    protected void layout() {
        layout.setPosition((getWidth() - layout.getPreferredWidth()) / 2, (getHeight() - layout.getPreferredHeight()) / 2);
    }
        
	private ComboBox<String> addKeyCombo(ColumnLayout layout, String label,
			int key) {
		ComboBox<String> combo = new ComboBox<String>(keyModel);
		Row rowKeys = layout.addRow("label","key");
		rowKeys.addWithLabel(label, combo);
		combo.setSelected(keyModel.findElement((Keyboard.getKeyName(key))));
		return combo;
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
