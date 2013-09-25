package im.bci.newtonadv.platform.lwjgl.nuit.controls;

public class ActionActivatedDetector {
    private final Action action;
    private Float[] previousStates;
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
        Control[] controls = action.getControls();
        int nbActions = controls.length;
        if(null == previousStates || nbActions != previousStates.length) {
            previousStates = new Float[nbActions];
        }
        activated = false;
        for(int i=0; i<nbActions; ++i) {
            Control control = controls[i];
            float newState =  control.getValue();
            if(null != previousStates[i]) {
                if(newState > control.getDeadZone() && previousStates[i] <= control.getDeadZone()) {
                    activated = true;
                }
            }
            previousStates[i] = newState;
        }        
    }
    
    public void reset() {
        previousStates = null;
    }
}
