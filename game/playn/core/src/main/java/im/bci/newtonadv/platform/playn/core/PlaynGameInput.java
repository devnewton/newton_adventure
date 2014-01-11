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
package im.bci.newtonadv.platform.playn.core;

import im.bci.newtonadv.platform.interfaces.IGameInput;
import java.util.EnumSet;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.PlayN;
import playn.core.Pointer;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynGameInput implements IGameInput {
    
    private EnumSet<Key> keysDown = EnumSet.noneOf(Key.class);
    private ROVector2f mousePos;
    private boolean mouseButtonDown;
    boolean virtualPadUpDown;
    boolean virtualPadRotateCounterClockwiseDown;
    boolean virtualPadRotateClockwiseDown;
    boolean virtualPadLeftDown;
    boolean virtualPadRightDown;
    boolean backButtonDown;
    
    public PlaynGameInput() {
        PlayN.keyboard().setListener(new Keyboard.Listener() {
            
            @Override
            public void onKeyDown(Keyboard.Event event) {
                keysDown.add(event.key());
            }
            
            @Override
            public void onKeyTyped(Keyboard.TypedEvent event) {
            }
            
            @Override
            public void onKeyUp(Keyboard.Event event) {
                keysDown.remove(event.key());
            }
        });
        
        PlayN.pointer().setListener(new Pointer.Listener() {
            
            @Override
            public void onPointerStart(Pointer.Event event) {
                mousePos = eventToMousePos(event);
                System.out.println("Mouse click at " + event.x() + "," + event.y());
                mouseButtonDown = true;
            }
            
            @Override
            public void onPointerDrag(Pointer.Event event) {
                mousePos = eventToMousePos(event);
                mouseButtonDown = true;
            }
            
            @Override
            public void onPointerCancel(Pointer.Event event) {
                mousePos = eventToMousePos(event);
                mouseButtonDown = false;
            }
            
            @Override
            public void onPointerEnd(Pointer.Event event) {
                mousePos = eventToMousePos(event);
                mouseButtonDown = false;
            }
            
        });
    }
    
    private Vector2f eventToMousePos(Pointer.Event event) {
        return new Vector2f(event.x(), PlayN.graphics().height() - event.y());
    }
    
    @Override
    public boolean isKeyCheatActivateAllDown() {
        return keysDown.contains(Key.F12);
    }
    
    @Override
    public boolean isKeyCheatGotoNextLevelDown() {
        return keysDown.contains(Key.F12);
    }
    
    @Override
    public boolean isKeyDownDown() {
        return keysDown.contains(Key.DOWN);
    }
    
    @Override
    public boolean isKeyJumpDown() {
        return keysDown.contains(Key.UP) || virtualPadUpDown;
    }
    
    @Override
    public boolean isKeyLeftDown() {
        return keysDown.contains(Key.LEFT) || virtualPadLeftDown;
    }
    
    @Override
    public boolean isKeyReturnDown() {
        return keysDown.contains(Key.ENTER);
    }
    
    @Override
    public boolean isKeyReturnToMenuDown() {
        return keysDown.contains(Key.ESCAPE) || backButtonDown;
    }
    
    @Override
    public boolean isKeyRightDown() {
        return keysDown.contains(Key.RIGHT) || virtualPadRightDown;
    }
    
    @Override
    public boolean isKeyRotate90ClockwiseDown() {
        return keysDown.contains(Key.S);
    }
    
    @Override
    public boolean isKeyRotate90CounterClockwiseDown() {
        return keysDown.contains(Key.D);
    }
    
    @Override
    public boolean isKeyRotateClockwiseDown() {
        return keysDown.contains(Key.C) || virtualPadRotateClockwiseDown;
    }
    
    @Override
    public boolean isKeyRotateCounterClockwiseDown() {
        return keysDown.contains(Key.X) || virtualPadRotateCounterClockwiseDown;
    }
    
    @Override
    public boolean isKeyToggleFullscreenDown() {
        return false;
    }
    
    @Override
    public boolean isKeyUpDown() {
        return keysDown.contains(Key.UP);
    }
    
    @Override
    public boolean isKeyCheatGotoNextBonusLevelDown() {
        return keysDown.contains(Key.F10);
    }
    
    @Override
    public ROVector2f getMousePos() {
        return mousePos;
    }
    
    @Override
    public boolean isMouseButtonDown() {
        return mouseButtonDown;
    }
    
    @Override
    public boolean isKeyCheatGetWorldMapDown() {
        return keysDown.contains(Key.F9);
    }
    
    @Override
    public boolean isKeyCheatGetCompassDown() {
        return keysDown.contains(Key.F8);
    }
    
    private static final int NB_POLL_PER_TICK = 1;
    private int pollCount;
    
    @Override
    public void beginPoll() {
        pollCount = NB_POLL_PER_TICK;
    }
    
    @Override
    public boolean poll() {
        return pollCount-- > 0;
    }
    
    @Override
    public boolean isKeyCheatSetAllCompletedDown() {
        return keysDown.contains(Key.F12);
    }
    
    void onBackPressed() {
        backButtonDown = true;
        PlayN.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                backButtonDown = false;
            }
        });
    }
    
}
