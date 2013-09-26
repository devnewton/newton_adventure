package im.bci.lwjgl.nuit.controls;

public class Action {
    private final String name;
    private Control[] controls;
    
    public Action(String name, Control main) {
        this.name = name;
        controls = new Control[2];
        controls[0] = main;
        controls[1] = NullControl.INSTANCE;
    }

    public Action(Action action) {
        this.name = action.name;
        this.controls = action.controls.clone();
    }

    public String getName() {
        return name;
    }

    public Control[] getControls() {
        return controls;
    }

    public Control getMainControl() {
        return controls[0];
    }
    
    public void setMainControl(Control control) {
        controls[0] = control;
    }
    
    public Control getAlternativeControl() {
        return controls[1];
    }
    
    public void setAlternativeControl(Control control) {
        controls[1] = control;
    }
}
