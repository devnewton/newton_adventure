/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.score;

import java.util.TreeMap;

/**
 *
 * @author Borome
 */
public class QuestScore extends TreeMap<String/*level*/, LevelScore> {
    private final String questName;
    
    QuestScore(String questName) {
        this.questName = questName;
    }
    public void setLevelScore(String levelName, LevelScore score) {
        put(levelName, score);
    }
    
    public int computeScore() {
        int result = 0;
        for(LevelScore levelScore : this.values()) {
            result += levelScore.computeScore();
        }
        return result;
    }

    public String getQuestName() {
        return questName;
    }
}
