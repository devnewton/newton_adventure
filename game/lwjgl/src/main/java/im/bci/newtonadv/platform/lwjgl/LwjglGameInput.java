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

import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.controls.Action;
import im.bci.jnuit.controls.ActionActivatedDetector;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.lwjgl.controls.GamepadAxisControl;
import im.bci.jnuit.lwjgl.controls.GamepadButtonControl;
import im.bci.jnuit.lwjgl.controls.JoystickAxisControl;
import im.bci.jnuit.lwjgl.controls.JoystickButtonControl;
import im.bci.jnuit.lwjgl.controls.KeyControl;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;

import java.util.Arrays;
import java.util.List;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import org.lwjgl.glfw.GLFW;

/**
 *
 * @author devnewton
 */
public class LwjglGameInput extends AbstractGameInput {

	private final long window;

	public LwjglGameInput(NuitToolkit toolkit, NuitPreferences config, long window) throws Exception {
		super(toolkit, config);
		this.window = window;
	}

	class DefaultGameControls {
		ActionActivatedDetector defaultActivate;
		ActionActivatedDetector defaultJump;
		ActionActivatedDetector defaultLeft;
		ActionActivatedDetector defaultReturnToMenu;
		ActionActivatedDetector defaultRight;
		ActionActivatedDetector defaultRotate90Clockwise;
		ActionActivatedDetector defaultRotate90CounterClockwise;
		ActionActivatedDetector defaultRotateClockwise;
		ActionActivatedDetector defaultRotateCounterClockwise;

