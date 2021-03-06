/*
 *  Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  *  Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  *  Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  *  Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package im.bci.newtonadv.game;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.anim.Play;
import im.bci.newtonadv.platform.interfaces.ITexture;
import java.io.IOException;

/**
 *
 * @author devnewton
 */
public class FadeSequence implements Sequence {

    private final float r, g, b;
    private float a;
    private final Game game;
    private final long duration;
    private long endTime = -1;
    protected AbstractTransitionException transition;
    private Play loadingPlay;
    private ITexture backgroundTexture;

    public FadeSequence(Game game, AbstractTransitionException transition, float r, float g, float b, long duration) {
        this.game = game;
        this.r = r;
        this.g = g;
        this.b = b;
        this.duration = duration;
        this.transition = transition;
    }

    @Override
    public void start() {
        try {
            backgroundTexture = game.getView().getTextureCache().grabScreenToTexture();
            loadingPlay = game.getView().loadFromAnimation(game.getData().getFile("loading.json")).getFirst().start();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void draw() {
        game.getView().drawFadeSequence(backgroundTexture, loadingPlay, r, g, b, a);
    }

    @Override
    public void stop() {
        backgroundTexture = null;
        loadingPlay = null;
    }

    @Override
    public void update() throws Sequence.NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        final FrameTimeInfos frameTimeInfos = game.getFrameTimeInfos();
        if(endTime < 0) {
            endTime = game.getFrameTimeInfos().currentTime + duration;
        }
        loadingPlay.update(frameTimeInfos.elapsedTime / 1000000);
        long remaining = endTime - frameTimeInfos.currentTime;
        if (remaining <= 0) {
            fadeDone();
        }
        a = 1.0f - (float) remaining / (float) duration;
    }

    @Override
    public void processInputs() {
    }

    @Override
    public void resume() {
    }

    protected void fadeDone() throws NormalTransitionException, ResumableTransitionException, ResumeTransitionException {
        transition.throwMe();
    }
}
