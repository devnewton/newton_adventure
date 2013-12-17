/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.game.time;

import im.bci.newtonadv.game.FrameTimeInfos;

/**
 *
 * @author bob
 */
public abstract class TimedAction {

    public abstract float getProgress();

    public abstract void update(FrameTimeInfos f);
    
}
