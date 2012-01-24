package im.bci.newtonadv.platform.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Properties;
import java.util.Map.Entry;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.GameOverSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.QuestMenuSequence;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.StoryboardSequence;
import im.bci.newtonadv.game.MenuSequence.Button;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.util.AbsoluteAABox;
import im.bci.newtonadv.world.Apple;
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
import im.bci.newtonadv.world.LosedApple;
import im.bci.newtonadv.world.MobilePikeAnchor;
import im.bci.newtonadv.world.MobilePikes;
import im.bci.newtonadv.world.Mummy;
import im.bci.newtonadv.world.PickedUpApple;
import im.bci.newtonadv.world.Platform;
import im.bci.newtonadv.world.UpLeftHalfPlatform;
import im.bci.newtonadv.world.UpRightHalfPlatform;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;

public class AndroidGameView implements IGameView {

	private AndroidTextureCache textureCache;
	private ITrueTypeFont fpsFont;
	private AndroidGameViewQuality quality;
	private GL10 gl;
	private int viewPortWidth;
	private int viewPortHeight;
	private AssetManager assets;
	private Properties config;

	public AndroidGameView(AssetManager assets, Properties config) {
		this.quality = AndroidGameViewQuality.valueOf(config
				.getProperty("view.quality"));
		this.assets = assets;
		this.config = config;
	}

	@Override
	public void toggleFullscreen() {
	}

	private void initDisplay(Properties config) {

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_DITHER);
		gl.glDisable(GL10.GL_FOG);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glShadeModel(GL10.GL_FLAT);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glColor4f(1, 1, 1, 1);

