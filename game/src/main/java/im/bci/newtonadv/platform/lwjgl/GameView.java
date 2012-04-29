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

import im.bci.nanim.NanimParser;
import im.bci.nanim.NanimParser.Nanim;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.GameOverSequence;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.MenuSequence.Button;
import im.bci.newtonadv.game.QuestMenuSequence;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.StoryboardSequence;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.world.Axe;
import im.bci.newtonadv.world.AxeAnchor;
import im.bci.newtonadv.world.Bat;
import im.bci.newtonadv.world.Blocker;
import im.bci.newtonadv.world.Cloud;
import im.bci.newtonadv.world.Door;
import im.bci.newtonadv.world.DownLeftHalfPlatform;
import im.bci.newtonadv.world.DownRightHalfPlatform;
import im.bci.newtonadv.world.EgyptianBoss;
import im.bci.newtonadv.world.EgyptianBossHand;
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
import im.bci.newtonadv.world.PickedUpObject;
import im.bci.newtonadv.world.Platform;
import im.bci.newtonadv.world.UpLeftHalfPlatform;
import im.bci.newtonadv.world.UpRightHalfPlatform;
import im.bci.newtonadv.world.UsedKey;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.util.AbsoluteAABox;
import im.bci.newtonadv.world.PickableObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import im.bci.newtonadv.world.World;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;
import org.lwjgl.util.glu.GLU;

/**
 * 
 * @author devnewton
 */
