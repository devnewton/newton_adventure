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

import im.bci.newtonadv.platform.interfaces.ISoundCache;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * 
 * @author bci
 */
public class SoundCache implements ISoundCache {

	private HashMap<String/* name */, ClipWeakReference> clips = new HashMap<String, ClipWeakReference>();
	private ReferenceQueue<Clip> clipReferenceQueue = new ReferenceQueue<Clip>();
	private String currentMusicName;
	private OggClip currentMusic;
	private boolean enabled;
	private final GameData data;

	public static final class PlayableClipWrapper implements Playable {

		private final Clip clip;

		PlayableClipWrapper(Clip clip) {
			this.clip = clip;
		}

		@Override
		public void play() {
			clip.setFramePosition(0);
			clip.loop(0);
		}

		@Override
		public void stop() {
			clip.stop();
		}
	}

	public SoundCache(GameData data, boolean enabled) {
		this.data = data;
		this.enabled = enabled;
	}

	@Override
	public void stopMusic() {
		currentMusicName = null;
		if (null != currentMusic) {
			currentMusic.stop();
			currentMusic.close();
		}
	}

	@Override
	public void playMusicIfEnabled(String name) {
		if (name.equals(currentMusicName)) {
			return;
		}

		if (null != currentMusic) {
			currentMusic.stop();
			currentMusic.close();
		}

		try {
			currentMusic = getMusicIfEnabled(name);
			if (null != currentMusic) {
				currentMusic.loop();
				currentMusicName = name;
			}
		} catch (Exception ex) {
			Logger.getLogger(SoundCache.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private OggClip getMusicIfEnabled(String name) {
		if (!enabled) {
			return null;
		}
		OggClip clip = loadOggClip(name);
		return clip;
	}

	@Override
	public Playable getSoundIfEnabled(String name) {
		if (!enabled) {
			return null;
		}
		ClipWeakReference clipRef = clips.get(name);
		if (clipRef != null) {
			Clip clip = clipRef.get();
			if (clip != null) {
				return new PlayableClipWrapper(clip);
			} else {
				clips.remove(name);
			}
		}
		final Clip clip = loadClip(name);
		if (clip != null) {
			clips.put(name, new ClipWeakReference(clip, clipReferenceQueue));
		}
		return new PlayableClipWrapper(clip);
	}

	@Override
	public void clearAll() {
		for (ClipWeakReference ref : clips.values()) {
			if (ref.get() != null) {
				ref.get().close();
			}
		}
		clips.clear();
		stopMusic();
	}

	@Override
	public void clearUseless() {
		ClipWeakReference ref;
		while ((ref = (ClipWeakReference) clipReferenceQueue.poll()) != null) {
			if (ref.get() != null) {
				ref.get().close();
			}
		}
	}

	private Clip loadClip(String filename) {
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(data.openFile(filename));
			AudioFormat format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioInputStream);
			return clip;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Impossible de charger le son " + filename, e);
			System.exit(0);
			return null;
		}
	}

	private OggClip loadOggClip(String filename) {
		try {
			OggClip clip = new OggClip(data.openFile(filename));
			return clip;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Impossible de charger la musique " + filename, e);
			System.exit(0);
			return null;
		}
	}

	private static final class ClipWeakReference extends WeakReference<Clip> {

		ClipWeakReference(Clip clip, ReferenceQueue<Clip> queue) {
			super(clip, queue);
		}
	}

	public boolean isSoundEnabled() {
		return enabled;
	}

	public void setSoundEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled)
			stopMusic();
	}
}
