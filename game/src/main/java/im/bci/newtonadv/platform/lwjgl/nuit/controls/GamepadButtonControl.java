package im.bci.newtonadv.platform.lwjgl.nuit.controls;

import org.lwjgl.input.Controller;

public class GamepadButtonControl implements Control {
    private final Controller pad;
    private final int button;

    public GamepadButtonControl(Controller pad, int button) {
        this.pad = pad;
        this.button = button;
    }

    @Override
    public String getName() {
        return pad.getButtonName(button);
    }

    @Override
    public float getDeadZone() {
        return 0.1f;
    }

    @Override
    public float getValue() {
        return pad.isButtonPressed(button) ? 1.0f : 0.0f;
    }

    @Override
    public String getControllerName() {
        return pad.getName();
    }

}