		textureCache = new AndroidTextureCache(assets);
		fpsFont = new AndroidTrueTypeFont();
	}

	@Override
	public void draw(Sequence sequence) {
		sequence.draw();
	}

	@Override
	public void drawFPS(int nbFps) {
		String fps = nbFps + " FPS";
		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthox(0, viewPortWidth, 0, viewPortHeight, -1, 1);
		gl.glTranslatef(viewPortWidth - fpsFont.getWidth(fps),
				viewPortHeight - 64, 0);
		fpsFont.drawString(fps);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public ITextureCache getTextureCache() {
		return textureCache;
	}

	public void drawTexturedQuad(ITexture texture, float vert[], float tex[]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getId());

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		ByteBuffer bb = ByteBuffer.allocateDirect(vert.length
				* (Float.SIZE / Byte.SIZE));
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer vb = bb.asFloatBuffer();
		vb.put(vert);
		vb.position(0);
		
		bb = ByteBuffer.allocateDirect(tex.length
				* (Float.SIZE / Byte.SIZE));
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer tb = bb.asFloatBuffer();
		tb.put(tex);
		tb.position(0);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vb);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vert.length / 2);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public void drawTexturedTriangle(ITexture texture, float vert[],
			float tex[]) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getId());

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, FloatBuffer.wrap(vert));
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, FloatBuffer.wrap(tex));

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public void drawApple(Apple apple, ITexture texture, World world) {
		AABox bounds = apple.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(apple.getPosition().getX(), apple.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -bounds.getWidth() / 2.0f;
		final float x2 = bounds.getWidth() / 2.0f;
		final float y1 = -bounds.getHeight() / 2.0f;
		final float y2 = bounds.getHeight() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		/*
		 * final float u1 = 0.0f, u2 = 1.0f; gl.glBegin(gl.GL_QUADS);
		 * gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1, y2); gl.glTexCoord2f(u2,
		 * 0.0f); gl.glVertex2f(x2, y2); gl.glTexCoord2f(u2, 1.0f);
		 * gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1, 1.0f); gl.glVertex2f(x1,
		 * y1); gl.glEnd();
		 */
		float tex[] = { 0, 0, 1, 0, 1, 1, 0, 1 };
		float vert[] = { x1, y1, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawAxe(Axe axe, ITexture texture) {
		Box box = (Box) axe.getShape();
		Vector2f[] pts = box.getPoints(axe.getPosition(), axe.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[3].x, pts[3].y); gl.glEnd();
		 */
		float tex[] = { 0, 1, 1, 1, 1, 0, 0, 0 };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawTexturedQuad(texture, vert, tex);

		gl.glDisable(GL10.GL_ALPHA_TEST);

	}

	@Override
	public void drawAxeAnchor(AxeAnchor axeAnchor, float radius,
			ITexture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(axeAnchor.getPosition().getX(), axeAnchor.getPosition()
				.getY(), 0.0f);
		final float x1 = -radius;
		final float x2 = radius;
		final float y1 = -radius;
		final float y2 = radius;

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		final float u1 = 0.18f, u2 = 0.8f;
		final float v1 = 0.2f, v2 = 0.8f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, v1); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, v1); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, v2); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * v2); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float tex[] = { u1, v1, u2, v1, u2, v2, u1, v2 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void drawBat(Bat bat, float scale, ITexture texture, World world) {
		AABox bounds = bat.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(bat.getPosition().getX(), bat.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		gl.glScalef(scale, scale, 1);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		final float u1 = 1, u2 = 0;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glPopMatrix();

		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawBlocker(Blocker blocker, ITexture texture, float alpha) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(blocker, texture);
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawPlatform(Platform platform, ITexture texture) {
		Box box = (Box) platform.getShape();
		Vector2f[] pts = box.getPoints(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.0f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[3].x, pts[3].y); gl.glEnd();
		 */
		float tex[] = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawCloud(Cloud cloud, ITexture texture, float alpha) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(cloud, texture);
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawDoor(Door door, ITexture texture) {
		Box box = (Box) door.getShape();
		Vector2f[] pts = box.getPoints(door.getPosition(), door.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[3].x, pts[3].y); gl.glEnd();
		 */
		float tex[] = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawDownLeftHalfPlatform(DownLeftHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_TRIANGLES); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glEnd();
		 */
		float tex[] = { 0, 0, 0, 1, 1, 1 };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		drawTexturedTriangle(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawDownRightHalfPlatform(DownRightHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_TRIANGLES); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glEnd();
		 */
		float tex[] = { 0, 1, 1, 1, 1, 0 };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		drawTexturedTriangle(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawUpLeftHalfPlatform(UpLeftHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_TRIANGLES); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glEnd();
		 */
		float tex[] = { 0, 0, 0, 1, 1, 0 };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		drawTexturedTriangle(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawUpRightHalfPlatform(UpRightHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_TRIANGLES); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glEnd();
		 */
		float tex[] = { 0, 0, 1, 1, 1, 0 };
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		drawTexturedTriangle(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawExplosion(Explosion explosion, ITexture texture, World world) {
		gl.glPushMatrix();
		gl.glTranslatef(explosion.getPosition().getX(), explosion.getPosition()
				.getY(), 0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -explosion.getSize() / 2.0f;
		final float x2 = explosion.getSize() / 2.0f;
		final float y1 = -explosion.getSize() / 2.0f;
		final float y2 = explosion.getSize() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

	@Override
	public void drawFireBall(FireBall fireball, ITexture texture, World world) {
		gl.glPushMatrix();
		ROVector2f pos = fireball.getPosition();
		gl.glTranslatef(pos.getX(), pos.getY(), 0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -fireball.getSize() / 2.0f;
		final float x2 = fireball.getSize() / 2.0f;
		final float y1 = -fireball.getSize() / 2.0f;
		final float y2 = fireball.getSize() / 2.0f;

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_BLEND);
		gl.glPopMatrix();

	}

	@Override
	public void drawHero(Hero hero, ITexture texture, World world, float scale) {
		AABox bounds = hero.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(hero.getPosition().getX(), hero.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		gl.glScalef(scale, scale, 1);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		// gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		float u1, u2;
		if (hero.isLookingLeft()) {
			u1 = 1.0f;
			u2 = 0.0f;
		} else {
			u1 = 0.0f;
			u2 = 1.0f;
		}
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

	@Override
	public void drawKey(Key key, ITexture texture, World world) {
		AABox bounds = key.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(key.getPosition().getX(), key.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -bounds.getWidth() / 2.0f;
		final float x2 = bounds.getWidth() / 2.0f;
		final float y1 = -bounds.getHeight() / 2.0f;
		final float y2 = bounds.getHeight() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();

	}

	@Override
	public void drawLosedApple(LosedApple apple, World world, ITexture texture,
			float alpha) {
		gl.glPushMatrix();
		gl.glTranslatef(apple.getPosition().getX(), apple.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -apple.getSize() / 2.0f;
		final float x2 = apple.getSize() / 2.0f;
		final float y1 = -apple.getSize() / 2.0f;
		final float y2 = apple.getSize() / 2.0f;

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glPopMatrix();
	}

	@Override
	public void drawMobilePikeAnchor(MobilePikeAnchor anchor, ITexture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(anchor.getPosition().getX(), anchor.getPosition()
				.getY(), 0.0f);
		final float x1 = -anchor.getRadius();
		final float x2 = anchor.getRadius();
		final float y1 = -anchor.getRadius();
		final float y2 = anchor.getRadius();

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		final float u1 = 0.18f, u2 = 0.8f;
		final float v1 = 0.2f, v2 = 0.8f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, v1); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, v1); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, v2); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * v2); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, v1, u2, v1, u2, v2, u1, v2 };
		drawTexturedQuad(texture, vert, tex);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);
		// gl.glPopAttrib();
	}

	@Override
	public void drawMobilePikes(MobilePikes pikes, ITexture texture) {
		Box box = (Box) pikes.getShape();
		Vector2f[] pts = box
				.getPoints(pikes.getPosition(), pikes.getRotation());

		// gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(0.0f, 1.0f);
		 * gl.glVertex2f(pts[0].x, pts[0].y); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(pts[1].x, pts[1].y); gl.glTexCoord2f(1.0f, 0.0f);
		 * gl.glVertex2f(pts[2].x, pts[2].y); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(pts[3].x, pts[3].y); gl.glEnd();
		 */
		float vert[] = { 0, 1, 1, 1, 1, 0, 0, 0 };
		float tex[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawTexturedQuad(texture, vert, tex);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawMummy(Mummy mummy, World world, ITexture texture,
			float scale) {
		AABox bounds = mummy.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(mummy.getPosition().getX(), mummy.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		gl.glScalef(scale, scale, 1);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);

		float u1, u2;
		if (mummy.isLookingLeft()) {
			u1 = 1.0f;
			u2 = 0.0f;
		} else {
			u1 = 0.0f;
			u2 = 1.0f;
		}
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

	@Override
	public void drawPickedUpApple(PickedUpApple apple, World world,
			ITexture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(apple.getPosition().getX(), apple.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -apple.getSize() / 2.0f;
		final float x2 = apple.getSize() / 2.0f;
		final float y1 = -apple.getSize() / 2.0f;
		final float y2 = apple.getSize() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

	@Override
	public void drawUsedKey(UsedKey key, ITexture texture, World world) {
		gl.glPushMatrix();
		gl.glTranslatef(key.getPosition().getX(), key.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		float x1 = -key.getSize() / 2.0f;
		final float x2 = key.getSize() / 2.0f;
		final float y1 = -key.getSize() / 2.0f;
		final float y2 = key.getSize() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

	@Override
	public void drawEgyptianBoss(EgyptianBoss boss, ITexture bodyTexture,
			boolean isHurtBlinkState) {
		AABox bounds = boss.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(boss.getPosition().getX(), boss.getPosition().getY(),
				0.0f);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f); // sets aplha function

		if (isHurtBlinkState) {
			gl.glColor4f(1, 0, 0, 1);
		}
		final float u1 = 1, u2 = 0;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(bodyTexture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		if (isHurtBlinkState) {
			gl.glColor4f(1, 1, 1, 1);
		}
		gl.glPopMatrix();
	}

	@Override
	public void drawEgyptianBossHand(EgyptianBossHand hand, ITexture texture) {
		AABox bounds = hand.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(hand.getPosition().getX(), hand.getPosition().getY(),
				0.0f);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);

		final float u1 = 1, u2 = 0;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(texture, vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();

	}

	@Override
	public void drawStoryBoardSequence(StoryboardSequence sequence,
			ITrueTypeFont font) {
		sequence.setDirty(false);

		gl.glPushMatrix();
		gl.glOrthof(StoryboardSequence.ortho2DLeft,
				StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom,
				StoryboardSequence.ortho2DTop, -1, 1);
		final float x1 = StoryboardSequence.ortho2DLeft;
		final float x2 = StoryboardSequence.ortho2DRight;
		final float y1 = StoryboardSequence.ortho2DBottom;
		final float y2 = StoryboardSequence.ortho2DTop;
		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(getTextureCache().getTexture(sequence.getTexture()),
				vert, tex);
		drawContinueText(font);
		gl.glPopMatrix();
	}

	private void drawContinueText(ITrueTypeFont font) {
		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight(),
				"Press enter to continue ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	private void drawGameOverText(ITrueTypeFont font) {
		drawContinueText(font);
		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight() * 2,
				"Press up to retry ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawScoreSequence(ScoreSequence sequence, ITrueTypeFont font,
			QuestScore questScore, long scorePerCentToShow) {
		sequence.setDirty(false);
		gl.glPushMatrix();
		gl.glOrthof(ScoreSequence.ortho2DLeft, ScoreSequence.ortho2DRight,
				ScoreSequence.ortho2DBottom, ScoreSequence.ortho2DTop, -1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		int i = 1;
		font.drawString(
				(ScoreSequence.ortho2DLeft + ScoreSequence.ortho2DRight) / 2.0f,
				i++ * font.getHeight(), "SCORES", 1, -1,
				ITrueTypeFont.ALIGN_CENTER);
		font.drawString(0, i++ * font.getHeight(), questScore.getQuestName(),
				1, -1, ITrueTypeFont.ALIGN_LEFT);
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
		font.drawString(ScoreSequence.ortho2DRight, ScoreSequence.ortho2DBottom
				- font.getHeight() * 2, "Press enter to send score to server ",
				1, -1, ITrueTypeFont.ALIGN_RIGHT);
		font.drawString(ScoreSequence.ortho2DRight, ScoreSequence.ortho2DBottom
				- font.getHeight(), "Press right to skip ", 1, -1,
				ITrueTypeFont.ALIGN_RIGHT);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawGameOverSequence(GameOverSequence sequence,
			ITrueTypeFont font) {
		sequence.setDirty(false);

		gl.glPushMatrix();
		gl.glOrthof(StoryboardSequence.ortho2DLeft,
				StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom,
				StoryboardSequence.ortho2DTop, -1, 1);
		final float x1 = StoryboardSequence.ortho2DLeft;
		final float x2 = StoryboardSequence.ortho2DRight;
		final float y1 = StoryboardSequence.ortho2DBottom;
		final float y2 = StoryboardSequence.ortho2DTop;
		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(getTextureCache().getTexture(sequence.getTexture()),
				vert, tex);
		drawGameOverText(font);
		gl.glPopMatrix();
	}

	@Override
	public void drawSnowLayer(SnowLayer layer) {
		// todo
	}

	private void drawAppleIndicatorIcon(World world, float x, float y, float w,
			float h) {
		final float x1 = x;
		final float x2 = x + w;
		final float y1 = y;
		final float y2 = y + h;

		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);

		final float u1 = 0.0f, u2 = 1.0f;
		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f); gl.glVertex2f(x1,
		 * y2); gl.glTexCoord2f(u2, 0.0f); gl.glVertex2f(x2, y2);
		 * gl.glTexCoord2f(u2, 1.0f); gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1,
		 * 1.0f); gl.glVertex2f(x1, y1); gl.glEnd();
		 */
		float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedQuad(world.getAppleIconTexture(), vert, tex);
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawLevelIndicators(World world, ITrueTypeFont appleFont) {
		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1f);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(0, viewPortWidth, 0, viewPortHeight, -1, 1);
		gl.glTranslatef(0, viewPortHeight - 64, 0);
		String nbAppleStr = "" + world.getHero().getNbApple();
		appleFont.drawString(nbAppleStr);
		int iconWidth = appleFont.getWidth("O");
		drawAppleIndicatorIcon(world, appleFont.getWidth(nbAppleStr),
				appleFont.getWidth(nbAppleStr), iconWidth, iconWidth);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_ALPHA_TEST);
	}

	@Override
	public void drawMenuSequence(MenuSequence sequence) {

		sequence.setDirty(false);
		gl.glPushMatrix();
		gl.glOrthof(MenuSequence.ortho2DLeft, MenuSequence.ortho2DRight,
				MenuSequence.ortho2DBottom, MenuSequence.ortho2DTop, -1, 1);

		if (sequence.getBackgroundImage() != null) {
			final float x1 = MenuSequence.ortho2DLeft;
			final float x2 = MenuSequence.ortho2DRight;
			final float y1 = MenuSequence.ortho2DBottom;
			final float y2 = MenuSequence.ortho2DTop;
			final float u1 = 0.0F;
			final float u2 = 1.0F;
			/*
			 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0F);
			 * gl.glVertex2f(x1, y2); gl.glTexCoord2f(u2, 0.0F);
			 * gl.glVertex2f(x2, y2); gl.glTexCoord2f(u2, 1.0F);
			 * gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1, 1.0F);
			 * gl.glVertex2f(x1, y1); gl.glEnd();
			 */
			float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
			float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
			drawTexturedQuad(
					getTextureCache().getTexture(sequence.getBackgroundImage()),
					vert, tex);
		} else {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}

		gl.glEnable(GL10.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.1F);
		for (Button b : sequence.getButtons()) {
			b.draw();
		}
		gl.glDisable(GL10.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

	@Override
	public void drawButton(Button button) {
		if (button.currentTexture != null) {
			ITexture texture = getTextureCache().getTexture(
					button.currentTexture);
			final float x1 = button.x;
			final float x2 = button.x
					+ (button.w > 0 ? button.w : texture.getWidth());
			final float y1 = button.y
					+ (button.h > 0 ? button.h : texture.getHeight());
			final float y2 = button.y;
			final float u1 = 0.0f, u2 = 1.0f;
			/*
			 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(u1, 0.0f);
			 * gl.glVertex2f(x1, y2); gl.glTexCoord2f(u2, 0.0f);
			 * gl.glVertex2f(x2, y2); gl.glTexCoord2f(u2, 1.0f);
			 * gl.glVertex2f(x2, y1); gl.glTexCoord2f(u1, 1.0f);
			 * gl.glVertex2f(x1, y1); gl.glEnd();
			 */
			float vert[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
			float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
			drawTexturedQuad(texture, vert, tex);
		}
	}

	@Override
	public void drawQuestMenuButton(Button button, ITrueTypeFont questNameFont,
			String questName) {
		drawButton(button);
		gl.glPushMatrix();
		gl.glTranslatef(button.x,
				button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT
						+ questNameFont.getHeight(), 0);
		gl.glScalef(1, -1, 1);
		questNameFont.drawString(questName);
		gl.glPopMatrix();
	}

	@Override
	public void drawWorld(World world) {
		gl.glPushMatrix();

		float aspectRatio = (float) viewPortWidth / (float) viewPortHeight;
		gl.glOrthof(World.ortho2DLeft * aspectRatio, World.ortho2DRight
				* aspectRatio, World.ortho2DBottom, World.ortho2DTop, -1, 1);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
				1.0f);
		drawWorldBackground(world, aspectRatio);

		ROVector2f heroPos = world.getHero().getPosition();
		gl.glTranslatef(-heroPos.getX(), -heroPos.getY(), 0.0f);

		final float cameraSize = World.ortho2DBaseSize * 1.5f;
		final BodyList visibleBodies = world.getVisibleBodies(heroPos.getX()
				- cameraSize, heroPos.getY() - cameraSize, heroPos.getX()
				+ cameraSize, heroPos.getY() + cameraSize);
		for (int i = 0; i < visibleBodies.size(); i++) {
			Body body = visibleBodies.get(i);
			if (body instanceof Drawable) {
				((Drawable) body).draw();
			}
		}
		world.getTopLevelEntities().draw();
		gl.glPopMatrix();
	}

	private void drawWorldBackground(World world, float aspectRatio) {
		gl.glPushMatrix();

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

		/*
		 * gl.glBegin(gl.GL_QUADS); gl.glTexCoord2f(0.0f, 0.0f);
		 * gl.glVertex2f(staticBounds.x1, staticBounds.y2);
		 * gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(staticBounds.x2,
		 * staticBounds.y2); gl.glTexCoord2f(1.0f, 1.0f);
		 * gl.glVertex2f(staticBounds.x2, staticBounds.y1);
		 * gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(staticBounds.x1,
		 * staticBounds.y1); gl.glEnd();
		 */
		float vert[] = { staticBounds.x1, staticBounds.y2, staticBounds.x2,
				staticBounds.y2, staticBounds.x2, staticBounds.y1,
				staticBounds.x1, staticBounds.y1 };
		float tex[] = { 0, 0, 1, 0, 1, 1, 0, 1 };
		drawTexturedQuad(world.getBackgroundTexture(), vert, tex);
		gl.glPopMatrix();
	}

	@Override
	public ITrueTypeFont createStoryBoardSequenceFont() {
		return new AndroidTrueTypeFont(/*
										 * new Font("monospaced", Font.BOLD,
										 * 32), false
										 */);
	}

	@Override
	public ITrueTypeFont createQuestNameFont() {
		return new AndroidTrueTypeFont();
	}

	@Override
	public ITrueTypeFont createAppleFont() {
		return new AndroidTrueTypeFont();
	}

	@Override
	public ITrueTypeFont createScoreSequenceFont() {
		return new AndroidTrueTypeFont(/*
										 * new Font("monospaced", Font.BOLD,
										 * 32), false
										 */);
	}

	@Override
	public Animation loadFromGif(String name) throws FileNotFoundException,
			IOException {
		AndroidGifDecoder d = new AndroidGifDecoder();
		d.read(assets.open(name));
		Animation animation = new Animation();
		int n = d.getFrameCount();
		for (int i = 0; i < n; i++) {
			Bitmap frameImage = d.getFrame(i); // frame i
			int t = d.getDelay(i); // display duration of frame in milliseconds
			animation.addFrame(
					textureCache.createTexture(name + '#' + i, frameImage), t);
		}
		return animation;
	}

	public void setGl(GL10 gl2) {
		if (null == gl) {
			gl = gl2;
			initDisplay(config);
		}

	}

	public void setViewPort(int width, int height) {
		gl.glViewport(0, 0, width, height);
		this.viewPortWidth = width;
		this.viewPortHeight = height;

	}

}
