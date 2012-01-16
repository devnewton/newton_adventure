/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl;

import java.util.Properties;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author bob
 */
public class GameInput {

    Properties config;
    private int keyJump;
    private int keyLeft;
    private int keyRight;
    private int keyRotateClockwise;
    private int keyRotateCounterClockwise;
    private int keyRotate90Clockwise;
    private int keyRotate90CounterClockwise;
    private int keyToggleFullscreen;
    private int keyPause;
    private int keyReturnToMenu;

    public GameInput(Properties gameConfig) throws Exception {
        config = new Properties();
        config.setProperty("key.jump", "KEY_UP");
        config.setProperty("key.left", "KEY_LEFT");
        config.setProperty("key.right", "KEY_RIGHT");
        config.setProperty("key.rotate_clockwise", "KEY_C");
        config.setProperty("key.rotate_counter_clockwise", "KEY_X");
        config.setProperty("key.rotate_90_clockwise", "KEY_S");
        config.setProperty("key.rotate_90_counter_clockwise", "KEY_D");
        config.setProperty("key.toggle_fullscreen", "KEY_F");
        config.setProperty("key.pause", "KEY_PAUSE");
        config.setProperty("key.return_to_menu", "KEY_ESCAPE");

        config.putAll(gameConfig);
        setupKeys();
    }

    int getKeyCode(String propertyName) throws Exception {
        String lwjglName = config.getProperty(propertyName);
        int code = Keyboard.getKeyIndex(lwjglName);
        if (Keyboard.KEY_NONE == code) {
            try {
                code = Keyboard.class.getDeclaredField(lwjglName).getInt(null);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Unknow key : " + propertyName);
            }
        }
        return code;
    }

    private void setupKeys() throws Exception {
        keyJump = getKeyCode("key.jump");
        keyLeft = getKeyCode("key.left");
        keyRight = getKeyCode("key.right");
        keyRotateClockwise = getKeyCode("key.rotate_clockwise");
        keyRotateCounterClockwise = getKeyCode("key.rotate_counter_clockwise");
        keyRotate90Clockwise = getKeyCode("key.rotate_90_clockwise");
        keyRotate90CounterClockwise = getKeyCode("key.rotate_90_counter_clockwise");
        keyToggleFullscreen = getKeyCode("key.toggle_fullscreen");
        keyPause = getKeyCode("key.pause");
        keyReturnToMenu = getKeyCode("key.return_to_menu");
    }

    public boolean isKeyReturnToMenuDown() {
        return Keyboard.isKeyDown(keyReturnToMenu);
    }

    public boolean isKeyToggleFullscreenDown() {
        return Keyboard.isKeyDown(keyToggleFullscreen);
    }

    public boolean isKeyPauseDown() {
        return Keyboard.isKeyDown(keyPause);
    }

    public boolean isKeyRotateClockwiseDown() {
        return Keyboard.isKeyDown(keyRotateClockwise);
    }

    public boolean isKeyRotateCounterClockwiseDown() {
        return Keyboard.isKeyDown(keyRotateCounterClockwise);
    }

    public boolean isKeyRotate90ClockwiseDown() {
        return Keyboard.isKeyDown(keyRotate90Clockwise);
    }

    public boolean isKeyRotate90CounterClockwiseDown() {
        return Keyboard.isKeyDown(keyRotate90CounterClockwise);
    }

    public boolean isKeyRightDown() {
        return Keyboard.isKeyDown(keyRight);
    }

    public boolean isKeyLeftDown() {
        return Keyboard.isKeyDown(keyLeft);
    }

    public boolean isKeyJumpDown() {
        return Keyboard.isKeyDown(keyJump);
    }

    public boolean isKeyCheatActivateAllDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_F11);
    }

    public boolean isKeyCheatGotoNextLevelDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_F12);
    }

    public boolean isKeyUpDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_UP);
    }

    public boolean isKeyDownDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_DOWN);
    }

    public boolean isKeyReturnDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_RETURN);
    }
}
