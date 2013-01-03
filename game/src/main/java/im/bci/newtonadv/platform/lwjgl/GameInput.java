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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
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
	public int keyReturn;
	public int keyReturnToMenu;

	public Controller joypad;
	public int joypadXAxis = -1;
	public int joypadYAxis = -1;
	public int joypadKeyJump;
	public int joypadKeyLeft;
	public int joypadKeyRight;
	public int joypadKeyRotateClockwise;
	public int joypadKeyRotateCounterClockwise;
	public int joypadKeyRotate90Clockwise;
	public int joypadKeyRotate90CounterClockwise;
	public int joypadKeyPause;
	public int joypadKeyReturn;
	public int joypadKeyReturnToMenu;

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
		config.setProperty("key.return", "KEY_RETURN");		
	
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
		keyReturn = getKeyCode("key.return");
		setupJoypad();
	}

	private void setupJoypad() {
		
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cannot initialize joypad", e);
			return;
		}
		String joypadName = config.getProperty("joypad.name");
		if (null != joypadName && !joypadName.isEmpty()) {
			for (int i = 0, n = Controllers.getControllerCount(); i < n; ++i) {
				Controller controller = Controllers.getController(i);
				if (controller.getName().equals(joypadName)) {
					joypad = controller;
					break;
				}
			}
			if ((null != joypad)) {
				joypadXAxis = findJoypadAxisByName(joypad,
						config.getProperty("joypad.axis.x"));
				joypadYAxis = findJoypadAxisByName(joypad,
						config.getProperty("joypad.axis.y"));
				joypadKeyLeft = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.left"));
				joypadKeyRight = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.right"));
				joypadKeyJump = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.jump"));
				joypadKeyRotateClockwise = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.rotate_clockwise"));
				joypadKeyRotateCounterClockwise = findJoypadButtonByName(
						joypad,
						config.getProperty("joypad.button.rotate_counter_clockwise"));
				joypadKeyPause = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.pause"));
				joypadKeyReturn = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.return"));
				joypadKeyReturnToMenu = findJoypadButtonByName(joypad,
						config.getProperty("joypad.button.return_to_menu"));
				joypadKeyRotate90Clockwise = findJoypadButtonByName(
						joypad,
						config.getProperty("joypad.button.rotate_90_clockwise"));
				joypadKeyRotate90CounterClockwise = findJoypadButtonByName(
						joypad,
						config.getProperty("joypad.button.rotate_90_counter_clockwise"));
			} else {
				Logger.getLogger(this.getClass().getName()).log(
						Level.WARNING, "Cannot find joypad ''{0}''", joypadName);
			}
		}
	}

	public static int findJoypadButtonByName(Controller controller,
			String buttonName) {
		for (int i = 0, n = controller.getButtonCount(); i < n; ++i)
			if (controller.getButtonName(i).equals(buttonName))
				return i;
		return -1;
	}

	public static int findJoypadAxisByName(Controller controller,
			String axisName) {
		for (int i = 0, n = controller.getAxisCount(); i < n; ++i)
			if (controller.getAxisName(i).equals(axisName))
				return i;
		return -1;
	}

	@Override
	public boolean isKeyReturnToMenuDown() {
		return Keyboard.isKeyDown(keyReturnToMenu)
				|| (joypad != null && joypadKeyReturnToMenu>=0 && joypad
						.isButtonPressed(joypadKeyReturnToMenu));
	}

	@Override
	public boolean isKeyToggleFullscreenDown() {
		return Keyboard.isKeyDown(keyToggleFullscreen);
	}

	@Override
	public boolean isKeyPauseDown() {
		return Keyboard.isKeyDown(keyPause)
				|| (joypad != null && joypadKeyPause>=0 && joypad.isButtonPressed(joypadKeyPause));
	}

	@Override
	public boolean isKeyRotateClockwiseDown() {
		return Keyboard.isKeyDown(keyRotateClockwise)
				|| (joypad != null && joypadKeyRotateClockwise>=0 && joypad
						.isButtonPressed(joypadKeyRotateClockwise));
	}

	@Override
	public boolean isKeyRotateCounterClockwiseDown() {
		return Keyboard.isKeyDown(keyRotateCounterClockwise)
				|| (joypad != null && joypadKeyRotateCounterClockwise>= 0 && joypad
						.isButtonPressed(joypadKeyRotateCounterClockwise));
	}

	@Override
	public boolean isKeyRotate90ClockwiseDown() {
		return Keyboard.isKeyDown(keyRotate90Clockwise)
				|| (joypad != null && joypadKeyRotate90Clockwise>= 0 && joypad
						.isButtonPressed(joypadKeyRotate90Clockwise));
	}

	@Override
	public boolean isKeyRotate90CounterClockwiseDown() {
		return Keyboard.isKeyDown(keyRotate90CounterClockwise)
				|| (joypad != null && joypadKeyRotate90CounterClockwise>= 0 && joypad
						.isButtonPressed(joypadKeyRotate90CounterClockwise));
	}

	@Override
	public boolean isKeyRightDown() {
		if (Keyboard.isKeyDown(keyRight))
			return true;
		if (null != joypad) {
			if(joypadKeyRight >=0 && joypad.isButtonPressed(joypadKeyRight))
				return true;
			if (joypadXAxis < 0)
				return joypad.getXAxisValue() > joypad.getXAxisDeadZone();
			else
				return joypad.getAxisValue(joypadXAxis) > joypad
						.getDeadZone(joypadXAxis);
		}
		return false;
	}

	@Override
	public boolean isKeyLeftDown() {
		if (Keyboard.isKeyDown(keyLeft))
			return true;
		if (null != joypad) {
			if(joypadKeyLeft >=0 && joypad.isButtonPressed(joypadKeyLeft))
				return true;
			if (joypadXAxis < 0)
				return joypad.getXAxisValue() < -joypad.getXAxisDeadZone();
			else
				return joypad.getAxisValue(joypadXAxis) < -joypad
						.getDeadZone(joypadXAxis);
		}
		return false;
	}

	@Override
	public boolean isKeyJumpDown() {
		return Keyboard.isKeyDown(keyJump)
				|| (joypad != null && joypadKeyJump>=0 && joypad.isButtonPressed(joypadKeyJump));
	}

	@Override
	public boolean isKeyUpDown() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			return true;
		}
		if (null != joypad) {
			if (joypadYAxis < 0)
				return joypad.getYAxisValue() < -joypad.getYAxisDeadZone();
			else
				return joypad.getAxisValue(joypadYAxis) < -joypad
						.getDeadZone(joypadYAxis);
		}
		return false;
	}

	@Override
	public boolean isKeyDownDown() {
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			return true;
		if (null != joypad) {
			if (joypadXAxis < 0)
				return joypad.getYAxisValue() > joypad.getYAxisDeadZone();
			else
				return joypad.getAxisValue(joypadYAxis) > joypad
						.getDeadZone(joypadYAxis);
		}
		return false;
	}

	@Override
	public boolean isKeyReturnDown() {
		return Keyboard.isKeyDown(keyReturn)
				|| (joypad != null && joypadKeyReturn>=0 && joypad.isButtonPressed(joypadKeyReturn));
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
		return new Vector2f(Mouse.getX(), Mouse.getY());
	}

	@Override
	public boolean isMouseButtonDown() {
		return Mouse.isButtonDown(0);
	}
	
	private static final int NB_POLL_PER_TICK = 1;
	private int pollCount;
	

	@Override
	public void beginPoll() {
		pollCount = NB_POLL_PER_TICK;		
	}

	@Override
	public boolean poll() {
		return pollCount-- > 0;
	}

    @Override
    public boolean isKeyCheatSetAllCompletedDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_F12);
    }

}
