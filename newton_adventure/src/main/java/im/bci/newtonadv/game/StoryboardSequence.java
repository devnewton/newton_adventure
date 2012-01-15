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
package im.bci.newtonadv.game;

import org.lwjgl.input.Keyboard;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.platform.lwjgl.TrueTypeFont;
import java.awt.Font;

public class StoryboardSequence implements Sequence {

    Sequence nextSequence;
    String texture;
    static public final float ortho2DBottom = Game.DEFAULT_SCREEN_HEIGHT;
    static public final float ortho2DLeft = 0;
    static public final float ortho2DRight = Game.DEFAULT_SCREEN_WIDTH;
    static public final float ortho2DTop = 0;
    protected Game game;
    private final String music;
    private boolean redraw = true;
    protected TrueTypeFont font;

    public StoryboardSequence(Game game, String texture, String music, Sequence nextSequence) {
        this.game = game;
        this.texture = texture;
        this.nextSequence = nextSequence;
        this.music = music;
    }

    @Override
    public void draw() {
        game.getView().drawStoryBoardSequence(this,font);
    }

    @Override
    public void update() throws TransitionException {
        //NOTHING
    }
    private boolean mustQuit;

    @Override
    public void processInputs() throws TransitionException {
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
            mustQuit = true;
        } else if (mustQuit) {
            throw new Sequence.TransitionException(nextSequence);
        }
    }

    public void start() {
        if (music != null) {
            game.getSoundCache().playMusicIfEnabled(music);
        }
        redraw = true;
        mustQuit = false;
        font = new TrueTypeFont(new Font("monospaced", Font.BOLD, 32), false);
    }

    public void stop() {
        font.destroy();
    }

    void setNextSequence(Sequence nextSequence) {
        this.nextSequence = nextSequence;
    }

    public boolean isDirty() {
        return redraw;
    }

    public void setDirty(boolean b) {
        redraw = b;
    }

    public String getTexture() {
        return texture;
    }
}
