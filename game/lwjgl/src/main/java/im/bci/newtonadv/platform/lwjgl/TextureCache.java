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

import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.stb.STBImage;

/**
 *
 * @author devnewton
 */
public class TextureCache implements ITextureCache {

    private static final Logger logger = Logger.getLogger(TextureCache.class.getName());
    private final HashMap<String/* name */, TextureWeakReference> textures = new HashMap<String/* name */, TextureWeakReference>();
    private final ReferenceQueue<Texture> referenceQueue = new ReferenceQueue<Texture>();
    private final FileGameData data;
    private GameViewQuality quality = GameViewQuality.DEFAULT;

    TextureCache(FileGameData data) {
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
        logger.log(Level.INFO, "Load texture {0}", name);
        Texture texture = loadTextureFromFile(name);
        putTexture(name, texture);
        return texture;
    }

    private void setupGLTextureParams() {
        if (GL.getCapabilities().OpenGL12) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        }
        setupGLTextureQualityParams();
        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
    }

    private void setupGLTextureQualityParams() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, quality.toGLTextureFilter());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, quality.toGLTextureFilter());
    }

    private void deleteTexture(TextureWeakReference texture) {
        logger.log(Level.INFO, "Unload texture {0}", texture.name);
        ByteBuffer temp = ByteBuffer.allocateDirect(4);
        temp.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = temp.asIntBuffer();
        intBuffer.put(texture.textureId);
        GL11.glDeleteTextures(intBuffer);
    }

    private void putTexture(String name, Texture texture) {
        textures.put(name, new TextureWeakReference(name, texture, referenceQueue));
    }

    private void updateQuality() {
        for (TextureWeakReference ref : textures.values()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ref.textureId);
            setupGLTextureQualityParams();
        }
    }

    private Texture loadTextureFromFile(String filename) {
        try {
            InputStream is = data.openFile(filename);
            if (null == filename) {
                throw new RuntimeException("Impossible de charger la texture " + filename);
            }
            try {
                IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
                IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
                IntBuffer bppBuffer = BufferUtils.createIntBuffer(1);
                byte[] bytes = is.readAllBytes();
                ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
                buffer.put(bytes).flip();
                ByteBuffer pixels = STBImage.stbi_load_from_memory(buffer, widthBuffer, heightBuffer, bppBuffer, 0);
                int width = widthBuffer.get();
                int height = heightBuffer.get();
                int bpp = bppBuffer.get();
                int pixelFormat = bpp == 4 ? GL11.GL_RGBA : GL11.GL_RGB;
                Texture texture = new Texture(width, height, pixelFormat == GL11.GL_RGBA);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
                setupGLTextureParams();
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, width, height, 0, pixelFormat,
                        GL11.GL_UNSIGNED_BYTE, pixels);
                return texture;
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger la texture " + filename, e);
        }

    }

    @Override
    public ITexture grabScreenToTexture() {
        int maxSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Texture texture = new Texture(Math.min(maxSize, viewport[2]), Math.min(maxSize, viewport[3]), false);
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
