package im.bci.newtonadv.platform.teavm;

import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.controls.Action;
import im.bci.jnuit.controls.ActionActivatedDetector;
import im.bci.jnuit.teavm.TeavmNuitControls;
import im.bci.jnuit.teavm.TeavmNuitPreferences;
import im.bci.jnuit.teavm.controls.Keyboard;
import im.bci.jnuit.teavm.controls.Mouse;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import java.util.Arrays;
import java.util.List;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import org.teavm.jso.browser.Window;

/**
 *
 * @author devnewton
 */
class TeavmGameInput extends AbstractGameInput {

    private final Keyboard keyboard;
    private final Mouse mouse;

    TeavmGameInput(NuitToolkit nuitToolkit, TeavmNuitControls controls, TeavmNuitPreferences config) {
        super(nuitToolkit, config);
        this.keyboard = controls.getKeyboard();
        this.mouse = controls.getMouse();
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
                    new Action("action.activate", keyboard.getKeyControl("ArrowDown")));
            defaultJump = new ActionActivatedDetector(
                    new Action("action.jump", keyboard.getKeyControl("ArrowUp")));
            defaultLeft = new ActionActivatedDetector(
                    new Action("action.left", keyboard.getKeyControl("ArrowLeft")));
            defaultRight = new ActionActivatedDetector(
                    new Action("action.right", keyboard.getKeyControl("ArrowRight")));
            defaultRotateClockwise = new ActionActivatedDetector(
                    new Action("action.rotate.clockwise", keyboard.getKeyControl("c")));
            defaultRotateCounterClockwise = new ActionActivatedDetector(
                    new Action("action.rotate.counterclockwise", keyboard.getKeyControl("x")));
            defaultRotate90Clockwise = new ActionActivatedDetector(
                    new Action("action.rotate.clockwise.90", keyboard.getKeyControl("d")));
            defaultRotate90CounterClockwise = new ActionActivatedDetector(
                    new Action("action.rotate.counterclockwise.90", keyboard.getKeyControl("s")));
            defaultReturnToMenu = new ActionActivatedDetector(
                    new Action("action.returntomenu", keyboard.getKeyControl("Escape")));
        }

        public List<Action> toList() {
            return Arrays.asList(defaultJump.getAction(), defaultActivate.getAction(), defaultLeft.getAction(), defaultRight.getAction(),
                    defaultRotateClockwise.getAction(), defaultRotateCounterClockwise.getAction(), defaultRotate90Clockwise.getAction(),
                    defaultRotate90CounterClockwise.getAction(), defaultReturnToMenu.getAction());
        }
    }

    @Override
    public List<Action> getDefaultGameActionList() {
        return new DefaultGameControls().toList();
    }

    @Override
    public ROVector2f getMousePos() {
        int x= mouse.getClientX();
        int y = mouse.getClientY();
        int height = Window.current().getInnerHeight();
        return new Vector2f(x, height - y);
    }

    @Override
    public boolean isMouseButtonDown() {
        return this.mouse.isPressed();
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
                new Action("cheat.activate.all", keyboard.getKeyControl("F8")));
        cheatGetWorldMap = new ActionActivatedDetector(
                new Action("cheat.get.world.map", keyboard.getKeyControl("F9")));
        cheatGetCompass = new ActionActivatedDetector(
                new Action("cheat.get.compass", keyboard.getKeyControl("F10")));
        cheatGotoNextBonusLevel = new ActionActivatedDetector(
                new Action("cheat.goto.next.bonus.level", keyboard.getKeyControl("F11")));
        cheatGotoNextLevel = new ActionActivatedDetector(
                new Action("cheat.goto.next.level", keyboard.getKeyControl("F12")));
        cheatSetAllCompleted = new ActionActivatedDetector(
                new Action("cheat.set.all.completed", keyboard.getKeyControl("F12")));
    }

}
