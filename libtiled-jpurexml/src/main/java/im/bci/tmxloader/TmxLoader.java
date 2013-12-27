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

import com.github.asilvestre.jpurexml.XmlDoc;
import com.github.asilvestre.jpurexml.XmlParseException;
import com.github.asilvestre.jpurexml.XmlParser;
import com.github.asilvestre.jpurexml.XmlTag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author devnewton
 */
public class TmxLoader {

    public TmxMap load(String xml) {
        try {
            XmlDoc doc = XmlParser.parseXml(xml);
            TmxMap map = new TmxMap();
            parseMap(doc.root, map);
            parseTilesets(doc.root, map);
            parseExternalTilesets(map);
            decodeLayerData(map);
            return map;
        } catch (XmlParseException | IOException ex) {
            throw new RuntimeException("Cannot load tmx", ex);
        }
    }

    private void parseMap(XmlTag xmlMap, TmxMap map) throws NumberFormatException {
        map.setWidth(getMandatoryIntAttribute(xmlMap, "width"));
        map.setHeight(getMandatoryIntAttribute(xmlMap, "height"));
        map.setTilewidth(getMandatoryIntAttribute(xmlMap, "tilewidth"));
        map.setTileheight(getMandatoryIntAttribute(xmlMap, "tileheight"));
        map.setOrientation(TmxMapOrientation.valueOf(xmlMap.attributes.get("orientation").toUpperCase()));
        map.setProperties(parseProperties(findChild(xmlMap, "properties")));
        List<TmxLayer> layers = new ArrayList<>();
        for (XmlTag child : xmlMap.children) {
            if ("layer".equals(child.name)) {
                TmxLayer layer = new TmxLayer();
                parseLayer(child, layer);
                layer.afterUnmarshal();
                layers.add(layer);
            }
        }
        map.setLayers(layers);
    }

    XmlTag findChild(XmlTag parent, String name) {
        for (XmlTag child : parent.children) {
            if (name.equals(child.name)) {
                return child;
            }
        }
        return null;
    }

    protected String openExternalTileset(String source) {
        throw new RuntimeException("Not implemented");
    }

