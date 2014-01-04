/*
 * Copyright (c) 2013 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.playn.core;

import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.tmxloader.TmxLoader;
import im.bci.tmxloader.TmxMap;
import im.bci.tmxloader.TmxTile;
import im.bci.tmxloader.TmxTileset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import playn.core.Json;
import playn.core.PlayN;
import playn.core.util.Callback;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynGameData implements IGameData {

    private Set<String> assetsList = Collections.emptySet();
    private final QuestsConfig questsConfig = new QuestsConfig();
    private final RealWatchedAssets assets;
    private static final Logger LOGGER = Logger.getLogger(PlaynGameData.class.getName());

    public static class QuestsConfig {

        Map<String, QuestConfig> quests = new HashMap<String, QuestConfig>();
        List<String> visibleQuests = new ArrayList<String>();
        private boolean ready;

        public boolean isReady() {
            return ready && areQuestReady();
        }

        private boolean areQuestReady() {
            for (QuestConfig conf : quests.values()) {
                if (!conf.isReady()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class QuestConfig {

        List<String> levels = new ArrayList<String>();
        List<String> lockedBy = new ArrayList<String>();
        boolean ready;

        public boolean isReady() {
            return ready;
        }
    }

    public PlaynGameData(RealWatchedAssets assets) {
        this.assets = assets;
        initAssetsList();
    }

    private void initQuests() {
        assets.getText("quests/quests.json", new Callback<String>() {

            @Override
            public void onSuccess(String result) {
                for (String questName : PlayN.json().parse(result).getArray("quests", String.class)) {
                    questsConfig.visibleQuests.add(questName);
                }
                questsConfig.ready = true;
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load quests.json", cause);

            }
        });
        for (final String file : assetsList) {
            if (file.endsWith("quest.json")) {
                final QuestConfig questConfig = new QuestConfig();
                String questName = file.replace("/quest.json", "");
                questName = questName.substring(questName.lastIndexOf("/") + 1, questName.length());
                questsConfig.quests.put(questName, questConfig);
                assets.getText(file, new Callback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        final Json.Object json = PlayN.json().parse(result);
                        for (String levelName : json.getArray("levels", String.class)) {
                            questConfig.levels.add(levelName);
                        }
                       /* if (json.containsKey("lockedBy")) {
                            for (String lockQuestName : json.getArray("lockedBy", String.class)) {
                                questConfig.lockedBy.add(lockQuestName);
                            }
                        }*/
                        questConfig.ready = true;
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        LOGGER.log(Level.SEVERE, "Cannot load " + file, cause);

                    }
                });
            }
        }
    }

    private void initAssetsList() {
        assets.getText("assets.txt", new Callback<String>() {

            @Override
            public void onSuccess(String result) {
                HashSet<String> newAssets = new HashSet<String>();
                for (String s : result.split("[\r\n]+")) {
                    if (!s.isEmpty()) {
                        newAssets.add(s);
                    }
                }
                assetsList = newAssets;

                /* Charge tout comme un bourrin...
                 for(String a : assetsList) {
                 if(a.endsWith(".png")) {
                 assets.getImage(a);
                 }
                 }*/
                initQuests();
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load assets.txt", cause);
            }
        });
    }

    @Override
    public List<String> listQuests() {
        return questsConfig.visibleQuests;
    }

    @Override
    public String getFile(String filepath) {
        return filepath;
    }

    @Override
    public String getQuestFile(String questName, String file) {
        return "quests/" + questName + "/" + file;
    }

    @Override
    public List<String> listQuestLevels(String questName) {
        return questsConfig.quests.get(questName).levels;
    }

    @Override
    public TmxMap openLevelTmx(String questName, String levelName) {
        final TmxMap map = new TmxMap();
        final TmxLoader loader = new TmxLoader();
        final String tmxDir = "quests/" + questName + "/levels/" + levelName;
        final String tsxDir = "quests/" + questName;
        final String tmxFile = tmxDir + "/" + levelName + ".tmx";
        assets.getText(tmxFile, new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                loader.parseTmx(map, result);
                loadNextTileset();
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load " + tmxFile, cause);
            }

            private void loadNextTileset() {
                for (final TmxTileset tileset : map.getTilesets()) {
                    if (!tileset.isReady() && null != tileset.getSource()) {
                        //TODO real path resolve...
                        final String tsxFile = tsxDir + "/" + tileset.getSource().replaceAll("../", "");
                        assets.getText(tsxFile, new Callback<String>() {

                            @Override
                            public void onSuccess(String result) {
                                loader.parseTsx(map, tileset, result);
                                for (TmxTile tile : tileset.getTiles()) {
                                    tile.getFrame().getImage().setSource(tsxDir + "/" + tile.getFrame().getImage().getSource());
                                }
                                loadNextTileset();
                            }

                            @Override
                            public void onFailure(Throwable cause) {
                                LOGGER.log(Level.SEVERE, "Cannot load " + tsxFile, cause);
                            }
                        });
                        return;
                    }
                }
                loader.decode(map);
            }
        });
        return map;
    }

    @Override
    public String getLevelFilePath(String questName, String levelName, String filename) {
        String file = "quests/" + questName + "/levels/" + levelName + "/" + filename;
        if (fileExists(file)) {
            return file;
        }
        file = "quests/" + questName + "/" + filename;
        if (fileExists(file)) {
            return file;
        }
        return "default_level_data/" + filename;
    }

    @Override
    public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
        return questsConfig.quests.get(questName).lockedBy;
    }

    @Override
    public boolean fileExists(String path) {
        return assetsList.contains(path);
    }

    public boolean isReady() {
        return assets.isDone() && !assetsList.isEmpty() && questsConfig.isReady();
    }

}
