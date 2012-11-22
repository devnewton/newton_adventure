package im.bci.newtonadv.platform.lwjgl.openal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

/**
 * A sound implementation wrapped round a player which reads (and potentially) rereads
 * a stream. This supplies streaming audio
 *
 * @author kevin
 * @author Nathan Sweet <misc@n4te.com>
 * @author Rockstar playAsMusic cleanup 
 * @author Asier
 */
class Streamer {

    private static final Logger logger = Logger.getLogger(Streamer.class.getName());
    public static final int BUFFER_COUNT = 3;
    private static final int sectionSize = 4096 * 20;
    
    private int source;
    private byte[] buffer;
    private IntBuffer buffersId;
    private int remainingBufferCount;
    private ByteBuffer bufferData;
    private IntBuffer auxBuffer;
    private OggInputStream audio;
    private File audioFile;
    private boolean loop;
    private boolean idle;
    private boolean initiated;
    private boolean paused;

    public Streamer() {
        buffer = new byte[sectionSize];
        bufferData = BufferUtils.createByteBuffer(sectionSize);
        auxBuffer = BufferUtils.createIntBuffer(1);
        idle = true;
    }

    public void init() {
        if(!initiated){
            buffersId = BufferUtils.createIntBuffer(BUFFER_COUNT);
            AL10.alGenBuffers(buffersId);
            if (AL10.alGetError() != AL10.AL_NO_ERROR) {
                logger.log(Level.WARNING, "Initializing buffers of music streamer {0}", AL10.alGetError());
            }
            source = AL10.alGenSources();
            if (AL10.alGetError() != AL10.AL_NO_ERROR) {
                logger.log(Level.WARNING, "Initializing source of music streamer {0}", AL10.alGetError());
            }
            initiated = true;
        }
    }

    public void destroy() {
        if(initiated){
            stop();
            AL10.alSourcei(source, AL10.AL_BUFFER, 0);
            AL10.alDeleteSources(source);
            AL10.alDeleteBuffers(buffersId);
            initiated = false;
        }
    }

    public void setSourceFile(File f) {
        stop();
        audioFile = f;
    }

    public void setPause(boolean pause) {
        if (idle || !initiated) {
            return;
        }
        if (pause && !paused) {
            paused = true;
            AL10.alSourcePause(source);
        } else if (paused) {
            paused = false;
            AL10.alSourcePlay(source);
        }
    }

    public void stop() {
        if(!idle || !initiated){
            AL10.alSourceStop(source);
            unqueueBuffers();
            AL10.alSourcei(source, AL10.AL_BUFFER, 0);
            idle = true;
        }
    }

    public void play(boolean loop) throws IOException {
        if(!initiated) {return;}
        this.loop = loop;
        if(!idle){
            stop();
        }
        initStreams();
        startPlayback();
        idle = false;
        paused = false;
    }
    
   
    public void poll() {
        if (idle || !initiated || paused) {
            return;
        }
        for (int processed = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
                processed > 0; processed--) {
            auxBuffer.clear();
            AL10.alSourceUnqueueBuffers(source, auxBuffer);
            int bufferIndex = auxBuffer.get(0);
            if (stream(bufferIndex)) {
                AL10.alSourceQueueBuffers(source, auxBuffer);
                if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
                    AL10.alSourcePlay(source);
                }
            } else {
                remainingBufferCount--;
                System.out.println("RemBuf " + remainingBufferCount);
                if (remainingBufferCount <= 0) {
                    stop();
                } else {
                    if (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) != AL10.AL_PLAYING) {
                        AL10.alSourcePlay(source);
                    }
                }
            }
        }

    }
    


    private void initStreams() throws IOException {
        if (audio != null) {
            audio.close();
        }
        if (audioFile != null) {
            audio = new OggInputStream(audioFile);
        } else {
            throw new IOException("File not setted");
        }
        remainingBufferCount = BUFFER_COUNT;
    }


    private boolean stream(int bufferId) {
        try {
            int count = audio.read(buffer);
            if (count != -1) {
                bufferData.clear();
                bufferData.put(buffer, 0, count);
                bufferData.flip();

                int format = audio.getChannels() > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16;
                try {
                    AL10.alBufferData(bufferId, format, bufferData, audio.getRate());
                } catch (OpenALException e) {
                    logger.log(Level.WARNING, "Failed to loop buffer: " + bufferId + " " + format + " " + count + " " + audio.getRate(), e);
                    return false;
                }
            } else {
                if (loop) {
                    initStreams();
                    stream(bufferId);
                } else {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "audio error", e);
            return false;
        }
    }


    private void startPlayback() {
        AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);
        AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
        remainingBufferCount = BUFFER_COUNT;
        for (int i = 0; i < BUFFER_COUNT; i++) {
            stream(buffersId.get(i));
        }
        AL10.alSourceQueueBuffers(source, buffersId);
        AL10.alSourcePlay(source);
    }

    

    private void unqueueBuffers() {
        for(int queued = AL10.alGetSourcei(source, AL10.AL_BUFFERS_QUEUED);
                queued>0;queued--){
            AL10.alSourceUnqueueBuffers(source, auxBuffer);
        }
    }

    
}
