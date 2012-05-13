package im.bci.newtonadv.platform.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.res.AssetManager;

import im.bci.newtonadv.platform.interfaces.IGameData;

public class AndroidGameData implements IGameData {

	private final AssetManager assets;
	private static final List<String> quests;
	private static final HashMap<String, List<String>> questLevels;

	static {
		quests = Arrays.asList("jungle", "vatican", "arctic", "volcano",
				"egypt");
		questLevels = new HashMap<String, List<String>>();
		questLevels.put("jungle", Arrays.asList("level0", "level0.5", "level1",
				"level2", "level3", "level4"));
		questLevels.put("vatican", Arrays.asList("level0", "level0.5",
				"level1", "level2", "level3", "level4"));
		questLevels.put("arctic", Arrays.asList("level0", "level0.5", "level1",
				"level2", "level3", "level4"));
		questLevels.put("egypt", Arrays.asList("level0", "level1", "level2",
				"level3", "level4", "level5"));
		questLevels.put("volcano", Arrays.asList("level0", "level0.5",
				"level1", "level2", "level3", "level4"));
		questLevels.put("bonus", Arrays.asList("bonus_level1", "bonus_level2",
				"bonus_level3", "bonus_level4", "bonus_level5"));

	}

	public AndroidGameData(AssetManager assets) {
		this.assets = assets;
	}

	@Override
	public List<String> listQuests() {
		return quests;
	}

	@Override
	public String getFile(String file) {
		return file;
	}

	@Override
	public String getQuestFile(String questName, String file) {
		return "quests/" + questName + "/" + file;
	}

	@Override
	public List<String> listQuestLevels(String questName) {
		return questLevels.get(questName);
	}

	@Override
	public InputStream openLevelTmx(String questName, String levelName)
			throws Exception {
		String path = "quests/" + questName + "/levels/" + levelName + "/"
				+ levelName + ".tmx";
		return assets.open(path);
	}

	@Override
	public String getLevelFilePath(String questName, String levelName,
			String filename) {
		String path = "quests/" + questName + "/levels/" + levelName + "/"
				+ filename;
		try {
			InputStream s = assets.open(path);
			s.close();
			return path;
		} catch (IOException e) {
		}
		return "default_level_data/" + filename;
	}

	@Override
	public InputStream openFile(String path) throws IOException {
		return assets.open(path);
	}

	@Override
	public boolean fileExists(String path) {
		try {
			assets.open(path);
			assets.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
