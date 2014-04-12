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
package im.bci.newtonadv.platform.interfaces;

import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.controls.Action;
import im.bci.jnuit.controls.ActionActivatedDetector;
import java.util.Arrays;
import java.util.List;
import net.phys2d.math.ROVector2f;

/**
 *
 * @author devnewton
 */
public abstract class AbstractGameInput {

    protected ActionActivatedDetector cheatActivateAll;
    protected ActionActivatedDetector cheatGetCompass;
    protected ActionActivatedDetector cheatGetWorldMap;
    protected ActionActivatedDetector cheatGotoNextBonusLevel;
    protected ActionActivatedDetector cheatGotoNextLevel;
    protected ActionActivatedDetector cheatSetAllCompleted;
    protected NuitPreferences config;
    protected ActionActivatedDetector jump;
    protected ActionActivatedDetector left;
    protected ActionActivatedDetector returnToMenu;
    protected ActionActivatedDetector right;
    protected ActionActivatedDetector rotate90Clockwise;
    protected ActionActivatedDetector rotate90CounterClockwise;
    protected ActionActivatedDetector rotateClockwise;
    protected ActionActivatedDetector rotateCounterClockwise;
    protected ActionActivatedDetector menuDown;
    protected ActionActivatedDetector menuLeft;
    protected ActionActivatedDetector menuOk;
    protected ActionActivatedDetector menuRight;
    protected ActionActivatedDetector menuUp;
    protected NuitToolkit toolkit;

    public AbstractGameInput(NuitToolkit toolkit, NuitPreferences config) {
        this.config = config;
        this.toolkit = toolkit;
    }

    public void setup() {
        menuOk = new ActionActivatedDetector(toolkit.getMenuOK());
        menuUp = new ActionActivatedDetector(toolkit.getMenuUp());
        menuDown = new ActionActivatedDetector(toolkit.getMenuDown());
        menuLeft = new ActionActivatedDetector(toolkit.getMenuLeft());
        menuRight = new ActionActivatedDetector(toolkit.getMenuRight());
        setupGameControls();
        for (Action action : getGameActionList()) {
            loadControlsForAction(action);
        }
        for (Action action : toolkit.getMenuActionList()) {
            loadControlsForAction(action);
        }
    }
    
    public void saveConfig() {
        for (Action action : getGameActionList()) {
            saveControlsForAction(action);
        }
        for (Action action : toolkit.getMenuActionList()) {
            saveControlsForAction(action);
        }
    }

    public ActionActivatedDetector getCheatActivateAll() {
        return cheatActivateAll;
    }

    public ActionActivatedDetector getCheatGetCompass() {
        return cheatGetCompass;
    }

    public ActionActivatedDetector getCheatGetWorldMap() {
        return cheatGetWorldMap;
    }

    public ActionActivatedDetector getCheatGotoNextBonusLevel() {
        return cheatGotoNextBonusLevel;
    }

    public ActionActivatedDetector getCheatGotoNextLevel() {
        return cheatGotoNextLevel;
    }

    public abstract List<Action> getDefaultGameActionList();

    public List<Action> getGameActionList() {
        return Arrays.asList(getLeft().getAction(), getRight().getAction(), getJump().getAction(), getRotateClockwise().getAction(), getRotateCounterClockwise().getAction(), getRotate90Clockwise().getAction(), getRotate90CounterClockwise().getAction(), getReturnToMenu().getAction());
    }

    public ActionActivatedDetector getJump() {
        return jump;
    }

    public ActionActivatedDetector getLeft() {
        return left;
    }

    public abstract ROVector2f getMousePos();

    public ActionActivatedDetector getReturnToMenu() {
        return returnToMenu;
    }

    public ActionActivatedDetector getRight() {
        return right;
    }

    public ActionActivatedDetector getRotate90Clockwise() {
        return rotate90Clockwise;
    }

    public ActionActivatedDetector getRotate90CounterClockwise() {
        return rotate90CounterClockwise;
    }

    public ActionActivatedDetector getRotateClockwise() {
        return rotateClockwise;
    }

    public ActionActivatedDetector getRotateCounterClockwise() {
        return rotateCounterClockwise;
    }

    public abstract boolean isMouseButtonDown();

    protected void loadControlsForAction(Action action) {
        String name = action.getName();
        action.setMainControl(config.getControl(name + ".main", action.getMainControl()));
        action.setAlternativeControl(config.getControl(name + ".alternative", action.getAlternativeControl()));
    }
    
    protected void saveControlsForAction(Action action) {
        String name = action.getName();
        config.putControl(name + ".main", action.getMainControl());
        config.putControl(name + ".alternative", action.getAlternativeControl());
    }

    public void poll() {
        menuOk.poll();
        menuUp.poll();
        menuDown.poll();
        menuLeft.poll();
        menuRight.poll();
        jump.poll();
        right.poll();
        rotateClockwise.poll();
        rotateCounterClockwise.poll();
        rotate90Clockwise.poll();
        rotate90CounterClockwise.poll();
        returnToMenu.poll();
    }

    protected abstract void setupGameControls();

    public ActionActivatedDetector getCheatSetAllCompleted() {
        return cheatSetAllCompleted;
    }

    public ActionActivatedDetector getMenuDown() {
        return menuDown;
    }

    public ActionActivatedDetector getMenuLeft() {
        return menuLeft;
    }

    public ActionActivatedDetector getMenuOk() {
        return menuOk;
    }

    public ActionActivatedDetector getMenuRight() {
        return menuRight;
    }

    public ActionActivatedDetector getMenuUp() {
        return menuUp;
    }
    
    
}
