package im.bci.newtonadv.platform.lwjgl.twl;

import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.EnumListModel;
import de.matthiasmann.twl.model.SimpleChangableListModel;

public class OptionsGUI extends Widget {

	boolean okPressed, cancelPressed;
	ToggleButton fullscreen;
	ComboBox<DisplayMode> mode;
	ComboBox<GameViewQuality> quality;

	OptionsGUI(GameView gameView) throws LWJGLException {
		setSize(Display.getWidth(), Display.getHeight());
		BoxLayout layout = new BoxLayout(BoxLayout.Direction.VERTICAL);
		layout.setSize(Display.getWidth(), Display.getHeight());

		fullscreen = new ToggleButton("Fullscreen");
		fullscreen.setActive(Display.isFullscreen());
		mode = new ComboBox<DisplayMode>(
				new SimpleChangableListModel<DisplayMode>(getDisplayModes()));
		mode.setSelected(0);
		EnumListModel<GameViewQuality> gameViewQualityModel = new EnumListModel<GameViewQuality>(GameViewQuality.class);
		quality = new ComboBox<GameViewQuality>(gameViewQualityModel);
		quality.setSelected(gameViewQualityModel.findEntry(gameView.getQuality()));
		BoxLayout displayModeLayout = new BoxLayout();
		displayModeLayout.add(new Label("Display mode:"));
		displayModeLayout.add(fullscreen);
		displayModeLayout.add(mode);
		displayModeLayout.add(quality);
		layout.add(displayModeLayout);

		
		Button ok = new Button("OK");		
		Button cancel = new Button("Cancel");
		BoxLayout okCancelLayout = new BoxLayout();
		okCancelLayout.add(cancel);
		okCancelLayout.add(ok);
		
		layout.add(okCancelLayout);
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
