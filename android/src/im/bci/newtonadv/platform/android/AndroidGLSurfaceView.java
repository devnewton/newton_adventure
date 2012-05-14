/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.android;

import im.bci.newtonadv.Game;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;

public class AndroidGLSurfaceView extends GLSurfaceView {

	private final AndroidGameRenderer renderer;
	private final AndroidGameInput input;
	private AndroidGameInputData data = new AndroidGameInputData();

	public AndroidGLSurfaceView(Context context, AndroidGameInput input) {
		super(context);
		setEGLConfigChooser(false);
		setDebugFlags(DEBUG_CHECK_GL_ERROR);

		this.input = input;

		// Set the Renderer for drawing on the GLSurfaceView
		renderer = new AndroidGameRenderer();
		setRenderer(renderer);

		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.requestFocus();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		data = new AndroidGameInputData(data);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			data.keyRightDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			data.keyLeftDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			data.keyUpDown = true;
			data.keyRotateClockwiseDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			data.keyDownDown = true;
			data.keyRotateCounterClockwiseDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			data.keyReturnDown = true;
			data.keyJumpDown = true;
			break;
		case KeyEvent.KEYCODE_MENU:
			data.keyReturnToMenuDown = true;
			break;
		case KeyEvent.KEYCODE_N:
			data.keyCheatGotoNextLevelDown = true;
			break;
		case KeyEvent.KEYCODE_A:
			data.keyCheatActivateAllDown = true;
			break;
		case KeyEvent.KEYCODE_B:
			data.keyCheatGotoNextBonusLevelDown = true;
			break;
		}
		input.dataBuffer.add(data);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		data = new AndroidGameInputData(data);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			data.keyRightDown = false;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			data.keyLeftDown = false;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			data.keyUpDown = false;
			data.keyRotateClockwiseDown = false;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			data.keyDownDown = false;
			data.keyRotateCounterClockwiseDown = false;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			data.keyReturnDown = false;
			data.keyJumpDown = false;
			break;
		case KeyEvent.KEYCODE_MENU:
			data.keyReturnToMenuDown = false;
			break;
		case KeyEvent.KEYCODE_N:
			data.keyCheatGotoNextLevelDown = false;
			break;
		case KeyEvent.KEYCODE_A:
			data.keyCheatActivateAllDown = false;
			break;
		case KeyEvent.KEYCODE_B:
			data.keyCheatGotoNextBonusLevelDown = false;
			break;
		}
		input.dataBuffer.add(data);
		return super.onKeyUp(keyCode, event);
	}

	public void setGame(Game game) {
		renderer.setGame(game);
	}
}
