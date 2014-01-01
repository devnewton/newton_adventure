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

import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.util.ShapeUtils;
import im.bci.newtonadv.world.IStaticPlatformDrawable;
import im.bci.newtonadv.world.StaticPlatform;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Shape;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
class PlaynStaticPlatformDrawable implements IStaticPlatformDrawable {

    final List<StaticPlatform> platforms = new ArrayList<>();

    FloatBuffer vertices;
    FloatBuffer texCoords;
    IntBuffer indices;
    private int zOrder;
    private IGameView view;
    public PlaynTexture texture;
    private static final Comparator<StaticPlatform> COMPARATOR = new Comparator<StaticPlatform>() {

        @Override
        public int compare(StaticPlatform o1, StaticPlatform o2) {
            int result = Integer.compare(o1.getZOrder(), o2.getZOrder());
            if (result == 0) {
                final PlaynTexture texture1 = (PlaynTexture) o1.getTexture();
                final PlaynTexture texture2 = (PlaynTexture) o2.getTexture();
                result = texture1.getName().compareTo(texture2.getName());
            }
            return result;
        }

    };

    static List<PlaynStaticPlatformDrawable> create(IGameView view, List<StaticPlatform> platforms) {
        List<PlaynStaticPlatformDrawable> result = new ArrayList<>();
        java.util.Collections.sort(platforms, COMPARATOR);
        for (List<StaticPlatform> partitionedPlatforms : partition(platforms)) {
            if (!partitionedPlatforms.isEmpty()) {
                PlaynStaticPlatformDrawable drawable = new PlaynStaticPlatformDrawable();
                StaticPlatform p = partitionedPlatforms.get(0);
                drawable.view = view;
                drawable.texture = (PlaynTexture) p.getTexture();
                drawable.zOrder = p.getZOrder();
                drawable.addStaticPlatforms(partitionedPlatforms);
                result.add(drawable);
            }
        }
        return result;
    }

    private static List<List<StaticPlatform>> partition(List<StaticPlatform> platforms) {
        List<List<StaticPlatform>> result = new ArrayList<>();
        StaticPlatform previousPlatform = null;
        List<StaticPlatform> currentPlatforms = null;
        for (StaticPlatform platform : platforms) {
            if (null == currentPlatforms || 0 != COMPARATOR.compare(previousPlatform, platform)) {
                currentPlatforms = new ArrayList<>();
                result.add(currentPlatforms);
            }
            currentPlatforms.add(platform);
            previousPlatform = platform;
        }
        return result;
    }

    @Override
    public void addStaticPlatforms(List<StaticPlatform> platforms) {
        final int nb = platforms.size();
        vertices = FloatBuffer.wrap(new float[nb * 2 * 4]);
        texCoords = FloatBuffer.wrap(new float[nb * 2 * 4]);
        indices = IntBuffer.wrap(new int[nb * 6]);
        for (int i = 0; i < nb; ++i) {
            StaticPlatform platform = platforms.get(i);
            platform.vertexBufferIndex = i;
            addStaticPlatform(platform);
        }
        vertices.flip();
        texCoords.flip();
    }

    private void addStaticPlatform(StaticPlatform platform) {
        platforms.add(platform);
        platform.drawable = this;
        Shape shape = platform.getShape();
        Vector2f[] points = ShapeUtils.getVertices(shape, platform.getPosition(),
                platform.getRotation());
        int nbPoints = Math.min(points.length, vertices.capacity());
        for (int i = 0; i < nbPoints; ++i) {
            Vector2f point = points[i];
            vertices.put(point.x);
            vertices.put(point.y);
        }
        texCoords.put(platform.getU1());
        texCoords.put(platform.getV2());
        texCoords.put(platform.getU2());
        texCoords.put(platform.getV2());
        texCoords.put(platform.getU2());
        texCoords.put(platform.getV1());
        texCoords.put(platform.getU1());
        texCoords.put(platform.getV1());
    }

    @Override
    public void draw() {
        view.drawStaticPlatforms(this);
    }

    @Override
    public int getZOrder() {
        return zOrder;
    }

}
