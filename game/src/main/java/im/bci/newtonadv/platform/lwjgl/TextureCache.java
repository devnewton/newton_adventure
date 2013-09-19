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

import de.matthiasmann.twl.utils.PNGDecoder;
import im.bci.nanim.NanimParser.Nanim;
import im.bci.nanim.NanimParser.PixelFormat;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

/**
 * 
 * @author devnewton
 */
public class TextureCache implements ITextureCache {
    
    private static final Logger logger = Logger.getLogger(TextureCache.class.getName());
    private HashMap<String/* name */, TextureWeakReference> textures = new HashMap<String, TextureWeakReference>();
    private ReferenceQueue<Texture> referenceQueue = new ReferenceQueue<Texture>();
    private final IGameData data;
    /** The colour model including alpha for the GL image */
    private static final ColorModel glAlphaColorModel = new ComponentColorModel(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 8}, true, false,
            ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    /** The colour model for the GL image */
    private static final ColorModel glColorModel = new ComponentColorModel(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 0}, false, false, ComponentColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE);
    private GameViewQuality quality = GameViewQuality.DEFAULT;
    
    TextureCache(IGameData data) {
        this.data = data;
    }
    
    public void setQuality(GameViewQuality newQuality) {
        if (newQuality != quality) {
            quality = newQuality;
            updateQuality();
        }
    }
    
    @Override
    public void clearAll() {
        for (TextureWeakReference ref : textures.values()) {
            deleteTexture(ref);
        }
        textures.clear();
    }
    
    @Override
    public void clearUseless() {
        TextureWeakReference ref;
        while ((ref = (TextureWeakReference) referenceQueue.poll()) != null) {
            deleteTexture(ref);
        }
    }
    
    public ITexture createTexture(String name, BufferedImage bufferedImage) {
        Texture texture = convertImageToTexture(bufferedImage, false);
        putTexture(name, texture);
        return texture;
    }
    
    @Override
    public ITexture getTexture(String questName, String levelName,
            tiled.core.Map map, tiled.core.Tile tile) {
        String name = questName + "#" + levelName + "#tiled_" + tile.getGid();
        TextureWeakReference textureRef = textures.get(name);
        if (textureRef != null) {
            ITexture texture = textureRef.get();
            if (texture != null) {
                return texture;
            } else {
                textures.remove(name);
            }
        }
        BufferedImage loaded = convertToBufferedImage(tile.getImage());
        Texture texture = convertImageToTexture(loaded, false);
        putTexture(name, texture);
        
        return texture;
    }
    
    public Map<String, ITexture> getTextures(String nanimName, Nanim nanim) {
        String baseName = nanimName + "#nanim_";
        Map<String, ITexture> nanimTextures = new HashMap<String, ITexture>();
        for (im.bci.nanim.NanimParser.Image nimage : nanim.getImagesList()) {
            String name = baseName + nimage.getName();
            TextureWeakReference textureRef = textures.get(name);
            if (textureRef != null) {
                ITexture texture = textureRef.get();
                if (texture != null) {
                    nanimTextures.put(nimage.getName(), texture);
                    continue;
                } else {
                    textures.remove(nanimName);
                }
            }
            Texture texture = convertNImageToTexture(nimage);
            putTexture(name, texture);
            nanimTextures.put(nimage.getName(), texture);
        }
        return nanimTextures;
    }
    
    @Override
    public ITexture getTexture(String name) {
        return getTexture(name, false);
    }
    
    private Texture getTexture(String name, boolean usePowerOfTwoTexture) {
        TextureWeakReference textureRef = textures.get(name);
        if (textureRef != null) {
            Texture texture = textureRef.get();
            if (texture != null) {
                return texture;
            } else {
                textures.remove(name);
            }
        }
        Texture texture;
        if (name.endsWith("png")) {
            texture = loadPngTexture(name);
        } else {
            BufferedImage loaded = loadImage(name);
            texture = convertImageToTexture(loaded, usePowerOfTwoTexture);
        }
        putTexture(name, texture);        
        return texture;
    }
    
    private Texture convertImageToTexture(BufferedImage bufferedImage,
            boolean usePowerOfTwoTexture) {
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
        } else {
            texWidth = bufferedImage.getWidth();
            texHeight = bufferedImage.getHeight();
        }

        // create a raster that can be used by OpenGL as a source
        // for a texture
        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false,
                    new Hashtable<String, Object>());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                    texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false,
                    new Hashtable<String, Object>());
        }

        // create a raster that can be used by OpenGL as a source
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0f));
        g.fillRect(0, 0, texWidth, texHeight);
        g.drawImage(bufferedImage, 0, 0, null);

        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] bytes = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
        
        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(bytes.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(bytes, 0, bytes.length);
        imageBuffer.flip();
        
        Texture texture = new Texture(texWidth, texHeight, texImage.getColorModel().hasAlpha());

        // produce a texture from the byte buffer
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        setupGLTextureParams();
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        int pixelFormat = texImage.getColorModel().hasAlpha() ? GL11.GL_RGBA
                : GL11.GL_RGB;
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, texWidth,
                texHeight, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        return texture;
    }
    
    private Texture convertNImageToTexture(im.bci.nanim.NanimParser.Image nimage) {
        int texWidth = nimage.getWidth();
        int texHeight = nimage.getHeight();
        Texture texture = new Texture(texWidth, texHeight, nimage.getFormat().equals(PixelFormat.RGBA_8888));
        
        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(nimage.getPixels().size());
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(nimage.getPixels().asReadOnlyByteBuffer());
        imageBuffer.flip();

        // produce a texture from the byte buffer
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        setupGLTextureParams();
        int pixelFormat = nimage.getFormat().equals(PixelFormat.RGBA_8888) ? GL11.GL_RGBA
                : GL11.GL_RGB;
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, texWidth,
                texHeight, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        return texture;
    }
    
    private Texture loadPngTexture(InputStream is) throws IOException {
        PNGDecoder decoder = new PNGDecoder(is);
        int bpp;
        PNGDecoder.Format format;
        int pixelFormat;
        int texWidth = decoder.getWidth();
        int texHeight = decoder.getHeight();
        boolean hasAlpha = decoder.hasAlpha();
        if (hasAlpha) {
            bpp = 4;
            format = PNGDecoder.Format.RGBA;
            pixelFormat = GL11.GL_RGBA;
        } else {
            bpp = 3;
            format = PNGDecoder.Format.RGB;
            pixelFormat = GL11.GL_RGB;
        }
        
        int stride = bpp * texWidth;
        ByteBuffer buffer = ByteBuffer.allocateDirect(stride * texHeight);
        decoder.decode(buffer, stride, format);
        buffer.flip();
        Texture texture = new Texture(texWidth, texHeight, hasAlpha);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        setupGLTextureParams();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, texWidth,
                texHeight, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, buffer);
        return texture;
    }
    
    private void setupGLTextureParams() {
        if(GLContext.getCapabilities().OpenGL12) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        }
        setupGLTextureQualityParams();
        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
                GL11.GL_MODULATE);
    }
    
    private void setupGLTextureQualityParams() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                quality.toGLTextureFilter());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                quality.toGLTextureFilter());
    }
    
    private void deleteTexture(TextureWeakReference texture) {
        ByteBuffer temp = ByteBuffer.allocateDirect(4);
        temp.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = temp.asIntBuffer();
        intBuffer.put(texture.textureId);
        GL11.glDeleteTextures(intBuffer);
        
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "texture {0} unallocated", texture.name);
        }
    }

    private void putTexture(String name, Texture texture) {
        textures.put(name, new TextureWeakReference(name, texture, referenceQueue));
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "texture {0} allocated", name);
        }        
    }
    
    private BufferedImage loadImage(String filename) {
        try {
            InputStream is = data.openFile(filename);
            if (null == filename) {
                throw new RuntimeException("Impossible de charger la texture " + filename);
            }
            try {
                return ImageIO.read(new BufferedInputStream(is));
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger la texture " + filename, e);
        }
    }
    
    private BufferedImage convertToBufferedImage(Image image) {
        BufferedImage bi = new BufferedImage(image.getWidth(null),
                image.getHeight(null), hasAlpha(image) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(image, 0, 0, null);
        bg.dispose();
        return bi;
    }
    
    private static boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
    
    private void updateQuality() {
        for (TextureWeakReference ref : textures.values()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ref.textureId);
            setupGLTextureQualityParams();
        }
    }
    
    private Texture loadPngTexture(String filename) {
        try {
            InputStream is = data.openFile(filename);
            if (null == filename) {
                throw new RuntimeException("Impossible de charger la texture " + filename);
            }
            try {
                return loadPngTexture(is);
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger la texture " + filename, e);
        }
        
    }
    
    @Override
    public ITexture grabScreenToTexture() {
        
        Texture texture = new Texture(LwjglHelper.getWidth(), LwjglHelper.getHeight(), false);
        putTexture("!screenCapture", texture);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        setupGLTextureParams();
        GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 0, 0, texture.getWidth(), texture.getHeight(), 0);
        return texture;
    }
    
    private static final class TextureWeakReference extends WeakReference<Texture> {
        
        int textureId;
        String name;
        
        TextureWeakReference(String name, Texture texture, ReferenceQueue<Texture> queue) {
            super(texture, queue);
            textureId = texture.getId();
            this.name = name;
        }
    }
}
