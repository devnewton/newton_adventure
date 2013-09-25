package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import im.bci.newtonadv.platform.lwjgl.LwjglHelper;
import im.bci.newtonadv.platform.lwjgl.nuit.NuitToolkit;

public class VideoConfigurator extends Table {

	private Select<DisplayMode> mode;
	private Toggle fullscreen;

	public VideoConfigurator(NuitToolkit toolkit) throws LWJGLException {
		super(toolkit);
		defaults().expand().fill();
		cell(new Label(toolkit, "Mode"));
		mode = new Select<DisplayMode>(toolkit, getDisplayModes());
		cell(mode).colspan(2);
		row();
		cell(new Label(toolkit, "Fullscreen"));
		fullscreen = new Toggle(toolkit);
		cell(fullscreen).colspan(2);
		row();
		cell(new Button(toolkit, "Back") {
			public void onOK() {
				closeVideoSettings();
			}			
		});
		cell(new Button(toolkit, "Apply") {
			@Override
			public void onOK() {
				changeVideoSettings();
				closeVideoSettings();
			}
		});
	}

	protected void changeVideoSettings() {
		DisplayMode chosenMode = mode.getSelected();
		try {
			if (fullscreen.isEnabled()) {
				Display.setDisplayModeAndFullscreen(chosenMode);
			} else {
				Display.setFullscreen(false);
				Display.setDisplayMode(chosenMode);
			}
			LwjglHelper.setResizable(true);
			if (!Display.isCreated()) {
				Display.create();
			}
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			throw new RuntimeException("Unable to create display");
		}

	}
	
	protected void closeVideoSettings() {
	};

	@Override
	public void onShow() {
		mode.setSelected(Display.getDisplayMode());
		fullscreen.setEnabled(Display.isFullscreen());
	}

	private List<DisplayMode> getDisplayModes() throws LWJGLException {
		ArrayList<DisplayMode> modes = new ArrayList<DisplayMode>(
				java.util.Arrays.asList(Display.getAvailableDisplayModes()));
		java.util.Collections.sort(modes, new Comparator<DisplayMode>() {
			@Override
			public int compare(DisplayMode o1, DisplayMode o2) {
				int w1 = o1.getWidth(), w2 = o2.getWidth();
				if (w1 < w2) {
					return -1;
				} else if (w2 < w1) {
					return 1;
				}

				int h1 = o1.getHeight(), h2 = o2.getHeight();
				if (h1 < h2) {
					return -1;
				} else if (h2 < h1) {
					return 1;
				}

				int b1 = o1.getBitsPerPixel(), b2 = o2.getBitsPerPixel();
				if (b1 < b2) {
					return -1;
				} else if (b2 < b1) {
					return 1;
				}

				int f1 = o1.getFrequency(), f2 = o2.getFrequency();
				if (f1 < f2) {
					return -1;
				} else if (f2 < f1) {
					return 1;
				} else {
					return 0;
				}

			}
		});
		modes.add(0, Display.getDisplayMode());
		return modes;
	}

}
