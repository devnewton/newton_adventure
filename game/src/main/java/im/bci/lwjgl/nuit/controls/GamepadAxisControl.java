package im.bci.lwjgl.nuit.controls;

import org.lwjgl.input.Controller;

public class GamepadAxisControl implements Control {
    private final Controller pad;
    private final int axis;

    public GamepadAxisControl(Controller pad, int axis) {
        this.pad = pad;
        this.axis = axis;
    }

    @Override
    public String getName() {
        return pad.getAxisName(axis);
    }

    @Override
    public float getDeadZone() {
        return pad.getDeadZone(axis);
    }

    @Override
    public float getValue() {
        return pad.getAxisValue(axis);
    }

    @Override
    public String getControllerName() {
        return pad.getName();
    }

}
