package im.bci.lwjgl.nuit.controls;

import org.lwjgl.input.Keyboard;

public class KeyControl implements Control {

    private final int key;
    
    public KeyControl(int key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return Keyboard.getKeyName(key);
    }

    @Override
    public float getDeadZone() {
        return 0.1f;
    }

    @Override
    public float getValue() {
        return Keyboard.isKeyDown(key) ? 1.0f : 0.0f;
    }

    @Override
    public String getControllerName() {
        return "Keyboard";
    }

}
