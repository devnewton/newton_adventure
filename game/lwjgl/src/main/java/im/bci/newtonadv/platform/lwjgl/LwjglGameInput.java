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
import im.bci.jnuit.lwjgl.controls.KeyControl;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import java.util.Arrays;
import java.util.List;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author devnewton
 */
public class LwjglGameInput extends AbstractGameInput {

    public LwjglGameInput(NuitToolkit toolkit, NuitPreferences config) throws Exception {
        super(toolkit, config);
    }

    @Override
    protected void setupGameControls() {
        jump = new ActionActivatedDetector(new Action("action.jump", new KeyControl(Keyboard.KEY_UP)));
        left = new ActionActivatedDetector(new Action("action.left", new KeyControl(Keyboard.KEY_LEFT)));
        right = new ActionActivatedDetector(new Action("action.right", new KeyControl(Keyboard.KEY_RIGHT)));
        rotateClockwise = new ActionActivatedDetector(new Action("action.rotate.clockwise", new KeyControl(Keyboard.KEY_C)));
        rotateCounterClockwise = new ActionActivatedDetector(new Action("action.rotate.counterclockwise", new KeyControl(Keyboard.KEY_X)));
        rotate90Clockwise = new ActionActivatedDetector(new Action("action.rotate.clockwise.90", new KeyControl(Keyboard.KEY_S)));
        rotate90CounterClockwise = new ActionActivatedDetector(new Action("action.rotate.counterclockwise.90", new KeyControl(Keyboard.KEY_D)));
        returnToMenu = new ActionActivatedDetector(new Action("action.returntomenu", new KeyControl(Keyboard.KEY_ESCAPE)));

        cheatActivateAll = new ActionActivatedDetector(new Action("cheat.activate.all", new KeyControl(Keyboard.KEY_F8)));
        cheatGetWorldMap = new ActionActivatedDetector(new Action("cheat.get.world.map", new KeyControl(Keyboard.KEY_F9)));
        cheatGetCompass = new ActionActivatedDetector(new Action("cheat.get.compass", new KeyControl(Keyboard.KEY_F10)));
        cheatGotoNextBonusLevel = new ActionActivatedDetector(new Action("cheat.goto.next.bonus.level", new KeyControl(Keyboard.KEY_F11)));
        cheatGotoNextLevel = new ActionActivatedDetector(new Action("cheat.goto.next.level", new KeyControl(Keyboard.KEY_F12)));
        cheatSetAllCompleted = new ActionActivatedDetector(new Action("cheat.set.all.completed", new KeyControl(Keyboard.KEY_F12)));
    }

    @Override
    public List<Action> getDefaultGameActionList() {
        return Arrays.asList(new Action("action.jump", new KeyControl(Keyboard.KEY_UP)), new Action("action.left", new KeyControl(Keyboard.KEY_LEFT)), new Action("action.right", new KeyControl(Keyboard.KEY_RIGHT)), new Action("action.rotate.clockwise", new KeyControl(Keyboard.KEY_C)), new Action("action.rotate.counterclockwise", new KeyControl(Keyboard.KEY_X)), new Action("action.rotate.clockwise.90", new KeyControl(Keyboard.KEY_S)), new Action("action.rotate.counterclockwise.90", new KeyControl(Keyboard.KEY_D)), new Action("action.returntomenu", new KeyControl(Keyboard.KEY_ESCAPE)));
    }

    @Override
    public ROVector2f getMousePos() {
        return new Vector2f(Mouse.getX(), Mouse.getY());
    }

    @Override
    public boolean isMouseButtonDown() {
        return Mouse.isButtonDown(0);
    }
}
