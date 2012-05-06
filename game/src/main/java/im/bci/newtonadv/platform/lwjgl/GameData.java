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

import im.bci.newtonadv.platform.interfaces.IGameData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author devnewton
 */
class GameData implements IGameData {

	private static final String dataDir = "";
	private static final List<String> quests;
	private static final HashMap<String, List<String>> questLevels;

	static {
		quests = Arrays.asList("jungle", "vatican", "arctic", "volcano",
				"egypt");
		questLevels = new HashMap<String, List<String>>();
		questLevels.put("jungle", Arrays.asList("level0",
				"level1", "level2", "level3",
				"level4"));
		questLevels.put("vatican", Arrays.asList("level0",
				"level1", "level2", "level3",
				"level4"));
		questLevels.put("arctic", Arrays.asList("level0", "level0.5", "level1",
				"level2", "level3", "level4"));
		questLevels.put("egypt", Arrays.asList("level0", "level1", "level2",
				"level3", "level4", "level5"));
		questLevels
				.put("volcano", Arrays.asList("level0", "level1", "level2",
						"level3", "level4"));
		questLevels.put("bonus", Arrays.asList("bonus_level1", "bonus_level2",
				"bonus_level3", "bonus_level4", "bonus_level5"));

	}

	public GameData(Properties config) {
	}

	@Override
	public List<String> listQuests() {
		return quests;
	}

	@Override
	public String getFile(String name) {
		return dataDir + name;
	}

	@Override
	public List<String> listQuestLevels(String questName) {
		return questLevels.get(questName);
	}

	@Override
	public String getQuestFile(String questName, String file) {
		return dataDir + "quests/" + questName + "/" + file;
	}

	@Override
	public InputStream openLevelTmx(String questName, String levelName)
			throws FileNotFoundException {
		String path = dataDir + "quests/" + questName + "/levels/" + levelName
				+ "/" + levelName + ".tmx";
		return openFile(path);
	}

	@Override
	public InputStream openFile(String path) throws FileNotFoundException {
		return getClass().getClassLoader().getResourceAsStream(path);
	}

	@Override
	public String getLevelFilePath(String questName, String levelName,
			String filename) {
		String path = dataDir + "quests/" + questName + "/levels/" + levelName
				+ "/" + filename;
		try {
			InputStream s = openFile(path);
			if (s != null) {
				s.close();
				return path;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
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
