package im.bci.newtonadv.platform.interfaces;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public interface IGameData {

    List<String> listQuests();

    String getFile(String file);

    String getQuestFile(String questName, String file);

    List<String> listQuestLevels(String questName);

    InputStream openLevelTmx(String questName, String levelName)  throws Exception;

	String getLevelFilePath(String questName, String levelName, String filename);

	InputStream openFile(String path) throws FileNotFoundException;
}
