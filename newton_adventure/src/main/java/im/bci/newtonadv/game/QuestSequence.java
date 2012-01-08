/*
 * Copyright (c) 2009-2010 devnewton <devnewton@bci.im>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'devnewton <devnewton@bci.im>' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    private ScoreSequence scoreSequence;

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
        String questName = dir.getParentFile().getName();
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
            scoreSequence = new ScoreSequence(game, questName, nextSequence);
            StoryboardSequence completedSequence = new StoryboardSequence(game, questDirectory + File.separator + "completed.jpg", "data" + File.separator + "story_time.ogg", scoreSequence);
            lastSequence.setNextSequence(completedSequence);
        }
    }

    public void setNextSequence(Sequence s) {
        nextSequence = s;
        if (null != scoreSequence) {
            scoreSequence.setNextSequence(nextSequence);
        }
    }
}
