/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl.openal;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Asier
 */
class SoundSourceEntry {
    public final IntBuffer sourceId;
    private SoundBufferEntry lastPlayed;

    public SoundSourceEntry() {
        lastPlayed = null;
        sourceId = BufferUtils.createIntBuffer(1);
    }

    public SoundBufferEntry getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(SoundBufferEntry lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
    
}
