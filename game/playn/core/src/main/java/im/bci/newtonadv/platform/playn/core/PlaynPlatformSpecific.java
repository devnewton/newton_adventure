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

import im.bci.jnuit.NuitDisplay;
import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.playn.PlaynNuitAudio;
import im.bci.jnuit.playn.PlaynNuitDisplay;
import im.bci.jnuit.playn.PlaynNuitFont;
import im.bci.jnuit.playn.PlaynNuitPreferences;
import im.bci.jnuit.playn.PlaynNuitRenderer;
import im.bci.jnuit.playn.controls.PlaynNuitControls;
import im.bci.newtonadv.GameProgression;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IMod;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.score.GameScore;
import im.bci.newtonadv.ui.NewtonAdventureNuitTranslator;
import java.util.ArrayList;
import java.util.List;
import playn.core.CachingAssets;
import playn.core.Font;
import playn.core.PlayN;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynPlatformSpecific implements IPlatformSpecific {

    private final RealWatchedAssets assets = new RealWatchedAssets(new CachingAssets(PlayN.assets()));
    private final PlaynGameView gameView;
    private final PlaynGameData data = new PlaynGameData(assets);
    private final NuitToolkit nuitToolkit;
    private final NuitPreferences config;
    private final AbstractGameInput input;
    private final VirtualPad virtualPad;

    public PlaynPlatformSpecific(NuitDisplay display) {
        PlaynNuitControls controls = new PlaynNuitControls();
        PlaynNuitFont font = new PlaynNuitFont("Arial", Font.Style.BOLD, 24f, true);
        NewtonAdventureNuitTranslator translator = new NewtonAdventureNuitTranslator();
        nuitToolkit = new NuitToolkit(display, controls, translator, font, new PlaynNuitRenderer(translator, font), new PlaynNuitAudio());
        config = new PlaynNuitPreferences(controls, "newtonadventure");
        gameView = new PlaynGameView(assets, config);
        virtualPad = new VirtualPad(assets);
        input = new PlaynGameInput(nuitToolkit, config, controls, virtualPad);
        input.setup();
    }

    public VirtualPad getVirtualPad() {
        return virtualPad;
    }

    @Override
    public IGameView getGameView() {
        return gameView;
    }

    @Override
    public IGameData getGameData() {
        return data;
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

    @Override
    public long nanoTime() {
        return PlayN.tick() * 1000000L;
    }

    @Override
    public NuitPreferences getConfig() {
        return config;
    }

    @Override
    public AbstractGameInput getGameInput() {
        return input;
    }

    @Override
    public NuitToolkit getNuitToolkit() {
        return nuitToolkit;
    }
    
    public void saveConfig() {
        config.putFloat("music.volume", nuitToolkit.getAudio().getMusicVolume());
        config.putFloat("effects.volume", nuitToolkit.getAudio().getEffectsVolume());
        config.putInt("video.width", nuitToolkit.getResolution().getWidth());
        config.putInt("video.height", nuitToolkit.getResolution().getHeight());
        config.putBoolean("video.fullscreen", nuitToolkit.isFullscreen());
        input.saveConfig();
        config.saveConfig();
    }

}
