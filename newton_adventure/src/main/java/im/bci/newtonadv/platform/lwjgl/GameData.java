/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IGameData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Borome
 */
class GameData implements IGameData {

    private final String dataDir;

    public GameData(Properties config) {
        dataDir = PlatformFactory.getDataDir() + "/";
    }

    @Override
	public List<String> listQuests() {
        File dir = new File(dataDir + "quests");
        File[] files = dir.listFiles();
        java.util.Arrays.sort(files, new Comparator<File>() {

            @Override
			public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        ArrayList<String> questNames = new ArrayList<String>();
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.isDirectory()) {
                questNames.add(f.getName());
            }
        }
        questNames.remove("bonus");
        return questNames;
    }

    @Override
	public String getFile(String name) {
        return dataDir + name;
    }

    @Override
	public List<String> listQuestLevels(String questName) {
        List<String> levelNames = new ArrayList<String>();
        File dir = new File(dataDir + "quests/" + questName + "/levels");
        File[] files = dir.listFiles();
        java.util.Arrays.sort(files, new Comparator<File>() {

            @Override
			public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        for (File f : files) {
            if (f.isDirectory()) {
                levelNames.add(f.getName());
            }
        }
        return levelNames;
    }

    @Override
    public String getQuestFile(String questName, String file) {
        return dataDir + "quests/" + questName + "/" + file;
    }

    @Override
    public InputStream openLevelTmx(String questName, String levelName) throws FileNotFoundException {
        String path = dataDir + "quests/" + questName + "/levels/" + levelName;
        File levelDir = new File(path);
        File[] tmxFiles = levelDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".tmx");
            }
        });
        if (tmxFiles.length == 0) {
            throw new FileNotFoundException("cannot find *.tmx file in level path: " + path);
        }
        return new FileInputStream(tmxFiles[0]);
    }

    @Override
	public String getLevelFile(String questName, String levelName, String filename) {
        String path = dataDir + "quests/" + questName + "/levels/" + levelName + "/" + filename;
        if ((new File(path)).exists()) {
            return path;
        } else {
            return dataDir + "default_level_data/" + filename;
        }
    }
}
