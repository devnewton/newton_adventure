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
package im.bci.newtonadv.platform.lwjgl;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.Display;

class IconLoader {

    static void setIcon() {
        try {
            int nbIcons = Display.setIcon(IconLoader.loadIcon());
            Logger.getLogger(IconLoader.class.getName()).log(Level.INFO, "nbIcons = {0}", nbIcons);
        } catch (Exception ex) {
            Logger.getLogger(IconLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ByteBuffer[] loadIcon() throws IOException {
        BufferedImage image = ImageIO.read(IconLoader.class.getClassLoader().getResource("icon.png"));
        ByteBuffer[] buffers = new ByteBuffer[3];
        buffers[0] = loadIconInstance(image, 128);
        buffers[1] = loadIconInstance(image, 32);
        buffers[2] = loadIconInstance(image, 16);
        return buffers;
    }

    private static BufferedImage scale(BufferedImage source, int width, int height) {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = buf.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();

        return buf;
    }

    private static ByteBuffer loadIconInstance(BufferedImage image, int dimension) {
        return readImageAsByteBuffer(scale(image, dimension, dimension));
    }

    private static byte[] getRGBAPixels(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        byte[] pixels = new byte[w * h * 4];
        int pixelIndex = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int rgba = image.getRGB(x, y);
                byte a = (byte) ((rgba >> 24) & 0xff);
                byte r = (byte) ((rgba >> 16) & 0xff);
                byte g = (byte) ((rgba >> 8) & 0xff);
                byte b = (byte) (rgba & 0xff);
                pixels[pixelIndex++] = r;
                pixels[pixelIndex++] = g;
                pixels[pixelIndex++] = b;
                pixels[pixelIndex++] = a;
            }
        }
        return pixels;
    }

    private static ByteBuffer readImageAsByteBuffer(BufferedImage image) {
        return ByteBuffer.wrap(getRGBAPixels(image));
    }
}