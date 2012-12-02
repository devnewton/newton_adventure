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
package im.bci.newtonadv.platform.lwjgl.javaxsound;

import im.bci.newtonadv.platform.lwjgl.javaxsound.OggClip;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.ISoundCache;

import java.io.BufferedInputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * 
 * @author bci
 */
public class JavaxSoundCache implements ISoundCache {

	private HashMap<String/* name */, ClipWeakReference> clips = new HashMap<String, ClipWeakReference>();
	private ReferenceQueue<PlayableClipWrapper> clipReferenceQueue = new ReferenceQueue<PlayableClipWrapper>();
	private String currentMusicName;
	private OggClip currentMusic;
	private boolean soundEnabled;
	private boolean musicEnabled;
	private final IGameData data;
	private static final Logger logger = Logger.getLogger(JavaxSoundCache.class.getName());

        @Override
        public void close() {
            stopMusic();
            clearAll();
        }

	public static final class PlayableClipWrapper implements Playable, LineListener{

		private final Clip clip;
                private int nbLoopTodo = 0;
                private boolean isPlaying;

		PlayableClipWrapper(Clip clip) {
			this.clip = clip;
                        clip.addLineListener(this);
		}

		@Override
		public void play() {
                    ++nbLoopTodo;
                    System.out.println("play " + nbLoopTodo);
		}

		@Override
		public void stop() {
			clip.stop();
		}

                public void update() {

                    if(nbLoopTodo > 0 && !isPlaying) {
                        System.out.println("update " + nbLoopTodo);
                        clip.setFramePosition(0);
                        clip.loop(0);
                    }
                }

                @Override
                public void update(LineEvent event) {
                    if(LineEvent.Type.START == event.getType()) {
                        if(nbLoopTodo>0) {
                            --nbLoopTodo;
                        }
                        isPlaying = true; 
                    }else if(LineEvent.Type.STOP == event.getType()) {
                        isPlaying = false;
                    }
                }
	}
	
	public static final class NullPlayable implements Playable {

		@Override
		public void play() {
		}

		@Override
		public void stop() {
		}
		
	}

	public JavaxSoundCache(IGameData data, Properties config) {
		this.data = data;
		this.soundEnabled = config.getProperty("sound.enabled").equals("true");
		this.musicEnabled = config.getProperty("music.enabled").equals("true");
	}

	@Override
	public void stopMusic() {
		try {
			currentMusicName = null;
			if (null != currentMusic) {
				currentMusic.stop();
				currentMusic.close();
			}
		} catch( Exception ex) {
			logger.log(Level.SEVERE,"Cannot stop music ", ex);
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
			logger.log(Level.SEVERE,"Cannot play music " + name, ex);
		}
	}

	private OggClip getMusicIfEnabled(String name) {
		if (!musicEnabled) {
			return null;
		}
		return loadOggClip(name);
	}

	@Override
	public Playable getSound(String name) {
		if (!soundEnabled) {
			return new NullPlayable();
		}
		ClipWeakReference clipRef = clips.get(name);
		if (clipRef != null) {
			PlayableClipWrapper playable = clipRef.get();
			if (playable != null) {
				return playable;
			} else {
				clips.remove(name);
			}
		}
		final Clip clip = loadClip(name);
		if (clip != null) {
                        PlayableClipWrapper playable = new PlayableClipWrapper(clip);
			clips.put(name, new ClipWeakReference(playable, clipReferenceQueue));
			return playable;
		} else {
			return new NullPlayable();
		}
		
	}

	@Override
	public void clearAll() {
		for (ClipWeakReference ref : clips.values()) {
			if (ref.get() != null) {
				ref.get().clip.close();
			}
		}
		clips.clear();
		stopMusic();
	}
        
        @Override
	public void update() {
		for (ClipWeakReference ref : clips.values()) {
			if (ref.get() != null) {
				ref.get().update();
			}
		}
	}

	@Override
	public void clearUseless() {
		ClipWeakReference ref;
		while ((ref = (ClipWeakReference) clipReferenceQueue.poll()) != null) {
			if (ref.get() != null) {
				ref.get().clip.close();
			}
		}
	}

	private Clip loadClip(String filename) {
		try {
			BufferedInputStream stream = new BufferedInputStream(data.openFile(filename), 32 * 1024);
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(stream);
			AudioFormat format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioInputStream);
			return clip;
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Impossible de charger le son " + filename, e);
			return null;
		}
	}

	private OggClip loadOggClip(String filename) {
		try {
			OggClip clip = new OggClip(data.openFile(filename));
			return clip;
		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"Impossible de charger la musique " + filename, e);
			return null;
		}
	}

	private static final class ClipWeakReference extends WeakReference<PlayableClipWrapper> {

		ClipWeakReference(PlayableClipWrapper clip, ReferenceQueue<PlayableClipWrapper> queue) {
			super(clip, queue);
		}
	}

	public boolean isSoundEnabled() {
		return soundEnabled;
	}

	public void setSoundEnabled(boolean enabled) {
		this.soundEnabled = enabled;
	}
	
	public boolean isMusicEnabled() {
		return musicEnabled;
	}

	public void setMusicEnabled(boolean enabled) {
		this.musicEnabled = enabled;
		if (!enabled)
			stopMusic();
	}
}