/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IGameData;
import java.io.File;
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
        dataDir = config.getProperty("data.dir", "data" + File.separator);
    }

    public List<String> listQuests() {
        File dir = new File(dataDir + "quests");
        File[] files = dir.listFiles();
        java.util.Arrays.sort(files, new Comparator<File>() {

            public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        ArrayList<String> questNames = new ArrayList<String>();
        for (int i=0; i<files.length; ++i) {
            File f = files[i];
            if (f.isDirectory()) {
                questNames.add(f.getName());
            }
        }
        return questNames;
    }

    public String getQuestOffButton(String questName) {
        return dataDir + "quests" + File.separator + questName + File.separator + "bt-quest-off.jpg";
    }

    public String getQuestOnButton(String questName) {
        return dataDir + "quests" + File.separator + questName + File.separator + "bt-quest-on.jpg";
    }
    
}
