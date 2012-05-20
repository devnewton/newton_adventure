package im.bci.newtonadv.platform.interfaces;

import im.bci.newtonadv.game.CustomTickSequence;
import im.bci.newtonadv.game.Sequence;

public interface IOptionsSequence extends Sequence, CustomTickSequence {
	
	void setNextSequence(Sequence mainMenuSequence);

}
