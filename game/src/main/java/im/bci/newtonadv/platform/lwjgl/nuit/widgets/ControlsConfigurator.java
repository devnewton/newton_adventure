package im.bci.newtonadv.platform.lwjgl.nuit.widgets;

import im.bci.newtonadv.platform.lwjgl.nuit.controls.Action;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.Control;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.GamepadAxisControl;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.GamepadButtonControl;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.KeyControl;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.MouseButtonControl;
import im.bci.newtonadv.platform.lwjgl.twl.OptionsGUI;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ControlsConfigurator {
    
    private List<Control> possibleControls;
    private List<Action> actions;
    
    ControlsConfigurator(List<Action> actions) {
        this.actions = actions;
        initPossibleControls();
    }

    private void initPossibleControls() {
        possibleControls = new ArrayList<>();
        for(int c=0; c<Controllers.getControllerCount();  ++c) {
            Controller pad = Controllers.getController(c);
            for(int a=0; a<pad.getAxisCount(); ++a) {
                possibleControls.add(new GamepadAxisControl(pad, a));
            }
            for(int b=0; b<pad.getButtonCount(); ++b) {
                possibleControls.add(new GamepadButtonControl(pad, b));
            }
        }
        for (Field field : Keyboard.class.getFields()) {
            String name = field.getName();
            if (name.startsWith("KEY_")) {
                try {
                    int key = field.getInt(null);
                    possibleControls.add(new KeyControl(key));
                } catch (Exception e) {
                    Logger.getLogger(OptionsGUI.class.getName()).log(Level.SEVERE, "error retrieving key", e);
                }
            }
        }
        for(int m = 0; m < Mouse.getButtonCount(); ++m) {
            possibleControls.add(new MouseButtonControl(m));
        }
    }

}
