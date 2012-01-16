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
package im.bci.newtonadv.world;

import im.bci.newtonadv.platform.lwjgl.Texture;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;

class Activator extends Platform {
    private final int activableId;
    private boolean activated;
    private final Texture onTexture;

    Activator(World world, int activableId, Texture onTexture, Texture offTexture) {
        super(world);
        this.activableId = activableId;
        this.setTexture(offTexture);
        this.onTexture = onTexture;
    }

    @Override
    public strictfp void collided(Body body) {
        if (activated) {
            return;
        }
        if (body instanceof Hero || body instanceof Key) {
            activated = true;
            BodyList bodies = world.getBodies();
            for (int i = 0; i < bodies.size(); ++i) {
                Body b = bodies.get(i);
                if (b instanceof Blocker) {
                    Blocker a = (Blocker) b;
                    if (a.getActivableId() == activableId) {
                        a.activate();
                        this.setTexture(onTexture);
                    }
                }
            }
        }
    }
}
