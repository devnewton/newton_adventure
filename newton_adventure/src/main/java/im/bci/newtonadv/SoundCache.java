/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 *
 * @author bci
 */
public class SoundCache {

    private HashMap<String/*name*/, ClipWeakReference> clips = new HashMap();
    private ReferenceQueue<Clip> clipReferenceQueue = new ReferenceQueue<Clip>();
    private Sequencer sequencer;
    private String currentSequenceName;
    private boolean enabled;

    SoundCache(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            try {
                sequencer = MidiSystem.getSequencer();
            } catch (MidiUnavailableException ex) {
                Logger.getLogger(SoundCache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stopMusic() {
        currentSequenceName = null;
        if (null != sequencer && sequencer.isOpen()) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
        }
    }

    public void playMusicIfEnabled(String name) {
        if (sequencer == null) {
            return;
        }
        if (name.equals(currentSequenceName)) {
            return;
        }

        try {
            Sequence music = getMusicIfEnabled(name);
            if (sequencer.isOpen()) {
                if (sequencer.isRunning()) {
                    sequencer.stop();
                }
            } else {
                sequencer.open();
            }

            sequencer.setSequence(music);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            currentSequenceName = name;
        } catch (Exception ex) {
            Logger.getLogger(SoundCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Sequence getMusicIfEnabled(String name) {
        if (!enabled) {
            return null;
        }
        Sequence sequence = loadSequence(name);
        return sequence;
    }

    public Clip getSoundIfEnabled(String name) {
        if (!enabled) {
            return null;
        }
        ClipWeakReference clipRef = clips.get(name);
        if (clipRef != null) {
            Clip clip = clipRef.get();
            if (clip != null) {
                return clip;
            } else {
                clips.remove(name);
            }
        }
        Clip clip = loadClip(name);
        if (clip != null) {
            clips.put(name, new ClipWeakReference(clip, clipReferenceQueue));
        }
        return clip;
    }

    public void clearAll() {
        for (ClipWeakReference ref : clips.values()) {
            if (ref.get() != null) {
                ref.get().close();
            }
        }
        clips.clear();
        stopMusic();
    }

    void clearUseless() {
        ClipWeakReference ref;
        while ((ref = (ClipWeakReference) clipReferenceQueue.poll()) != null) {
            if (ref.get() != null) {
                ref.get().close();
            }
        }
    }

    private Clip loadClip(String filename) {
        try {

            final File file = new File(filename);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            System.out.println("Impossible de charger le son " + filename);
            System.out.println("Erreur : " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    private Sequence loadSequence(String filename) {
        try {
            Sequence sequence = MidiSystem.getSequence(new File(filename));
            return sequence;
        } catch (Exception e) {
            System.out.println("Impossible de charger la musique " + filename);
            System.out.println("Erreur : " + e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    private static final class ClipWeakReference extends WeakReference<Clip> {

        ClipWeakReference(Clip clip, ReferenceQueue queue) {
            super(clip, queue);
        }
    }
}
