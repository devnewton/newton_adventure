/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.game;

/**
 *
 * @author bob
 */
public interface PreloadableSequence extends Sequence {
    
    void startPreload();

    boolean preloadSomeAndCheckIfTerminated();

    void finishPreload();
    
}
