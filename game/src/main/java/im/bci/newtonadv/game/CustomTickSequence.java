package im.bci.newtonadv.game;

import im.bci.newtonadv.game.Sequence.NormalTransitionException;

public interface CustomTickSequence {
	
	void tick() throws NormalTransitionException, RestartGameException;

}
