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
public class TmxLayer {

    private String name;
    private int x, y;
    private int width;
    private int height;
    private List<TmxProperty> properties = new ArrayList<TmxProperty>();
    private TmxData data;
    private TmxTileInstance tiles[][];

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<TmxProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TmxProperty> properties) {
        this.properties = properties;
    }

    public TmxData getData() {
        return data;
    }

    public void setData(TmxData data) {
        this.data = data;
    }

    public void setTileAt(int tx, int ty, TmxTileInstance tile) {
        tx -= x;
        ty -= y;
        tiles[tx][ty] = tile;
    }

    public TmxTileInstance getTileAt(int tx, int ty) {
        tx -= x;
        ty -= y;
        if (tx >= 0 && ty >= 0 && tx < width && ty < height) {
            return tiles[tx][ty];
        } else {
            return null;
        }
    }

    public void afterUnmarshal() {
        tiles = new TmxTileInstance[width][height];
    }
}
