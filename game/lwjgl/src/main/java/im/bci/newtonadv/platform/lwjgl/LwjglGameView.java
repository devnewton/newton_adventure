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

import im.bci.jnuit.lwjgl.assets.LwjglTextureQuality;
import im.bci.jnuit.lwjgl.Sync;
import im.bci.jnuit.NuitPreferences;
import im.bci.jnuit.animation.IAnimationFrame;
import im.bci.jnuit.animation.IAnimationImage;
import im.bci.jnuit.animation.IPlay;
import im.bci.jnuit.lwjgl.IconLoader;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.MenuSequence.Button;
import im.bci.newtonadv.game.QuestMenuSequence;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.jnuit.animation.ITexture;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.util.AbsoluteAABox;
import im.bci.newtonadv.util.NewtonColor;
import im.bci.newtonadv.world.Axe;
import im.bci.newtonadv.world.AxeAnchor;
import im.bci.newtonadv.world.Bat;
import im.bci.newtonadv.world.Blocker;
import im.bci.newtonadv.world.Bomb;
import im.bci.newtonadv.world.Boss;
import im.bci.newtonadv.world.BossHand;
import im.bci.newtonadv.world.Cloud;
import im.bci.newtonadv.world.Door;
import im.bci.newtonadv.world.Explosion;
import im.bci.newtonadv.world.FireBall;
import im.bci.newtonadv.world.Hero;
import im.bci.newtonadv.world.Key;
import im.bci.newtonadv.world.KeyLock;
import im.bci.newtonadv.world.LosedApple;
import im.bci.newtonadv.world.MobilePikeAnchor;
import im.bci.newtonadv.world.MobilePikes;
import im.bci.newtonadv.world.MovingPlatform;
import im.bci.newtonadv.world.Mummy;
import im.bci.newtonadv.world.PickableObject;
import im.bci.newtonadv.world.PickedUpObject;
import im.bci.newtonadv.world.AnimatedPlatform;
import im.bci.newtonadv.world.IStaticPlatformDrawable;
import im.bci.newtonadv.world.ScoreVisualIndicator;
import im.bci.newtonadv.world.StaticPlatform;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author devnewton
 */
