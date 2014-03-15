/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.ui;

import im.bci.newtonadv.ui.OptionsGUI;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.widgets.Root;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;

public class OptionsSequence implements IOptionsSequence {

    private NuitToolkit toolkit;
    private Root root;
    private OptionsGUI optionsGui;
    private Sequence nextSequence;
    private final IPlatformSpecific platform;

    public OptionsSequence(IPlatformSpecific platform) {
        this.platform = platform;
    }

    @Override
    public void start() {
        toolkit = platform.getNuitToolkit();
        optionsGui = new OptionsGUI(toolkit, platform);
        root = new Root(toolkit);
        root.show(optionsGui);
    }

    @Override
    public void stop() {
        root = null;
        optionsGui = null;
        toolkit = null;
        platform.getConfig().putString("locale", platform.getNuitToolkit().getCurrentLocale().toString());
        platform.getConfig().saveConfig();
    }

    @Override
    public void draw() {
        toolkit.getRenderer().render(root);
    }

    @Override
    public void processInputs() {
    }

    @Override
    public void update() throws Sequence.NormalTransitionException, ResumeTransitionException, RestartGameException {
        toolkit.update(root);
        root.update(1.0f / Game.FPS);
        if (optionsGui != root.getFocusedChild()) {
            platform.loadModIfNeeded(optionsGui.getSelectedModName());
            throw new Sequence.ResumeTransitionException(nextSequence);
        }
    }

    private void applyOptions() throws RestartGameException {
        /* view.setRotateViewWithGravity(optionsGui.rotateViewWithGravity.isActive());
         view.setMustDrawFPS(optionsGui.mustDrawFPS.isActive());
         view.setDisplayMode(optionsGui.fullscreen.isActive(),
         getSelectedQuality(), getSelectedMode());
         input.keyJump = findKeyIndex(optionsGui.keyJump);
         input.keyLeft = findKeyIndex(optionsGui.keyLeft);
         input.keyRight = findKeyIndex(optionsGui.keyRight);
         input.keyRotate90Clockwise = findKeyIndex(optionsGui.keyRotate90Clockwise);
         input.keyRotate90CounterClockwise = findKeyIndex(optionsGui.keyRotate90CounterClockwise);
         input.keyRotateClockwise = findKeyIndex(optionsGui.keyRotateClockwise);
         input.keyRotateCounterClockwise = findKeyIndex(optionsGui.keyRotateCounterClockwise);
         input.keyReturn = findKeyIndex(optionsGui.keyReturn);
         input.keyReturnToMenu = findKeyIndex(optionsGui.keyReturnToMenu);
         if (optionsGui.joypad.getSelected() >= 0
         && optionsGui.joypad.getSelected() < optionsGui.joypad
         .getModel().getNumEntries()) {
         ControllerItem controllerItem = optionsGui.joypad.getModel()
         .getEntry(optionsGui.joypad.getSelected());
         input.joypad = null != controllerItem ? controllerItem
         .getController() : null;
         if (null != input.joypad) {
         input.joypadXAxis = findJoypadAxisIndex(input.joypad,
         optionsGui.joypadXAxis);
         input.joypadYAxis = findJoypadAxisIndex(input.joypad,
         optionsGui.joypadYAxis);
         input.joypadKeyJump = findJoypadButtonIndex(input.joypad,
         optionsGui.keyJump);
         input.joypadKeyLeft = findJoypadButtonIndex(input.joypad,
         optionsGui.keyLeft);
         input.joypadKeyRight = findJoypadButtonIndex(input.joypad,
         optionsGui.keyRight);
         input.joypadKeyReturn = findJoypadButtonIndex(input.joypad,
         optionsGui.keyReturn);
         input.joypadKeyReturnToMenu = findJoypadButtonIndex(
         input.joypad, optionsGui.keyReturnToMenu);
         input.joypadKeyRotate90Clockwise = findJoypadButtonIndex(
         input.joypad, optionsGui.keyRotate90Clockwise);
         input.joypadKeyRotate90CounterClockwise = findJoypadButtonIndex(
         input.joypad, optionsGui.keyRotate90CounterClockwise);
         input.joypadKeyRotateClockwise = findJoypadButtonIndex(
         input.joypad, optionsGui.keyRotateClockwise);
         input.joypadKeyRotateCounterClockwise = findJoypadButtonIndex(
         input.joypad, optionsGui.keyRotateCounterClockwise);
         if (null == JoypadPreset.find(input.joypad)) {
         Logger.getLogger(JoypadPreset.class.getName()).log(Level.INFO, "This joypad has no preset. Please copy and send this message by mail to devnewton@bci.im to improve Newton Adventure joypad support:  name ''{0}'', xAxis={1}, yAxis={2}, keyJump={3}, keyLeft={4}, keyRight={5}, keyRotateClockwise={6}, keyRotateCounterClock={7}, keyRotate90Clockwise={8}, keyRotate90CounterClock={9}, keyReturn={10}, keyReturnToMenu={11}]", new Object[]{input.joypad.getName(), input.joypadXAxis, input.joypadYAxis, input.joypadKeyJump, input.joypadKeyLeft, input.joypadKeyRight, input.joypadKeyRotateClockwise, input.joypadKeyRotateCounterClockwise, input.joypadKeyRotate90Clockwise, input.joypadKeyRotate90CounterClockwise, input.joypadKeyReturn, input.joypadKeyReturnToMenu});
         }
         }
         } else {
         input.joypad = null;
         }
         soundCache.setSoundEnabled(optionsGui.soundEnabled.isActive());
         soundCache.setMusicEnabled(optionsGui.musicEnabled.isActive());*/
    }

    @Override
    public void setNextSequence(Sequence mainMenuSequence) {
        this.nextSequence = mainMenuSequence;
    }

    @Override
    public void resume() {
    }
}
