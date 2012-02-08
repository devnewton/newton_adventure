package im.bci.newtonadv.platform.android;

import java.util.Properties;

import net.phys2d.math.ROVector2f;

import im.bci.newtonadv.platform.interfaces.IGameInput;

public class AndroidGameInput implements IGameInput {

	boolean keyJumpDown;
	boolean keyLeftDown;
	boolean keyRightDown;
	boolean keyRotateClockwiseDown;
	boolean keyRotateCounterClockwiseDown;
	boolean keyRotate90ClockwiseDown;
	boolean keyRotate90CounterClockwiseDown;
	boolean keyPauseDown;
	boolean keyReturnToMenuDown;
	boolean keyCheatActivateAllDown;
	boolean keyCheatGotoNextLevelDown;
	boolean keyDownDown;
	boolean keyReturnDown;
	boolean keyUpDown;
	boolean keyCheatGotoNextBonusLevelDown;

	public AndroidGameInput(Properties config) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isKeyCheatActivateAllDown() {
		return keyCheatActivateAllDown;
	}

	@Override
	public boolean isKeyCheatGotoNextLevelDown() {
		return keyCheatGotoNextLevelDown;
	}

	@Override
	public boolean isKeyDownDown() {
		return keyDownDown;
	}

	@Override
	public boolean isKeyJumpDown() {
		return keyJumpDown;
	}

	@Override
	public boolean isKeyLeftDown() {
		return keyLeftDown;
	}

	@Override
	public boolean isKeyPauseDown() {
		return keyPauseDown;
	}

	@Override
	public boolean isKeyReturnDown() {

		return keyReturnDown;
	}

	@Override
	public boolean isKeyReturnToMenuDown() {

		return keyReturnToMenuDown;
	}

	@Override
	public boolean isKeyRightDown() {

		return keyRightDown;
	}

	@Override
	public boolean isKeyRotate90ClockwiseDown() {

		return keyRotate90ClockwiseDown;
	}

	@Override
	public boolean isKeyRotate90CounterClockwiseDown() {

		return keyRotate90CounterClockwiseDown;
	}

	@Override
	public boolean isKeyRotateClockwiseDown() {

		return keyRotateClockwiseDown;
	}

	@Override
	public boolean isKeyRotateCounterClockwiseDown() {

		return keyRotateCounterClockwiseDown;
	}

	@Override
	public boolean isKeyToggleFullscreenDown() {
		return false;
	}

	@Override
	public boolean isKeyUpDown() {
		return keyUpDown;
	}

	@Override
	public boolean isKeyCheatGotoNextBonusLevelDown() {
		return keyCheatGotoNextBonusLevelDown;
	}

	@Override
	public ROVector2f getMousePos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMouseButtonDown() {
		// TODO Auto-generated method stub
		return false;
	}
}
