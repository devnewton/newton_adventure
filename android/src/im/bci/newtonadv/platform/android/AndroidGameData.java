package im.bci.newtonadv.platform.android;

import java.io.IOException;
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
			return Arrays.asList(assets.list("quests"));
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}
	
	@Override
    public String getQuestOffButton(String questName) {
        return "quests/" + questName + "/bt-quest-off.jpg";
    }

	@Override
    public String getQuestOnButton(String questName) {
    	return "quests/" + questName + "/bt-quest-on.jpg";
    }

}
