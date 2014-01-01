/*
 * Copyright (c) 2014 devnewton <devnewton@bci.im>
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

import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.world.IStaticPlatformDrawer;
import im.bci.newtonadv.world.StaticPlatform;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
class PlaynStaticPlatformDrawer implements IStaticPlatformDrawer {

    private List<PlaynStaticPlatformDrawable> drawables;
    private final List<StaticPlatform> platforms = new ArrayList<>();

    public PlaynStaticPlatformDrawer() {
    }

    @Override
    public void addVisible(StaticPlatform platform) {
        int v = platform.vertexBufferIndex * 4;
        final IntBuffer indices = ((PlaynStaticPlatformDrawable) platform.drawable).indices;

        indices.put(v);
        indices.put(v + 1);
        indices.put(v + 2);

        indices.put(v);
        indices.put(v + 2);
        indices.put(v + 3);
    }

    @Override
    public void postConstruct(IGameView view) {
        drawables = PlaynStaticPlatformDrawable.create(view, platforms);
        platforms.clear();
    }

    @Override
    public void getVisibleDrawables(List<Drawable> visibleDrawables) {
        for (PlaynStaticPlatformDrawable drawable : drawables) {
            drawable.indices.flip();
            if (drawable.indices.limit() > 0) {
                visibleDrawables.add(drawable);
            }
        }
    }

    @Override
    public void resetVisibles() {
        for (PlaynStaticPlatformDrawable drawable : drawables) {
            drawable.indices.rewind();
            drawable.indices.limit(drawable.indices.capacity());
        }
    }

    @Override
    public void add(StaticPlatform platform) {
        platforms.add(platform);
    }

}
