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

import im.bci.nanim.NanimParser.Nanim;
import im.bci.nanim.NanimParser.PixelFormat;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.platform.interfaces.ITexture;
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
import org.lwjgl.opengl.GL11;

/**
 * 
 * @author devnewton
 */
public class TextureCache implements ITextureCache {

	private HashMap<String/* name */, TextureWeakReference> textures = new HashMap<String, TextureWeakReference>();
	private ReferenceQueue<Texture> referenceQueue = new ReferenceQueue<Texture>();
	private final GameData data;
	/** The colour model including alpha for the GL image */
	private static final ColorModel glAlphaColorModel = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] { 8, 8, 8, 8 }, true, false,
			ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
	/** The colour model for the GL image */
	private static final ColorModel glColorModel = new ComponentColorModel(
			ColorSpace.getInstance(ColorSpace.CS_sRGB),
			new int[] { 8, 8, 8, 0 }, false, false, ComponentColorModel.OPAQUE,
			DataBuffer.TYPE_BYTE);

	TextureCache(GameData data) {
		this.data = data;
	}

	@Override
	public void clearAll() {
		for (TextureWeakReference ref : textures.values()) {
			deleteTexture(ref.textureId);
		}
		textures.clear();
	}

	@Override
	public void clearUseless() {
		TextureWeakReference ref;
		while ((ref = (TextureWeakReference) referenceQueue.poll()) != null) {
			deleteTexture(ref.textureId);
		}
	}

	public ITexture createTexture(String name, BufferedImage bufferedImage) {
		Texture texture = convertImageToTexture(bufferedImage, false);
		textures.put(name, new TextureWeakReference(texture, referenceQueue));
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
		textures.put(name, new TextureWeakReference(texture, referenceQueue));
		return texture;
	}
	
	public Map<String, ITexture> getTextures(String nanimName, Nanim nanim) {
		String baseName = nanimName + "#nanim_";
		Map<String, ITexture> nanimTextures = new HashMap<String, ITexture>();
		for(im.bci.nanim.NanimParser.Image nimage : nanim.getImagesList()) {
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
			textures.put(name, new TextureWeakReference(texture, referenceQueue));
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
		BufferedImage loaded = loadImage(name);
		Texture texture = convertImageToTexture(loaded, usePowerOfTwoTexture);
		textures.put(name, new TextureWeakReference(texture, referenceQueue));
		return texture;
	}

	private static Texture convertImageToTexture(BufferedImage bufferedImage,
			boolean usePowerOfTwoTexture) {
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
		} else {
			texWidth = bufferedImage.getWidth();
			texHeight = bufferedImage.getHeight();
		}

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha() && hasUsefullAlphaChannel(bufferedImage)) {
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
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		// {

		// GL11.glTexParameteri(target, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		// }
		Texture texture = new Texture(texWidth, texHeight, texImage.getColorModel().hasAlpha());

		// produce a texture from the byte buffer
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
				GL11.GL_MODULATE);
		int pixelFormat = texImage.getColorModel().hasAlpha() ? GL11.GL_RGBA
				: GL11.GL_RGB;
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, texWidth,
				texHeight, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, imageBuffer);
		return texture;
	}
	
	private static boolean hasUsefullAlphaChannel(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				int rgba = image.getRGB(x, y);
				byte a = (byte) ((rgba >> 24) & 0xff);
				if(a != -1) {
					return true;
				}
			}
		}
		return false;
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
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE,
				GL11.GL_MODULATE);
		int pixelFormat = nimage.getFormat().equals(PixelFormat.RGBA_8888) ? GL11.GL_RGBA
				: GL11.GL_RGB;
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixelFormat, texWidth,
				texHeight, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, imageBuffer);
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
			InputStream is = data.openFile(filename);
			try {
				return ImageIO.read(is);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Impossible de charger la texture " + filename, e);
			System.exit(0);
			return null;
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
	        BufferedImage bimage = (BufferedImage)image;
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


	private static final class TextureWeakReference extends
			WeakReference<Texture> {

		int textureId;

		TextureWeakReference(Texture texture, ReferenceQueue<Texture> queue) {
			super(texture, queue);
			textureId = texture.getId();
		}
	}
}
