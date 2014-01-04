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
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.tmxloader.TmxLoader;
import im.bci.tmxloader.TmxMap;
import im.bci.tmxloader.TmxTileset;
import im.bci.tmxloader.TmxTile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;

/**
 *
 * @author devnewton
 */
public class FileGameData implements IGameData {

    private List<File> dataDirs;

    public void setDataDirs(List<File> dataDirs) {
        ListIterator<File> it = dataDirs.listIterator();
        File previousDir = null;
        while (it.hasNext()) {
            File currentDir = it.next();
            if (currentDir.equals(previousDir)) {
                it.remove();
            }
            previousDir = currentDir;
        }
        this.dataDirs = dataDirs;
    }

    public List<File> getDataDirs() {
        return dataDirs;
    }

    @Override
    public List<String> listQuests() {
        List<String> quests = listSubDirectories("quests", getConfiguredQuests());
        return quests;
    }

    @Override
    public List<String> listQuestLevels(String questName) {
        return listSubDirectories("quests/" + questName + "/levels", getConfiguredLevels(questName));
    }

    public InputStream openFile(String path) throws IOException {
        File f = new File(path);
        if (f.exists()) {
            return new FileInputStream(f);
        } else {
            return null;
        }
    }

    private List<String> listSubDirectories(String path, List<String> order) {
        TreeSet<String> subdirs = new TreeSet<>();
        for (File dataDir : dataDirs) {
            File dir = new File(dataDir, path);
            if (dir.exists()) {
                for (File f : dir.listFiles()) {
                    if (f.isDirectory()) {
                        subdirs.add(f.getName());
                    }
                }
            }
        }
        subdirs.retainAll(order);
        ArrayList<String> result = new ArrayList<>(subdirs);
        reorderList(result, order);
        return result;
    }

    @Override
    public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
        try (InputStream is = new FileInputStream(getVirtualFile("quests/" + questName + "/quest.json")); InputStreamReader reader = new InputStreamReader(is)) {
            QuestConfig conf = new Gson().fromJson(reader, QuestConfig.class);
            return conf.getLockedBy();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public TmxMap openLevelTmx(String questName, String levelName) {

        File file = getVirtualFile("quests/" + questName + "/levels/" + levelName + "/" + levelName + ".tmx");
        try {
            final File mapParentDir = file.getParentFile().getCanonicalFile();
            TmxLoader loader = new TmxLoader();
            TmxMap map = new TmxMap();
            loader.parseTmx(map, loadText(file));
            for (TmxTileset tileset : map.getTilesets()) {
                File tilesetParentDir;
                if (null != tileset.getSource()) {
                    final File tilesetFile = new File(mapParentDir, tileset.getSource());
                    tilesetParentDir = tilesetFile.getParentFile().getCanonicalFile();
                    loader.parseTsx(map, tileset, loadText(tilesetFile));
                } else {
                    tilesetParentDir = mapParentDir;
                }
                if (null != tileset.getImage()) {
                    tileset.getImage().setSource(new File(tilesetParentDir, tileset.getImage().getSource()).getCanonicalPath());
                }
                for (TmxTile tile : tileset.getTiles()) {
                    tile.getFrame().getImage().setSource(new File(tilesetParentDir, tile.getFrame().getImage().getSource()).getCanonicalPath());
                }
            }
            loader.decode(map);
            return map;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load " + file, ex);
        }
    }

    public String loadText(File f) {
        try (InputStream is = new FileInputStream(f); Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\Z")) {
            return s.next();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Cannot find text file: " + f, ex);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load text file: " + f, ex);
        }
    }

    private static void reorderList(List<String> list, final List<String> order) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = order.indexOf(o1);
                int i2 = order.indexOf(o2);
                if (i1 > i2) {
                    return 1;
                } else if (i1 < i2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
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

    private List<String> getConfiguredQuests() {
        final File file = getVirtualFile("quests/quests.json");
        try (InputStream is = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(is)) {
            QuestsConfig quests = new Gson().fromJson(reader, QuestsConfig.class);
            return quests.getQuests();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load " + file, ex);
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

    private List<String> getConfiguredLevels(String questName) {
        final File file = getVirtualFile("quests/" + questName + "/quest.json");
        try (InputStream is = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(is)) {
            QuestConfig levels = new Gson().fromJson(reader, QuestConfig.class);
            return levels.getLevels();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load " + file, ex);
        }
    }

    public BufferedImage openImage(String file) throws IOException {
        try (InputStream is = openFile(getFile(file))) {
            return ImageIO.read(is);
        }
    }

    @Override
    public String getFile(String name) {
        return getVirtualFile(name).getAbsolutePath();
    }

    @Override
    public String getQuestFile(String questName, String file) {
        return getVirtualFile("quests/" + questName + "/" + file).getAbsolutePath();
    }

    @Override
    public String getLevelFilePath(String questName, String levelName, String filename) {
        File file = getVirtualFile("quests/" + questName + "/levels/" + levelName + "/" + filename);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        file = getVirtualFile("quests/" + questName + "/" + filename);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return getVirtualFile("default_level_data/" + filename).getAbsolutePath();
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

    private File getVirtualFile(String path) {
        for (File dir : dataDirs) {
            File f = new File(dir, path);
            if (f.exists()) {
                return f;
            }
        }
        return new File(path);
    }
}
