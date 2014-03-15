/*
 * Copyright (c) 2014 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.ui;

import im.bci.jnuit.NuitLocale;
import im.bci.jnuit.NuitTranslator;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class NewtonAdventureNuitTranslator extends NuitTranslator {

    public NewtonAdventureNuitTranslator() {
        addEnglish();
        addFrench();
    }

    private void addEnglish() {
        this.addTranslation(NuitLocale.ENGLISH, "options.video", "VIDEO");
        this.addTranslation(NuitLocale.ENGLISH, "options.audio", "AUDIO");
        this.addTranslation(NuitLocale.ENGLISH, "options.game.controls", "GAME CONTROLS");
        this.addTranslation(NuitLocale.ENGLISH, "options.menu.controls", "MENU CONTROLS");
        this.addTranslation(NuitLocale.ENGLISH, "options.language", "LANGUAGE");
        this.addTranslation(NuitLocale.ENGLISH, "options.mods", "MODS");
        this.addTranslation(NuitLocale.ENGLISH, "options.back", "BACK");

        this.addTranslation(NuitLocale.ENGLISH, "modchooser.back", "BACK");

        this.addTranslation(NuitLocale.ENGLISH, "action.jump", "Jump");
        this.addTranslation(NuitLocale.ENGLISH, "action.left", "Left");
        this.addTranslation(NuitLocale.ENGLISH, "action.right", "Right");
        this.addTranslation(NuitLocale.ENGLISH, "action.rotate.clockwise", "Rotate clockwise");
        this.addTranslation(NuitLocale.ENGLISH, "action.rotate.counterclockwise", "Rotate counterclockwise");
        this.addTranslation(NuitLocale.ENGLISH, "action.rotate.clockwise.90", "Rotate clockwise 90");
        this.addTranslation(NuitLocale.ENGLISH, "action.rotate.counterclockwise.90", "Rotate counterclockwise 90");
        this.addTranslation(NuitLocale.ENGLISH, "action.returntomenu", "Menu");
    }

    private void addFrench() {
        this.addTranslation(NuitLocale.FRENCH, "options.video", "VIDEO");
        this.addTranslation(NuitLocale.FRENCH, "options.audio", "AUDIO");
        this.addTranslation(NuitLocale.FRENCH, "options.game.controls", "CONTROLES DU JEU");
        this.addTranslation(NuitLocale.FRENCH, "options.menu.controls", "CONTROLES DES MENUS");
        this.addTranslation(NuitLocale.FRENCH, "options.language", "LANGUE");
        this.addTranslation(NuitLocale.FRENCH, "options.mods", "MODS");
        this.addTranslation(NuitLocale.FRENCH, "options.back", "RETOUR");

        this.addTranslation(NuitLocale.FRENCH, "modchooser.back", "RETOUR");

        this.addTranslation(NuitLocale.FRENCH, "action.jump", "Sauter");
        this.addTranslation(NuitLocale.FRENCH, "action.left", "Gauche");
        this.addTranslation(NuitLocale.FRENCH, "action.right", "Droite");
        this.addTranslation(NuitLocale.FRENCH, "action.rotate.clockwise", "Tourne (sens horaire)");
        this.addTranslation(NuitLocale.FRENCH, "action.rotate.counterclockwise", "Tourne (sens antihoraire)");
        this.addTranslation(NuitLocale.FRENCH, "action.rotate.clockwise.90", "Tourne (sens horaire 90)");
        this.addTranslation(NuitLocale.FRENCH, "action.rotate.counterclockwise.90", "Tourne (sens antihoraire 90)");
        this.addTranslation(NuitLocale.FRENCH, "action.returntomenu", "Menu");
    }

}
