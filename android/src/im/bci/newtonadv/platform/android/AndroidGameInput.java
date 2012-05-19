package im.bci.newtonadv.platform.android;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import net.phys2d.math.ROVector2f;

import im.bci.newtonadv.platform.interfaces.IGameInput;

public class AndroidGameInput implements IGameInput {

	AndroidGameInputData data = new AndroidGameInputData();
	LinkedBlockingQueue<AndroidGameInputData> dataBuffer = new LinkedBlockingQueue<AndroidGameInputData>();

	public AndroidGameInput(Properties config) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isKeyCheatActivateAllDown() {
		return data.keyCheatActivateAllDown;
	}

	@Override
	public boolean isKeyCheatGotoNextLevelDown() {
		return data.keyCheatGotoNextLevelDown;
	}

	@Override
	public boolean isKeyDownDown() {
		return data.keyDownDown;
	}

	@Override
	public boolean isKeyJumpDown() {
		return data.keyJumpDown;
	}

	@Override
	public boolean isKeyLeftDown() {
		return data.keyLeftDown;
	}

	@Override
	public boolean isKeyPauseDown() {
		return data.keyPauseDown;
	}

	@Override
	public boolean isKeyReturnDown() {

		return data.keyReturnDown;
	}

	@Override
	public boolean isKeyReturnToMenuDown() {

		return data.keyReturnToMenuDown;
	}

	@Override
	public boolean isKeyRightDown() {

		return data.keyRightDown;
	}

	@Override
	public boolean isKeyRotate90ClockwiseDown() {

		return data.keyRotate90ClockwiseDown;
	}

	@Override
	public boolean isKeyRotate90CounterClockwiseDown() {

		return data.keyRotate90CounterClockwiseDown;
	}

	@Override
	public boolean isKeyRotateClockwiseDown() {

		return data.keyRotateClockwiseDown;
	}

	@Override
	public boolean isKeyRotateCounterClockwiseDown() {

		return data.keyRotateCounterClockwiseDown;
	}

	@Override
	public boolean isKeyToggleFullscreenDown() {
		return false;
	}

	@Override
	public boolean isKeyUpDown() {
		return data.keyUpDown;
	}

	@Override
	public boolean isKeyCheatGotoNextBonusLevelDown() {
		return data.keyCheatGotoNextBonusLevelDown;
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

	@Override
	public boolean isKeyCheatGetWorldMapDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isKeyCheatGetCompassDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean poll() {
		AndroidGameInputData newData = dataBuffer.poll();
		if(null != newData) {
			data = newData;
			return true;
		}else {
			return false;
		}
	}
}
