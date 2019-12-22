package im.bci.newtonadv.platform.teavm;

import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.controls.Action;
import im.bci.jnuit.teavm.TeavmNuitPreferences;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import java.util.List;
import net.phys2d.math.ROVector2f;

/**
 *
 * @author devnewton
 */
class TeavmGameInput extends AbstractGameInput {

    TeavmGameInput(NuitToolkit nuitToolkit, TeavmNuitPreferences config) {
        super(nuitToolkit, config);
    }

    @Override
    public List<Action> getDefaultGameActionList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ROVector2f getMousePos() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isMouseButtonDown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setupGameControls() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
