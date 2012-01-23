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

	public AndroidGLSurfaceView(Context context, AndroidGameInput input) {
		super(context);

		this.input = input;

		// Set the Renderer for drawing on the GLSurfaceView
		renderer = new AndroidGameRenderer();
		setRenderer(renderer);

		this.setFocusable(true);
		this.requestFocus();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			input.keyRightDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			input.keyLeftDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			input.keyUpDown = true;
			input.keyRotateClockwiseDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			input.keyDownDown = true;
			input.keyRotateCounterClockwiseDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			input.keyReturnDown = true;
			input.keyJumpDown = true;
			break;
		case KeyEvent.KEYCODE_MENU:
			input.keyReturnToMenuDown = true;
			break;
		case KeyEvent.KEYCODE_N:
			input.keyCheatGotoNextLevelDown = true;
			break;
		case KeyEvent.KEYCODE_A:
			input.keyCheatActivateAllDown = true;
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			input.keyRightDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			input.keyLeftDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			input.keyUpDown = true;
			input.keyRotateClockwiseDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			input.keyDownDown = true;
			input.keyRotateCounterClockwiseDown = true;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			input.keyReturnDown = true;
			input.keyJumpDown = true;
			break;
		case KeyEvent.KEYCODE_MENU:
			input.keyReturnToMenuDown = true;
			break;
		case KeyEvent.KEYCODE_N:
			input.keyCheatGotoNextLevelDown = true;
			break;
		case KeyEvent.KEYCODE_A:
			input.keyCheatActivateAllDown = true;
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void setGame(Game game) {
		renderer.setGame(game);
	}
}
