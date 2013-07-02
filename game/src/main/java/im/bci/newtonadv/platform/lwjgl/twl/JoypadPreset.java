package im.bci.newtonadv.platform.lwjgl.twl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Controller;

public class JoypadPreset {

	private static final List<JoypadPreset> presets = new ArrayList<JoypadPreset>();
	
	public JoypadPreset(Controller joypad) {
		setName("Default Newton Adventure Gamepad Configuration");
		if(joypad.getAxisCount()>=1) {
			setxAxis(0);
		}
		if(joypad.getAxisCount()>=2) {
			setyAxis(1);
		}
		if(joypad.getButtonCount()>=1) {
			setKeyJump(0);
			setKeyReturn(0);
		}
		if(joypad.getButtonCount()>=3) {
			setKeyRotateCounterClockWise(1);
			setKeyRotateClockwise(2);
		}
		if(joypad.getButtonCount()>=3) {
			setKeyRotateCounterClockWise(1);
			setKeyRotateClockwise(2);
		}
		if(joypad.getButtonCount()>=4) {
			setKeyReturnToMenu(3);
		}
	}

	public JoypadPreset() {
	}

	public static JoypadPreset findByName(String name) {
		for(JoypadPreset preset: presets) {
			if(preset.getName().equals(name)) {
				return preset;
			}
		}
		return null;
	}

	static {	
		JoypadPreset megaWorld = new JoypadPreset();
		megaWorld.setName("Mega World USB Game Controllers");
		megaWorld.setxAxis(0);
		megaWorld.setyAxis(1);
		megaWorld.setKeyJump(0);
		megaWorld.setKeyRotateCounterClockWise(6);
		megaWorld.setKeyRotateClockwise(4);
		megaWorld.setKeyReturn(0);
		megaWorld.setKeyReturnToMenu(9);
		presets.add(megaWorld);		
	}
	private String name;
	private int xAxis = -1;
	private int yAxis = -1;
	private int keyJump = -1;
	private int keyLeft = -1;
	private int keyRight = -1;
	private int keyRotateClockwise = -1;
	private int keyRotateCounterClockWise = -1;
	private int keyRotate90Clockwise = -1;
	private int keyRotate90CounterClockWise = -1;
	private int keyReturn = -1;
	private int keyReturnToMenu = -1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getxAxis() {
		return xAxis;
	}

	public void setxAxis(int xAxis) {
		this.xAxis = xAxis;
	}

	public int getyAxis() {
		return yAxis;
	}

	public void setyAxis(int yAxis) {
		this.yAxis = yAxis;
	}

	public int getKeyJump() {
		return keyJump;
	}

	public void setKeyJump(int keyJump) {
		this.keyJump = keyJump;
	}

	public int getKeyLeft() {
		return keyLeft;
	}

	public void setKeyLeft(int keyLeft) {
		this.keyLeft = keyLeft;
	}

	public int getKeyRight() {
		return keyRight;
	}

	public void setKeyRight(int keyRight) {
		this.keyRight = keyRight;
	}

	public int getKeyRotateClockwise() {
		return keyRotateClockwise;
	}

	public void setKeyRotateClockwise(int keyRotateClockwise) {
		this.keyRotateClockwise = keyRotateClockwise;
	}

	public int getKeyRotateCounterClockWise() {
		return keyRotateCounterClockWise;
	}

	public void setKeyRotateCounterClockWise(int keyRotateCounterClockWise) {
		this.keyRotateCounterClockWise = keyRotateCounterClockWise;
	}

	public int getKeyRotate90Clockwise() {
		return keyRotate90Clockwise;
	}

	public void setKeyRotate90Clockwise(int keyRotate90Clockwise) {
		this.keyRotate90Clockwise = keyRotate90Clockwise;
	}

	public int getKeyRotate90CounterClockWise() {
		return keyRotate90CounterClockWise;
	}

	public void setKeyRotate90CounterClockWise(int keyRotate90CounterClockWise) {
		this.keyRotate90CounterClockWise = keyRotate90CounterClockWise;
	}

	public int getKeyReturn() {
		return keyReturn;
	}

	public void setKeyReturn(int keyReturn) {
		this.keyReturn = keyReturn;
	}

	public int getKeyReturnToMenu() {
		return keyReturnToMenu;
	}

	public void setKeyReturnToMenu(int keyReturnToMenu) {
		this.keyReturnToMenu = keyReturnToMenu;
	}

	@Override
	public String toString() {
		return "JoypadPreset [name=" + name + ", xAxis=" + xAxis + ", yAxis=" + yAxis + ", keyJump=" + keyJump + ", keyLeft=" + keyLeft + ", keyRight=" + keyRight + ", keyRotateClockwise=" + keyRotateClockwise + ", keyRotateCounterClock=" + keyRotateCounterClockWise + ", keyRotate90Clockwise=" + keyRotate90Clockwise + ", keyRotate90CounterClock=" + keyRotate90CounterClockWise + ", keyReturn=" + keyReturn + ", keyReturnToMenu=" + keyReturnToMenu + "]";
	}
}