		DefaultGameControls() {
			defaultActivate = new ActionActivatedDetector(
					new Action("action.activate", new KeyControl(window, GLFW.GLFW_KEY_DOWN, "DOWN")));
			defaultJump = new ActionActivatedDetector(
					new Action("action.jump", new KeyControl(window, GLFW.GLFW_KEY_UP, "UP")));
			defaultLeft = new ActionActivatedDetector(
					new Action("action.left", new KeyControl(window, GLFW.GLFW_KEY_LEFT, "LEFT")));
			defaultRight = new ActionActivatedDetector(
					new Action("action.right", new KeyControl(window, GLFW.GLFW_KEY_RIGHT, "RIGHT")));
			defaultRotateClockwise = new ActionActivatedDetector(
					new Action("action.rotate.clockwise", new KeyControl(window, GLFW.GLFW_KEY_C, "C")));
			defaultRotateCounterClockwise = new ActionActivatedDetector(
					new Action("action.rotate.counterclockwise", new KeyControl(window, GLFW.GLFW_KEY_X, "X")));
			defaultRotate90Clockwise = new ActionActivatedDetector(
					new Action("action.rotate.clockwise.90", new KeyControl(window, GLFW.GLFW_KEY_S, "S")));
			defaultRotate90CounterClockwise = new ActionActivatedDetector(
					new Action("action.rotate.counterclockwise.90", new KeyControl(window, GLFW.GLFW_KEY_D, "D")));
			defaultReturnToMenu = new ActionActivatedDetector(
					new Action("action.returntomenu", new KeyControl(window, GLFW.GLFW_KEY_ESCAPE, "ESCAPE")));

			if (GLFW.glfwJoystickIsGamepad(GLFW.GLFW_JOYSTICK_1)) {
				defaultActivate.getAction().setAlternativeControl(
						new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_B, "B"));
				defaultJump.getAction().setAlternativeControl(
						new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_A, "A"));
				defaultLeft.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1,
						GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, "L-STICK left", GamepadAxisControl.SCALE_LEFT));
				defaultRight.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1,
						GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, "L-STICK right", GamepadAxisControl.SCALE_RIGHT));
				defaultRotateClockwise.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1,
						GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, "Left trigger", GamepadAxisControl.SCALE_OTHER));
				defaultRotateCounterClockwise.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1,
						GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, "Right trigger", GamepadAxisControl.SCALE_OTHER));
				defaultRotate90Clockwise.getAction().setAlternativeControl(new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1,
						GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, "Left bumper"));
				defaultRotate90CounterClockwise.getAction().setAlternativeControl(new GamepadButtonControl(
						GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, "Right bumper"));
				defaultReturnToMenu.getAction().setAlternativeControl(
						new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_START, "Start"));
			} else if (GLFW.glfwJoystickPresent(GLFW.GLFW_JOYSTICK_1)) {
				defaultJump.getAction().setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 0));
				defaultActivate.getAction().setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 1));
				defaultLeft.getAction().setAlternativeControl(
						new JoystickAxisControl(GLFW.GLFW_JOYSTICK_1, 0, JoystickAxisControl.SCALE_LEFT));
				defaultRight.getAction().setAlternativeControl(
						new JoystickAxisControl(GLFW.GLFW_JOYSTICK_1, 0, JoystickAxisControl.SCALE_RIGHT));
				defaultRotateClockwise.getAction().setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 2));
				defaultRotateCounterClockwise.getAction()
						.setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 3));
				defaultRotate90Clockwise.getAction().setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 4));
				defaultRotate90CounterClockwise.getAction()
						.setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 5));
				defaultReturnToMenu.getAction().setAlternativeControl(new JoystickButtonControl(GLFW.GLFW_JOYSTICK_1, 6));
			}
		}

		public List<Action> toList() {	
			return Arrays.asList(defaultJump.getAction(), defaultActivate.getAction(), defaultLeft.getAction(), defaultRight.getAction(),
					defaultRotateClockwise.getAction(), defaultRotateCounterClockwise.getAction(), defaultRotate90Clockwise.getAction(),
					defaultRotate90CounterClockwise.getAction(), defaultReturnToMenu.getAction());
		}
	}

	@Override
	protected void setupGameControls() {
		DefaultGameControls defaults = new DefaultGameControls();
		this.activate = defaults.defaultActivate;
		this.jump = defaults.defaultJump;
		this.left = defaults.defaultLeft;
		this.returnToMenu = defaults.defaultReturnToMenu;
		this.right = defaults.defaultRight;
		this.rotate90Clockwise = defaults.defaultRotate90Clockwise;
		this.rotate90CounterClockwise = defaults.defaultRotate90CounterClockwise;
		this.rotateClockwise = defaults.defaultRotateClockwise;
		this.rotateCounterClockwise = defaults.defaultRotateCounterClockwise;
		
		cheatActivateAll = new ActionActivatedDetector(
				new Action("cheat.activate.all", new KeyControl(window, GLFW.GLFW_KEY_F8, "F8")));
		cheatGetWorldMap = new ActionActivatedDetector(
				new Action("cheat.get.world.map", new KeyControl(window, GLFW.GLFW_KEY_F9, "F9")));
		cheatGetCompass = new ActionActivatedDetector(
				new Action("cheat.get.compass", new KeyControl(window, GLFW.GLFW_KEY_F10, "F10")));
		cheatGotoNextBonusLevel = new ActionActivatedDetector(
				new Action("cheat.goto.next.bonus.level", new KeyControl(window, GLFW.GLFW_KEY_F11, "F11")));
		cheatGotoNextLevel = new ActionActivatedDetector(
				new Action("cheat.goto.next.level", new KeyControl(window, GLFW.GLFW_KEY_F12, "F12")));
		cheatSetAllCompleted = new ActionActivatedDetector(
				new Action("cheat.set.all.completed", new KeyControl(window, GLFW.GLFW_KEY_F12, "F12")));
	}

	@Override
	public List<Action> getDefaultGameActionList() {
		return new DefaultGameControls().toList();
	}

	@Override
	public ROVector2f getMousePos() {
		double[] x = new double[1];
		double[] y = new double[1];
		GLFW.glfwGetCursorPos(window, x, y);
		int[] width = new int[1];
		int[] height = new int[1];
		GLFW.glfwGetFramebufferSize(this.window, width, height);
		return new Vector2f((float) x[0], (float) (height[0] - y[0]));
	}

	@Override
	public boolean isMouseButtonDown() {
		return GLFW.GLFW_PRESS == GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}
}
