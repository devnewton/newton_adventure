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
import im.bci.newtonadv.game.special.occasion.SpecialOccasionFactory;
import im.bci.newtonadv.game.special.occasion.SpecialOccasionLayer;

/**
 *
 * @author devnewton
 */
public strictfp class MainMenuSequence extends MenuSequence {

    private Sequence playSequence, resumeSequence, helpSequence, quitSequence;
    private final Button playButton;
    private final Button resumeButton;
    private final SpecialOccasionLayer specialOccasionLayer;
    private Sequence optionsSequence;

    @Override
    public void draw() {
        game.getView().drawMainMenuSequence(this);
        specialOccasionLayer.draw();
    }

    @Override
    public void update() {
        super.update();
        specialOccasionLayer.update();
    }

    public MainMenuSequence(final Game game, Sequence playSeq, Sequence quitSeq,
            Sequence optSequence) {
        super(game);

        specialOccasionLayer = SpecialOccasionFactory
                .createSpecialOccasionLayer(game.getView());

        this.playSequence = playSeq;
        this.quitSequence = quitSeq;
        this.optionsSequence = optSequence;
        this.helpSequence = new HelpSequence(game, game.getData()
                .getFile("help.png"), null, new NormalTransitionException(this));

        setBackgroundTexturePath(game.getData().getFile("main_menu/home.png"));

        playButton = new Button() {
            @Override
            void activate() throws Sequence.NormalTransitionException {
                setResumeSequence(null);
                throw new Sequence.NormalTransitionException(playSequence);
            }
        };
        playButton.offTextureName = game.getData().getFile(
                "main_menu/bt-play-off.png");
        playButton.onTextureName = game.getData()
                .getFile("main_menu/bt-play-on.png");
        playButton.setOn();
        playButton.x = 480;
        playButton.y = 267;
        playButton.w = 312;
        playButton.h = 90;
        addButton(playButton);

        resumeButton = new Button() {
            @Override
            void activate() throws ResumeTransitionException {
                if (resumeSequence != null) {
                    ResumeTransitionException ex = new ResumeTransitionException(
                            resumeSequence);
                    setResumeSequence(null);
                    throw ex;
                }
            }
        };
        resumeButton.offTextureName = game.getData()
                .getFile("main_menu/bt-resume-off.png");
        resumeButton.onTextureName = game.getData().getFile(
                "main_menu/bt-resume-impossible.png");
        resumeButton.x = 480;
        resumeButton.y = 371;
        resumeButton.w = 312;
        resumeButton.h = 90;
        addButton(resumeButton);

        Button optionsButton = new Button() {
            @Override
            void activate() throws Sequence.NormalTransitionException {
                if (null != optionsSequence) {
                    throw new Sequence.NormalTransitionException(
                            optionsSequence);
                }
            }
        };
        optionsButton.offTextureName = game
                .getData().getFile("main_menu/bt-options-off.png");
        optionsButton.onTextureName = game.getData().getFile(
                "main_menu/bt-options-on.png");
        optionsButton.x = 480;
        optionsButton.y = 475;
        optionsButton.w = 312;
        optionsButton.h = 90;
        addButton(optionsButton);

        Button helpButton = new Button() {
            @Override
            void activate() throws NormalTransitionException {
                if (helpSequence != null) {
                    throw new Sequence.NormalTransitionException(helpSequence);
                }
            }
        };
        helpButton.offTextureName = game.getData()
                .getFile("main_menu/bt-help-off.png");
        helpButton.onTextureName = game.getData().getFile(
                "main_menu/bt-help-on.png");
        helpButton.x = 480;
        helpButton.y = 579;
        helpButton.w = 312;
        helpButton.h = 90;
        addButton(helpButton);

        Button quitButton = new Button() {
            @Override
            void activate() throws Sequence.NormalTransitionException {
                throw new Sequence.NormalTransitionException(quitSequence);
            }
        };
        quitButton.offTextureName = game.getData()
                .getFile("main_menu/bt-quit-off.png");
        quitButton.onTextureName = game.getData().getFile(
                "main_menu/bt-quit-on.png");
        quitButton.x = 480;
        quitButton.y = 683;
        quitButton.w = 312;
        quitButton.h = 90;
        addButton(quitButton);

        Button insertCoinsButton = new Button() {
            @Override
            void activate() throws Sequence.NormalTransitionException {
                game.insertCoins();
            }
        };
        insertCoinsButton.offTextureName = game.getData()
                .getFile("main_menu/bt-insert-coins-off.png");
        insertCoinsButton.onTextureName = game.getData().getFile(
                "main_menu/bt-insert-coins-on.png");
        insertCoinsButton.x = 920;
        insertCoinsButton.y = 683;
        insertCoinsButton.w = 312;
        insertCoinsButton.h = 90;
        addButton(insertCoinsButton);


    }

    @Override
    public void start() {
        super.start();
        if (resumeSequence instanceof LevelSequence) {
            setCurrentButton(resumeButton);
        } else {
            setCurrentButton(playButton);
            game.getSoundCache().playMusicIfEnabled(
                    game.getData().getFile("lovelace_0.ogg"));
        }
    }

    public void setResumeSequence(Sequence s) {
        this.resumeSequence = s;
        if (null != resumeSequence) {
            resumeButton.onTextureName = game.getData().getFile("main_menu/bt-resume-on.png");
        } else {
            resumeButton.onTextureName = game.getData().getFile("main_menu/bt-resume-impossible.png");
        }


    }

    @Override
    public void resume() {
    }
}
