package im.bci.newtonadv.platform.interfaces;

import java.io.InputStream;
import java.util.List;

public interface IGameData {

    List<String> listQuests();

    public String getFile(String file);

    public String getQuestFile(String questName, String file);

    public List<String> listQuestLevels(String questName);

    public InputStream openLevelTmx(String questName, String levelName)  throws Exception;

    public String getLevelFile(String questName, String levelName, String filename);
}
