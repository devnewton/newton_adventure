/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import im.bci.newtonadv.Game;

/**
 *
 * @author devnewton
 */
public class QuestSequence implements Sequence {

    private final Game game;
    List<LevelSequence> levels;
    private Sequence nextSequence;
    private StoryboardSequence completedSequence;

    public QuestSequence(Game game, String questDirectory) {
        this.game = game;
        loadLevels(questDirectory);
    }

    public void start() {
    }

    public void draw() {
    }

    public void stop() {
    }

    public void update() throws TransitionException {
        throw new TransitionException(levels.isEmpty() ? nextSequence : levels.get(0));
    }

    public void processInputs() throws TransitionException {
    }

    private void loadLevels(String questDirectory) {
        levels = new ArrayList();
        File dir = new File(questDirectory + File.separator + "levels");
        LevelSequence lastSequence = null;
        File[] files = dir.listFiles();
        java.util.Arrays.sort(files, new Comparator<File>() {

            public int compare(File a, File b) {
                return a.getName().compareTo(b.getName());
            }
        });
        for (File f : files) {
            if (f.isDirectory()) {
                LevelSequence levelSequence = new LevelSequence(game, f.getAbsolutePath());
                levels.add(levelSequence);
                if (lastSequence != null) {
                    lastSequence.setNextSequence(levelSequence);
                }
                lastSequence = levelSequence;
            }
        }

        if(lastSequence!=null) {
            completedSequence = new StoryboardSequence(game, questDirectory + File.separator + "completed.jpg", null, nextSequence);
            lastSequence.setNextSequence(completedSequence);
        }
    }

    public void setNextSequence(Sequence s) {
        nextSequence = s;
        if (null != completedSequence) {
            completedSequence.setNextSequence(nextSequence);
        }
    }
}
