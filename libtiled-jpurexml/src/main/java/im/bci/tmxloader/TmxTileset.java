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
import java.util.TreeMap;

/**
 *
 * @author devnewton
 */
public class TmxTileset {

    private String source;
    private String name;
    private int tilewidth;
    private int tileheight;
    private int spacing, margin;
    private List<TmxProperty> properties = new ArrayList<>();
    private TmxImage image;
    private int firstgid;
    private final TreeMap<Integer/*id*/, TmxTile> tilesById = new TreeMap<>();
    private List<TmxTile> tiles = new ArrayList<>();

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getFirstgid() {
        return firstgid;
    }

    public void setFirstgid(int firstgid) {
        this.firstgid = firstgid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public List<TmxProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TmxProperty> properties) {
        this.properties = properties;
    }

    public TmxImage getImage() {
        return image;
    }

    public void setImage(TmxImage image) {
        this.image = image;
    }

    public List<TmxTile> getTiles() {
        return tiles;
    }

    public void setTiles(List<TmxTile> tiles) {
        this.tiles = tiles;
    }

    public void afterUnmarshal() {
        this.tilesById.clear();
        for (TmxTile tile : tiles) {
            this.tilesById.put(tile.getId(), tile);
        }
        if (null != image) {
            final int maxX = image.getWidth() - margin;
            final int maxY = image.getHeight() - margin;
            int id = 0;
            for (int y = margin; y < maxY; y += tileheight + spacing) {
                for (int x = margin; x < maxX; x += tilewidth + spacing) {
                    TmxTile tile = tilesById.get(id);
                    if (null == tile) {
                        tile = new TmxTile();
                        tile.setId(id);
                        tilesById.put(id, tile);
                    }
                    if(null == tile.getFrame()) {
                        tile.setFrame(new TmxFrame(image, x, y, x + tilewidth, y + tileheight));
                    }
                    ++id;
                }
            }
        }
    }

    TmxTile getTileById(int i) {
        return tilesById.get(i);
    }

    public String getProperty(String name, String defaultValue) {
        return TmxUtils.getProperty(properties, name, defaultValue);
    }
}
