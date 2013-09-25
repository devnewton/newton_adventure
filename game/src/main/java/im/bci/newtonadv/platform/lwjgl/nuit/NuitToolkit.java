package im.bci.newtonadv.platform.lwjgl.nuit;

import java.awt.Font;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import im.bci.newtonadv.platform.lwjgl.TrueTypeFont;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.Action;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.ActionActivatedDetector;
import im.bci.newtonadv.platform.lwjgl.nuit.controls.KeyControl;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Root;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Table;
import im.bci.newtonadv.platform.lwjgl.nuit.widgets.Widget;

import com.esotericsoftware.tablelayout.BaseTableLayout.Debug;
import com.esotericsoftware.tablelayout.BaseTableLayout;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Toolkit;

public class NuitToolkit extends Toolkit<Widget, Table> {

    private ActionActivatedDetector menuUp, menuDown, menuLeft, menuRight, menuOK, menuCancel;
    
    private TrueTypeFont font;

    public NuitToolkit() {
        menuUp = new ActionActivatedDetector(new Action("menu up", new KeyControl(Keyboard.KEY_UP)));
        menuDown = new ActionActivatedDetector(new Action("menu down", new KeyControl(Keyboard.KEY_DOWN)));
        menuLeft = new ActionActivatedDetector(new Action("menu left", new KeyControl(Keyboard.KEY_LEFT)));
        menuRight = new ActionActivatedDetector(new Action("menu right", new KeyControl(Keyboard.KEY_RIGHT)));
        menuOK = new ActionActivatedDetector(new Action("menu ok", new KeyControl(Keyboard.KEY_RETURN)));
        menuCancel = new ActionActivatedDetector(new Action("menu cancel", new KeyControl(Keyboard.KEY_ESCAPE)));
       font = new TrueTypeFont(null, new Font("monospaced", Font.BOLD, 24), true, new char[0], new HashMap<Character, String>());
    }

    public TrueTypeFont getFont() {
        return font;
    }
    
    public Action getMenuUp() {
        return menuUp.getAction();
    }

    public Action getMenuDown() {
        return menuDown.getAction();
    }

    public Action getMenuLeft() {
        return menuLeft.getAction();
    }

    public Action getMenuRight() {
        return menuRight.getAction();
    }

    public Action getMenuOK() {
        return menuOK.getAction();
    }

    public Action getMenuCancel() {
        return menuCancel.getAction();
    }

    @Override
    public Cell<Widget, Table> obtainCell(BaseTableLayout<Widget, Table> layout) {
        Cell<Widget, Table> cell = new Cell<Widget, Table>();
        cell.setLayout(layout);
        return cell;
    }

    @Override
    public void freeCell(Cell<Widget, Table> cell) {
    }

    @Override
    public void addChild(Widget parent, Widget child) {
        parent.add(child);
    }

    @Override
    public void removeChild(Widget parent, Widget child) {
        parent.remove(child);
    }

    @Override
    public float getMinWidth(Widget widget) {
        return widget.getMinWidth();
    }

    @Override
    public float getMinHeight(Widget widget) {
        return widget.getMinHeight();
    }

    @Override
    public float getPrefWidth(Widget widget) {
        return widget.getPreferredWidth();
    }

    @Override
    public float getPrefHeight(Widget widget) {
        return widget.getPreferredHeight();
    }

    @Override
    public float getMaxWidth(Widget widget) {
        return widget.getMaxWidth();
    }

    @Override
    public float getMaxHeight(Widget widget) {
        return widget.getMaxHeight();
    }

    @Override
    public float getWidth(Widget widget) {
        return widget.getWidth();
    }

    @Override
    public float getHeight(Widget widget) {
        return widget.getHeight();
    }

    @Override
    public void clearDebugRectangles(BaseTableLayout<Widget, Table> layout) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addDebugRectangle(BaseTableLayout<Widget, Table> layout, Debug type, float x, float y, float w, float h) {
        // TODO Auto-generated method stub

    }

    public void update(Root root) {
        menuUp.poll();
        menuDown.poll();
        menuLeft.poll();
        menuRight.poll();
        menuOK.poll();
        menuCancel.poll();
        
        if(menuUp.isActivated()) {
            root.onUp();
        }
        if(menuDown.isActivated()) {
            root.onDown();
        }
        if(menuLeft.isActivated()) {
            root.onLeft();
        }
        if(menuRight.isActivated()) {
            root.onRight();
        }
        if(menuOK.isActivated()) {
            root.onOK();
        }
        if(menuCancel.isActivated()) {
            root.onCancel();
        }
    }
    
    public void resetInputPoll() {
        menuUp.reset();
        menuDown.reset();
        menuLeft.reset();
        menuRight.reset();
        menuOK.reset();
        menuCancel.reset();
    }
}
