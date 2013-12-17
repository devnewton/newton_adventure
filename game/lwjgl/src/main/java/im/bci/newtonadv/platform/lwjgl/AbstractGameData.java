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
package im.bci.newtonadv.platform.lwjgl;

import im.bci.newtonadv.platform.interfaces.IGameData;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

abstract class AbstractGameData implements IGameData {

    protected String dataDir;

    @Override
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    @Override
    public String getDataDir() {
        return dataDir;
    }

    @Override
    public String getFile(String name) {
        return dataDir + name;
    }

    @Override
    public String getQuestFile(String questName, String file) {
        return dataDir + "quests/" + questName + "/" + file;
    }

    @Override
    public InputStream openLevelNal(String questName, String levelName) throws Exception {
        String path = dataDir + "quests/" + questName + "/levels/" + levelName
                + "/" + levelName + ".nal";
        return openFile(path);
    }

    @Override
    public String getLevelFilePath(String questName, String levelName, String filename) {
        String path = dataDir + "quests/" + questName + "/levels/" + levelName
                + "/" + filename;
        if (fileExists(path)) {
            return path;
        }
        path = dataDir + "quests/" + questName + "/" + filename;
        if (fileExists(path)) {
            return path;
        }
        return dataDir + "default_level_data/" + filename;
    }

    @Override
    public boolean fileExists(String path) {
        try {
            InputStream s = openFile(path);
            if (s != null) {
                s.close();
                return true;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return false;
    }
}