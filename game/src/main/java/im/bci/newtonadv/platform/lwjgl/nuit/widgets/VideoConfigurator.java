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
        cell(mode);
        row();
        cell(new Label(toolkit, "Fullscreen"));
        fullscreen = new Toggle(toolkit);
        cell(fullscreen);
        row();
        cell(new Button(toolkit, "Apply") {
            @Override
            public void onOK() {
                changeVideoSettings();
                closeVideoSettings();
            }
        }).colspan(2);
        row();
        cell(new Button(toolkit, "Back") {
            public void onOK() {
                closeVideoSettings();
            }
        }).colspan(2);
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
    }

    @Override
    public void onShow() {
        mode.setSelected(Display.getDisplayMode());
        fullscreen.setEnabled(Display.isFullscreen());
    }

    private List<DisplayMode> getDisplayModes() throws LWJGLException {
        ArrayList<DisplayMode> modes = new ArrayList<DisplayMode>(java.util.Arrays.asList(Display.getAvailableDisplayModes()));
        java.util.Collections.sort(modes, new Comparator<DisplayMode>() {
            @Override
            public int compare(DisplayMode o1, DisplayMode o2) {
                int result = Integer.compare(o1.getWidth() * o1.getHeight(), o2.getWidth() * o2.getHeight());
                if(result == 0) {
                    result = Integer.compare(o1.getBitsPerPixel(), o2.getBitsPerPixel());
                }
                if(result == 0) {
                    result = Integer.compare(o1.getFrequency(), o2.getFrequency());
                }
                return result;
            }
        });
        modes.add(0, Display.getDisplayMode());
        return modes;
    }

}
