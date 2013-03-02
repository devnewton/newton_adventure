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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import tiled.core.Map;
import tiled.io.TMXMapReader;

/**
 *
 * @author devnewton
 */
class FileGameData extends AbstractGameData {

    private String version = "normal";

    @Override
    public void setDataDir(String dataDir) {
        super.setDataDir(addSeparator(dataDir));
        loadVersionInfo();
    }

    private static String addSeparator(String dataDir) {
        try {
            File f = new File(dataDir);
            return f.getCanonicalPath() + File.separator;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> listQuests() {
        List<String> quests = listSubDirectories(dataDir + "quests", getConfiguredQuests());
        return quests;
    }

    @Override
    public List<String> listQuestLevels(String questName) {
        return listSubDirectories(dataDir + "quests/" + questName + "/levels", getConfiguredLevels(questName));
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

    private static List<String> listSubDirectories(String path, List<String> order) {
        File dir = new File(path);
        ArrayList<String> subdirs = new ArrayList<String>();
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    subdirs.add(f.getName());
                }
            }
        }
        subdirs.retainAll(order);
        reorderList(subdirs, order);
        return subdirs;
    }

    @Override
    public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
        Properties questsProperties = RuntimeUtils.loadPropertiesFromFile(dataDir + "quests/" + questName + "/quest.properties");
        return RuntimeUtils.getPropertyAsList(questsProperties,("locked.by"));
    }

    @Override
    public Map openLevelTmx(String questName, String levelName) throws Exception {
        TMXMapReader mapReader = new TMXMapReader();
        File file = new File(dataDir + "quests/" + questName + "/levels/" + levelName + "/" + levelName + ".tmx"); 
        return mapReader.readMap(file.getCanonicalPath());
    }

    private static void reorderList(List<String> list, final List<String> order) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = order.indexOf(o1);
                int i2 = order.indexOf(o2);
                if(i1 > i2) {
                    return 1;
                } else if(i1 < i2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private List<String> getConfiguredQuests() {
        Properties questsProperties = RuntimeUtils.loadPropertiesFromFile(dataDir + "quests/quests.properties");
        return RuntimeUtils.getPropertyAsList(questsProperties,("quests"));
    }

    private List<String> getConfiguredLevels(String questName) {
        Properties questsProperties = RuntimeUtils.loadPropertiesFromFile(dataDir + "quests/" + questName + "/quest.properties");
        return RuntimeUtils.getPropertyAsList(questsProperties,("levels"));
    }

    private boolean isDeluxe() {
        return "deluxe".equals(version);
    }

    private void loadVersionInfo() {
        try {
            URL configFilePath = getClass().getClassLoader().getResource(
                    "version.properties");
            InputStream f = configFilePath.openStream();
            try {
                Properties prop = new Properties();
                prop.load(f);
                version = prop.getProperty("newton.adventure.version", version);
            } finally {
                f.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