public strictfp class LwjglGameView implements IGameView {

    private LwjglTextureQuality quality = LwjglTextureQuality.DEFAULT;
    private final FileGameData data;
    private TrueTypeFont font;
    private final PlatformSpecific platformSpecific;
    private long window;
    private final Sync sync = new Sync();

    LwjglGameView(FileGameData data, NuitPreferences config, PlatformSpecific platformSpecific) {
        this.data = data;
        this.platformSpecific = platformSpecific;
        initDisplay(config);
    }

    public long getWindow() {
        return window;
    }

    private void doDrawMenuSequence(MenuSequence sequence) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(MenuSequence.ortho2DLeft, MenuSequence.ortho2DRight, MenuSequence.ortho2DBottom,
                MenuSequence.ortho2DTop, -1f, 1f);
        IAnimationImage background = sequence.getBackgroundImage();
        if (background != null) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) background.getId());
            final float x1 = sequence.getBackgroundX1();
            final float x2 = sequence.getBackgroundX2();
            final float y1 = sequence.getBackgroundY1();
            final float y2 = sequence.getBackgroundY2();

            final float u1 = 0.0F;
            final float u2 = 1.0F;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(u1, 0.0F);
            GL11.glVertex2f(x1, y2);
            GL11.glTexCoord2f(u2, 0.0F);
            GL11.glVertex2f(x2, y2);
            GL11.glTexCoord2f(u2, 1.0F);
            GL11.glVertex2f(x2, y1);
            GL11.glTexCoord2f(u1, 1.0F);
            GL11.glVertex2f(x1, y1);
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
        }
        for (Button b : sequence.getButtons()) {
            b.draw();
        }
        GL11.glPopMatrix();
    }

    private void initDisplay(NuitPreferences config) {
        int width = config.getInt("video.width", 800);
        int height = config.getInt("video.height", 600);
        boolean fullscreen = config.getBoolean("video.fullscreen", false);
        LwjglTextureQuality quality = LwjglTextureQuality.valueOf(config.getString("video.quality", "DEFAULT"));
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }
        setDisplayMode(width, height, fullscreen, quality);
    }

    public void setDisplayMode(int width, int height, boolean fullscreen, LwjglTextureQuality quality) {
        if (0 == this.window) {
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            this.window = GLFW.glfwCreateWindow(width, height, "Newton Adventure",
                    fullscreen ? GLFW.glfwGetPrimaryMonitor() : 0, 0);
            if (0 == window) {
                throw new RuntimeException("Failed to create the GLFW window");
            }
            GLFW.glfwMakeContextCurrent(window);
            GLFW.glfwSwapInterval(1);// enable vsync
            GLFW.glfwShowWindow(window);
            GL.createCapabilities();
            setIcon();
        } else {
            GLFW.glfwSetWindowMonitor(this.window, fullscreen ? GLFW.glfwGetPrimaryMonitor() : 0, 0, 0, width, height,
                    GLFW.GLFW_DONT_CARE);
            GLFW.glfwSwapInterval(1);// enable vsync
        }
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_DITHER);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, quality.toGL());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glColor4f(1, 1, 1, 1);
        this.data.clearUseless();
        this.quality = quality;
        this.data.setQuality(quality);
        if (null != font) {
            font.deleteFontTexture();
        }
        font = initFont();
    }

    private void setIcon() {
        try ( InputStream is = this.getClass().getClassLoader().getResourceAsStream("icon.png")) {
            IconLoader.setIcon(window, is);
        } catch (Exception e) {
            Logger.getLogger(LwjglGameView.class.getName()).warning("Cannot set window icon");
        }
    }

    @Override
    public void draw(Sequence sequence) {
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(window, width, height);
        GL11.glViewport(0, 0, width[0], height[0]);
        sequence.draw();
        sync.sync(Game.FPS);
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        // finally check if the user has requested that the display be
        // shutdown
        if (GLFW.glfwWindowShouldClose(window)) {
            throw new GameCloseException();
        }
    }

    @Override
    public void drawFPS(int nbFps) {
        if (platformSpecific.getConfig().getBoolean("tweaks.show.fps", false)) {
            String fps = nbFps + " FPS";
            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_BLEND);

            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            int[] width = new int[1];
            int[] height = new int[1];
            GLFW.glfwGetFramebufferSize(this.window, width, height);
            GL11.glOrtho(0, width[0], 0, height[0], -1, 1);
            GL11.glTranslatef(width[0] - font.getWidth(fps), height[0] - 64, 0);
            font.drawString(fps);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    public void close() {
        if (null != font) {
            font.deleteFontTexture();
        }
        data.clearAll();
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    @Override
    public void drawPickableObject(PickableObject pickableObject, IAnimationFrame texture, World world) {
        AABox bounds = pickableObject.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(pickableObject.getPosition().getX(), pickableObject.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -bounds.getWidth() / 2.0f;
        final float x2 = bounds.getWidth() / 2.0f;
        final float y1 = -bounds.getHeight() / 2.0f;
        final float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawAxe(Axe axe, IAnimationFrame texture) {
        Box box = (Box) axe.getShape();
        Vector2f[] pts = box.getPoints(axe.getPosition(), axe.getRotation());

        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(texture.getU1(), texture.getV2());
        GL11.glVertex2f(pts[0].x, pts[0].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV2());
        GL11.glVertex2f(pts[1].x, pts[1].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV1());
        GL11.glVertex2f(pts[2].x, pts[2].y);
        GL11.glTexCoord2f(texture.getU1(), texture.getV1());
        GL11.glVertex2f(pts[3].x, pts[3].y);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);

    }

    @Override
    public void drawAxeAnchor(AxeAnchor axeAnchor, float radius, IAnimationFrame frame) {
        IAnimationImage texture = frame.getImage();
        GL11.glPushMatrix();
        GL11.glTranslatef(axeAnchor.getPosition().getX(), axeAnchor.getPosition().getY(), 0.0f);
        final float x1 = -radius;
        final float x2 = radius;
        final float y1 = -radius;
        final float y2 = radius;

        if (texture.hasAlpha()) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getId());

        final float u1 = 0.18f, u2 = 0.8f;
        final float v1 = 0.2f, v2 = 0.8f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();

        if (texture.hasAlpha()) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawBat(Bat bat, float scale, IAnimationFrame frame, World world) {
        AABox bounds = bat.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(bat.getPosition().getX(), bat.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        GL11.glScalef(scale, scale, 1);
        float x1 = -bounds.getWidth() / 2.0f;
        float x2 = bounds.getWidth() / 2.0f;
        float y1 = -bounds.getHeight() / 2.0f;
        float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) frame.getImage().getId());

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(frame.getU2(), frame.getV1());
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(frame.getU1(), frame.getV1());
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(frame.getU1(), frame.getV2());
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(frame.getU2(), frame.getV2());
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawBlocker(Blocker blocker, float alpha) {
        if (alpha < 1.0f) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        }
        drawPlatform(blocker);
        if (alpha < 1.0f) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawPlatform(AnimatedPlatform platform) {
        final boolean hasAlpha = platform.frame.getImage().hasAlpha();
        if (hasAlpha) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) platform.frame.getImage().getId());
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, platform.texCoords);
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, platform.vertices);
        int nbVertices = platform.vertices.limit() / 2;
        GL11.glDrawArrays((nbVertices % 4) == 0 ? GL11.GL_QUADS : GL11.GL_TRIANGLES, 0, nbVertices);
        if (hasAlpha) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawStaticPlatforms(IStaticPlatformDrawable iplatforms) {
        StaticPlatformDrawable platforms = (StaticPlatformDrawable) iplatforms;
        final boolean hasAlpha = platforms.texture.hasAlpha();
        if (hasAlpha) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, platforms.texture.getId());
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, platforms.texCoords);
        GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, platforms.vertices);
        int nbVertices = platforms.vertices.limit() / 2;
        GL11.glDrawElements((nbVertices % 4) == 0 ? GL11.GL_QUADS : GL11.GL_TRIANGLES, platforms.indices);
        if (hasAlpha) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawMovingPlatform(MovingPlatform platform, IAnimationFrame texture) {
        Box box = (Box) platform.getShape();
        Vector2f[] pts = box.getPoints(platform.getPosition(), platform.getRotation());

        if (texture.getImage().hasAlpha()) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(texture.getU1(), texture.getV2());
        GL11.glVertex2f(pts[0].x, pts[0].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV2());
        GL11.glVertex2f(pts[1].x, pts[1].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV1());
        GL11.glVertex2f(pts[2].x, pts[2].y);
        GL11.glTexCoord2f(texture.getU1(), texture.getV1());
        GL11.glVertex2f(pts[3].x, pts[3].y);
        GL11.glEnd();
        if (texture.getImage().hasAlpha()) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawCloud(Cloud cloud, float alpha) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        drawPlatform(cloud);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);

    }

    @Override
    public void drawDoor(Door door, IAnimationFrame texture) {
        Box box = (Box) door.getShape();
        Vector2f[] pts = box.getPoints(door.getPosition(), door.getRotation());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(texture.getU1(), texture.getV2());
        GL11.glVertex2f(pts[0].x, pts[0].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV2());
        GL11.glVertex2f(pts[1].x, pts[1].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV1());
        GL11.glVertex2f(pts[2].x, pts[2].y);
        GL11.glTexCoord2f(texture.getU1(), texture.getV1());
        GL11.glVertex2f(pts[3].x, pts[3].y);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawExplosion(Explosion explosion, IAnimationFrame frame, World world) {
        GL11.glPushMatrix();
        GL11.glTranslatef(explosion.getPosition().getX(), explosion.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -explosion.getSize() / 2.0f;
        final float x2 = explosion.getSize() / 2.0f;
        final float y1 = -explosion.getSize() / 2.0f;
        final float y2 = explosion.getSize() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) frame.getImage().getId());

        final float u1 = frame.getU1(), v1 = frame.getV1(), u2 = frame.getU2(), v2 = frame.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawFireBall(FireBall fireball, IAnimationFrame texture, World world) {
        GL11.glPushMatrix();
        ROVector2f pos = fireball.getPosition();
        GL11.glTranslatef(pos.getX(), pos.getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -fireball.getSize() / 2.0f;
        final float x2 = fireball.getSize() / 2.0f;
        final float y1 = -fireball.getSize() / 2.0f;
        final float y2 = fireball.getSize() / 2.0f;

        if (texture.getImage().hasAlpha()) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        if (texture.getImage().hasAlpha()) {
            GL11.glDisable(GL11.GL_BLEND);
        }

    }

    @Override
    public void drawHero(Hero hero, IAnimationFrame frame, World world) {
        AABox bounds = hero.getShape().getBounds();
        float r = (float) Math.toDegrees(world.getGravityAngle());
        float scale = 1.0f;
        if (null != world.getHero().getDyingTimedAction()) {
            float p = world.getHero().getDyingTimedAction().getProgress();
            if (p >= 0.5f) {
                p -= 0.5f;
                p *= 2.0f;
                r += 500 * p;
                scale += Math.sin(p * 1.5f * Math.PI) * 4.0f;
                if (scale < 0.0f) {
                    return;
                }
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(hero.getPosition().getX(), hero.getPosition().getY(), 0.0f);
        GL11.glRotatef(r, 0, 0, 1.0f);
        GL11.glScalef(scale, scale, 1);
        float x1 = -bounds.getWidth() / 2.0f;
        float x2 = bounds.getWidth() / 2.0f;
        float y1 = -bounds.getHeight() / 2.0f;
        float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) frame.getImage().getId());

        final float v1 = frame.getV1(), v2 = frame.getV2();
        final float u1, u2;
        if (hero.isLookingLeft()) {
            u1 = frame.getU2();
            u2 = frame.getU1();
        } else {
            u1 = frame.getU1();
            u2 = frame.getU2();
        }

        NewtonColor color = hero.getColor();
        GL11.glColor3f(color.r, color.g, color.b);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glColor3f(1f, 1f, 1f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawKey(Key key, IAnimationFrame texture, World world) {
        AABox bounds = key.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(key.getPosition().getX(), key.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -bounds.getWidth() / 2.0f;
        final float x2 = bounds.getWidth() / 2.0f;
        final float y1 = -bounds.getHeight() / 2.0f;
        final float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        NewtonColor color = key.getColor();
        GL11.glColor3f(color.r, color.g, color.b);

        if (color != NewtonColor.white) {
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        }

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor3f(1f, 1f, 1f);

        if (color != NewtonColor.white) {
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        }
    }

    @Override
    public void drawLosedApple(LosedApple apple, World world, IAnimationFrame texture, float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslatef(apple.getPosition().getX(), apple.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -apple.getSize() / 2.0f;
        final float x2 = apple.getSize() / 2.0f;
        final float y1 = -apple.getSize() / 2.0f;
        final float y2 = apple.getSize() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void drawMobilePikeAnchor(MobilePikeAnchor anchor, IAnimationFrame frame) {
        IAnimationImage texture = frame.getImage();
        GL11.glPushMatrix();
        GL11.glTranslatef(anchor.getPosition().getX(), anchor.getPosition().getY(), 0.0f);
        AABox bounds = anchor.getShape().getBounds();
        final float x1 = -bounds.getWidth() / 2.0f;
        final float x2 = bounds.getWidth() / 2.0f;
        final float y1 = -bounds.getHeight() / 2.0f;
        final float y2 = bounds.getHeight() / 2.0f;

        if (texture.hasAlpha()) {
            GL11.glEnable(GL11.GL_BLEND);
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getId());

        final float u1 = 0.18f, u2 = 0.8f;
        final float v1 = 0.2f, v2 = 0.8f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        if (texture.hasAlpha()) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawMobilePikes(MobilePikes pikes, IAnimationFrame texture) {
        Box box = (Box) pikes.getShape();
        Vector2f[] pts = box.getPoints(pikes.getPosition(), pikes.getRotation());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(texture.getU1(), texture.getV2());
        GL11.glVertex2f(pts[0].x, pts[0].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV2());
        GL11.glVertex2f(pts[1].x, pts[1].y);
        GL11.glTexCoord2f(texture.getU2(), texture.getV1());
        GL11.glVertex2f(pts[2].x, pts[2].y);
        GL11.glTexCoord2f(texture.getU1(), texture.getV1());
        GL11.glVertex2f(pts[3].x, pts[3].y);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawMummy(Mummy mummy, World world, IAnimationFrame frame, float scale) {
        AABox bounds = mummy.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(mummy.getPosition().getX(), mummy.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        GL11.glScalef(scale, scale, 1);
        float x1 = -bounds.getWidth() / 2.0f;
        float x2 = bounds.getWidth() / 2.0f;
        float y1 = -bounds.getHeight() / 2.0f;
        float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) frame.getImage().getId());

        final float v1 = frame.getV1(), v2 = frame.getV2();
        final float u1, u2;
        if (mummy.isLookingLeft()) {
            u1 = frame.getU2();
            u2 = frame.getU1();
        } else {
            u1 = frame.getU1();
            u2 = frame.getU2();
        }

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawPickedUpObject(PickedUpObject pickedUpObject, World world, IAnimationFrame texture) {
        GL11.glPushMatrix();
        GL11.glTranslatef(pickedUpObject.getPosition().getX(), pickedUpObject.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -pickedUpObject.getSize() / 2.0f;
        final float x2 = pickedUpObject.getSize() / 2.0f;
        final float y1 = -pickedUpObject.getSize() / 2.0f;
        final float y2 = pickedUpObject.getSize() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawUsedKey(UsedKey key, IAnimationFrame texture, World world) {
        GL11.glPushMatrix();
        GL11.glTranslatef(key.getPosition().getX(), key.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        float x1 = -key.getSize() / 2.0f;
        final float x2 = key.getSize() / 2.0f;
        final float y1 = -key.getSize() / 2.0f;
        final float y2 = key.getSize() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawBoss(Boss boss, IAnimationFrame texture) {
        AABox bounds = boss.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(boss.getPosition().getX(), boss.getPosition().getY(), 0.0f);
        float x1 = -bounds.getWidth() / 2.0f;
        float x2 = bounds.getWidth() / 2.0f;
        float y1 = -bounds.getHeight() / 2.0f;
        float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glColor3f(1f, 1f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
    }

    @Override
    public void drawBossHand(BossHand hand, IAnimationFrame texture) {
        AABox bounds = hand.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(hand.getPosition().getX(), hand.getPosition().getY(), 0.0f);
        float x1 = -bounds.getWidth() / 2.0f;
        float x2 = bounds.getWidth() / 2.0f;
        float y1 = -bounds.getHeight() / 2.0f;
        float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

    }

    @Override
    public void drawScoreSequence(ScoreSequence sequence, QuestScore questScore, long scorePerCentToShow) {
        // TODO if (Display.isVisible() || Display.isDirty() || LwjglHelper.wasResized()
        // || sequence.isDirty()) {
        sequence.setDirty(false);
        doDrawMenuSequence(sequence);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(ScoreSequence.ortho2DLeft, ScoreSequence.ortho2DRight, ScoreSequence.ortho2DBottom,
                ScoreSequence.ortho2DTop, -1f, 1f);
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);

        int i = 1;
        font.drawString((ScoreSequence.ortho2DLeft + ScoreSequence.ortho2DRight) / 2.0f, i++ * font.getHeight(),
                platformSpecific.getNuitToolkit().getMessage("score.sequence.title"), 1, -1, TrueTypeFont.Align.CENTER);
        font.drawString(0, i++ * font.getHeight(), questScore.getQuestName(), 1, -1, TrueTypeFont.Align.LEFT);
        for (Entry<String, LevelScore> levelEntry : questScore.entrySet()) {
            String levelScoreStr = levelEntry.getKey() + ": "
                    + (scorePerCentToShow * levelEntry.getValue().computeScore() / 100);
            font.drawString((ScoreSequence.ortho2DLeft + ScoreSequence.ortho2DRight) / 2.0f, i++ * font.getHeight(),
                    levelScoreStr, 1, -1, TrueTypeFont.Align.CENTER);
        }
        String questScoreStr = platformSpecific.getNuitToolkit().getMessage("score.sequence.quest.total") + ": "
                + (scorePerCentToShow * questScore.computeScore() / 100);
        font.drawString(0, i++ * font.getHeight(), questScoreStr, 1, -1, TrueTypeFont.Align.LEFT);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        // TODO }
    }

    @Override
    public void drawSnowLayer(SnowLayer layer) {
        GL11.glPushMatrix();

        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(this.window, width, height);
        float aspectRatio = (float) width[0] / (float) height[0];
        GL11.glLoadIdentity();
        GL11.glOrtho(SnowLayer.ortho2DLeft * aspectRatio, SnowLayer.ortho2DRight * aspectRatio, SnowLayer.ortho2DBottom,
                SnowLayer.ortho2DTop, -1f, 1f);
        layer.setAspectRatio(aspectRatio);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(GL11.GL_POINTS);
        for (int i = 0; i < layer.getFlakes().length; ++i) {
            if (null != layer.getFlakes()[i]) {
                GL11.glVertex2f(layer.getFlakes()[i].x, layer.getFlakes()[i].y);
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Override
    public void drawLevelIndicators(String indicators) {
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(this.window, width, height);
        GL11.glOrtho(0, width[0], 0, height[0], -1, 1);
        GL11.glTranslatef(0, height[0] - font.getHeight(), 0);
        font.drawString(indicators);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawMenuSequence(MenuSequence sequence) {
        // TODO if (Display.isVisible() || Display.isDirty() || LwjglHelper.wasResized()
        // || sequence.isDirty()) {
        sequence.setDirty(false);
        doDrawMenuSequence(sequence);
        // TODO }
    }

    @Override
    public void drawButton(Button button) {
        ITexture texture = button.getTexture();
        if (texture != null) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
            final float x1 = button.x;
            final float x2 = button.x + (button.w > 0 ? button.w : texture.getWidth());
            final float y1 = button.y + (button.h > 0 ? button.h : texture.getHeight());
            final float y2 = button.y;
            final float u1 = 0.0f, u2 = 1.0f;
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(u1, 0.0f);
            GL11.glVertex2f(x1, y2);
            GL11.glTexCoord2f(u2, 0.0f);
            GL11.glVertex2f(x2, y2);
            GL11.glTexCoord2f(u2, 1.0f);
            GL11.glVertex2f(x2, y1);
            GL11.glTexCoord2f(u1, 1.0f);
            GL11.glVertex2f(x1, y1);
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void drawMenuButton(Button button, String leftLabel, String rightLabel) {
        drawButton(button);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        GL11.glTranslatef(button.x, button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT + font.getHeight(), 0);
        GL11.glScalef(1, -1, 1);
        font.drawString(leftLabel, TrueTypeFont.Align.LEFT);

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(button.x + QuestMenuSequence.QUEST_MINIATURE_WIDTH,
                button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT + font.getHeight(), 0);
        GL11.glScalef(1, -1, 1);
        font.drawString(rightLabel, TrueTypeFont.Align.RIGHT);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawWorld(World world) {
        GL11.glPushMatrix();

        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(this.window, width, height);
        float aspectRatio = (float) width[0] / (float) height[0];
        GL11.glLoadIdentity();
        GL11.glOrtho(World.ortho2DLeft * aspectRatio, World.ortho2DRight * aspectRatio, World.ortho2DBottom,
                World.ortho2DTop, -1f, 1f);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        if (platformSpecific.getConfig().getBoolean("tweaks.rotate.view.with.gravity", true)) {
            GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0, 1.0f);
        }
        drawWorldBackground(world, aspectRatio);

        ROVector2f heroPos = world.getHero().getPosition();
        GL11.glTranslatef(-heroPos.getX(), -heroPos.getY(), 0.0f);

        float cameraW = (World.ortho2DRight - World.ortho2DLeft) * aspectRatio;
        float cameraH = World.ortho2DTop - World.ortho2DBottom;
        final float cameraSize = (float) Math.sqrt(cameraW * cameraW + cameraH * cameraH);
        final BodyList visibleBodies = world.getVisibleBodies(heroPos.getX() - cameraSize, heroPos.getY() - cameraSize,
                heroPos.getX() + cameraSize, heroPos.getY() + cameraSize);

        ArrayList<Drawable> drawableBodies = new ArrayList<>();
        world.staticPlatformDrawer.resetVisibles();
        for (int i = 0; i < visibleBodies.size(); i++) {
            Body body = visibleBodies.get(i);
            if (body instanceof Drawable) {
                drawableBodies.add(((Drawable) body));
            }
            if (body instanceof StaticPlatform) {
                world.staticPlatformDrawer.addVisible((StaticPlatform) body);
            }
        }
        world.staticPlatformDrawer.getVisibleDrawables(drawableBodies);
        java.util.Collections.sort(drawableBodies, Drawable.comparator);
        for (Drawable drawableBody : drawableBodies) {
            drawableBody.draw();
        }

        world.getTopLevelEntities().draw();
        GL11.glPopMatrix();
    }

    private void drawWorldBackground(World world, float aspectRatio) {
        ITexture backgroundTexture = world.getBackgroundTexture();
        if (null != backgroundTexture) {
            GL11.glPushMatrix();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundTexture.getId());

            ROVector2f heroPos = world.getHero().getPosition();

            float color = 1.0f;
            if (null != world.getHero().getDyingTimedAction()) {
                color -= world.getHero().getDyingTimedAction().getProgress() * 2.0f;
                if (color <= 0.0f) {
                    color = 0.0f;
                }
            }
            GL11.glColor4f(color, color, color, 1.0f);

            AbsoluteAABox worldStaticBounds = world.getStaticBounds();
            AbsoluteAABox staticBounds = new AbsoluteAABox();
            staticBounds.x1 = World.ortho2DLeft * aspectRatio * 2.0f;
            staticBounds.x2 = World.ortho2DRight * aspectRatio * 2.0f;
            staticBounds.y1 = World.ortho2DBottom * aspectRatio * 2.0f;
            staticBounds.y2 = World.ortho2DTop * aspectRatio * 2.0f;
            float xt = -heroPos.getX() * (staticBounds.getWidth() / worldStaticBounds.getWidth());
            float yt = -heroPos.getY() * (staticBounds.getHeight() / worldStaticBounds.getHeight());

            xt *= 0.1f;
            yt *= 0.1f;

            xt = Math.max(xt, -World.ortho2DBaseSize / 2.0f);
            xt = Math.min(xt, World.ortho2DBaseSize / 2.0f);
            yt = Math.max(yt, -World.ortho2DBaseSize / 2.0f);
            yt = Math.min(yt, World.ortho2DBaseSize / 2.0f);

            staticBounds.x1 += xt;
            staticBounds.x2 += xt;
            staticBounds.y1 += yt;
            staticBounds.y2 += yt;

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex2f(staticBounds.x1, staticBounds.y2);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex2f(staticBounds.x2, staticBounds.y2);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex2f(staticBounds.x2, staticBounds.y1);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex2f(staticBounds.x1, staticBounds.y1);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        }
    }

    @Override
    public void drawFadeSequence(ITexture backgroundTexture, IPlay loadingPlay, float r, float g, float b, float a) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glPushMatrix();
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(this.window, width, height);
        final float aspectRatio = (float) width[0] / (float) height[0];
        final float screenWidth = Game.DEFAULT_SCREEN_WIDTH * aspectRatio;
        GL11.glLoadIdentity();
        GL11.glOrtho(0, screenWidth, Game.DEFAULT_SCREEN_HEIGHT, 0, -1f, 1f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundTexture.getId());
        drawFadeBackground(screenWidth);

        GL11.glColor4f(r, g, b, a);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glRectf(0, 0, screenWidth, Game.DEFAULT_SCREEN_HEIGHT);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        drawLoadingIcon(loadingPlay, screenWidth);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawFadeBackground(final float screenWidth) {

        final float x1 = 0;
        final float x2 = screenWidth;
        final float y1 = 0;
        final float y2 = Game.DEFAULT_SCREEN_HEIGHT;
        final float u1 = 0.0f, u2 = 1.0f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, 0.0f);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, 0.0f);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, 1.0f);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, 1.0f);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
    }

    private void drawLoadingIcon(IPlay loadingPlay, final float screenWidth) {
        final IAnimationFrame texture = loadingPlay.getCurrentFrame();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());
        final float x1 = screenWidth - 256;
        final float x2 = screenWidth;
        final float y1 = Game.DEFAULT_SCREEN_HEIGHT;
        final float y2 = Game.DEFAULT_SCREEN_HEIGHT - 256;

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
    }

    @Override
    public void drawMainMenuSequence(MainMenuSequence mainMenuSequence) {
        drawMenuSequence(mainMenuSequence);
    }

    public LwjglTextureQuality getQuality() {
        return quality;
    }

    @Override
    public float getWidth() {
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(this.window, width, height);
        return width[0];
    }

    @Override
    public float getHeight() {
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetFramebufferSize(this.window, width, height);
        return height[0];
    }

    static final float minimapSize = 32;

    @Override
    public void drawMinimap(World world, ITexture minimapTexture) {
        if (!world.getHero().hasMap() && !world.getHero().hasCompass()) {
            return;
        }
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 100, 0, 100, -1, 1);
        GL11.glTranslatef(100 - minimapSize / 1.5f, minimapSize / 1.5f, 0);

        GL11.glPushMatrix();
        if (platformSpecific.getConfig().getBoolean("tweaks.rotate.view.with.gravity", true)) {
            GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0, 1.0f);
        }
        if (world.getHero().hasMap()) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minimapTexture.getId());
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
        }
        final float x1 = -minimapSize / 2.0f;
        final float x2 = minimapSize / 2.0f;
        final float y1 = -minimapSize / 2.0f;
        final float y2 = minimapSize / 2.0f;
        final float u1 = 0.0f, u2 = 1.0f;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, 0.0f);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, 0.0f);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, 1.0f);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, 1.0f);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (world.getHero().hasCompass()) {
            drawMinimapIcon(world, world.getHero().getPosition(), world.getHero().getAnimation().getCurrentFrame());
            for (Key key : world.getKeys()) {
                drawMinimapIcon(world, key.getPosition(), key.getAnimation().getCurrentFrame());
            }
        }
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public void drawMinimapIcon(World world, ROVector2f worldPos, IAnimationFrame texture) {
        float iconW = World.distanceUnit * 8.0f;
        float iconH = World.distanceUnit * 8.0f;

        final float icon_x1 = -iconW / 2.0f;
        final float icon_x2 = iconW / 2.0f;
        final float icon_y1 = -iconH / 2.0f;
        final float icon_y2 = iconH / 2.0f;
        float icon_u1 = texture.getU1(), icon_u2 = texture.getU2();
        float icon_v1 = texture.getV1(), icon_v2 = texture.getV2();
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0, 1.0f);
        final float miniMapPlatformSize = minimapSize * 4.0f / 256.0f;// harcoded,
        // that's
        // bad!
        GL11.glScalef(miniMapPlatformSize / (World.distanceUnit * 2.0f),
                miniMapPlatformSize / (World.distanceUnit * 2.0f), 1);
        GL11.glTranslatef(worldPos.getX(), worldPos.getY(), 0);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(icon_u1, icon_v1);
        GL11.glVertex2f(icon_x1, icon_y2);
        GL11.glTexCoord2f(icon_u2, icon_v1);
        GL11.glVertex2f(icon_x2, icon_y2);
        GL11.glTexCoord2f(icon_u2, icon_v2);
        GL11.glVertex2f(icon_x2, icon_y1);
        GL11.glTexCoord2f(icon_u1, icon_v2);
        GL11.glVertex2f(icon_x1, icon_y1);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    @Override
    public void drawKeyLock(KeyLock keyLock, float alpha) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        drawPlatform(keyLock);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawScoreVisualIndicator(World world, ScoreVisualIndicator scoreVisualIndicator) {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        GL11.glEnable(GL11.GL_BLEND);

        String value = scoreVisualIndicator.getValue();

        GL11.glPushMatrix();
        GL11.glTranslatef(scoreVisualIndicator.getPosition().getX(), scoreVisualIndicator.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        GL11.glScalef(scoreVisualIndicator.getSize() * 0.1f, scoreVisualIndicator.getSize() * 0.1f, 1.0f);
        GL11.glTranslatef(-font.getWidth(value) / 2.0f, -font.getHeight(value) / 2.0f, 0.0f);
        font.drawString(value);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

    }

    @Override
    public void drawBomb(Bomb bomb, IAnimationFrame texture, World world) {
        AABox bounds = bomb.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(bomb.getPosition().getX(), bomb.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        GL11.glRotatef((float) Math.toDegrees(bomb.getRotation()), 0, 0, 1.0f);
        final float x1 = -bounds.getWidth() / 2.0f;
        final float x2 = bounds.getWidth() / 2.0f;
        final float y1 = -bounds.getHeight() / 2.0f;
        final float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, (int) texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);

    }

    private TrueTypeFont initFont() {
        HashMap<Character, BufferedImage> fontSpecialCharacters = new HashMap<>();
        try {
            fontSpecialCharacters.put('$', data.openImage("default_level_data/apple.png"));
        } catch (IOException e) {
            Logger.getLogger(LwjglGameView.class.getName()).warning("Cannot load default_level_data/apple.png");
        }
        return new TrueTypeFont(new Font("monospaced", Font.BOLD, 24), true, new char[0], fontSpecialCharacters);
    }

    @Override
    public StaticPlatformDrawer createStaticPlatformDrawer() {
        return new StaticPlatformDrawer();
    }

    @Override
    public Collection<String> listShaders() {
        return Collections.emptyList();
    }
}
