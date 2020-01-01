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
package im.bci.newtonadv.platform.lwjgl;

import im.bci.jnuit.NuitAudio;
import im.bci.jnuit.NuitControls;
import im.bci.jnuit.NuitLocale;
import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.NuitToolkit;
import im.bci.jnuit.NuitTranslator;
import im.bci.jnuit.lwjgl.LwjglNuitControls;
import im.bci.jnuit.lwjgl.LwjglNuitDisplay;
import im.bci.jnuit.lwjgl.LwjglNuitFont;
import im.bci.jnuit.lwjgl.LwjglNuitPreferences;
import im.bci.jnuit.lwjgl.LwjglNuitRenderer;
import im.bci.jnuit.lwjgl.assets.VirtualFileSystem;
import im.bci.jnuit.lwjgl.audio.OpenALNuitAudio;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.GameProgression;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.AbstractGameInput;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IMod;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.score.GameScore;
import im.bci.newtonadv.ui.NewtonAdventureNuitTranslator;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author devnewton
 */
public class PlatformSpecific implements IPlatformSpecific {

    private LwjglGameView view;
    private LwjglGameInput input;
    private final LwjglNuitPreferences config;
    private FileGameData data;
    private NuitToolkit nuitToolkit;
    private NuitControls controls;
    private VirtualFileSystem vfs;

    public PlatformSpecific() throws Exception {
        config = new LwjglNuitPreferences("newton_adventure");

        createVfs();
        createGameData();
        createGameView();
        createControls();
        createNuitToolkit();
        createGameInput();
    }

    private void createVfs() {
        String modName = config.getString("mod", "Newton");
        currentMod = findModByName(modName);
        if (null == currentMod) {
            currentMod = listMods().get(0);
        }
        vfs = new VirtualFileSystem(currentMod.getPath(), "data");
    }

    private LwjglGameView createGameView() {
        if (null == data) {
            throw new RuntimeException("create IGameData before IGameView");
        }
        if (view == null) {
            view = new LwjglGameView(data, config, this);
        }
        return view;
    }

    private void createControls() {
        if (null == view) {
            throw new RuntimeException("create IGameView before controls");
        }
        controls = new LwjglNuitControls(view.getWindow());
        config.setControls(controls);

    }

    private LwjglGameInput createGameInput() throws Exception {
        if (null == view) {
            throw new RuntimeException("create IGameView before controls");
        }
        if (null == input) {
            input = new LwjglGameInput(nuitToolkit, config, view.getWindow());
            input.setup();
        }
        return input;
    }

    private IGameData createGameData() {
        if (data == null) {
            data = new FileGameData(vfs);
        }
        return data;
    }

    public static String getUserConfigDirPath() {
        String configDirPath = System.getenv("XDG_CONFIG_HOME");
        if (null == configDirPath) {
            configDirPath = System.getenv("APPDATA");
        }
        if (null == configDirPath) {
            configDirPath = System.getProperty("user.home") + File.separator + ".config";
        }
        return configDirPath + File.separator + "newton_adventure";
    }

    public static String getUserScoreDirPath() {
        return getUserConfigDirPath();
    }

    public static String getUserProgressionDirPath() {
        return getUserConfigDirPath();
    }

    public static String getUserScoreFilePath() {
        return getUserScoreDirPath() + File.separator + "scores";
    }

    public static String getUserProgressionFilePath() {
        return getUserScoreDirPath() + File.separator + "progression";
    }

    @Override
    public AbstractGameInput getGameInput() {
        return input;
    }

    @Override
    public IGameView getGameView() {
        return view;
    }

    @Override
    public IGameData getGameData() {
        return data;
    }

