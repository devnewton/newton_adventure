package im.bci.newtonadv.platform.lwjgl.nuit.controls;

import java.util.List;

public class ActionActivatedDetector {
    private final Action action;
    private float[] previousStates;
    private boolean activated;
    
    public ActionActivatedDetector(Action action) {
        this.action = action;
    }
    
    public Action getAction() {
        return action;
    }
    
    public boolean isActivated() {
        return activated;
    }
        
    public void poll() {
        List<Control> controls = action.getControls();
        int nbActions = controls.size();
        if(null == previousStates || nbActions != previousStates.length) {
            previousStates = new float[nbActions];
        }
        activated = false;
        for(int i=0; i<nbActions; ++i) {
            Control control = controls.get(i);
            float newState =  control.getValue();
            if(newState > control.getDeadZone() && previousStates[i] <= control.getDeadZone()) {
                activated = true;
            }
            previousStates[i] = newState;
        }        
    }
}
