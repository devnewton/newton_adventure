package im.bci.lwjgl.nuit.controls;

import org.lwjgl.input.Mouse;

public class MouseButtonControl implements Control {
    private int button;

    public MouseButtonControl(int button) {
        this.button = button;
    }

    @Override
    public String getName() {
        return Mouse.getButtonName(button);
    }

    @Override
    public float getDeadZone() {
        return 0.1f;
    }

    @Override
    public float getValue() {
        return Mouse.isButtonDown(button) ? 1.0f : 0.0f;
    }

    @Override
    public String getControllerName() {
        return "Mouse";
    }
}
