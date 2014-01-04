/*
 The MIT License (MIT)

 Copyright (c) 2013 devnewton <devnewton@bci.im>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
package im.bci.tmxloader;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author devnewton
 */
public class TmxMap {

    private boolean ready;
    private int width;
    private int height;
    private int tilewidth;
    private int tileheight;
    private TmxMapOrientation orientation;
    private List<TmxTileset> tilesets = new ArrayList<TmxTileset>();
    private List<TmxProperty> properties = new ArrayList<TmxProperty>();
    private List<TmxLayer> layers = new ArrayList<TmxLayer>();

    public TmxMapOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(TmxMapOrientation orientation) {
        this.orientation = orientation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTilewidth() {
        return tilewidth;
    }

    public void setTilewidth(int tilewidth) {
        this.tilewidth = tilewidth;
    }

    public int getTileheight() {
        return tileheight;
    }

    public void setTileheight(int tileheight) {
        this.tileheight = tileheight;
    }

    public List<TmxTileset> getTilesets() {
        return tilesets;
    }

    public void setTilesets(List<TmxTileset> tilesets) {
        this.tilesets = tilesets;
    }

    public List<TmxProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TmxProperty> properties) {
        this.properties = properties;
    }

    public List<TmxLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<TmxLayer> layers) {
        this.layers = layers;
    }

    public String getProperty(String name, String defaultValue) {
        return TmxUtils.getProperty(properties, name, defaultValue);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public boolean canDecode() {
        for(TmxTileset ts : tilesets) {
            if(!ts.isReady()) {
                return false;
            }
        }
        return true;
    }
}
