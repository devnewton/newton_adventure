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

import com.esotericsoftware.tablelayout.twl.Table;
import de.matthiasmann.twl.BoxLayout;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TabbedPane;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import im.bci.newtonadv.platform.interfaces.IMod;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.platform.lwjgl.GameInput;
import im.bci.newtonadv.platform.lwjgl.GameView;
import im.bci.newtonadv.platform.lwjgl.GameViewQuality;
import im.bci.newtonadv.score.ScoreServer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class OptionsGUI extends Table {

    boolean okPressed, cancelPressed;
    ToggleButton soundEnabled;
    ToggleButton fullscreen;
    ToggleButton rotateViewWithGravity;
    ToggleButton mustDrawFPS;
    ComboBox<DisplayMode> mode;
    ComboBox<QualityItem> quality;
    InputChoice keyJump;
    InputChoice keyLeft;
    InputChoice keyRight;
    InputChoice keyRotateClockwise;
    InputChoice keyRotateCounterClockwise;
    InputChoice keyRotate90Clockwise;
    InputChoice keyRotate90CounterClockwise;
    InputChoice keyReturn;
    InputChoice keyReturnToMenu;
    ToggleButton scoreShareEnabled;
    EditField scoreServerUrl, scorePlayer, scoreSecret;
    ToggleButton musicEnabled;
    ComboBox<ControllerItem> joypad;
    ComboBox<String> joypadXAxis;
    ComboBox<String> joypadYAxis;
    ComboBox<IMod> mod;

    private static SimpleChangableListModel<String> keyModel = buildKeyListModel();
    private SimpleChangableListModel<ControllerItem> controllerModel = buildControllerListModel();
    private SimpleChangableListModel<QualityItem> qualityModel = buildQualityListModel();
    private SimpleChangableListModel<IMod> modModel = new SimpleChangableListModel<IMod>();
    private SimpleChangableListModel<String> joyAxisModel = new SimpleChangableListModel<String>();
    private SimpleChangableListModel<JoyButtonItem> joyButtonModel = new SimpleChangableListModel<JoyButtonItem>();
    private final IPlatformSpecific platform;

    OptionsGUI(GameView gameView, GameInput gameInput, ScoreServer scoreServer, ISoundCache soundCache, IPlatformSpecific platform) throws LWJGLException {
        this.platform = platform;
        setSize(Display.getWidth(), Display.getHeight());
        
        Table soundTable= new Table();
        soundTable.defaults().pad(3);
        soundTable.columnDefaults(0).pad(10);
        soundTable.columnDefaults(1).expandX();
        soundTable.columnDefaults(2).expandX();

        soundEnabled = new ToggleButton();
        soundEnabled.setTheme("checkbox");
        soundEnabled.setActive(soundCache.isSoundEnabled());
        musicEnabled = new ToggleButton();
        musicEnabled.setTheme("checkbox");
        musicEnabled.setActive(soundCache.isMusicEnabled());
        soundTable.addCell(addWithLabel(soundEnabled, platform.getMessage("options.sound.effect.enabled")));
        soundTable.row();
        soundTable.addCell(addWithLabel(musicEnabled, platform.getMessage("options.sound.music.enabled")));
        soundTable.row();
        
        Table videoTable= new Table();
        videoTable.defaults().pad(3);
        videoTable.columnDefaults(0).pad(10);
        videoTable.columnDefaults(1).expandX();

        mode = new ComboBox<DisplayMode>(new SimpleChangableListModel<DisplayMode>(getDisplayModes()));
        mode.setSelected(0);
        videoTable.addCell(new Label(platform.getMessage("options.video.mode")));
        videoTable.addCell(mode).fillX();
        videoTable.row();

        fullscreen = new ToggleButton();
        fullscreen.setTheme("checkbox");
        fullscreen.setActive(Display.isFullscreen());
        videoTable.addCell(new Label());
        videoTable.addCell(addWithLabel(fullscreen, platform.getMessage("options.fullscreen")));
        videoTable.row();
        
        quality = new ComboBox<QualityItem>(qualityModel);
        quality.setSelected(qualityModel.findElement(new QualityItem(gameView.getQuality())));
        videoTable.addCell(new Label(platform.getMessage("options.quality")));
        videoTable.addCell(quality).fillX();
        videoTable.row();

        rotateViewWithGravity = new ToggleButton();
        rotateViewWithGravity.setTheme("checkbox");
        rotateViewWithGravity.setActive(gameView.isRotateViewWithGravity());
        mustDrawFPS = new ToggleButton();
        mustDrawFPS.setTheme("checkbox");
        mustDrawFPS.setActive(gameView.getMustDrawFPS());
        videoTable.addCell(new Label());
        videoTable.addCell(addWithLabel(rotateViewWithGravity, platform.getMessage("options.view.rotate.with.gravity")));
        videoTable.row();
        videoTable.addCell(new Label());
        videoTable.addCell(addWithLabel(mustDrawFPS,platform.getMessage("options.view.draw.fps")));
        videoTable.row();
        
        Table joypadTable= new Table();
        joypadTable.defaults().pad(3);
        joypadTable.columnDefaults(0).pad(10);
        joypadTable.columnDefaults(1).expandX();
        joypadTable.columnDefaults(2).expandX();
        
        Table keyboardTable= new Table();
        keyboardTable.defaults().pad(3);
        keyboardTable.columnDefaults(0).pad(10);
        keyboardTable.columnDefaults(1).expandX();
        keyboardTable.columnDefaults(2).expandX();

        joypad = new ComboBox<ControllerItem>(controllerModel);
        joypad.setNoSelectionIsError(false);
        joypad.addCallback(new Runnable() {
            @Override
            public void run() {
                ControllerItem item = joypad.getModel().getEntry(joypad.getSelected());
                controllerSelected(item.getController());
            }
        });
        joypadXAxis = new ComboBox<String>(joyAxisModel);
        joypadYAxis = new ComboBox<String>(joyAxisModel);
        joypadTable.addCell(new Label(platform.getMessage("options.joypad")));
        joypadTable.addCell(joypad).fillX();
        joypadTable.row();

        joypadTable.addCell(new Label(platform.getMessage("options.joypad.xaxis")));
        joypadTable.addCell(joypadXAxis).fillX();
        joypadTable.row();
        joypadTable.addCell(new Label(platform.getMessage("options.joypad.xaxis")));
        joypadTable.addCell(joypadYAxis).fillX();
        joypadTable.row();
        if (null != gameInput.joypad) {
            int joypadIndex = controllerModel.findElement(new ControllerItem(gameInput.joypad));
            joypad.setSelected(joypadIndex);
            controllerSelected(gameInput.joypad);
            if (gameInput.joypadXAxis >= 0 && gameInput.joypadXAxis < gameInput.joypad.getAxisCount()) {
                joypadXAxis.setSelected(joyAxisModel.findElement(gameInput.joypad.getAxisName(gameInput.joypadXAxis)));
            }
            if (gameInput.joypadYAxis >= 0 && gameInput.joypadYAxis < gameInput.joypad.getAxisCount()) {
                joypadYAxis.setSelected(joyAxisModel.findElement(gameInput.joypad.getAxisName(gameInput.joypadYAxis)));
            }
        }

        keyJump = addInputChoice(joypadTable, keyboardTable, platform.getMessage("options.input.jump"), gameInput.keyJump, gameInput.joypadKeyJump);
        keyLeft = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.left"), gameInput.keyLeft, gameInput.joypadKeyLeft);
        keyRight = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.right"), gameInput.keyRight, gameInput.joypadKeyRight);
        keyRotateClockwise = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.rotate.clockwise"), gameInput.keyRotateClockwise, gameInput.joypadKeyRotateClockwise);
        keyRotateCounterClockwise = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.rotate.counterclockwise"), gameInput.keyRotateCounterClockwise, gameInput.joypadKeyRotateCounterClockwise);
        keyRotate90Clockwise = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.rotate.clockwise90"), gameInput.keyRotate90Clockwise, gameInput.joypadKeyRotate90Clockwise);
        keyRotate90CounterClockwise = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.rotate.counterclockwise90"), gameInput.keyRotate90CounterClockwise, gameInput.joypadKeyRotate90CounterClockwise);
        keyReturn = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.input.return"), gameInput.keyReturn, gameInput.joypadKeyReturn);
        keyReturnToMenu = addInputChoice(joypadTable, keyboardTable,platform.getMessage("options.return.to.menu"), gameInput.keyReturnToMenu, gameInput.joypadKeyReturnToMenu);

        Table scoreTable= new Table();
        scoreTable.defaults().pad(3);
        scoreTable.columnDefaults(0).pad(10);
        scoreTable.columnDefaults(1).expandX();
        scoreTable.columnDefaults(2).expandX();
        
        scoreShareEnabled = new ToggleButton();
        scoreShareEnabled.setTheme("checkbox");
        scoreShareEnabled.setActive(scoreServer.isScoreShareEnabled());
        scoreTable.addCell(new Widget());
        scoreTable.addCell(addWithLabel(scoreShareEnabled,platform.getMessage("options.share.score")));
        scoreTable.row();
        
        scoreServerUrl = new EditField();
        scoreServerUrl.setText(scoreServer.getServerUrl());
        scoreTable.addCell(new Label(platform.getMessage("options.score.server")));
        scoreTable.addCell(scoreServerUrl).fillX();
        scoreTable.row();

        scorePlayer = new EditField();
        scorePlayer.setText(scoreServer.getPlayer());
        scoreTable.addCell(new Label(platform.getMessage("options.player.name")));
        scoreTable.addCell(scorePlayer).fillX();
        scoreTable.row();

        scoreSecret = new EditField();
        scoreSecret.setText(scoreServer.getSecret());
        scoreTable.addCell(new Label(platform.getMessage("options.player.password")));
        scoreTable.addCell(scoreSecret).fillX();
        scoreTable.row();
        
        Table modTable= new Table();
        modTable.defaults().pad(3);
        modTable.columnDefaults(0).pad(10);
        modTable.columnDefaults(1).expandX();
        modTable.columnDefaults(2).expandX();
        
        modModel.addElement(new NullMod());
        modModel.addElements(platform.listMods());
        mod = new ComboBox<IMod>(modModel);
        mod.setNoSelectionIsError(false);
        if (null != platform.getCurrentMod()) {
            mod.setSelected(modModel.findElement(platform.getCurrentMod()));
        } else {
            mod.setSelected(0);
        }
        modTable.addCell(new Label(platform.getMessage("options.mod")));
        modTable.addCell(mod).fillX();
        modTable.row();

        Button ok = new Button(platform.getMessage("options.ok"));
        Button cancel = new Button(platform.getMessage("options.cancel"));
        ok.addCallback(new Runnable() {
            @Override
            public void run() {
                okPressed = true;
            }
        });
        cancel.addCallback(new Runnable() {
            @Override
            public void run() {
                cancelPressed = true;
            }
        });

        joypad.addCallback(new Runnable() {
            @Override
            public void run() {
                ControllerItem item = joypad.getModel().getEntry(joypad.getSelected());
                presetControllers(item.getController());
            }
        });
        
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addTab(platform.getMessage("options.video"), videoTable);
        tabbedPane.addTab(platform.getMessage("options.sound"), soundTable);
        tabbedPane.addTab(platform.getMessage("options.input.keyboard"), keyboardTable);
        tabbedPane.addTab(platform.getMessage("options.input.joypad"), joypadTable);
        tabbedPane.addTab(platform.getMessage("options.score"), scoreTable);
        tabbedPane.addTab(platform.getMessage("options.mod"), modTable);
        
        this.defaults().pad(10);
        this.addCell(tabbedPane).colspan(2).expand().fill();
        this.row();
        this.addCell(ok).right();
        this.addCell(cancel).left();
        this.row();
    }

    private void controllerSelected(Controller controller) {
        joyAxisModel.clear();
        joyButtonModel.clear();

        if (null != controller) {
            joyAxisModel.addElement("");
            for (int i = 0, n = controller.getAxisCount(); i < n; ++i) {
                joyAxisModel.addElement(controller.getAxisName(i));
            }
            joyButtonModel.addElement(new JoyButtonItem());
            for (int i = 0, n = controller.getButtonCount(); i < n; ++i) {
                joyButtonModel.addElement(new JoyButtonItem(i, controller.getButtonName(i)));
            }
        }
    }

    private void presetControllers(Controller controller) {
        if (null != controller) {
            JoypadPreset preset = JoypadPreset.find(controller);
            if (null == preset) {
                preset = new JoypadPreset(controller);
            }
            joypadXAxis.setSelected(preset.getxAxis() + 1);
            joypadYAxis.setSelected(preset.getyAxis() + 1);
            keyJump.joyButton.setSelected(preset.getKeyJump() + 1);
            keyRotateCounterClockwise.joyButton.setSelected(preset.getKeyRotateCounterClockWise() + 1);
            keyRotateClockwise.joyButton.setSelected(preset.getKeyRotateClockwise() + 1);
            keyRotate90CounterClockwise.joyButton.setSelected(preset.getKeyRotate90CounterClockWise() + 1);
            keyRotate90Clockwise.joyButton.setSelected(preset.getKeyRotate90Clockwise() + 1);
            keyReturnToMenu.joyButton.setSelected(preset.getKeyReturnToMenu() + 1);
            keyReturn.joyButton.setSelected(preset.getKeyReturn() + 1);
        }
    }

    String getSelectedModName() {
        if (mod.getSelected() >= 0) {
            return mod.getModel().getEntry(mod.getSelected()).getName();
        } else {
            return "";
        }
    }

    private SimpleChangableListModel<QualityItem> buildQualityListModel() {
        ArrayList<QualityItem> items = new ArrayList<QualityItem>();
        for (GameViewQuality q : GameViewQuality.values()) {
            items.add(new QualityItem(q));
        }
        return new SimpleChangableListModel<QualityItem>(items);
    }

    private Widget addWithLabel(ToggleButton button, String message) {
        BoxLayout b = new BoxLayout();
        b.add(button);
        b.add(new Label(" " + message));
        return b;
    }

    public class QualityItem {

        private final GameViewQuality quality;

        public QualityItem(GameViewQuality quality) {
            this.quality = quality;
        }

        public GameViewQuality getQuality() {
            return quality;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof QualityItem) {
                return quality == ((QualityItem) obj).quality;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (this.quality != null ? this.quality.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return platform.getMessage("options.quality." + quality.name());
        }
    }

    public static class ControllerItem {

        private Controller controller;

        ControllerItem(Controller controller) {
            this.controller = controller;
        }

        public ControllerItem() {
        }

        public Controller getController() {
            return controller;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ControllerItem) {
                Controller otherController = ((ControllerItem) o).getController();
                if (null == controller) {
                    return null == otherController;
                } else {
                    return controller.equals(otherController);
                }
            } else {
                return super.equals(o);
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + (this.controller != null ? this.controller.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            if (null != controller) {
                return controller.getName();
            } else {
                return "";
            }
        }
    }

    public static class JoyButtonItem {

        private final int buttonIndex;
        private final String buttonName;

        JoyButtonItem(int buttonIndex, String buttonName) {
            this.buttonIndex = buttonIndex;
            this.buttonName = buttonName;
        }

        public JoyButtonItem() {
            buttonIndex = -1;
            buttonName = "";
        }

        // used only for searching...
        public JoyButtonItem(String name) {
            buttonIndex = -42;
            buttonName = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof JoyButtonItem) {
                return buttonName.equals(((JoyButtonItem) o).buttonName);
            } else {
                return super.equals(o);
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (this.buttonName != null ? this.buttonName.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return buttonName;
        }

        public int getButtonIndex() {
            return buttonIndex;
        }
    }

    private SimpleChangableListModel<ControllerItem> buildControllerListModel() {
        SimpleChangableListModel<ControllerItem> model = new SimpleChangableListModel<ControllerItem>();
        model.addElement(new ControllerItem());
        for (int i = 0, n = Controllers.getControllerCount(); i < n; ++i) {
            model.addElement(new ControllerItem(Controllers.getController(i)));
        }
        return model;
    }

    /*@Override
    protected void layout() {
        table.setPosition((getWidth() - table.getPreferredWidth()) / 2, (getHeight() - table.getPreferredHeight()) / 2);
    }*/

    private static class NullMod implements IMod {

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getPath() {
            return null;
        }

        @Override
        public String toString() {
            return "default game";
        }
    }

    public class InputChoice {

        ComboBox<String> key;
        ComboBox<JoyButtonItem> joyButton;
    }

    private InputChoice addInputChoice(Table joypadTable, Table keyboardTable, String label, int key, int button) {
        ++button;
        InputChoice choice = new InputChoice();
        choice.key = new ComboBox<String>(keyModel);
        choice.joyButton = new ComboBox<JoyButtonItem>(joyButtonModel);
        joypadTable.addCell(new Label(label));
        joypadTable.addCell(choice.joyButton).fillX();
        joypadTable.row();
        keyboardTable.addCell(new Label(label));
        keyboardTable.addCell(choice.key).fillX();
        keyboardTable.row();
        choice.key.setSelected(keyModel.findElement((Keyboard.getKeyName(key))));
        if (button >= 0 && button < choice.joyButton.getModel().getNumEntries()) {
            choice.joyButton.setSelected(button);
        }
        return choice;
    }

    private static SimpleChangableListModel<String> buildKeyListModel() {
        SimpleChangableListModel<String> model = new SimpleChangableListModel<String>();
        for (Field field : Keyboard.class.getFields()) {
            String name = field.getName();
            if (name.startsWith("KEY_")) {
                try {
                    model.addElement(Keyboard.getKeyName(field.getInt(null)));
                } catch (Exception e) {
                    Logger.getLogger(OptionsGUI.class.getName()).log(Level.SEVERE, "error retrieving key name", e);
                }
            }
        }
        return model;
    }

    private Collection<DisplayMode> getDisplayModes() throws LWJGLException {
        ArrayList<DisplayMode> modes = new ArrayList<DisplayMode>(java.util.Arrays.asList(Display.getAvailableDisplayModes()));
        java.util.Collections.sort(modes, new Comparator<DisplayMode>() {
            @Override
            public int compare(DisplayMode o1, DisplayMode o2) {
                int w1 = o1.getWidth(), w2 = o2.getWidth();
                if (w1 < w2) {
                    return -1;
                } else if (w2 < w1) {
                    return 1;
                }

                int h1 = o1.getHeight(), h2 = o2.getHeight();
                if (h1 < h2) {
                    return -1;
                } else if (h2 < h1) {
                    return 1;
                }

                int b1 = o1.getBitsPerPixel(), b2 = o2.getBitsPerPixel();
                if (b1 < b2) {
                    return -1;
                } else if (b2 < b1) {
                    return 1;
                }

                int f1 = o1.getFrequency(), f2 = o2.getFrequency();
                if (f1 < f2) {
                    return -1;
                } else if (f2 < f1) {
                    return 1;
                } else {
                    return 0;
                }

            }
        });
        modes.add(0, Display.getDisplayMode());
        return modes;
    }
}
