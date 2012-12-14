/*
 * Copyright (c) 2012 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.lwjgl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author devnewton
 */
class EmbeddedGameData extends AbstractGameData {

    private List<String> quests;
    private HashMap<String, List<String>> questLevels;
    private HashMap<String, List<String>> questsToCompleteToUnlockQuests;
    private String version = "normal";

    public EmbeddedGameData() {
        super("");
        loadVersionInfo();
        setup();
    }

    private void setup() {
        boolean isDeluxe = "deluxe".equals(version);
        quests = new ArrayList<String>(Arrays.asList("jungle", "vatican", "arctic", "volcano",
                "egypt"));
        if (isDeluxe) {
            quests.addAll(Arrays.asList("bridge", "lab", "prison"));
        }
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
        if (isDeluxe) {
            questLevels.put("bridge", Arrays.asList("level0", "level1", "level2", "level3", "level4", "level5"));
            questLevels.put("lab", Arrays.asList("level0", "level1", "level2",
                    "level3", "level4", "level5"));
            questLevels.put("prison", Arrays.asList("level0", "level1", "level2", "boss", "credits"));
        }
        questLevels.put("bonus", Arrays.asList("bonus_level1", "bonus_level2",
                "bonus_level3", "bonus_level4", "bonus_level5"));

        questsToCompleteToUnlockQuests = new HashMap<String, List<String>>();
        if (isDeluxe) {
            questsToCompleteToUnlockQuests.put("prison", Arrays.asList("jungle",
                    "vatican", "arctic", "volcano", "egypt", "bridge", "lab"));
        }
    }

    @Override
    public List<String> listQuests() {
        return quests;
    }

    @Override
    public List<String> listQuestLevels(String questName) {
        return questLevels.get(questName);
    }

    @Override
    public InputStream openFile(String path) throws FileNotFoundException {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
        List<String> questsToComplete = questsToCompleteToUnlockQuests.get(questName);
        if (null == questsToComplete) {
            questsToComplete = Collections.emptyList();
        }
        return questsToComplete;
    }

    private void loadVersionInfo() {
        try {
            URL configFilePath = getClass().getClassLoader().getResource(
                    "version.properties");
            InputStream f = configFilePath.openStream();
            try {
                Properties prop = new Properties();
                prop.load(f);
                version = prop.getProperty("newton.adventure.version", version);
            } finally {
                f.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(EmbeddedGameData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
