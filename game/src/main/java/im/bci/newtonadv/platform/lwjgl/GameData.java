/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IGameData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Borome
 */
class GameData implements IGameData {

	private static final String dataDir = "";
	private static final List<String> quests;
	private static final HashMap<String, List<String>> questLevels;

	static {
		quests = Arrays.asList("jungle", "vatican", "arctic", "hades", "egypt");
		questLevels = new HashMap<String, List<String>>();
		questLevels.put("jungle", Arrays.asList("jungle_level0",
				"jungle_level1", "jungle_level2", "jungle_level3",
				"jungle_level4"));
		questLevels.put("vatican", Arrays.asList("vatican_level0",
				"vatican_level1", "vatican_level2", "vatican_level3",
				"vatican_level4"));
		questLevels.put("arctic", Arrays.asList("artic_level0", "artic_level1",
				"artic_level2", "artic_level3", "artic_level4"));
		questLevels.put("egypt", Arrays.asList("level0", "level1", "level2",
				"level3", "level4", "level5"));
		questLevels.put("hades", Arrays.asList("hades_level0",
				"hades_level1"/*, "hades_level2", "hades_level3",
				"hades_level4"*/));
		questLevels.put("bonus", Arrays.asList("bonus_level1", "bonus_level2",
				"bonus_level3", "bonus_level4", "bonus_level5"));
		
	}

	public GameData(Properties config) {
	}

	@Override
	public List<String> listQuests() {
		return quests;
	}

	@Override
	public String getFile(String name) {
		return dataDir + name;
	}

	@Override
	public List<String> listQuestLevels(String questName) {
		return questLevels.get(questName);
	}

	@Override
	public String getQuestFile(String questName, String file) {
		return dataDir + "quests/" + questName + "/" + file;
	}

	@Override
	public InputStream openLevelTmx(String questName, String levelName)
			throws FileNotFoundException {
		String path = dataDir + "quests/" + questName + "/levels/" + levelName
				+ "/" + levelName + ".tmx";
		return openFile(path);
	}

	@Override
	public InputStream openFile(String path) throws FileNotFoundException {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	@Override
	public String getLevelFilePath(String questName, String levelName,
			String filename) {
		String path = dataDir + "quests/" + questName + "/levels/" + levelName
				+ "/" + filename;
		try {
			InputStream s = openFile(path);
			if (s != null) {
				s.close();
				return path;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return dataDir + "default_level_data/" + filename;
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
