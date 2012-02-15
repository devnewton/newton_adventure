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

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * 
 * @author devnewton
 */
public class GameInput implements IGameInput {

	Properties config;
	public int keyJump;
	public int keyLeft;
	public int keyRight;
	public int keyRotateClockwise;
	public int keyRotateCounterClockwise;
	public int keyRotate90Clockwise;
	public int keyRotate90CounterClockwise;
	public int keyToggleFullscreen;
	public int keyPause;
	public int keyReturnToMenu;

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

	@Override
	public boolean isKeyReturnToMenuDown() {
		return Keyboard.isKeyDown(keyReturnToMenu);
	}

	@Override
	public boolean isKeyToggleFullscreenDown() {
		return Keyboard.isKeyDown(keyToggleFullscreen);
	}

	@Override
	public boolean isKeyPauseDown() {
		return Keyboard.isKeyDown(keyPause);
	}

	@Override
	public boolean isKeyRotateClockwiseDown() {
		return Keyboard.isKeyDown(keyRotateClockwise);
	}

	@Override
	public boolean isKeyRotateCounterClockwiseDown() {
		return Keyboard.isKeyDown(keyRotateCounterClockwise);
	}

	@Override
	public boolean isKeyRotate90ClockwiseDown() {
		return Keyboard.isKeyDown(keyRotate90Clockwise);
	}

	@Override
	public boolean isKeyRotate90CounterClockwiseDown() {
		return Keyboard.isKeyDown(keyRotate90CounterClockwise);
	}

	@Override
	public boolean isKeyRightDown() {
		return Keyboard.isKeyDown(keyRight);
	}

	@Override
	public boolean isKeyLeftDown() {
		return Keyboard.isKeyDown(keyLeft);
	}

	@Override
	public boolean isKeyJumpDown() {
		return Keyboard.isKeyDown(keyJump);
	}

	@Override
	public boolean isKeyUpDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_UP);
	}

	@Override
	public boolean isKeyDownDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_DOWN);
	}

	@Override
	public boolean isKeyReturnDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_RETURN);
	}
	
	@Override
	public boolean isKeyCheatGotoNextLevelDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_F12);
	}
	
	@Override
	public boolean isKeyCheatActivateAllDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_F11);
	}

	@Override
	public boolean isKeyCheatGotoNextBonusLevelDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_F10);
	}
	
	@Override
	public boolean isKeyCheatGetWorldMapDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_F9);
	}

	@Override
	public boolean isKeyCheatGetCompassDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_F8);
	}

	@Override
	public ROVector2f getMousePos() {
		return new Vector2f(Mouse.getX(),Mouse.getY());
	}

	@Override
	public boolean isMouseButtonDown() {
		return Mouse.isButtonDown(0);
	}
}
