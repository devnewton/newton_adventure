/*
 * Copyright (c) 2019 devnewton <devnewton@bci.im>
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
package im.bci.newtonadv.platform.teavm;

import im.bci.jnuit.animation.IAnimationCollection;
import im.bci.jnuit.animation.ITexture;
import im.bci.jnuit.teavm.JsonArray;
import im.bci.jnuit.teavm.JsonMap;
import im.bci.jnuit.teavm.assets.TeavmAssets;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.tmxloader.TmxLoader;
import im.bci.tmxloader.TmxMap;
import im.bci.tmxloader.TmxTile;
import im.bci.tmxloader.TmxTileset;
import java.util.ArrayList;
import java.util.List;
import org.teavm.jso.core.JSString;
import org.teavm.jso.json.JSON;

/**
 *
 * @author devnewton
 */
class TeavmGameData implements IGameData {

    private final TeavmAssets assets;

    TeavmGameData(TeavmAssets assets) {
        this.assets = assets;
    }

    @Override
    public List<String> listQuests() {
        final JsonMap json = JSON.parse(assets.getText("quests/quests.json")).cast();
        final JsonArray jsonQuests = json.get("quests").cast();
        ArrayList<String> results = new ArrayList<>();
        for (int q = 0, nq = jsonQuests.getLength(); q < nq; ++q) {
            JSString quest = jsonQuests.get(q).cast();
            results.add(quest.stringValue());
        }
        return results;
    }

    @Override
    public String getFile(String name) {
        return name;
    }

    @Override
    public String getQuestFile(String questName, String file) {
        return "quests/" + questName + "/" + file;
    }

    @Override
    public List<String> listQuestLevels(String questName) {
        final JsonMap json = JSON.parse(assets.getText("quests/" + questName + "/quest.json")).cast();
        final JsonArray jsonLevels = json.get("levels").cast();
        ArrayList<String> results = new ArrayList<>();
        for (int l = 0, nl = jsonLevels.getLength(); l < nl; ++l) {
            JSString level = jsonLevels.get(l).cast();
            results.add(level.stringValue());
        }
        return results;
    }

    @Override
    public TmxMap openLevelTmx(String questName, String levelName) {
        try {
            final String tmxDir = "quests/" + questName + "/levels/" + levelName;
            final String tsxDir = "quests/" + questName;
            final String tmxFile = tmxDir + "/" + levelName + ".tmx";
            TmxLoader loader = new TmxLoader();
            TmxMap map = new TmxMap();
            loader.parseTmx(map, assets.getText(tmxFile));
            for (TmxTileset tileset : map.getTilesets()) {
                if (null != tileset.getSource()) {
                    //TODO real path resolve...
                    final String tsxFile = tsxDir + "/" + tileset.getSource().replaceAll("../", "");
                    loader.parseTsx(map, tileset, assets.getText(tsxFile));
                    for (TmxTile tile : tileset.getTiles()) {
                        tile.getFrame().getImage().setSource(tsxDir + "/" + tile.getFrame().getImage().getSource());
                    }
                }
            }
            loader.decode(map);
            return map;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot load level " + questName + "/" + levelName, ex);
        }
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
    public boolean fileExists(String path) {
        return null != assets.getVfs().getRealResourcePath(path);
    }

    @Override
    public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
        ArrayList<String> results = new ArrayList<>();
        //TODO
        return results;
    }

    @Override
    public ITexture getTexture(String minimapPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearUseless() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ITexture grabScreenToTexture() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IAnimationCollection loadFromAnimation(String file) {
        return assets.getAnimations(file);
    }

    @Override
    public IAnimationCollection loadFromSubTexture(String source, float u1, float v1, float u2, float v2) {
        return assets.getAnimationFromSubTexture(source, u1, v1, u2, v2);
    }

}
