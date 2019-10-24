/*
 * Copyright (c) 2012 devnewton <devnewton@bci.im>
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
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
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
package im.bci.newtonadv.platform.lwjgl;

import com.google.gson.Gson;

import im.bci.jnuit.lwjgl.assets.VirtualFileSystem;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.tmxloader.TmxLoader;
import im.bci.tmxloader.TmxMap;
import im.bci.tmxloader.TmxTileset;
import im.bci.tmxloader.TmxTile;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 *
 * @author devnewton
 */
public class FileGameData implements IGameData {

    private VirtualFileSystem vfs;

    FileGameData(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    @Override
    public List<String> listQuests() {
        String file = "quests/quests.json";
        try {
            InputStream is = vfs.open(file);
            try {
                InputStreamReader reader = new InputStreamReader(is);
                QuestsConfig quests = new Gson().fromJson(reader, QuestsConfig.class);
                return quests.getQuests();
            } finally {
                is.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load " + file, ex);
        }
    }

    @Override
    public List<String> listQuestLevels(String questName) {
        final String file = "quests/" + questName + "/quest.json";
        try {
            InputStream is = vfs.open(file);
            try {
                InputStreamReader reader = new InputStreamReader(is);
                QuestConfig levels = new Gson().fromJson(reader, QuestConfig.class);
                return levels.getLevels();
            } finally {
                is.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load " + file, ex);
        }
    }

    public InputStream openFile(String path) throws IOException {
        return vfs.open(path);
    }

    @Override
    public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
        try {
            InputStream is = vfs.open("quests/" + questName + "/quest.json");
            try {
                InputStreamReader reader = new InputStreamReader(is);
                QuestConfig conf = new Gson().fromJson(reader, QuestConfig.class);
                return conf.getLockedBy();
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public TmxMap openLevelTmx(String questName, String levelName) {
        try {
            final String tmxDir = "quests/" + questName + "/levels/" + levelName;
            final String tsxDir = "quests/" + questName;
            final String tmxFile = tmxDir + "/" + levelName + ".tmx";
            TmxLoader loader = new TmxLoader();
            TmxMap map = new TmxMap();
            loader.parseTmx(map, loadText(tmxFile));
            for (TmxTileset tileset : map.getTilesets()) {
                if (null != tileset.getSource()) {
                    //TODO real path resolve...
                    final String tsxFile = tsxDir + "/" + tileset.getSource().replaceAll("../", "");
                    loader.parseTsx(map, tileset, loadText(tsxFile));
                    for(TmxTile tile : tileset.getTiles()) {
                        tile.getFrame().getImage().setSource(tsxDir + "/" + tile.getFrame().getImage().getSource());
                    }
                }
            }
            loader.decode(map);
            return map;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load level " + questName + "/" + levelName, ex);
        }
    }

    public String loadText(String f) {
        try {
            InputStream is = vfs.open(f);
            try {
                Scanner s = new Scanner(is, "UTF-8");
                s.useDelimiter("\\Z");
                String str = s.next();
                s.close();
                return str;
            } finally {
                is.close();
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Cannot find text file: " + f, ex);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load text file: " + f, ex);
        }
    }

    public static class QuestsConfig {

        public List<String> quests = Collections.emptyList();

        public List<String> getQuests() {
            return quests;
        }

        public void setQuests(List<String> quests) {
            this.quests = quests;
        }

    }

    public static class QuestConfig {

        public List<String> levels = Collections.emptyList();
        public List<String> lockedBy = Collections.emptyList();

        public List<String> getLockedBy() {
            return lockedBy;
        }

        public void setLockedBy(List<String> lockedBy) {
            this.lockedBy = lockedBy;
        }

        public List<String> getLevels() {
            return levels;
        }

        public void setLevels(List<String> levels) {
            this.levels = levels;
        }

    }

    public BufferedImage openImage(String file) throws IOException {
        InputStream is = openFile(getFile(file));
        try {
            return ImageIO.read(is);
        } finally {
            is.close();
        }
    }

    @Override
    public String getFile(String name) {
        return name;
    }

    @Override
    public String getQuestFile(String questName, String file) {
        return "quests/" + questName + "/" + file;
    }

    @Override
    public String getLevelFilePath(String questName, String levelName, String filename) {
        String file = "quests/" + questName + "/levels/" + levelName + "/" + filename;
        if (fileExists(file)) {
            return file;
        }
        file = "quests/" + questName + "/" + filename;
        if (fileExists(file)) {
            return file;
        }
        return "default_level_data/" + filename;
    }

    @Override
    public boolean fileExists(String path) {
        try {
            InputStream s = openFile(path);
            if (s != null) {
                s.close();
                return true;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return false;
    }
}
