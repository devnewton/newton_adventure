package im.bci.lwjgl.nuit.controls;

public class NullControl implements Control {
    
    public static NullControl INSTANCE = new NullControl();

    @Override
    public String getName() {
        return "";
    }

    @Override
    public float getDeadZone() {
        return 0.1f;
    }

    @Override
    public float getValue() {
        return 0.0f;
    }

    @Override
    public String getControllerName() {
        return "";
    }
}
