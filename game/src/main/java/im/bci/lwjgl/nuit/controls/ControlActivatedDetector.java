package im.bci.lwjgl.nuit.controls;

public class ControlActivatedDetector {
    private final Control control;
    private Float previousState;
    private boolean activated;

    public ControlActivatedDetector(Control control) {
        this.control = control;
    }

    public Control getControl() {
        return control;
    }

    public boolean isActivated() {
        return activated;
    }

    public void poll() {
        float newState = control.getValue();
        activated = false;
        if (null != previousState) {
            if (newState > control.getDeadZone() && previousState <= control.getDeadZone()) {
                activated = true;
            }
        }
        previousState = newState;
    }

    public void reset() {
        previousState = null;
    }
}
