package im.bci.newtonadv.platform.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.res.AssetManager;

import im.bci.newtonadv.platform.interfaces.IGameData;

public class AndroidGameData implements IGameData {

	private final AssetManager assets;

	public AndroidGameData(AssetManager assets) {
		this.assets = assets;
	}

	@Override
	public List<String> listQuests() {
		try {
			ArrayList<String> quests = new ArrayList<String>(Arrays.asList(assets.list("quests")));
			quests.remove("bonus");
			return quests;
		} catch (IOException e) {
			return Collections.emptyList();
		}
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
		try {
			return Arrays
					.asList(assets.list("quests/" + questName + "/levels"));
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	@Override
	public InputStream openLevelTmx(String questName, String levelName)
			throws Exception {
		String path = "quests/" + questName + "/levels/" + levelName;
		for (String file : assets.list(path)) {
			if (file.endsWith(".tmx"))
				return assets.open(path + "/" + file);
		}
		throw new RuntimeException("no tmx file found in level path " + path);
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
}
