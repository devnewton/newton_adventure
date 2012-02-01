package im.bci.newtonadv.platform.lwjgl.twl;

import java.io.File;

import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import im.bci.newtonadv.game.Sequence;

public class OptionsSequence implements Sequence {

	private GUI gui;

	public void start() {
		LWJGLRenderer renderer;
		try {
			renderer = new LWJGLRenderer();
			OptionsGUI optionsGui = new OptionsGUI();
			gui = new GUI(optionsGui, renderer);
			File themeFile = new File("twl/simple.xml");
			ThemeManager themeManager = ThemeManager
					.createThemeManager(themeFile.toURI().toURL(), renderer);
			gui.applyTheme(themeManager);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		gui.destroy();
	}

	@Override
	public void draw() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		gui.update();

	}

	@Override
	public void processInputs() throws TransitionException {

	}

	@Override
	public void update() throws TransitionException {
	}

}
