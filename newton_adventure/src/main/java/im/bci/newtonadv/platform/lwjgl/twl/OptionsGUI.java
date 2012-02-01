package im.bci.newtonadv.platform.lwjgl.twl;

import org.lwjgl.opengl.Display;

import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.Widget;

public class OptionsGUI extends Widget {
	
	OptionsGUI() {
		setSize(Display.getWidth(), Display.getHeight());
		BoxLayout layout = new BoxLayout();
		ToggleButton b= new ToggleButton("OK");
		layout.add( b);
		add(layout);
	}

}
