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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;

/**
 *
 * @author devnewton
 */
class FileGameData implements IGameData {

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

    @Override
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
        Properties questsProperties = RuntimeUtils.loadPropertiesFromFile(getVirtualFile("quests/" + questName + "/quest.properties"));
        return RuntimeUtils.getPropertyAsList(questsProperties, ("locked.by"));
    }

    @Override
    public TmxMap openLevelTmx(String questName, String levelName) throws Exception {
        File file = getVirtualFile("quests/" + questName + "/levels/" + levelName + "/" + levelName + ".tmx");
        final File mapParentDir = file.getParentFile().getCanonicalFile();
        TmxLoader loader = new TmxLoader() {
            @Override
            protected String openExternalTileset(String source) {
                return loadText(new File(mapParentDir, source));
            }
        };
        TmxMap map = loader.load(loadText(file));
        for (TmxTileset tileset : map.getTilesets()) {
            File tilesetParentDir;
            if (null != tileset.getSource()) {
                tilesetParentDir = new File(mapParentDir, tileset.getSource()).getParentFile().getCanonicalFile();
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
        return map;
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

    private List<String> getConfiguredQuests() {
        Properties questsProperties = RuntimeUtils.loadPropertiesFromFile(getVirtualFile("quests/quests.properties"));
        return RuntimeUtils.getPropertyAsList(questsProperties, ("quests"));
    }

    private List<String> getConfiguredLevels(String questName) {
        Properties questsProperties = RuntimeUtils.loadPropertiesFromFile(getVirtualFile("quests/" + questName + "/quest.properties"));
        return RuntimeUtils.getPropertyAsList(questsProperties, ("levels"));
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
