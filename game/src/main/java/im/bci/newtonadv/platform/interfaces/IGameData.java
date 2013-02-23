package im.bci.newtonadv.platform.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IGameData {

    List<String> listQuests();

    String getFile(String file);

    String getQuestFile(String questName, String file);

    List<String> listQuestLevels(String questName);

    tiled.core.Map openLevelTmx(String questName, String levelName) throws Exception;

	String getLevelFilePath(String questName, String levelName, String filename);

	InputStream openFile(String path) throws IOException;

	boolean fileExists(String minimapPath);

	InputStream openLevelNal(String questName, String levelName) throws Exception;

	List<String> listQuestsToCompleteToUnlockQuest(String questName);
}
