/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package im.bci.newtonadv.platform.interfaces;

/**
 *
 * @author devnewton
 */
public interface ITrueTypeFont {
    int ALIGN_CENTER = 2;
    int ALIGN_LEFT = 0;
    int ALIGN_RIGHT = 1;

    void destroy();

    void drawString(String msg);

    void drawString(float x, float y, String whatchars, float scaleX, float scaleY);

    void drawString(float x, float y, String whatchars, float scaleX, float scaleY, int format);

    void drawString(float x, float y, String whatchars, int startIndex, int endIndex, float scaleX, float scaleY, int format);

    int getHeight();

    int getHeight(String HeightString);

    int getLineHeight();

    int getWidth(String whatchars);

}
