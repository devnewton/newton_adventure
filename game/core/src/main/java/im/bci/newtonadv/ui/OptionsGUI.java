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
package im.bci.newtonadv.ui;

import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.background.ColoredBackground;
import im.bci.jnuit.widgets.AudioConfigurator;
import im.bci.jnuit.widgets.Button;
import im.bci.jnuit.widgets.ControlsConfigurator;
import im.bci.jnuit.widgets.LanguageConfigurator;
import im.bci.jnuit.widgets.Stack;
import im.bci.jnuit.widgets.Table;
import im.bci.jnuit.widgets.VideoConfigurator;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import im.bci.newtonadv.platform.interfaces.IMod;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import java.util.List;

public class OptionsGUI extends Stack {

    private final Table optionsMenu;
    private final VideoConfigurator videoConfigurator;
    private final AudioConfigurator audioConfigurator;
    private final ControlsConfigurator gameControlsConfigurator, menuControlsConfigurator;
    //private final LanguageConfigurator languageConfigurator;

    private ModChooser modChooser;

    public OptionsGUI(final NuitToolkit toolkit, final IPlatformSpecific platform) {
        this.setBackground(new ColoredBackground(0, 0, 0, 1));
        videoConfigurator = new VideoConfigurator(toolkit);
        audioConfigurator = new AudioConfigurator(toolkit);
        AbstractGameInput gameInput = platform.getGameInput();
        gameControlsConfigurator = new ControlsConfigurator(toolkit, gameInput.getGameActionList(), gameInput.getDefaultGameActionList());
        menuControlsConfigurator = new ControlsConfigurator(toolkit, toolkit.getMenuActionList(), toolkit.getDefaultMenuActionList());
       // languageConfigurator = new LanguageConfigurator(toolkit);
        final List<IMod> mods = platform.listMods();
        optionsMenu = new Table(toolkit);
        optionsMenu.cell(new Button(toolkit, "options.video") {

            @Override
            public void onOK() {
                OptionsGUI.this.show(videoConfigurator);
            }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "options.audio") {

            @Override
            public void onOK() {
                OptionsGUI.this.show(audioConfigurator);
            }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "options.menu.controls") {

            @Override
            public void onOK() {
                OptionsGUI.this.show(menuControlsConfigurator);
            }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "options.game.controls") {

            @Override
            public void onOK() {
                OptionsGUI.this.show(gameControlsConfigurator);
            }
        });
        optionsMenu.row();
        //TODO uncomment when all gui code will use jnuit
        /*optionsMenu.cell(new Button(toolkit, "options.language") {

            @Override
            public void onOK() {
                OptionsGUI.this.show(languageConfigurator);
            }
        });
        optionsMenu.row();*/
        if (!mods.isEmpty()) {
            modChooser = new ModChooser(toolkit, mods);
            optionsMenu.cell(new Button(toolkit, "options.mods") {

                @Override
                public void onOK() {
                    OptionsGUI.this.show(modChooser);
                }
            });
            optionsMenu.row();
        }
        optionsMenu.cell(new Button(toolkit, "options.tweaks") {

            @Override
            public void onOK() {
                OptionsGUI.this.show(new TweaksGUI(toolkit, platform.getConfig()));
            }
        });
        optionsMenu.row();
        optionsMenu.cell(new Button(toolkit, "options.back") {

            @Override
            public void onOK() {
                OptionsGUI.this.close();
            }
        });
        show(optionsMenu);

    }

    public String getSelectedModName() {
        if (null != modChooser) {
            final IMod selected = modChooser.mods.getSelected();
            if (null != selected) {
                return selected.getName();
            }
        }
        return null;
    }

}
