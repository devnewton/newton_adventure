/* 
 * Copyright (c) 2002-2004 LWJGL Project
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
 * * Neither the name of 'LWJGL' nor the names of 
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


package im.bci.newtonadv.platform.lwjgl.openal;

import im.bci.newtonadv.platform.interfaces.IGameData;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.lwjgl.openal.AL10;

/**
 *
 * @author Asier
 */
class SoundData {

    private static final Logger logger = Logger.getLogger(SoundData.class.getName());
    public final ByteBuffer data;
    public final int format;
    public final int samplerate;

    public SoundData(ByteBuffer data, int format, int samplerate) {
        this.data = data;
        this.format = format;
        this.samplerate = samplerate;
    }
    
    public static SoundData create(String path, IGameData gameData){
        if(path.endsWith(".wav")){
            return createFromWav(path, gameData);
        }else if(path.endsWith(".ogg")){
            return createFromOgg(path, gameData);
        }
        return null;
    }
    
    private static SoundData createFromWav(String path, IGameData gameData) {
        InputStream fis = null;
        BufferedInputStream bis = null;
        AudioInputStream ais = null;
        try {
            fis = gameData.openFile(path);
            bis = new BufferedInputStream(fis);
            ais = AudioSystem.getAudioInputStream(bis);

            AudioFormat info = ais.getFormat();
            int size = info.getChannels()
                    * (int) ais.getFrameLength()
                    * info.getSampleSizeInBits() / 8;
            byte[] array = new byte[size];
            ais.read(array);
            ByteBuffer buffer = convertAudioBytes(array, info.getSampleSizeInBits() == 16);
            array = null;
            return new SoundData(buffer, getALFormat(info), (int) info.getSampleRate());
        } catch (Exception ex) {
            logger.log(Level.WARNING,"Loading wav file " + path, ex);
        } finally {
            try {ais.close();} catch (Exception e) {}
            try {bis.close();} catch (Exception e) {}
            try {fis.close();} catch (Exception e) {}
        }
        return null;
    }
    
    private static SoundData createFromOgg(String path, IGameData gameData) {
        InputStream fis = null;
        BufferedInputStream bis = null;
        OggInputStream ogg = null;
        try {
            fis = gameData.openFile(path);
            bis = new BufferedInputStream(fis);
            ogg = new OggInputStream(bis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(128*1024);
            byte[] buffer = new byte[1024];
            int readed;
            while((readed = ogg.read(buffer)) > 0){
                bos.write(buffer, 0, readed);
            }
            buffer = bos.toByteArray(); bos = null;
            ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length);
            bb.put(buffer);
            bb.rewind(); buffer = null;
            
           return new SoundData(bb, getALFormat(ogg), ogg.getRate());
           
        } catch (Exception ex) {
            logger.log(Level.WARNING,"Loading ogg file " + path, ex);
        } finally {
            try {ogg.close();} catch (Exception e) {}
            try {bis.close();} catch (Exception e) {}
            try {fis.close();} catch (Exception e) {}
        }
        return null;
    }
    
    
    private static ByteBuffer convertAudioBytes(byte[] audio, boolean is16bits) {
        ByteBuffer dest = ByteBuffer.allocateDirect(audio.length);
        dest.order(ByteOrder.nativeOrder());
        ByteBuffer src = ByteBuffer.wrap(audio);
        src.order(ByteOrder.LITTLE_ENDIAN);
        if (is16bits) {
            ShortBuffer dest_short = dest.asShortBuffer();
            ShortBuffer src_short = src.asShortBuffer();
            while (src_short.hasRemaining()) {
                dest_short.put(src_short.get());
            }
        } else {
            while (src.hasRemaining()) {
                dest.put(src.get());
            }
        }
        dest.rewind();
        return dest;
    }
    
    private static int getALFormat(AudioFormat info) {
        int format = 0;
        if (info.getChannels() == 1) {
            if (info.getSampleSizeInBits() == 8) {
                format = AL10.AL_FORMAT_MONO8;
            } else if (info.getSampleSizeInBits() == 16) {
                format = AL10.AL_FORMAT_MONO16;
            } else {
                throw new RuntimeException("Illegal sample size");
            }
        } else if (info.getChannels() == 2) {
            if (info.getSampleSizeInBits() == 8) {
                format = AL10.AL_FORMAT_STEREO8;
            } else if (info.getSampleSizeInBits() == 16) {
                format = AL10.AL_FORMAT_STEREO16;
            } else {
                throw new RuntimeException("Illegal sample size");
            }
        } else {
            throw new RuntimeException("Only mono or stereo is supported");
        }
        return format;
    }
    
    private static int getALFormat(OggInputStream ogg) {
        int format = 0;
        if (ogg.getChannels() == 1) {
             format = AL10.AL_FORMAT_MONO16;
        } else if (ogg.getChannels() == 2) {
            format = AL10.AL_FORMAT_STEREO16;
        } else {
            throw new RuntimeException("Only mono or stereo is supported");
        }
        return format;
    }
}
