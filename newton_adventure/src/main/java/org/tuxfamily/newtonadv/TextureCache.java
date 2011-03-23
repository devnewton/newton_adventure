/*
 * Copyright (c) 2009-2010 devnewton <devnewton@tuxfamily.org>
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
 * * Neither the name of 'devnewton <devnewton@tuxfamily.org>' nor the names of
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
package org.tuxfamily.newtonadv;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author devnewton
 */
public class TextureCache {

    private HashMap<String/*name*/, TextureWeakReference> textures = new HashMap();
    private ReferenceQueue<Texture> referenceQueue = new ReferenceQueue<Texture>();
    /** The colour model including alpha for the GL image */
    private static final ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 8},
            true,
            false,
            ComponentColorModel.TRANSLUCENT,
            DataBuffer.TYPE_BYTE);
    /** The colour model for the GL image */
    private static final ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 0},
            false,
            false,
            ComponentColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE);

    TextureCache() {
    }

    public void clearAll() {
        for (TextureWeakReference ref : textures.values()) {
            deleteTexture(ref.textureId);
        }
        textures.clear();
    }

    public void clearUseless() {
        TextureWeakReference ref;
        while ((ref = (TextureWeakReference) referenceQueue.poll()) != null) {
            deleteTexture(ref.textureId);
        }
    }

    public Texture getTextureIfLoaded(String name) {
        TextureWeakReference textureRef = textures.get(name);
        if (textureRef != null) {
            Texture texture = textureRef.get();
            if (texture != null) {
                return texture;
            }
        }
        return null;
    }

    public Texture createTexture(String name, BufferedImage bufferedImage) {
        Texture texture = convertImageToTexture(bufferedImage,false);
        textures.put(name, new TextureWeakReference(texture, referenceQueue));
        return texture;
    }
    public Texture createTexture(String name, BufferedImage bufferedImage, boolean usePowerOfTwoTexture) {
        Texture texture = convertImageToTexture(bufferedImage,usePowerOfTwoTexture);
        textures.put(name, new TextureWeakReference(texture, referenceQueue));
        return texture;
    }

    public Texture getTexture(String name) {
        return getTexture(name, false);
    }
    public Texture getTexture(String name, boolean usePowerOfTwoTexture) {
        TextureWeakReference textureRef = textures.get(name);
        if (textureRef != null) {
            Texture texture = textureRef.get();
            if (texture != null) {
                return texture;
            } else {
                textures.remove(name);
            }
        }
        BufferedImage loaded = loadImage(name);
        Texture texture = convertImageToTexture(loaded,usePowerOfTwoTexture);
        textures.put(name, new TextureWeakReference(texture, referenceQueue));
        return texture;
    }

    public static Texture convertImageToTexture(BufferedImage bufferedImage, boolean usePowerOfTwoTexture) {
        ByteBuffer imageBuffer = null;
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth;
        int texHeight;
        if (usePowerOfTwoTexture) {
            texWidth = 2;
            texHeight = 2;

            // find the closest power of 2 for the width and height
            // of the produced texture
            while (texWidth < bufferedImage.getWidth()) {
                texWidth *= 2;
            }
            while (texHeight < bufferedImage.getHeight()) {
                texHeight *= 2;
            }
        } else
        {
            texWidth = bufferedImage.getWidth();
            texHeight = bufferedImage.getHeight();
        }

        // create a raster that can be used by OpenGL as a source
        // for a texture
        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
        }

        // create a raster that can be used by OpenGL as a source
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0f));
        g.fillRect(0, 0, texWidth, texHeight);
        g.drawImage(bufferedImage, 0, 0, null);

        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();


//        {

//            GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
//        }
        Texture texture = new Texture(texWidth, texHeight);

        // produce a texture from the byte buffer
        texture.bind();
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        int pixelFormat = texImage.getColorModel().hasAlpha() ? GL11.GL_RGBA : GL11.GL_RGB;
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, texWidth, texHeight, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        return texture;
    }

    private void deleteTexture(int textureId) {
        ByteBuffer temp = ByteBuffer.allocateDirect(4);
        temp.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = temp.asIntBuffer();
        intBuffer.put(textureId);
        GL11.glDeleteTextures(intBuffer);
    }

    private BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(new File(filename));
        } catch (Exception e) {
            System.out.println("Impossible de charger " + filename);
            System.out.println("Erreur : " + e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    private static final class TextureWeakReference extends WeakReference<Texture> {

        int textureId;

        TextureWeakReference(Texture texture, ReferenceQueue queue) {
            super(texture, queue);
            textureId = texture.getId();
        }
    }
}
