/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IGameInput;
import java.util.Properties;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author devnewton
 */
public class GameInput implements IGameInput {

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

    public boolean isKeyCheatGotoNextBonusLevelDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_F10);
    }
}
