/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.game;

import im.bci.newtonadv.Game;

/**
 *
 * @author bob
 */
class HelpSequence extends StoryboardSequence {

    public HelpSequence(Game game, String file, Object object, AbstractTransitionException transition) {
        super(game, file, null, transition);
    }

    @Override
    public void processInputs() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        super.processInputs();
        if (game.getInput().isKeyCheatSetAllCompletedDown()) {
            game.cheatSetAllCompleted();
        }
    }
    
    
    
}
