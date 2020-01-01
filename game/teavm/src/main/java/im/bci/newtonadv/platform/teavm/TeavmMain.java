/*
 The MIT License (MIT)

 Copyright (c) 2019 devnewton <devnewton@bci.im>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
package im.bci.newtonadv.platform.teavm;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.RestartGameException;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;

/**
 *
 * @author devnewton
 */
public class TeavmMain {

    private CanvasRenderingContext2D ctx;
    private HTMLCanvasElement canvas;
    private Game game;
    private TeavmPlatformSpecific platform;

    public void launch(String[] args) throws Exception {
        this.canvas = (HTMLCanvasElement) Window.current().getDocument().getElementById("newtonadventure-canvas");
        this.ctx = canvas.getContext("2d").cast();
        this.platform = new TeavmPlatformSpecific(canvas, ctx);
        this.game = new Game(this.platform);
        this.game.start();
        Window.requestAnimationFrame(this::frame);
    }

    public void frame(double timestamp) {
        try {
            game.tick();
        } catch (RestartGameException e) {
            try {
                game = new Game(platform);
                game.start();
                game.tick();
                platform.saveConfig();//only save config if everything seems ok
            } catch (Exception ex) {
                System.out.println(ex);
                throw new RuntimeException(ex);
            }
        }
        Window.requestAnimationFrame(this::frame);
    }

    public static void main(String[] args) throws Exception {
        TeavmMain sample = new TeavmMain();
        sample.launch(args);
    }

}
