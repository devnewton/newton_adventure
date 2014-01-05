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

import im.bci.newtonadv.GameProgression;
import im.bci.newtonadv.game.RestartGameException;
import im.bci.newtonadv.platform.interfaces.IGameData;
import im.bci.newtonadv.platform.interfaces.IGameInput;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.IMod;
import im.bci.newtonadv.platform.interfaces.IOptionsSequence;
import im.bci.newtonadv.platform.interfaces.IPlatformSpecific;
import im.bci.newtonadv.platform.interfaces.ISoundCache;
import im.bci.newtonadv.platform.lwjgl.openal.OpenALSoundCache;
import im.bci.newtonadv.platform.lwjgl.twl.OptionsSequence;
import im.bci.newtonadv.score.GameScore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.Sys;

/**
 *
 * @author devnewton
 */
public class PlatformSpecific implements IPlatformSpecific {

    private GameView view;
    private GameInput input;
    private Properties config;
    private FileGameData data;
    private ISoundCache soundCache;
    private IOptionsSequence options;
    private final ResourceBundle messages;

    public PlatformSpecific() throws Exception {
        messages = ResourceBundle.getBundle("messages");

        config = new Properties();
        loadConfig(config);

        createGameData();
        createSoundCache();
        createGameView();
        createGameInput();
        createOptionsSequence();

    }

    private ISoundCache createSoundCache() {
        if (null == data) {
            throw new RuntimeException("create IGameData before  SoundCache");
        }
        if (null == soundCache) {
            soundCache = new OpenALSoundCache(data, config);
        }
        return soundCache;
    }

    private GameView createGameView() {
        if (null == data) {
            throw new RuntimeException("create IGameData before IGameView");
        }
        if (view == null) {
            view = new GameView(data, config, this);
        }
        return view;
    }

    private GameInput createGameInput() throws Exception {
        if (null == input) {
            input = new GameInput(config);
        }
        return input;
    }

    public static void loadConfig(Properties conf) {
        URL configFilePath = getUserOrDefaultConfigFilePath();
        Logger.getLogger(PlatformSpecific.class.getName()).log(Level.INFO, "Load config from file {0}", configFilePath);
        try (InputStream f = configFilePath.openStream()) {
            conf.load(f);
        } catch (IOException ex) {
            Logger.getLogger(PlatformSpecific.class.getName()).log(Level.SEVERE, "Cannot load config file " + configFilePath, ex);
        }
    }

    private IGameData createGameData() {
        if (data == null) {
            List<File> dataDirs = new ArrayList<>();
            IMod mod = findModByName(config.getProperty("newton_adventure.mod"));
            if (null != mod) {
                dataDirs.add(new File(mod.getPath()));
            }
            dataDirs.add(getDefaultDataDir());
            data = new FileGameData();
            data.setDataDirs(dataDirs);
        }
        return data;
    }

    private IOptionsSequence createOptionsSequence() {
        if (null == view) {
            throw new RuntimeException(
                    "create IGameView before IOptionsSequence");
        }
        if (null == input) {
            throw new RuntimeException(
                    "create IGameInput before IOptionsSequence");
        }
        if (null == config) {
            throw new RuntimeException(
                    "load config before creating IOptionsSequence");
        }
        options = new OptionsSequence(this, view, input, soundCache, config);
        return options;
    }

    public static URL getDefaultConfigFilePath() {
        return PlatformSpecific.class.getClassLoader().getResource(
                "config.properties");
    }

    public static String getUserConfigDirPath() {
        String configDirPath = System.getenv("XDG_CONFIG_HOME");
        if (null == configDirPath) {
            configDirPath = System.getenv("APPDATA");
        }
        if (null == configDirPath) {
            configDirPath = System.getProperty("user.home") + File.separator
                    + ".config";
        }
        return configDirPath + File.separator + "newton_adventure";
    }

    public static String getUserScoreDirPath() {
        return getUserConfigDirPath();
    }

    public static String getUserProgressionDirPath() {
        return getUserConfigDirPath();
    }

    public static String getUserConfigFilePath() {
        return getUserConfigDirPath() + File.separator + "config.properties";
    }