    private void decodeLayerData(TmxMap map) {
        HashMap<TmxTileInstance, TmxTileInstance> tileInstancePool = new HashMap<>();
        for (TmxLayer layer : map.getLayers()) {
            int[][] data = new int[layer.getWidth()][layer.getHeight()];
            layer.getData().decodeTo(layer.getWidth(), layer.getHeight(), data);
            for (int x = 0; x < layer.getWidth(); ++x) {
                for (int y = 0; y < layer.getHeight(); ++y) {
                    int gid = data[x][y];
                    if (0 != gid) {
                        EnumSet<TmxTileInstanceEffect> effects = EnumSet.noneOf(TmxTileInstanceEffect.class);
                        for (TmxTileInstanceEffect effect : TmxTileInstanceEffect.values()) {
                            if ((gid & effect.gidFlag) != 0) {
                                effects.add(effect);
                            }
                            gid &= ~effect.gidFlag;
                        }
                        ListIterator<TmxTileset> it = map.getTilesets().listIterator(map.getTilesets().size());
                        while (it.hasPrevious()) {
                            TmxTileset tileset = it.previous();
                            if (tileset.getFirstgid() <= gid) {
                                TmxTileInstance instance = new TmxTileInstance(tileset.getTileById(gid - tileset.getFirstgid()), effects);
                                TmxTileInstance pooledInstance = tileInstancePool.get(instance);
                                if (null != pooledInstance) {
                                    instance = pooledInstance;
                                } else {
                                    tileInstancePool.put(instance, instance);
                                }
                                layer.setTileAt(x, y, instance);
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseExternalTilesets(TmxMap map) throws IOException, XmlParseException {
        for (TmxTileset tileset : map.getTilesets()) {
            final String source = tileset.getSource();
            if (null != source) {
                parseTileset(XmlParser.parseXml(openExternalTileset(source)).root, map, tileset);
                String tilesetDir = source.substring(0, source.lastIndexOf('/') + 1);
                final TmxImage image = tileset.getImage();
                if (null != image) {
                    tileset.getImage().setSource(tilesetDir + image.getSource());
                }
                tileset.afterUnmarshal();
            }
        }
    }

    private List<TmxProperty> parseProperties(XmlTag xmlProperties) {
        List<TmxProperty> properties = new ArrayList<>();
        if (null != xmlProperties) {
            for (XmlTag child : xmlProperties.children) {
                if ("property".equals(child.name)) {
                    TmxProperty property = new TmxProperty();
                    property.setName(child.attributes.get("name"));
                    property.setValue(child.attributes.get("value"));
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    private void parseTilesets(XmlTag xmlMap, TmxMap map) throws XmlParseException {
        List<TmxTileset> tilesets = new ArrayList<>();
        for (XmlTag child : xmlMap.children) {
            if ("tileset".equals(child.name)) {
                TmxTileset tileset = new TmxTileset();
                String source = child.attributes.get("source");
                if (null != source) {
                    tileset.setFirstgid(getMandatoryIntAttribute(child, "firstgid"));
                    tileset.setSource(source);
                } else {
                    parseTileset(child, map, tileset);
                    tileset.afterUnmarshal();
                }
                tilesets.add(tileset);
            }
        }

        map.setTilesets(tilesets);
    }

    private void parseTileset(XmlTag xmlTileset, TmxMap map, TmxTileset tileset) {
        tileset.setName(xmlTileset.attributes.get("name"));
        tileset.setMargin(getIntAttribute(xmlTileset, "margin", 0));
        tileset.setSpacing(getIntAttribute(xmlTileset, "spacing", 0));
        tileset.setTilewidth(getIntAttribute(xmlTileset, "tilewidth", map.getTilewidth()));
        tileset.setTileheight(getIntAttribute(xmlTileset, "tileheight", map.getTileheight()));
        tileset.setProperties(parseProperties(findChild(xmlTileset, "properties")));
        tileset.setImage(parseImage(findChild(xmlTileset, "image")));
        List<TmxTile> tiles = new ArrayList<>();
        for (XmlTag child : xmlTileset.children) {
            if ("tile".equals(child.name)) {
                tiles.add(parseTile(tileset, child));
            }
        }
        tileset.setTiles(tiles);
    }

    private TmxImage parseImage(XmlTag xmlImage) {
        if (null != xmlImage) {
            TmxImage image = new TmxImage();
            image.setSource(xmlImage.attributes.get("source"));
            image.setWidth(getMandatoryIntAttribute(xmlImage, "width"));
            image.setHeight(getMandatoryIntAttribute(xmlImage, "height"));
            return image;
        } else {
            return null;
        }
    }

    private TmxTile parseTile(TmxTileset tileset, XmlTag xmlTile) {
        TmxTile tile = new TmxTile();
        tile.setId(getMandatoryIntAttribute(xmlTile, "id"));
        tile.setProperties(parseProperties(findChild(xmlTile, "properties")));
        XmlTag xmlImage = findChild(xmlTile, "image");
        if (null != xmlImage) {
            TmxImage image = new TmxImage();
            image.setSource(xmlImage.attributes.get("source"));
            image.setWidth(getIntAttribute(xmlImage, "width", tileset.getTilewidth()));
            image.setHeight(getIntAttribute(xmlImage, "height", tileset.getTileheight()));
            TmxFrame frame = new TmxFrame(image, 0, 0, image.getWidth(), image.getHeight());
            tile.setFrame(frame);
        }
        return tile;
    }

    private void parseLayer(XmlTag xmlLayer, TmxLayer layer) {
        layer.setName(xmlLayer.attributes.get("name"));
        layer.setX(getIntAttribute(xmlLayer, "x", 0));
        layer.setY(getIntAttribute(xmlLayer, "x", 0));
        layer.setWidth(getMandatoryIntAttribute(xmlLayer, "width"));
        layer.setHeight(getMandatoryIntAttribute(xmlLayer, "height"));
        layer.setProperties(parseProperties(findChild(xmlLayer, "properties")));
        layer.setData(parseData(findChild(xmlLayer, "data")));
    }

    private TmxData parseData(XmlTag xmlData) {
        TmxData data = new TmxData();
        data.setEncoding(xmlData.attributes.get("encoding"));
        data.setData(xmlData.content);
        return data;
    }

    private static int getIntAttribute(XmlTag xml, String name, int defaultValue) {
        String value = xml.attributes.get(name);
        if (null != value) {
            return Integer.parseInt(value);
        } else {
            return defaultValue;
        }
    }

    private static int getMandatoryIntAttribute(XmlTag xml, String name) {
        String value = xml.attributes.get(name);
        if (null != value) {
            return Integer.parseInt(value);
        } else {
            throw new RuntimeException("Missing attribute " + name + " for tag " + xml.name);
        }
    }
}
