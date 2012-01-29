package im.bci.newtonadv.platform.android;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLUtils;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;

public class AndroidTextureCache implements ITextureCache {

	private final AssetManager assets;

	public AndroidTextureCache(AssetManager assets) {
		this.assets = assets;
	}

	private HashMap<String/* name */, TextureWeakReference> textures = new HashMap<String, TextureWeakReference>();
	private ReferenceQueue<AndroidTexture> referenceQueue = new ReferenceQueue<AndroidTexture>();

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

	public ITexture createTexture(String name, Bitmap Bitmap) {
		AndroidTexture texture = convertImageToTexture(Bitmap, false);
		textures.put(name, new TextureWeakReference(texture, referenceQueue));
		return texture;
	}

	public ITexture getTexture(String questName, String levelName, tiled.core.Map map, tiled.core.Tile tile) {
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
		AndroidTexture texture = convertImageToTexture(tile.getImage(), false);
		textures.put(name, new TextureWeakReference(texture, referenceQueue));
		return texture;
	}

	public ITexture getTexture(String name) {
		return getTexture(name, false);
	}

	private AndroidTexture getTexture(String name, boolean usePowerOfTwoTexture) {
		TextureWeakReference textureRef = textures.get(name);
		if (textureRef != null) {
			AndroidTexture texture = textureRef.get();
			if (texture != null) {
				return texture;
			} else {
				textures.remove(name);
			}
		}
		Bitmap loaded = loadImage(name);
		AndroidTexture texture = convertImageToTexture(loaded, usePowerOfTwoTexture);
		textures.put(name, new TextureWeakReference(texture, referenceQueue));
		return texture;
	}

	private static AndroidTexture convertImageToTexture(Bitmap bitmap,
			boolean usePowerOfTwoTexture) {
		int texWidth;
		int texHeight;
		if (usePowerOfTwoTexture) {
			texWidth = 2;
			texHeight = 2;

			// find the closest power of 2 for the width and height
			// of the produced texture
			while (texWidth < bitmap.getWidth()) {
				texWidth *= 2;
			}
			while (texHeight < bitmap.getHeight()) {
				texHeight *= 2;
			}
			bitmap = Bitmap.createScaledBitmap(bitmap, texWidth, texHeight,
					false);
		} else {
			texWidth = bitmap.getWidth();
			texHeight = bitmap.getHeight();
		}

		AndroidTexture texture = new AndroidTexture(texWidth, texHeight);

		// produce a texture from the byte buffer
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, texture.getId());
		GLES10.glPixelStorei(GLES10.GL_UNPACK_ALIGNMENT, 1);
		GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S,
				GLES10.GL_CLAMP_TO_EDGE);
		GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T,
				GLES10.GL_CLAMP_TO_EDGE);
		GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,
				GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST);
		GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D,
				GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST);
		GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES10.glTexEnvf(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE,
				GLES10.GL_MODULATE);
		return texture;
	}

	private void deleteTexture(int textureId) {
		int[] textures = { textureId };
		GLES10.glDeleteTextures(1, textures, 0);
	}

	private Bitmap loadImage(String filename) {
		try {
			return BitmapFactory.decodeStream(assets.open(filename));
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Impossible de charger la texture " + filename, e);
			System.exit(0);
			return null;
		}
	}

	private static final class TextureWeakReference extends
			WeakReference<AndroidTexture> {

		int textureId;

		TextureWeakReference(AndroidTexture texture, ReferenceQueue<AndroidTexture> queue) {
			super(texture, queue);
			textureId = texture.getId();
		}
	}

}
