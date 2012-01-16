/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package im.bci.newtonadv.platform.interfaces;

import java.awt.image.BufferedImage;
import tiled.core.Map;
import tiled.core.Tile;

/**
 *
 * @author devnewton
 */
public interface ITextureCache {

    void clearAll();

    void clearUseless();

    ITexture createTexture(String name, BufferedImage bufferedImage);

    ITexture getTexture(Map map, Tile tile);

    ITexture getTexture(String name);

}
