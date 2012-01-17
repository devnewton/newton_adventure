/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IPlatformFactory;
import java.util.Properties;

/**
 *
 * @author Borome
 */
public class PlatformFactory implements IPlatformFactory {

    public SoundCache createSoundCache(Properties config) {
        return new SoundCache(config.getProperty("sound.enabled").equals("true"));
    }

    public GameView createGameView(Properties config) {
        return new GameView(config);
    }

    public GameInput createGameInput(Properties config) throws Exception {
        return new GameInput(config);
    }
    
}