    @Override
    public void openUrl(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot open url " + url, e);
        }
    }

    public void close() {
        nuitToolkit.getAudio().stopMusic();
    }

    @Override
    public GameScore loadScore() {
        File scoreFile = new File(getUserScoreFilePath());
        if (scoreFile.exists()) {
            try {
                FileInputStream fs = new FileInputStream(scoreFile);
                try {
                    ObjectInputStream is = new ObjectInputStream(fs);
                    Object o = is.readObject();
                    is.close();
                    if (o instanceof GameScore) {
                        return (GameScore) o;
                    }
                } finally {
                    fs.close();
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load score", e);
            }

        }
        return new GameScore();
    }

    @Override
    public void saveScore(GameScore score) {
        File scoreFile = new File(getUserScoreFilePath());

        if (!scoreFile.exists()) {
            (new File(getUserScoreDirPath())).mkdirs();
        }
        try {
            FileOutputStream fs = new FileOutputStream(scoreFile);
            try {
                ObjectOutputStream os = new ObjectOutputStream(fs);
                os.writeObject(score);
                os.close();
            } finally {
                fs.close();
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot save config", e);
        }
    }

    @Override
    public GameProgression loadProgression() {
        File progressionFile = new File(getUserProgressionFilePath());
        if (progressionFile.exists()) {
            try {
                FileInputStream fs = new FileInputStream(progressionFile);
                try {
                    ObjectInputStream is = new ObjectInputStream(fs);
                    Object o = is.readObject();
                    is.close();
                    if (o instanceof GameProgression) {
                        return (GameProgression) o;
                    }
                } finally {
                    fs.close();
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot save config", e);
            }

        }
        return new GameProgression();
    }

    @Override
    public void saveProgression(GameProgression progression) {
        File scoreFile = new File(getUserProgressionFilePath());

        if (!scoreFile.exists()) {
            (new File(getUserProgressionDirPath())).mkdirs();
        }
        try {
            FileOutputStream fs = new FileOutputStream(scoreFile);
            try {
                ObjectOutputStream os = new ObjectOutputStream(fs);
                os.writeObject(progression);
                os.close();
            } finally {
                fs.close();
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot save progression", e);
        }
    }

    @Override
    public List<IMod> listMods() {
        ArrayList<IMod> mods = new ArrayList<IMod>();
        mods.add(new Mod().withName("Newton").withPath("data"));
        mods.add(new Mod().withName("Bald").withPath("bald"));
        mods.add(new Mod().withName("Du Châtelet").withPath("duchatelet"));
        mods.add(new Mod().withName("Retro").withPath("retro"));
        return mods;
    }

    @Override
    public void loadModIfNeeded(String modName) throws RestartGameException {
        IMod newMod = findModByName(modName);
        if (!Objects.equals(currentMod, newMod)) {
            currentMod = newMod;
            this.vfs.setResourcePaths(currentMod.getPath(), "data");
            config.putString("mod", currentMod.getName());
            throw new RestartGameException();
        }
    }

    private IMod findModByName(String modName) {
        for (IMod mod : listMods()) {
            if (mod.getName().equals(modName)) {
                return mod;
            }
        }
        return null;
    }

    private IMod currentMod;

    @Override
    public IMod getCurrentMod() {
        return currentMod;
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }

    private void createNuitToolkit() {
        if (null == view) {
            throw new RuntimeException("create IGameView before NuitToolkit");
        }
        final NuitTranslator nuitTranslator = new NewtonAdventureNuitTranslator();
        final LwjglNuitFont lwjglNuitFont = new LwjglNuitFont(new Font("Monospaced", Font.PLAIN, 24), true, new char[0],
                new HashMap<>());
        NuitAudio openALNuitAudio = new OpenALNuitAudio(vfs);
        nuitToolkit = new NuitToolkit(new LwjglNuitDisplay(view.getWindow()), controls, nuitTranslator, lwjglNuitFont,
                new LwjglNuitRenderer(nuitTranslator, lwjglNuitFont, view.getWindow()), openALNuitAudio);
        nuitToolkit.setVirtualResolutionWidth(Game.DEFAULT_SCREEN_WIDTH);
        nuitToolkit.setVirtualResolutionHeight(Game.DEFAULT_SCREEN_HEIGHT);
        openALNuitAudio.setMusicVolume(config.getFloat("music.volume", 1.0f));
        openALNuitAudio.setEffectsVolume(config.getFloat("effects.volume", 1.0f));
        nuitTranslator.setCurrentLocale(
                Locale.getDefault().getLanguage().equals(new Locale("fr").getLanguage()) ? NuitLocale.FRENCH
                : NuitLocale.ENGLISH);
    }

    @Override
    public NuitToolkit getNuitToolkit() {
        return nuitToolkit;
    }

    @Override
    public NuitPreferences getConfig() {
        return config;
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
