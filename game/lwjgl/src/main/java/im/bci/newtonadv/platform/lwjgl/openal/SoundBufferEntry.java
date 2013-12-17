/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl.openal;

import java.nio.IntBuffer;
import java.util.HashMap;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Asier
 */
class SoundBufferEntry {
    public final IntBuffer bufferId;
    public final HashMap<SoundSourceEntry,SoundSourceEntry> playingAt;

    public SoundBufferEntry() {
        bufferId = BufferUtils.createIntBuffer(1);
        playingAt = new HashMap<SoundSourceEntry, SoundSourceEntry>();
    }
    
    
}
