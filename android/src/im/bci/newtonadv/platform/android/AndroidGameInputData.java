package im.bci.newtonadv.platform.android;

import net.phys2d.math.Vector2f;

public class AndroidGameInputData {
	public boolean keyJumpDown;
	public boolean keyLeftDown;
	public boolean keyRightDown;
	public boolean keyRotateClockwiseDown;
	public boolean keyRotateCounterClockwiseDown;
	public boolean keyRotate90ClockwiseDown;
	public boolean keyRotate90CounterClockwiseDown;
	public boolean keyPauseDown;
	public boolean keyReturnToMenuDown;
	public boolean keyCheatActivateAllDown;
	public boolean keyCheatGotoNextLevelDown;
	public boolean keyDownDown;
	public boolean keyReturnDown;
	public boolean keyUpDown;
	public boolean keyCheatGotoNextBonusLevelDown;
	public boolean mouseButtonDown;
	public Vector2f mousePos;

	public AndroidGameInputData() {
	}

	public AndroidGameInputData(AndroidGameInputData other) {
		this.keyJumpDown = other.keyJumpDown;
		this.keyLeftDown = other.keyLeftDown;
		this.keyRightDown = other.keyRightDown;
		this.keyRotateClockwiseDown = other.keyRotateClockwiseDown;
		this.keyRotateCounterClockwiseDown = other.keyRotateCounterClockwiseDown;
		this.keyRotate90ClockwiseDown = other.keyRotate90ClockwiseDown;
		this.keyRotate90CounterClockwiseDown = other.keyRotate90CounterClockwiseDown;
		this.keyPauseDown = other.keyPauseDown;
		this.keyReturnToMenuDown = other.keyReturnToMenuDown;
		this.keyCheatActivateAllDown = other.keyCheatActivateAllDown;
		this.keyCheatGotoNextLevelDown = other.keyCheatGotoNextLevelDown;
		this.keyDownDown = other.keyDownDown;
		this.keyReturnDown = other.keyReturnDown;
		this.keyUpDown = other.keyUpDown;
		this.keyCheatGotoNextBonusLevelDown = other.keyCheatGotoNextBonusLevelDown;
		this.mouseButtonDown = other.mouseButtonDown;
		if (null != other.mousePos) {
			this.mousePos = new Vector2f(other.mousePos);
		}
	}
}