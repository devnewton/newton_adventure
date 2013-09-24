package im.bci.newtonadv.platform.lwjgl.nuit.controls;

public interface Control {
    String getControllerName();
    String getName();
    float getDeadZone();
    float getValue();
}
