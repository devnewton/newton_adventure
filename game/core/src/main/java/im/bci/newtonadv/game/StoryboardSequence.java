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

import im.bci.jnuit.controls.ActionActivatedDetector;
import im.bci.newtonadv.Game;

public class StoryboardSequence extends MenuSequence {

    private final String music;
    private boolean musicLoop = true;
    protected Button continueButton;
    private AbstractTransitionException transition;
    private ActionActivatedDetector skipActionActivatedDetector;

    public StoryboardSequence(Game game, String texture, String music, final AbstractTransitionException transition, boolean withContinueButton) {
        super(game);
        this.setBackgroundTexturePath(texture);
        this.transition = transition;
        this.music = music;
        if (withContinueButton) {
            continueButton = new Button() {

                @Override
                void activate() throws NormalTransitionException,
                        ResumeTransitionException, ResumableTransitionException {
                    transition.throwMe();
                }
            };
            continueButton.offTextureName = game.getButtonFile("bt-continue-off");
            continueButton.onTextureName = game.getButtonFile("bt-continue-on");
            continueButton.x = 960;
            continueButton.y = 700;
            continueButton.w = 312;
            continueButton.h = 90;
            addButton(continueButton);
        } else {
            skipActionActivatedDetector = new ActionActivatedDetector(game.getNuitToolkit().getPressAnyKeyAction());
        }
    }

    public StoryboardSequence(Game game, String texture, String music, final AbstractTransitionException transition) {
        this(game, texture, music, transition, true);
    }

    @Override
    public void start() {
        super.start();
        if (null != music) {
            game.getNuitToolkit().getAudio().playMusic(music, musicLoop);
        }
        if (null != skipActionActivatedDetector) {
            skipActionActivatedDetector.reset();
        }
    }

    @Override
    public void update() throws Sequence.NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        super.update();
        if (null != backgroundTexture && backgroundTexture.isStopped()) {
            transition.throwMe();
        }
    }

    @Override
    public void processInputs() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        super.processInputs();
        if (null != skipActionActivatedDetector) {
            skipActionActivatedDetector.poll();
            if (skipActionActivatedDetector.isActivated()) {
                transition.throwMe();
            }
        }
    }

    public void setMusicLoop(boolean musicLoop) {
        this.musicLoop = musicLoop;
    }
}
