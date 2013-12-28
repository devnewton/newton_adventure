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

import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import playn.core.CanvasImage;
import playn.core.Font;
import static playn.core.PlayN.*;

import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.TextFormat;
import playn.core.TextLayout;

public class PlaynNewtonAdventureGame extends Game.Default {

    im.bci.newtonadv.Game game;
    private IPlatformSpecific platform;

    public PlaynNewtonAdventureGame() {
        super(1000 / 60);
    }

    @Override
    public void init() {
        // create and add background image layer
        Image bgImage = assets().getImage("help.png");
        ImageLayer bgLayer = graphics().createImageLayer(bgImage);
        graphics().rootLayer().add(bgLayer);
        


        try {
            
                    platform = new PlaynPlatformSpecific();
            /*game = new im.bci.newtonadv.Game(platform);
            game.start();
            game.tick();
                    */
        } catch (Exception ex) {
            throw new RuntimeException("Error during init", ex);
        }
        
                Font font = graphics().createFont("arial", Font.Style.PLAIN, 12);
        TextLayout layout = graphics().layoutText("lol" + platform.getMessage("options.input.rotate.clockwise"), new TextFormat().withFont(font));
        CanvasImage textImage = graphics().createImage((int) Math.ceil(layout.width()), (int) Math.ceil(layout.height()));
        textImage.canvas().fillCircle(0, 0, 10);
        textImage.canvas().fillText(layout, 0, 0);
        ImageLayer textLayer = graphics().createImageLayer(textImage);
        graphics().rootLayer().add(textLayer);
    }

    @Override
    public void update(int delta) {
        /* try {
            if (game.isRunning()) {
                try {
                    game.tick();
                } catch (RestartGameException e) {
                    game = new im.bci.newtonadv.Game(platform);
                    game.start();
                    game.tick();
                    platform.saveConfig();//only save config if everything seems ok
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error during update", ex);
        }*/

    }

    @Override
    public void paint(float alpha) {
        // the background automatically paints itself, so no need to do anything here!
    }

}
