package im.bci.newtonadv.platform.interfaces;

import java.util.List;

public interface IGameData {

	List<String> listQuests();

    public String getQuestOffButton(String questName);

    public String getQuestOnButton(String questName);

}
