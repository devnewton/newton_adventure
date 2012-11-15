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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author devnewton
 */
class FileGameData extends AbstractGameData {

	public FileGameData(String dataDir) {
		super(addSeparator(dataDir));
	}

	private static String addSeparator(String dataDir) {
		File f = new File(dataDir);
		return f.getAbsolutePath() + "/";
	}

	@Override
	public List<String> listQuests() {
		return listSubDirectories(dataDir + "quests");
	}

	@Override
	public List<String> listQuestLevels(String questName) {
		return listSubDirectories(dataDir + "quests/" + questName + "/levels");
	}

	@Override
	public InputStream openFile(String path) throws IOException {
		File f = new File(path);
		if (f.exists()) {
			return new FileInputStream(f);
		} else {
			return null;
		}
	}

	private static List<String> listSubDirectories(String path) {
		File dir = new File(path);
		ArrayList<String> subdirs = new ArrayList<String>();
		if (dir.exists()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory()) {
					subdirs.add(f.getName());
				}
			}
		}
		return subdirs;
	}

	@Override
	public List<String> listQuestsToCompleteToUnlockQuest(String questName) {
		return Collections.emptyList();
	}

}
