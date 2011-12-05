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

import im.bci.newtonadv.Game;
import java.io.File;

/**
 *
 * @author bob
 */
public strictfp class MainMenuSequence extends MenuSequence {

    private Sequence playSequence, resumeSequence, helpSequence, quitSequence;
    private final Button playButton;
    private final Button resumeButton;

    public MainMenuSequence(Game game, Sequence playSeq, Sequence quitSeq) {
        super(game);
        this.playSequence = playSeq;
        this.quitSequence = quitSeq;
        this.helpSequence = new StoryboardSequence(game, "data" + File.separator + "help.jpg", null, this);

        setBackgroundImage("data/main_menu/home.png");

        playButton = new Button() {

            @Override
            void activate() throws TransitionException {
                throw new Sequence.TransitionException(playSequence);
            }
        };
        playButton.offTexture = "data/main_menu/bt-play-off.png";
        playButton.currentTexture = playButton.onTexture = "data/main_menu/bt-play-on.png";
        playButton.y = 318;
        addButton(playButton);

        resumeButton = new Button() {

            @Override
            void activate() throws TransitionException {
                if (resumeSequence != null) {
                    throw new Sequence.TransitionException(resumeSequence);
                }
            }
        };
        resumeButton.currentTexture = resumeButton.offTexture = "data/main_menu/bt-resume-off.png";
        resumeButton.onTexture = "data/main_menu/bt-resume-on.png";
        resumeButton.y = 441;
        addButton(resumeButton);

        Button helpButton = new Button() {

            @Override
            void activate() throws TransitionException {
                if (helpSequence != null) {
                    throw new Sequence.TransitionException(helpSequence);
                }
            }
        };
        helpButton.currentTexture = helpButton.offTexture = "data/main_menu/bt-help-off.png";
        helpButton.onTexture = "data/main_menu/bt-help-on.png";
        helpButton.y = 558;
        addButton(helpButton);

        Button quitButton = new Button() {

            @Override
            void activate() throws TransitionException {
                throw new Sequence.TransitionException(quitSequence);
            }
        };
        quitButton.currentTexture = quitButton.offTexture = "data/main_menu/bt-quit-off.png";
        quitButton.onTexture = "data/main_menu/bt-quit-on.png";
        quitButton.y = 675;
        addButton(quitButton);
    }

    @Override
    public void start() {
        super.start();
        if (resumeSequence == null) {
            setCurrentButton(playButton);
        } else {
            setCurrentButton(resumeButton);
        }
    }

    @Override
    public void stop() {
        super.stop();
        resumeSequence = null;
    }

    public void setResumeSequence(Sequence s) {
        this.resumeSequence = s;
    }

    public boolean isResumeSequence(Sequence s) {
        return s == resumeSequence && s != null;
    }
}