    public static String getUserScoreFilePath() {
        return getUserScoreDirPath() + File.separator + "scores";
    }

    public static String getUserProgressionFilePath() {
        return getUserScoreDirPath() + File.separator + "progression";
    }

    public static URL getUserOrDefaultConfigFilePath() {
        File f = new File(getUserConfigFilePath());
        if (f.exists() && f.canRead()) {
            try {
                return f.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid user config file path" + f);
            }
        }
        return getDefaultConfigFilePath();
    }

    private static void writeConfig(String path, Properties conf) throws FileNotFoundException,
            IOException {
        FileOutputStream os = new FileOutputStream(path);
        try {
            conf.store(os, "Newton adventure configuration, see "
                    + PlatformSpecific.getDefaultConfigFilePath()
                    + " for example and documentation");
        } finally {
            os.close();
        }

    }

    @Override
    public void saveConfig() {
        saveConfig(config);
    }

    public static void saveConfig(Properties conf) {
        File userConfigFile = new File(getUserConfigFilePath());
        
        if (!userConfigFile.exists()) {
            (new File(getUserConfigDirPath())).mkdirs();
        }
        try {
            writeConfig(userConfigFile.getAbsolutePath(), conf);
        } catch (Exception e) {
            Logger.getLogger(PlatformSpecific.class.getName()).log(Level.WARNING, "Cannot save config", e);
        }
    }

    @Override
    public IGameInput getGameInput() {
        return input;
    }

    @Override
    public IGameView getGameView() {
        return view;
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
        return options;
    }

    @Override
    public void openUrl(String url) {
        Sys.openURL(url);
    }

    public void close() {
        if (null != soundCache) {
            soundCache.stopMusic();
            soundCache.close();
        }
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
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot save config", e);
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

    private File getDefaultDataDir() {
        File dir = RuntimeUtils.getApplicationDir();
        do {
            File dataDir = new File(dir, "data");
            if (dataDir.exists() && dataDir.isDirectory()) {
                return dataDir;
            }
            dir = dir.getParentFile();
        } while (null != dir);
        return null;
    }

    @Override
    public List<IMod> listMods() {
        String modsDir = getUserConfigDirPath() + File.separator + "mods";
        return listModsInDirs(new File(modsDir));
    }

    private IMod findModByName(String modName) {
        if (null != modName) {
            List<IMod> mods = listMods();
            for (IMod mod : mods) {
                if (modName.equals(mod.getName())) {
                    return mod;
                }
            }
        }
        return null;
    }

    @Override
    public void loadModIfNeeded(String modName) throws RestartGameException {
        IMod mod = findModByName(modName);
        IMod currentMod = getCurrentMod();
        File modDir = null != mod ? new File(mod.getPath()) : getDefaultDataDir();
        File currentModDir = null != currentMod ? new File(currentMod.getPath()) : getDefaultDataDir();
        if (!modDir.equals(currentModDir)) {
            List<File> dataDirs = new ArrayList<>();
            dataDirs.add(modDir);
            dataDirs.add(getDefaultDataDir());
            data.setDataDirs(dataDirs);
            throw new RestartGameException();
        }
    }

    @Override
    public IMod getCurrentMod() {
        return findModByPath(data.getDataDirs().get(0));
    }

    private IMod findModByPath(File path) {
        List<IMod> mods = listMods();
        for (IMod mod : mods) {
            if (path.equals(new File(mod.getPath()))) {
                return mod;
            }
        }
        return null;
    }

    private List<IMod> listModsInDirs(File... dirs) {
        List<IMod> mods = new ArrayList<>();
        for (File dir : dirs) {
            if (dir.exists()) {
                for (File f : dir.listFiles()) {
                    if (f.isDirectory()) {
                        try {
                            mods.add(new Mod().withName(f.getName()).withPath(f.getCanonicalPath()));
                        } catch (IOException ex) {
                            Logger.getLogger(PlatformSpecific.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        return mods;
    }

    @Override
    public String getMessage(String msg) {
        return messages.getString(msg);
    }

    @Override
    public String getLocaleSuffix() {
        return "_" + messages.getLocale().getLanguage() + "_" + messages.getLocale().getCountry();
    }

    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}