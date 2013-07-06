/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.platform.lwjgl.twl;

import im.bci.newtonadv.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Controller;

public class JoypadPreset {

	private static final List<JoypadPreset> presets = new ArrayList<JoypadPreset>();

	public JoypadPreset(Controller joypad) {
		setName(joypad.getName());
		if (joypad.getAxisCount() >= 1) {
			setxAxis(0);
		}
		if (joypad.getAxisCount() >= 2) {
			setyAxis(1);
		}
		if (joypad.getButtonCount() >= 1) {
			setKeyJump(0);
			setKeyReturn(0);
		}
		if (joypad.getButtonCount() >= 3) {
			setKeyRotateCounterClockWise(1);
			setKeyRotateClockwise(2);
		}
		if (joypad.getButtonCount() >= 3) {
			setKeyRotateCounterClockWise(1);
			setKeyRotateClockwise(2);
		}
		if (joypad.getButtonCount() >= 4) {
			setKeyReturnToMenu(3);
		}
	}

	public JoypadPreset() {
	}

	public static JoypadPreset find(Controller joypad) {
		for (JoypadPreset preset : presets) {
			if (preset.isApplicableTo(joypad)) {
				return preset;
			}
		}
		return null;
	}

	private boolean isApplicableTo(Controller joypad) {
		return this.name.equals(joypad.getName()) && joypad.getAxisCount() >= getAxisCount() && joypad.getButtonCount() >= getButtonCount();
	}

	private int getButtonCount() {
		return 1 + MathUtils.max(keyJump, keyLeft, keyRight, keyRotateClockwise, keyRotateCounterClockWise, keyRotate90Clockwise, keyRotate90CounterClockWise, keyReturn, keyReturnToMenu);
	}

	private int getAxisCount() {
		return 1 + MathUtils.max(xAxis, yAxis);
	}

	static {
		JoypadPreset megaWorld = new JoypadPreset();
		megaWorld.setName("Mega World USB Game Controllers");
		megaWorld.setxAxis(4);
		megaWorld.setyAxis(5);
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
}
