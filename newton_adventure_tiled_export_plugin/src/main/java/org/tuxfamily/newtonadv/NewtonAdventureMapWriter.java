/*
 * Copyright (c) 2009-2010 devnewton <devnewton@tuxfamily.org>
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
 * * Neither the name of 'devnewton <devnewton@tuxfamily.org>' nor the names of
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
package org.tuxfamily.newtonadv;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.imageio.ImageIO;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.MapWriter;
import tiled.io.PluginLogger;

/**
 * 
 * @author devnewton
 */
public class NewtonAdventureMapWriter implements MapWriter {

    private PluginLogger logger;
    private Properties levelProperties;
    private String saveDir;
    private Set<Tile> usedTiles;

    @Override
    public void writeMap(Map map, String filename) throws Exception {
        usedTiles = new HashSet<Tile>();
        levelProperties = new Properties();
        File saveFile = new File(filename);
        saveFile.delete();
        saveFile.createNewFile();
        saveDir = saveFile.getParentFile().getAbsolutePath();
        levelProperties.putAll(map.getProperties());
        writeMap(map);
        writeTileSet();
        FileOutputStream fileStream = new FileOutputStream(saveFile, false);
        try {
            levelProperties.store(fileStream, "Exported from Tiled");
        } finally {
            fileStream.close();
        }
    }

    @Override
    public void writeMap(Map map, OutputStream out) throws Exception {
        logger.error("writeMap to output stream unsupported");
    }

    @Override
    public void writeTileset(TileSet set, String arg1) throws Exception {
        logger.error("Tilesets are not supported!");
    }

    public void writeMap(Map map) throws Exception {
        for (MapLayer layer : map.getLayerVector()) {
            if (layer instanceof TileLayer) {
                writeTileLayer((TileLayer) layer);
            }
        }
    }

    @Override
    public void writeTileset(TileSet set, OutputStream arg1) throws Exception {
        logger.error("Tilesets are not supported!");
    }

    @Override
    public String getFilter() throws Exception {
        return "*.properties";
    }

    @Override
    public String getName() {
        return "Newton Adventure export plugin";
    }

    @Override
    public String getDescription() {
        return "Export map for Newton Adventure game";
    }

    @Override
    public String getPluginPackage() {
        return "Newton Adventure export plugin";
    }

    @Override
    public void setLogger(PluginLogger logger) {
        this.logger = logger;
    }

    @Override
    public boolean accept(File arg0) {
        return true;
    }

    private void writeTileLayer(TileLayer layer) throws FileNotFoundException, IOException {
        final String layerFilename = layer.getName() + ".txt";
        levelProperties.setProperty("newton_adventure.map", layerFilename);
        File file = new File(saveDir + File.separator + layerFilename);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        try {
            for (int y = 0; y < layer.getHeight(); y++) {
                for (int x = 0; x < layer.getWidth(); x++) {
                    Tile tile = ((TileLayer) layer).getTileAt(x, y);
                    String c;
                    if (tile != null) {
                        usedTiles.add(tile);
                        c = tile.getProperties().getProperty("newton_adventure.char");
                        if (c == null) {
                            throw new RuntimeException("newton_adventure.char not defined for tile " + tile.getId());
                        }

                    } else {
                        c = " ";
                    }
                    out.write(c);
                }
                out.write("\n");
            }
        } finally {
            out.close();
        }
    }

    public static BufferedImage toBufferedImage(final Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        if (image instanceof VolatileImage) {
            return ((VolatileImage) image).getSnapshot();
        }
        final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = buffImg.createGraphics();
        g2.drawImage(image, null, null);
        g2.dispose();
        return buffImg;
    }

    private void writeTileSet() throws IOException {
        for (Tile tile : usedTiles) {
            if (tile.getProperties().getProperty("newton_adventure.isPlatform") != null) {
                String c = tile.getProperties().getProperty("newton_adventure.char");
                String name = "tile_" + tile.getGid();
                String filename = name + ".png";
                String absoluteFilename = saveDir + File.separator + filename;
                BufferedImage image = toBufferedImage(tile.getImage());
                ImageIO.write(image, "png", new File(absoluteFilename));
                levelProperties.setProperty(name + ".char", c);
                levelProperties.setProperty(name + ".texture.filename", filename);
            }
        }
    }
}
