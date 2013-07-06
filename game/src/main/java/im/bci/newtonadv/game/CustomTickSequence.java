package im.bci.newtonadv.game;

public interface CustomTickSequence {
	
	void tick() throws Sequence.NormalTransitionException, Sequence.ResumeTransitionException, RestartGameException;

}