public strictfp class GameView implements IGameView {

	private TextureCache textureCache;
	private ITrueTypeFont fpsFont;
	private GameViewQuality quality = GameViewQuality.DEFAULT;
	private final GameData data;

	public GameView(GameData data, Properties config) {
		this.data = data;
		initDisplay(config);
	}

	@Override
	public void toggleFullscreen() {
		try {
			Display.setFullscreen(!Display.isFullscreen());
		} catch (LWJGLException ex) {
			Logger.getLogger(GameView.class.getName()).log(Level.SEVERE, null,
					ex);
		}
	}

	private DisplayMode findGoodDisplayMode(final int targetHeight,
			final int targetWidth, final int targetBpp) {
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			java.util.Arrays.sort(modes, new Comparator<DisplayMode>() {

				@Override
				public int compare(DisplayMode a, DisplayMode b) {

					// test bpp
					if (a.getBitsPerPixel() >= targetBpp
							&& b.getBitsPerPixel() < targetBpp) {
						return -1;
					}
					if (a.getBitsPerPixel() < targetBpp
							&& b.getBitsPerPixel() >= targetBpp) {
						return 1;
					}

					// test resolution
					if (a.getWidth() == targetWidth
							&& a.getHeight() == targetHeight) {
						return -1;
					} else if (b.getWidth() == targetWidth
							&& b.getHeight() == targetHeight) {
						return 1;
					} else {
						Point pA = new Point(a.getWidth(), b.getHeight());
						Point pB = new Point(b.getWidth(), b.getHeight());
						Point pTarget = new Point(targetWidth, targetHeight);
						if (pA.distance(pTarget) < pB.distance(pTarget)) {
							return -1;
						} else if (pA.distance(pTarget) > pB.distance(pTarget)) {
							return 1;
						}
					}

					// test fullscreen capacity
					if (a.isFullscreenCapable() && !b.isFullscreenCapable()) {
						return -1;
					} else if (!a.isFullscreenCapable()
							&& b.isFullscreenCapable()) {
						return 1;
					} else {
						return 0;
					}

				}
			});

			if (modes.length > 0) {
				return modes[0];
			} else {
				return Display.getDesktopDisplayMode();
			}
		} catch (LWJGLException e) {
			Sys.alert("Error", "Unable to determine display modes.");
		}
		return Display.getDesktopDisplayMode();
	}

	String getDisplayModeInfos() {
		try {
			StringBuilder b = new StringBuilder();
			b.append("Adapter : ");
			b.append(Display.getAdapter());
			b.append('\n');
			b.append("Version : ");
			b.append(Display.getVersion());
			b.append('\n');
			b.append("Available display modes:");
			for (DisplayMode m : Display.getAvailableDisplayModes()) {
				b.append(m);
				b.append('\n');
			}
			return b.toString();
		} catch (LWJGLException ex) {
			return "Error cannot determine available display modes (" + ex
					+ ")";
		}
	}

	private void initDisplay(Properties config) {
		int targetWidth = Integer.parseInt(config.getProperty("view.width"));
		int targetHeight = Integer.parseInt(config.getProperty("view.height"));
		int targetBpp = Integer.parseInt(config.getProperty("view.bpp", ""
				+ Display.getDesktopDisplayMode().getBitsPerPixel()));
		boolean startFullscreen = Boolean.parseBoolean(config.getProperty(
				"view.fullscreen", "false"));
		GameViewQuality newQuality = GameViewQuality.valueOf(config
				.getProperty("view.quality"));

		DisplayMode chosenMode = findGoodDisplayMode(targetHeight, targetWidth,
				targetBpp);
		if (chosenMode == null) {
			Sys.alert("Error",
					"Unable to find appropriate display mode. Try to edit"
							+ PlatformFactory.getUserOrDefaultConfigFilePath()
							+ ".\n" + getDisplayModeInfos());
			System.exit(0);
		}
		setDisplayMode(startFullscreen, newQuality, chosenMode);
	}

	public void setDisplayMode(boolean startFullscreen,
			GameViewQuality newQuality, DisplayMode chosenMode) {

		if (Display.isFullscreen() == startFullscreen
				&& this.quality.equals(newQuality)
				&& Display.getDisplayMode().equals(chosenMode)) {
			return;
		}

		try {
			if (startFullscreen) {
				Display.setDisplayModeAndFullscreen(chosenMode);
			} else {
				Display.setDisplayMode(chosenMode);
			}
			Display.setResizable(true);
			Display.setTitle("Newton adventure");
			if (!Display.isCreated())
				Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			Sys.alert("Error", e + "\nUnable to create display. Try to edit "
					+ PlatformFactory.getUserOrDefaultConfigFilePath() + ".\n"
					+ getDisplayModeInfos());
			System.exit(0);
		}

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_DITHER);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, newQuality.toGL());
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 1);

		if (null == textureCache) {
			textureCache = new TextureCache(this.data);
		} else {
			textureCache.clearAll();
		}
		fpsFont = new TrueTypeFont(this.data);
		this.quality = newQuality;
	}

	@Override
	public void draw(Sequence sequence) {
		if (Display.isVisible() || Display.isDirty() || Display.wasResized()) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		sequence.draw();

		// now tell the screen to update
		Display.update();
		Display.sync(Game.FPS);

		// finally check if the user has requested that the display be
		// shutdown
		if (Display.isCloseRequested()) {
			close();
			System.exit(0);
		}
	}

	@Override
	public void drawFPS(int nbFps) {
		String fps = nbFps + " FPS";
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -1, 1);
		GL11.glTranslatef(Display.getWidth() - fpsFont.getWidth(fps),
				Display.getHeight() - 64, 0);
		fpsFont.drawString(fps);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	private void close() {
		textureCache.clearAll();
		Display.destroy();
	}

	@Override
	public ITextureCache getTextureCache() {
		return textureCache;
	}

	@Override
	public void drawPickableObject(PickableObject pickableObject,
			AnimationFrame texture, World world) {
		AABox bounds = pickableObject.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(pickableObject.getPosition().getX(), pickableObject
				.getPosition().getY(), 0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -bounds.getWidth() / 2.0f;
		final float x2 = bounds.getWidth() / 2.0f;
		final float y1 = -bounds.getHeight() / 2.0f;
		final float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

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
		GL11.glPopAttrib();
	}

	@Override
	public void drawAxe(Axe axe, AnimationFrame texture) {
		Box box = (Box) axe.getShape();
		Vector2f[] pts = box.getPoints(axe.getPosition(), axe.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
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
		GL11.glPopAttrib();

	}

	@Override
	public void drawAxeAnchor(AxeAnchor axeAnchor, float radius,
			ITexture texture) {
		GL11.glPushMatrix();
		GL11.glTranslatef(axeAnchor.getPosition().getX(), axeAnchor
				.getPosition().getY(), 0.0f);
		final float x1 = -radius;
		final float x2 = radius;
		final float y1 = -radius;
		final float y2 = radius;

		if (texture.hasAlpha()) {
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
					| GL11.GL_CURRENT_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		}

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
			GL11.glPopAttrib();
		}
	}

	@Override
	public void drawBat(Bat bat, float scale, AnimationFrame frame, World world) {
		AABox bounds = bat.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(bat.getPosition().getX(), bat.getPosition().getY(),
				0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		GL11.glScalef(scale, scale, 1);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.getImage().getId());

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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawBlocker(Blocker blocker, AnimationFrame frame, float alpha) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
				| GL11.GL_CURRENT_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(blocker, frame);
		GL11.glPopAttrib();
	}

	@Override
	public void drawPlatform(Platform platform, AnimationFrame frame) {
		Box box = (Box) platform.getShape();
		Vector2f[] pts = box.getPoints(platform.getPosition(),
				platform.getRotation());

		if (frame.getImage().hasAlpha()) {
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.getImage().getId());
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(frame.getU1(), frame.getV2());
		GL11.glVertex2f(pts[0].x, pts[0].y);
		GL11.glTexCoord2f(frame.getU2(), frame.getV2());
		GL11.glVertex2f(pts[1].x, pts[1].y);
		GL11.glTexCoord2f(frame.getU2(), frame.getV1());
		GL11.glVertex2f(pts[2].x, pts[2].y);
		GL11.glTexCoord2f(frame.getU1(), frame.getV1());
		GL11.glVertex2f(pts[3].x, pts[3].y);
		GL11.glEnd();
		if (frame.getImage().hasAlpha()) {
			GL11.glPopAttrib();
		}
	}

	@Override
	public void drawMovingPlatform(MovingPlatform platform, AnimationFrame texture) {
		Box box = (Box) platform.getShape();
		Vector2f[] pts = box.getPoints(platform.getPosition(),
				platform.getRotation());

		if (texture.getImage().hasAlpha()) {
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
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
			GL11.glPopAttrib();
		}
	}

	@Override
	public void drawCloud(Cloud cloud, AnimationFrame frame, float alpha) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
				| GL11.GL_CURRENT_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(cloud, frame);
		GL11.glPopAttrib();

	}

	@Override
	public void drawDoor(Door door, AnimationFrame texture) {
		Box box = (Box) door.getShape();
		Vector2f[] pts = box.getPoints(door.getPosition(), door.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
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
		GL11.glPopAttrib();
	}

	@Override
	public void drawDownLeftHalfPlatform(DownLeftHalfPlatform platform,
			AnimationFrame texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2f(texture.getU1(), texture.getV1());
		GL11.glVertex2f(pts[0].x, pts[0].y);
		GL11.glTexCoord2f(texture.getU1(), texture.getV2());
		GL11.glVertex2f(pts[1].x, pts[1].y);
		GL11.glTexCoord2f(texture.getU2(), texture.getV2());
		GL11.glVertex2f(pts[2].x, pts[2].y);
		GL11.glEnd();
		GL11.glPopAttrib();
	}

	@Override
	public void drawDownRightHalfPlatform(DownRightHalfPlatform platform,
			AnimationFrame texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2f(texture.getU1(), texture.getV2());
		GL11.glVertex2f(pts[0].x, pts[0].y);
		GL11.glTexCoord2f(texture.getU2(), texture.getV2());
		GL11.glVertex2f(pts[1].x, pts[1].y);
		GL11.glTexCoord2f(texture.getU2(), texture.getV1());
		GL11.glVertex2f(pts[2].x, pts[2].y);
		GL11.glEnd();
		GL11.glPopAttrib();
	}

	@Override
	public void drawUpLeftHalfPlatform(UpLeftHalfPlatform platform,
			AnimationFrame texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.9999f);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2f(texture.getU1(), texture.getV1());
		GL11.glVertex2f(pts[0].x, pts[0].y);
		GL11.glTexCoord2f(texture.getU1(), texture.getV2());
		GL11.glVertex2f(pts[1].x, pts[1].y);
		GL11.glTexCoord2f(texture.getU2(), texture.getV1());
		GL11.glVertex2f(pts[2].x, pts[2].y);
		GL11.glEnd();
		GL11.glPopAttrib();
	}

	@Override
	public void drawUpRightHalfPlatform(UpRightHalfPlatform platform,
			AnimationFrame texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glTexCoord2f(texture.getU1(), texture.getV1());
		GL11.glVertex2f(pts[0].x, pts[0].y);
		GL11.glTexCoord2f(texture.getU2(), texture.getV2());
		GL11.glVertex2f(pts[1].x, pts[1].y);
		GL11.glTexCoord2f(texture.getU2(), texture.getV1());
		GL11.glVertex2f(pts[2].x, pts[2].y);
		GL11.glEnd();
		GL11.glPopAttrib();
	}

	@Override
	public void drawExplosion(Explosion explosion, AnimationFrame frame,
			World world) {
		GL11.glPushMatrix();
		GL11.glTranslatef(explosion.getPosition().getX(), explosion
				.getPosition().getY(), 0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -explosion.getSize() / 2.0f;
		final float x2 = explosion.getSize() / 2.0f;
		final float y1 = -explosion.getSize() / 2.0f;
		final float y2 = explosion.getSize() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.getImage().getId());

		final float u1 = frame.getU1(), v1 = frame.getV1(), u2 = frame.getU2(), v2 = frame
				.getV2();
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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawFireBall(FireBall fireball, AnimationFrame texture,
			World world) {
		GL11.glPushMatrix();
		ROVector2f pos = fireball.getPosition();
		GL11.glTranslatef(pos.getX(), pos.getY(), 0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -fireball.getSize() / 2.0f;
		final float x2 = fireball.getSize() / 2.0f;
		final float y1 = -fireball.getSize() / 2.0f;
		final float y2 = fireball.getSize() / 2.0f;

		if (texture.getImage().hasAlpha()) {
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
					| GL11.GL_CURRENT_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

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
			GL11.glPopAttrib();
		}

	}

	@Override
	public void drawHero(Hero hero, AnimationFrame frame, World world,
			float scale) {
		AABox bounds = hero.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(hero.getPosition().getX(), hero.getPosition().getY(),
				0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		GL11.glScalef(scale, scale, 1);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.getImage().getId());

		final float v1 = frame.getV1(), v2 = frame.getV2();
		final float u1, u2;
		if (hero.isLookingLeft()) {
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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawKey(Key key, AnimationFrame texture, World world) {
		AABox bounds = key.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(key.getPosition().getX(), key.getPosition().getY(),
				0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -bounds.getWidth() / 2.0f;
		final float x2 = bounds.getWidth() / 2.0f;
		final float y1 = -bounds.getHeight() / 2.0f;
		final float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

		final float u1 = texture.getU1(), u2 = texture.getU2();
		final float v1 = texture.getU1(), v2 = texture.getU2();
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
		GL11.glPopAttrib();

	}

	@Override
	public void drawLosedApple(LosedApple apple, World world,
			AnimationFrame texture, float alpha) {
		GL11.glPushMatrix();
		GL11.glTranslatef(apple.getPosition().getX(), apple.getPosition()
				.getY(), 0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -apple.getSize() / 2.0f;
		final float x2 = apple.getSize() / 2.0f;
		final float y1 = -apple.getSize() / 2.0f;
		final float y2 = apple.getSize() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
				| GL11.GL_CURRENT_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawMobilePikeAnchor(MobilePikeAnchor anchor, ITexture texture) {
		GL11.glPushMatrix();
		GL11.glTranslatef(anchor.getPosition().getX(), anchor.getPosition()
				.getY(), 0.0f);
		final float x1 = -anchor.getRadius();
		final float x2 = anchor.getRadius();
		final float y1 = -anchor.getRadius();
		final float y2 = anchor.getRadius();

		if (texture.hasAlpha()) {
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
					| GL11.GL_CURRENT_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());

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
			GL11.glPopAttrib();
		}
	}

	@Override
	public void drawMobilePikes(MobilePikes pikes, AnimationFrame texture) {
		Box box = (Box) pikes.getShape();
		Vector2f[] pts = box
				.getPoints(pikes.getPosition(), pikes.getRotation());

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
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
		GL11.glPopAttrib();
	}

	@Override
	public void drawMummy(Mummy mummy, World world, AnimationFrame frame,
			float scale) {
		AABox bounds = mummy.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(mummy.getPosition().getX(), mummy.getPosition()
				.getY(), 0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		GL11.glScalef(scale, scale, 1);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, frame.getImage().getId());

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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawPickedUpObject(PickedUpObject pickedUpObject, World world,
			AnimationFrame texture) {
		GL11.glPushMatrix();
		GL11.glTranslatef(pickedUpObject.getPosition().getX(), pickedUpObject
				.getPosition().getY(), 0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -pickedUpObject.getSize() / 2.0f;
		final float x2 = pickedUpObject.getSize() / 2.0f;
		final float y1 = -pickedUpObject.getSize() / 2.0f;
		final float y2 = pickedUpObject.getSize() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawUsedKey(UsedKey key, AnimationFrame texture, World world) {
		GL11.glPushMatrix();
		GL11.glTranslatef(key.getPosition().getX(), key.getPosition().getY(),
				0.0f);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		float x1 = -key.getSize() / 2.0f;
		final float x2 = key.getSize() / 2.0f;
		final float y1 = -key.getSize() / 2.0f;
		final float y2 = key.getSize() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

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
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawEgyptianBoss(EgyptianBoss boss, ITexture texture,
			boolean isHurtBlinkState) {
		AABox bounds = boss.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(boss.getPosition().getX(), boss.getPosition().getY(),
				0.0f);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());

		if (isHurtBlinkState) {
			GL11.glColor3f(1, 0, 0);
		}
		final float u1 = 1, u2 = 0;
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
		if (isHurtBlinkState) {
			GL11.glColor3f(1, 1, 1);
		}
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawEgyptianBossHand(EgyptianBossHand hand, ITexture texture) {
		AABox bounds = hand.getShape().getBounds();

		GL11.glPushMatrix();
		GL11.glTranslatef(hand.getPosition().getX(), hand.getPosition().getY(),
				0.0f);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());

		final float u1 = 1, u2 = 0;
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
		GL11.glPopAttrib();
		GL11.glPopMatrix();

	}

	@Override
	public void drawStoryBoardSequence(StoryboardSequence sequence,
			ITrueTypeFont font) {
		if (Display.isVisible() || Display.wasResized() || Display.isDirty()
				|| sequence.isDirty()) {
			sequence.setDirty(false);

			GL11.glPushMatrix();
			GLU.gluOrtho2D(StoryboardSequence.ortho2DLeft,
					StoryboardSequence.ortho2DRight,
					StoryboardSequence.ortho2DBottom,
					StoryboardSequence.ortho2DTop);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureCache()
					.getTexture(sequence.getTexture()).getId());
			final float x1 = StoryboardSequence.ortho2DLeft;
			final float x2 = StoryboardSequence.ortho2DRight;
			final float y1 = StoryboardSequence.ortho2DBottom;
			final float y2 = StoryboardSequence.ortho2DTop;
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
			drawContinueText(font);
			GL11.glPopMatrix();
		}
	}

	private void drawContinueText(ITrueTypeFont font) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight(),
				"Press enter to continue ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
		GL11.glPopAttrib();
	}

	private void drawGameOverText(ITrueTypeFont font) {
		drawContinueText(font);
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight() * 2,
				"Press up to retry ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
		GL11.glPopAttrib();
	}

	@Override
	public void drawScoreSequence(ScoreSequence sequence, ITrueTypeFont font,
			QuestScore questScore, long scorePerCentToShow) {
		if (Display.isVisible() || Display.isDirty() || Display.wasResized()
				|| sequence.isDirty()) {
			sequence.setDirty(false);
			GL11.glPushMatrix();
			GLU.gluOrtho2D(ScoreSequence.ortho2DLeft,
					ScoreSequence.ortho2DRight, ScoreSequence.ortho2DBottom,
					ScoreSequence.ortho2DTop);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
			int i = 1;
			font.drawString(
					(ScoreSequence.ortho2DLeft + ScoreSequence.ortho2DRight) / 2.0f,
					i++ * font.getHeight(), "SCORES", 1, -1,
					ITrueTypeFont.ALIGN_CENTER);
			font.drawString(0, i++ * font.getHeight(),
					questScore.getQuestName(), 1, -1, ITrueTypeFont.ALIGN_LEFT);
			for (Entry<String, LevelScore> levelEntry : questScore.entrySet()) {
				String levelScoreStr = levelEntry.getKey()
						+ ": "
						+ (scorePerCentToShow
								* levelEntry.getValue().computeScore() / 100);
				font.drawString(
						(ScoreSequence.ortho2DLeft + ScoreSequence.ortho2DRight) / 2.0f,
						i++ * font.getHeight(), levelScoreStr, 1, -1,
						ITrueTypeFont.ALIGN_CENTER);
			}
			String questScoreStr = "Quest total: "
					+ (scorePerCentToShow * questScore.computeScore() / 100);
			font.drawString(0, i++ * font.getHeight(), questScoreStr, 1, -1,
					ITrueTypeFont.ALIGN_LEFT);
			font.drawString(ScoreSequence.ortho2DRight,
					ScoreSequence.ortho2DBottom - font.getHeight() * 2,
					"Press enter to send score to server ", 1, -1,
					ITrueTypeFont.ALIGN_RIGHT);
			font.drawString(ScoreSequence.ortho2DRight,
					ScoreSequence.ortho2DBottom - font.getHeight(),
					"Press right to skip ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
	}

	@Override
	public void drawGameOverSequence(GameOverSequence sequence,
			ITrueTypeFont font) {
		if (Display.isVisible() || Display.wasResized() || Display.isDirty()
				|| sequence.isDirty()) {
			sequence.setDirty(false);

			GL11.glPushMatrix();
			GLU.gluOrtho2D(StoryboardSequence.ortho2DLeft,
					StoryboardSequence.ortho2DRight,
					StoryboardSequence.ortho2DBottom,
					StoryboardSequence.ortho2DTop);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureCache()
					.getTexture(sequence.getTexture()).getId());
			final float x1 = StoryboardSequence.ortho2DLeft;
			final float x2 = StoryboardSequence.ortho2DRight;
			final float y1 = StoryboardSequence.ortho2DBottom;
			final float y2 = StoryboardSequence.ortho2DTop;
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
			drawGameOverText(font);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void drawSnowLayer(SnowLayer layer) {
		GL11.glPushMatrix();

		float aspectRatio = (float) Display.getWidth()
				/ (float) Display.getHeight();
		GLU.gluOrtho2D(SnowLayer.ortho2DLeft * aspectRatio,
				SnowLayer.ortho2DRight * aspectRatio, SnowLayer.ortho2DBottom,
				SnowLayer.ortho2DTop);
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
	public void drawLevelIndicators(String indicators,
			ITrueTypeFont indicatorsFont) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -1, 1);
		GL11.glTranslatef(0, Display.getHeight() - indicatorsFont.getHeight(),
				0);
		indicatorsFont.drawString(indicators);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@Override
	public void drawMenuSequence(MenuSequence sequence) {
		if (Display.isVisible() || Display.isDirty() || Display.wasResized()
				|| sequence.isDirty()) {
			sequence.setDirty(false);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GLU.gluOrtho2D(MenuSequence.ortho2DLeft, MenuSequence.ortho2DRight,
					MenuSequence.ortho2DBottom, MenuSequence.ortho2DTop);

			if (sequence.getBackgroundImage() != null) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureCache()
						.getTexture(sequence.getBackgroundImage()).getId());
				final float x1 = MenuSequence.ortho2DLeft;
				final float x2 = MenuSequence.ortho2DRight;
				final float y1 = MenuSequence.ortho2DBottom;
				final float y2 = MenuSequence.ortho2DTop;
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
			} else {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			}

			GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
			GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or
												// transperancy
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F); // sets aplha function
			for (Button b : sequence.getButtons()) {
				b.draw();
			}
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	@Override
	public void drawButton(Button button) {
		if (button.currentTexture != null) {
			ITexture texture = getTextureCache().getTexture(
					button.currentTexture);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
			final float x1 = button.x;
			final float x2 = button.x
					+ (button.w > 0 ? button.w : texture.getWidth());
			final float y1 = button.y
					+ (button.h > 0 ? button.h : texture.getHeight());
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
		}
	}

	@Override
	public void drawMenuButton(Button button, ITrueTypeFont questNameFont,
			String questName) {
		drawButton(button);
		GL11.glPushMatrix();
		GL11.glTranslatef(button.x,
				button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT
						+ questNameFont.getHeight(), 0);
		GL11.glScalef(1, -1, 1);
		questNameFont.drawString(questName);
		GL11.glPopMatrix();
	}

	@Override
	public void drawWorld(World world) {
		GL11.glPushMatrix();

		float aspectRatio = (float) Display.getWidth()
				/ (float) Display.getHeight();
		GLU.gluOrtho2D(World.ortho2DLeft * aspectRatio, World.ortho2DRight
				* aspectRatio, World.ortho2DBottom, World.ortho2DTop);

		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
				1.0f);
		drawWorldBackground(world, aspectRatio);

		ROVector2f heroPos = world.getHero().getPosition();
		GL11.glTranslatef(-heroPos.getX(), -heroPos.getY(), 0.0f);

		final float cameraSize = World.ortho2DBaseSize * 1.5f;
		final BodyList visibleBodies = world.getVisibleBodies(heroPos.getX()
				- cameraSize, heroPos.getY() - cameraSize, heroPos.getX()
				+ cameraSize, heroPos.getY() + cameraSize);

		ArrayList<Drawable> drawableBodies = new ArrayList<Drawable>();
		for (int i = 0; i < visibleBodies.size(); i++) {
			Body body = visibleBodies.get(i);
			if (body instanceof Drawable) {
				drawableBodies.add(((Drawable) body));
			}
		}
		java.util.Collections.sort(drawableBodies, Drawable.comparator);
		for (Drawable drawableBody : drawableBodies)
			drawableBody.draw();

		world.getTopLevelEntities().draw();
		GL11.glPopMatrix();
	}

	private void drawWorldBackground(World world, float aspectRatio) {
		GL11.glPushMatrix();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, world.getBackgroundTexture()
				.getId());

		ROVector2f heroPos = world.getHero().getPosition();

		AbsoluteAABox worldStaticBounds = world.getStaticBounds();
		AbsoluteAABox staticBounds = new AbsoluteAABox();
		staticBounds.x1 = World.ortho2DLeft * aspectRatio * 2.0f;
		staticBounds.x2 = World.ortho2DRight * aspectRatio * 2.0f;
		staticBounds.y1 = World.ortho2DBottom * aspectRatio * 2.0f;
		staticBounds.y2 = World.ortho2DTop * aspectRatio * 2.0f;
		float xt = -heroPos.getX()
				* (staticBounds.getWidth() / worldStaticBounds.getWidth());
		float yt = -heroPos.getY()
				* (staticBounds.getHeight() / worldStaticBounds.getHeight());

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
	}

	@Override
	public ITrueTypeFont createStoryBoardSequenceFont() {
		return new TrueTypeFont(this.data,
				new Font("monospaced", Font.BOLD, 32), false);
	}

	@Override
	public ITrueTypeFont createQuestNameFont() {
		return new TrueTypeFont(this.data);
	}

	@Override
	public ITrueTypeFont createAppleFont(String questName, String levelName) {
		HashMap<Character, String> fontSpecialCharacters = new HashMap<Character, String>();
		fontSpecialCharacters.put('$',
				data.getLevelFilePath(questName, levelName, "apple.png"));
		return new TrueTypeFont(this.data, fontSpecialCharacters);
	}

	@Override
	public ITrueTypeFont createScoreSequenceFont() {
		return new TrueTypeFont(this.data,
				new Font("monospaced", Font.BOLD, 32), false);
	}

	@Override
	public AnimationCollection loadFromAnimation(String name)
			throws IOException {
		if (name.endsWith("gif")) {
			GifDecoder d = new GifDecoder();
			d.read(data.openFile(name));
			Animation animation = new Animation();
			int n = d.getFrameCount();
			for (int i = 0; i < n; i++) {
				BufferedImage frameImage = d.getFrame(i); // frame i
				int t = d.getDelay(i); // display duration of frame in
										// milliseconds
				animation.addFrame(
						textureCache.createTexture(name + '#' + i, frameImage),
						t);
			}
			AnimationCollection collection = new AnimationCollection();
			collection.addAnimation(animation);
			return collection;
		} else if (name.endsWith("nanim")) {
			InputStream is = data.openFile(name);
			try {
				Nanim nanim = NanimParser.Nanim.parseFrom(is);
				Map<String, ITexture> textures = textureCache.getTextures(name,
						nanim);
				return new AnimationCollection(nanim, textures);
			} finally {
				is.close();
			}
		} else {
			return new AnimationCollection(textureCache.getTexture(name));
		}
	}

	@Override
	public void drawFadeSequence(float r, float g, float b, float a) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
				| GL11.GL_CURRENT_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 1, 0, 1, -1, 1);
		GL11.glColor4f(r, g, b, a);
		GL11.glRectf(0, 0, 1, 1);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	@Override
	public void drawMainMenuSequence(MainMenuSequence mainMenuSequence) {
		drawMenuSequence(mainMenuSequence);
	}

	public GameViewQuality getQuality() {
		return quality;
	}

	@Override
	public float getWidth() {
		return Display.getWidth();
	}

	@Override
	public float getHeight() {
		return Display.getHeight();
	}

	static final float minimapSize = 32;

	@Override
	public void drawMinimap(World world, ITexture minimapTexture) {
		if (!world.getHero().hasMap() && !world.getHero().hasCompass()) {
			return;
		}
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 100, 0, 100, -1, 1);
		GL11.glTranslatef(100 - minimapSize / 1.5f, minimapSize / 1.5f, 0);

		GL11.glPushMatrix();
		GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
				1.0f);
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
			drawMinimapIcon(world, world.getHero().getPosition(), world
					.getHero().getAnimation().getCurrentFrame());
			for (Key key : world.getKeys())
				drawMinimapIcon(world, key.getPosition(), key.getTexture()
						.getFirst().getCurrentFrame());
		}
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}

	public void drawMinimapIcon(World world, ROVector2f worldPos,
			AnimationFrame texture) {
		float iconW = World.distanceUnit * 8.0f;
		float iconH = World.distanceUnit * 8.0f;

		final float icon_x1 = -iconW / 2.0f;
		final float icon_x2 = iconW / 2.0f;
		final float icon_y1 = -iconH / 2.0f;
		final float icon_y2 = iconH / 2.0f;
		float icon_u1 = texture.getU1(), icon_u2 = texture.getU2();
		float icon_v1 = texture.getV1(), icon_v2 = texture.getV2();
		GL11.glPushMatrix();
		GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
				1.0f);
		final float miniMapPlatformSize = minimapSize * 4.0f / 256.0f;// harcoded,
																		// that's
																		// bad!
		GL11.glScalef(miniMapPlatformSize / (World.distanceUnit * 2.0f),
				miniMapPlatformSize / (World.distanceUnit * 2.0f), 1);
		GL11.glTranslatef(worldPos.getX(), worldPos.getY(), 0);
		GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());
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
	public void drawKeyLock(KeyLock keyLock, AnimationFrame frame, float alpha) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT
				| GL11.GL_CURRENT_BIT);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(keyLock, frame);
		GL11.glPopAttrib();
	}
}
