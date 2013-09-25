package im.bci.newtonadv.platform.lwjgl.nuit.controls;

import java.util.Arrays;
import java.util.List;

public class Action {
    private final String name;
    private List<Control> controls;
    
    public Action(String name, Control... controls) {
        this.name = name;
        this.controls = Arrays.asList(controls);
    }

    public String getName() {
        return name;
    }

    public List<Control> getControls() {
        return controls;
    }

    public void setControls(List<Control> controls) {
        this.controls = controls;
    }
}
