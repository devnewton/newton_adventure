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
package im.bci.newtonadv.platform.lwjgl.twl;

import im.bci.newtonadv.ui.OptionsGUI;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.NuitTranslator;
import im.bci.jnuit.lwjgl.LwjglNuitControls;
import im.bci.jnuit.lwjgl.LwjglNuitDisplay;
import im.bci.jnuit.lwjgl.LwjglNuitFont;
import im.bci.jnuit.lwjgl.LwjglNuitRenderer;
import im.bci.jnuit.noop.NoopNuitAudio;
import im.bci.jnuit.widgets.Root;
import im.bci.newtonadv.platform.lwjgl.LwjglHelper;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.platform.lwjgl.GameCloseException;
import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.ui.NewtonAdventureNuitTranslator;
import java.awt.Font;
import java.awt.image.BufferedImage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class OptionsSequence implements IOptionsSequence {

    private NuitToolkit toolkit;
    private Root root;
    private OptionsGUI optionsGui;
    private Sequence nextSequence;
    private final GameView view;
    private final GameInput input;
    private final Properties config;
    private final ISoundCache soundCache;
    private final IPlatformSpecific platform;

    public OptionsSequence(IPlatformSpecific platform, GameView view,
            GameInput input, ISoundCache soundCache,
            Properties config) {
        this.platform = platform;
        this.view = view;
        this.input = input;
        this.soundCache = soundCache;
        this.config = config;
    }

    @Override
    public void start() {
        final NuitTranslator nuitTranslator = new NewtonAdventureNuitTranslator();
        final LwjglNuitFont lwjglNuitFont = new LwjglNuitFont(new Font("Monospace", Font.PLAIN, 24), true, new char[0], new HashMap<Character, BufferedImage>());
        toolkit = new NuitToolkit(new LwjglNuitDisplay(), new LwjglNuitControls(), nuitTranslator, lwjglNuitFont, new LwjglNuitRenderer(nuitTranslator, lwjglNuitFont), new NoopNuitAudio());
        toolkit.setVirtualResolutionWidth(Game.DEFAULT_SCREEN_WIDTH);
        toolkit.setVirtualResolutionHeight(Game.DEFAULT_SCREEN_HEIGHT);
        optionsGui = new OptionsGUI(toolkit);
        root = new Root(toolkit);
        root.show(optionsGui);
    }

    @Override
    public void stop() {
        root = null;
        optionsGui = null;
        toolkit = null;
    }

    @Override
    public void draw() {
    }

    @Override
    public void processInputs() {
    }

    @Override
    public void update() throws Sequence.NormalTransitionException {
    }

    private void applyOptions() throws LWJGLException, RestartGameException {
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

    private void updateConfig() {
        config.setProperty("view.width", "" + LwjglHelper.getWidth());
        config.setProperty("view.height", "" + LwjglHelper.getHeight());
        config.setProperty("view.bpp", ""
                + Display.getDisplayMode().getBitsPerPixel());
        config.setProperty("view.fullscreen", "" + Display.isFullscreen());
        config.setProperty("view.rotate", "" + view.isRotateViewWithGravity());
        config.setProperty("view.draw.fps", "" + view.getMustDrawFPS());
        config.setProperty("view.quality", view.getQuality().toString());

        config.setProperty("key.jump", getKeyFieldName(input.keyJump));
        config.setProperty("key.left", getKeyFieldName(input.keyLeft));
        config.setProperty("key.right", getKeyFieldName(input.keyRight));
        config.setProperty("key.rotate_clockwise",
                getKeyFieldName(input.keyRotateClockwise));
        config.setProperty("key.rotate_counter_clockwise",
                getKeyFieldName(input.keyRotateCounterClockwise));
        config.setProperty("key.rotate_90_clockwise",
                getKeyFieldName(input.keyRotate90Clockwise));
        config.setProperty("key.rotate_90_counter_clockwise",
                getKeyFieldName(input.keyRotate90CounterClockwise));
        config.setProperty("key.return", getKeyFieldName(input.keyReturn));
        config.setProperty("key.return_to_menu",
                getKeyFieldName(input.keyReturnToMenu));

        if (Controllers.getControllerCount() > 0) {
            config.setProperty("joypad.name",
                    null != input.joypad ? input.joypad.getName() : "");
            config.setProperty("joypad.axis.x",
                    getJoypadAxisName(input.joypadXAxis));
            config.setProperty("joypad.axis.y",
                    getJoypadAxisName(input.joypadYAxis));
            config.setProperty("joypad.button.left",
                    getJoypadButtonName(input.joypadKeyLeft));
            config.setProperty("joypad.button.right",
                    getJoypadButtonName(input.joypadKeyRight));
            config.setProperty("joypad.button.jump",
                    getJoypadButtonName(input.joypadKeyJump));
            config.setProperty("joypad.button.return",
                    getJoypadButtonName(input.joypadKeyReturn));
            config.setProperty("joypad.button.return_to_menu",
                    getJoypadButtonName(input.joypadKeyReturnToMenu));
            config.setProperty("joypad.button.rotate_clockwise",
                    getJoypadButtonName(input.joypadKeyRotateClockwise));
            config.setProperty("joypad.button.rotate_counter_clockwise",
                    getJoypadButtonName(input.joypadKeyRotateCounterClockwise));
            config.setProperty("joypad.button.rotate_90_clockwise",
                    getJoypadButtonName(input.joypadKeyRotate90Clockwise));
            config.setProperty(
                    "joypad.button.rotate_90_counter_clockwise",
                    getJoypadButtonName(input.joypadKeyRotate90CounterClockwise));
        }
        config.setProperty("sound.enabled", "" + soundCache.isSoundEnabled());
        config.setProperty("music.enabled", "" + soundCache.isMusicEnabled());
        //config.setProperty("newton_adventure.mod", optionsGui.getSelectedModName());
    }

    private String getJoypadAxisName(int joypadXAxis) {
        if (null != input.joypad && joypadXAxis >= 0
                && joypadXAxis < input.joypad.getAxisCount()) {
            return input.joypad.getAxisName(joypadXAxis);
        }
        return "";
    }

    private String getJoypadButtonName(int joypadButton) {
        if (null != input.joypad && joypadButton >= 0
                && joypadButton < input.joypad.getButtonCount()) {
            return input.joypad.getButtonName(joypadButton);
        }
        return "";
    }

    private String getKeyFieldName(int key) {
        for (Field field : Keyboard.class.getFields()) {
            try {
                if (field.getName().startsWith("KEY_")
                        && field.getInt(null) == key) {
                    return field.getName();
                }
            } catch (Exception ex) {
                Logger.getLogger(OptionsSequence.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
        Logger.getLogger(OptionsSequence.class.getName()).log(Level.SEVERE,
                "Unknow lwjgl key value {0}", key);
        return "";
    }

    @Override
    public void resume() {
    }

    @Override
    public void tick() throws NormalTransitionException, ResumeTransitionException, RestartGameException {

        if (Display.isVisible() || Display.isDirty() || LwjglHelper.wasResized()) {
            toolkit.update(root);
            toolkit.getRenderer().render(root);

            Display.update(false);
            Display.sync(Game.FPS);
            Display.processMessages();

            if (Display.isCloseRequested()) {
                throw new GameCloseException();
            }
            
            if(optionsGui != root.getFocusedChild()) {
                 throw new Sequence.ResumeTransitionException(nextSequence);
            }

            /* if (optionsGui.okPressed) {
             try {
             applyOptions();
             } catch (LWJGLException e) {
             Logger.getLogger(getClass().getName()).log(Level.SEVERE,
             "cannot apply options", e);
             }
             updateConfig();
             platform.loadModIfNeeded(optionsGui.getSelectedModName());
             platform.saveConfig();
             throw new Sequence.ResumeTransitionException(nextSequence);
             }
             if (optionsGui.cancelPressed) {
             throw new Sequence.ResumeTransitionException(nextSequence);
             }*/
        }
    }
}
