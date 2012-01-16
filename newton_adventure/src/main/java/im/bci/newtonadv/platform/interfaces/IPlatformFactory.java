/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package im.bci.newtonadv.platform.interfaces;

import im.bci.newtonadv.util.SoundCache;
import java.util.Properties;

/**
 *
 * @author devnewton
 */
public interface IPlatformFactory {

    IGameInput createGameInput(Properties config) throws Exception;

    IGameView createGameView(Properties config);

    SoundCache createSoundCache(Properties config);

}
