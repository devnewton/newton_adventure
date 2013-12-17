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
package im.bci.newtonadv.game;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import im.bci.newtonadv.world.GameOverException;

/**
 * 
 * @author devnewton
 */
public class EntityList {

	private TreeMap<Integer, List<Entity>> entities = new TreeMap<Integer, List<Entity>>();

	public void draw() {
		for (Entry<Integer, List<Entity>> entry : entities.entrySet()) {
			for (Entity e : entry.getValue()) {
				e.draw();
			}
		}
	}

	public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
		for (Entry<Integer, List<Entity>> entry : entities.entrySet()) {
			Iterator<Entity> it = entry.getValue().iterator();
			while (it.hasNext()) {
				Entity e = it.next();
				e.update(frameTimeInfos);
				if (e.isDead())
					it.remove();
			}
		}

	}

	public void add(Entity e) {
		final int zOrder = e.getZOrder();
		List<Entity> entitiesWithSameZ = entities.get(zOrder);
		if(null == entitiesWithSameZ) {
			entitiesWithSameZ = new LinkedList<Entity>();
			entities.put(zOrder, entitiesWithSameZ);
		}
		entitiesWithSameZ.add(e);		
	}
}
