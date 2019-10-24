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
        this.window  = window;
    }

    @Override
    protected void setupGameControls() {
        activate = new ActionActivatedDetector(new Action("action.activate", new KeyControl(window, GLFW.GLFW_KEY_DOWN, "DOWN")));
        jump = new ActionActivatedDetector(new Action("action.jump", new KeyControl(window, GLFW.GLFW_KEY_UP, "UP")));
        left = new ActionActivatedDetector(new Action("action.left", new KeyControl(window, GLFW.GLFW_KEY_LEFT, "LEFT")));
        right = new ActionActivatedDetector(new Action("action.right", new KeyControl(window, GLFW.GLFW_KEY_RIGHT, "RIGHT")));
        rotateClockwise = new ActionActivatedDetector(new Action("action.rotate.clockwise", new KeyControl(window, GLFW.GLFW_KEY_C, "C")));
        rotateCounterClockwise = new ActionActivatedDetector(new Action("action.rotate.counterclockwise", new KeyControl(window, GLFW.GLFW_KEY_X, "X")));
        rotate90Clockwise = new ActionActivatedDetector(new Action("action.rotate.clockwise.90", new KeyControl(window, GLFW.GLFW_KEY_S, "S")));
        rotate90CounterClockwise = new ActionActivatedDetector(new Action("action.rotate.counterclockwise.90", new KeyControl(window, GLFW.GLFW_KEY_D, "D")));
        returnToMenu = new ActionActivatedDetector(new Action("action.returntomenu", new KeyControl(window, GLFW.GLFW_KEY_ESCAPE, "ESCAPE")));

        cheatActivateAll = new ActionActivatedDetector(new Action("cheat.activate.all", new KeyControl(window, GLFW.GLFW_KEY_F8, "F8")));
        cheatGetWorldMap = new ActionActivatedDetector(new Action("cheat.get.world.map", new KeyControl(window, GLFW.GLFW_KEY_F9, "F9")));
        cheatGetCompass = new ActionActivatedDetector(new Action("cheat.get.compass", new KeyControl(window, GLFW.GLFW_KEY_F10, "F10")));
        cheatGotoNextBonusLevel = new ActionActivatedDetector(new Action("cheat.goto.next.bonus.level", new KeyControl(window, GLFW.GLFW_KEY_F11, "F11")));
        cheatGotoNextLevel = new ActionActivatedDetector(new Action("cheat.goto.next.level", new KeyControl(window, GLFW.GLFW_KEY_F12, "F12")));
        cheatSetAllCompleted = new ActionActivatedDetector(new Action("cheat.set.all.completed", new KeyControl(window, GLFW.GLFW_KEY_F12, "F12")));
        
        if (GLFW.glfwJoystickIsGamepad(GLFW.GLFW_JOYSTICK_1))        {
            activate.getAction().setAlternativeControl(new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_B, "B"));
            jump.getAction().setAlternativeControl(new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_A, "A"));
            left.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, "Left stick ◀", false));
            right.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, "Left stick ▶", true));
            rotateClockwise.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, "Left trigger", false));
            rotateCounterClockwise.getAction().setAlternativeControl(new GamepadAxisControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, "Right trigger", true));
            rotate90Clockwise.getAction().setAlternativeControl(new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, "Left bumper"));
            rotate90CounterClockwise.getAction().setAlternativeControl(new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, "Right bumper"));
            returnToMenu.getAction().setAlternativeControl(new GamepadButtonControl(GLFW.GLFW_JOYSTICK_1, GLFW.GLFW_GAMEPAD_BUTTON_START, "Start"));
        }
    }

    @Override
    public List<Action> getDefaultGameActionList() {
        return Arrays.asList(new Action("action.jump", new KeyControl(window, GLFW.GLFW_KEY_UP, "UP")), new Action("action.left", new KeyControl(window, GLFW.GLFW_KEY_LEFT, "LEFT")), new Action("action.right", new KeyControl(window, GLFW.GLFW_KEY_RIGHT, "RIGHT")), new Action("action.rotate.clockwise", new KeyControl(window, GLFW.GLFW_KEY_C, "C")), new Action("action.rotate.counterclockwise", new KeyControl(window, GLFW.GLFW_KEY_X, "X")), new Action("action.rotate.clockwise.90", new KeyControl(window, GLFW.GLFW_KEY_S, "S")), new Action("action.rotate.counterclockwise.90", new KeyControl(window, GLFW.GLFW_KEY_D, "D")), new Action("action.activate", new KeyControl(window, GLFW.GLFW_KEY_DOWN, "DOWN")), new Action("action.returntomenu", new KeyControl(window, GLFW.GLFW_KEY_ESCAPE, "ESCAPE")));
    }

    @Override
    public ROVector2f getMousePos() {
        double[] x = new double[1];
        double[] y = new double[1];
        GLFW.glfwGetCursorPos(window, x, y);
        return new Vector2f((float)x[0], (float)y[0]);
    }

    @Override
    public boolean isMouseButtonDown() {
        return GLFW.GLFW_PRESS == GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }
}
