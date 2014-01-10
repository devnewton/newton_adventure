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
package im.bci.newtonadv.platform.playn.core;

import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.TextFormat;
import playn.core.TextLayout;

/**
 *
 * @author devnewton
 */
public class PlaynOptionsSequence implements IOptionsSequence {

    private Sequence nextSequence;
    private GroupLayer optionsLayer;
    private final TextFormat textFormat;
    private PlaynToggleButton useVirtualPad;
    private boolean mustQuit;

    PlaynOptionsSequence(PlaynPlatformSpecific platform) {
        textFormat = new TextFormat(PlayN.graphics().createFont("monospaced", Font.Style.BOLD, 24), true);
    }

    @Override
    public void setNextSequence(Sequence s) {
        this.nextSequence = s;
    }

    @Override
    public void start() {
        mustQuit = false;
        optionsLayer = PlayN.graphics().createGroupLayer();
        PlayN.graphics().rootLayer().add(optionsLayer);
        useVirtualPad = new PlaynToggleButton("Virtual pad: ", 0f, 0f);
        new PlaynButton("Apply", 0, PlayN.graphics().height() - textFormat.font.size()) {

            @Override
            public void onActivate() {
                mustQuit = true;
            }
        };
    }

    public class PlaynBaseButton {

        protected ImageLayer layer;

        float getHeight() {
            return layer.height();
        }
    }

    public abstract class PlaynButton extends PlaynBaseButton {

        PlaynButton(String label, float x, float y) {
            layer = PlayN.graphics().createImageLayer();
            optionsLayer.addAt(layer, x, y);
            layer.addListener(new Pointer.Adapter() {

                @Override
                public void onPointerEnd(Pointer.Event event) {
                    onActivate();
                }
            });
        }

        public abstract void onActivate();

    }

    public class PlaynToggleButton extends PlaynBaseButton {

        private boolean checked;
        private String label;

        PlaynToggleButton(String label, float x, float y) {
            layer = PlayN.graphics().createImageLayer();
            optionsLayer.addAt(layer, x, y);
            layer.addListener(new Pointer.Adapter() {

                @Override
                public void onPointerEnd(Pointer.Event event) {
                    toggle();
                }

            });
        }

        void setChecked(boolean c) {
            layer.setImage(createButtonLabel(label + ": " + (c ? "yes" : "no")));
            checked = c;
        }

        private void toggle() {
            setChecked(!isChecked());
        }

        public boolean isChecked() {
            return checked;
        }
    }

    private Image createButtonLabel(String label) {
        TextLayout textLayout = PlayN.graphics().layoutText(label, textFormat);
        CanvasImage textImage = PlayN.graphics().createImage(textLayout.width(), textLayout.height());
        final Canvas canvas = textImage.canvas();
        canvas.setFillColor(Color.rgb(255, 255, 255));
        canvas.fillText(textLayout, 0, 0);
        return textImage;
    }

    @Override
    public void draw() {
    }

    @Override
    public void stop() {
        useVirtualPad = null;
        PlayN.graphics().rootLayer().remove(optionsLayer);
        optionsLayer.destroyAll();
        optionsLayer = null;
    }

    @Override
    public void update() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
        if (mustQuit) {
            throw new Sequence.ResumeTransitionException(nextSequence);
        }
    }

    @Override
    public void processInputs() throws NormalTransitionException, ResumeTransitionException, ResumableTransitionException {
    }

    @Override
    public void resume() {
    }

    @Override
    public void tick() throws NormalTransitionException, ResumeTransitionException, RestartGameException {
    }

}
