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

import im.bci.newtonadv.GameProgression;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.IGameInput;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IMod;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.score.GameScore;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import playn.core.CachingAssets;
import playn.core.PlayN;
import playn.core.WatchedAssets;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynPlatformSpecific implements IPlatformSpecific {

    private final RealWatchedAssets assets = new RealWatchedAssets(new CachingAssets(PlayN.assets()));
    private final PlaynGameInput gameInput = new PlaynGameInput();
    private final PlaynGameView gameView = new PlaynGameView(assets);
    private final PlaynSoundCache soundCache = new PlaynSoundCache();
    private final PlaynGameData data = new PlaynGameData(assets);

    @Override
    public void saveConfig() {
    }

    @Override
    public IGameInput getGameInput() {
        return gameInput;
    }

    @Override
    public IGameView getGameView() {
        return gameView;
    }

    @Override
    public ISoundCache getSoundCache() {
        return soundCache;
    }

    @Override
    public IGameData getGameData() {
        return data;
    }

    @Override
    public IOptionsSequence getOptionsSequence() {
        //TODO
        return null;
    }

    @Override
    public void openUrl(String url) {
        PlayN.openURL(url);
    }

    @Override
    public GameScore loadScore() {
        return new GameScore();
    }

    @Override
    public void saveScore(GameScore score) {
        //TODO
    }

    @Override
    public GameProgression loadProgression() {
        //TODO
        return new GameProgression();
    }

    @Override
    public void saveProgression(GameProgression progression) {
        //TODO
    }

    @Override
    public List<IMod> listMods() {
        return new ArrayList<IMod>();
    }

    @Override
    public void loadModIfNeeded(String selectedModName) throws RestartGameException {
        //TODO
    }

    @Override
    public IMod getCurrentMod() {
        //TODO
        return null;
    }

    @Override
    public String getMessage(String msg) {
        //TODO
        return msg;
    }

    @Override
    public String getLocaleSuffix() {
        return "_en_US";
    }

    public RealWatchedAssets getAssets() {
        return assets;
    }

}
