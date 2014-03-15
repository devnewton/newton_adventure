package im.bci.newtonadv.platform.interfaces;

import im.bci.newtonadv.game.Sequence;

public interface IOptionsSequence extends Sequence {
	
	void setNextSequence(Sequence mainMenuSequence);

}
