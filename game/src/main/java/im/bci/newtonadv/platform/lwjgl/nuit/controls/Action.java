package im.bci.newtonadv.platform.lwjgl.nuit.controls;

public class Action {
    private final String name;
    private Control control = NullControl.INSTANCE;
    
    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

}
