package im.bci.newtonadv.platform.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.microedition.khronos.opengles.GL10;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;

import im.bci.newtonadv.Game;
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

	public AndroidGameView(Properties config) {
		this.quality = AndroidGameViewQuality.valueOf(config
				.getProperty("view.quality"));
		initDisplay(config);
	}

	public void toggleFullscreen() {
	}

	private void initDisplay(Properties config) {

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glDisable(gl.GL_DEPTH_TEST);
		gl.glDisable(gl.GL_DITHER);
		gl.glDisable(gl.GL_FOG);
		gl.glDisable(gl.GL_LIGHTING);
		gl.glShadeModel(gl.GL_FLAT);
		gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_FASTEST);
		gl.glHint(gl.GL_POLYGON_SMOOTH_HINT, quality.toGL());
		// gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
		gl.glEnable(gl.GL_TEXTURE_2D);

		textureCache = new AndroidTextureCache();
		fpsFont = new AndroidTrueTypeFont();
	}

	public void draw(Sequence sequence) {
		sequence.draw();
	}

	public void drawFPS(int nbFps) {
		String fps = nbFps + " FPS";
		gl.glEnable(gl.GL_ALPHA_TEST);
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthox(0, viewPortWidth, 0, viewPortHeight, -1, 1);
		gl.glTranslatef(viewPortWidth - fpsFont.getWidth(fps),
				viewPortHeight - 64, 0);
		fpsFont.drawString(fps);
		gl.glPopMatrix();
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	private void close() {
		textureCache.clearAll();
	}

	public ITextureCache getTextureCache() {
		return textureCache;
	}
	
	public void drawTexturedQuad(ITexture texture, float box[],	float tex[])
	{
		texture.bind();
	 	
		gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
		gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY);
	 
		gl.glVertexPointer(2, gl.GL_FLOAT, 0,FloatBuffer.wrap(box));
		gl.glTexCoordPointer(2, gl.GL_FLOAT, 0, FloatBuffer.wrap(tex));
	 
		gl.glDrawArrays(gl.GL_TRIANGLE_STRIP,0,4);
	 
		gl.glDisableClientState(gl.GL_VERTEX_ARRAY);
		gl.glDisableClientState(gl.GL_TEXTURE_COORD_ARRAY);
	}

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

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		/*gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();*/
		float box[] = {x1,y1, x2,y2, x2,y1, x1,y1};
		float tex[] = {0,0, 1,0, 1,1, 0,1};
		drawTexturedQuad(texture,box,tex);
		gl.glPopMatrix();
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawAxe(Axe axe, ITexture texture) {
		Box box = (Box) axe.getShape();
		Vector2f[] pts = box.getPoints(axe.getPosition(), axe.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();
		/*gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[3].x, pts[3].y);
		gl.glEnd();*/
		float vert[] = {pts[0].x,pts[0].y, pts[1].x,pts[1].y,  pts[2].x,pts[2].y,  pts[3].x,pts[3].y};
		float tex[] = {0,1, 1,1, 1,0, 0,0};
		drawTexturedQuad(texture,vert,tex);

		gl.glDisable(gl.GL_ALPHA_TEST);

	}

	public void drawAxeAnchor(AxeAnchor axeAnchor, float radius,
			ITexture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(axeAnchor.getPosition().getX(), axeAnchor.getPosition()
				.getY(), 0.0f);
		final float x1 = -radius;
		final float x2 = radius;
		final float y1 = -radius;
		final float y2 = radius;

		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		texture.bind();

		final float u1 = 0.18f, u2 = 0.8f;
		final float v1 = 0.2f, v2 = 0.8f;
		/*gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, v1);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, v1);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, v2);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, v2);
		gl.glVertex2f(x1, y1);
		gl.glEnd();*/
		float vert[] = {x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = {u1,v1, u2, v1, u2, v2, u1, v2};
		drawTexturedQuad(texture,vert,tex);
		gl.glPopMatrix();
		gl.glDisable(gl.GL_BLEND);
	}

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

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function

		texture.bind();

		final float u1 = 1, u2 = 0;
		/*gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();*/
		float vert[] = {x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = {u1, 0, u2, 0, u2, 1, u1, 1  };
		drawTexturedQuad(texture,vert,tex);
		gl.glPopMatrix();

		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawBlocker(Blocker blocker, ITexture texture, float alpha) {
		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(blocker, texture);
		gl.glDisable(gl.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void drawPlatform(Platform platform, ITexture texture) {
		Box box = (Box) platform.getShape();
		Vector2f[] pts = box.getPoints(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.0f); // sets aplha function
		texture.bind();
		/*gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[3].x, pts[3].y);
		gl.glEnd();*/
		float vert[] = {0.0f,1.0f, 1.0f,1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
		float tex[] = {pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x, pts[2].y, pts[3].x, pts[3].y };
		drawTexturedQuad(texture,vert,tex);
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawCloud(Cloud cloud, ITexture texture, float alpha) {
		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(cloud, texture);
		gl.glDisable(gl.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public void drawDoor(Door door, ITexture texture) {
		Box box = (Box) door.getShape();
		Vector2f[] pts = box.getPoints(door.getPosition(), door.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();
		/*gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[3].x, pts[3].y);
		gl.glEnd();*/
		float vert[] = {0.0f,1.0f, 1.0f,1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
		float tex[] = {pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x, pts[2].y, pts[3].x, pts[3].y };
		drawTexturedQuad(texture,vert,tex);
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawDownLeftHalfPlatform(DownLeftHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();
		gl.glBegin(gl.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glEnd();
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawDownRightHalfPlatform(DownRightHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();
		gl.glBegin(gl.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glEnd();
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawUpLeftHalfPlatform(UpLeftHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.9999f);
		texture.bind();
		gl.glBegin(gl.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glEnd();
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

	public void drawUpRightHalfPlatform(UpRightHalfPlatform platform,
			ITexture texture) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();
		gl.glBegin(gl.GL_TRIANGLES);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glEnd();
		gl.glDisable(gl.GL_ALPHA_TEST);
	}

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

		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glDisable(gl.GL_ALPHA_TEST);
		gl.glPopMatrix();
	}

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

		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glDisable(gl.GL_BLEND);
		gl.glPopMatrix();

	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function

		texture.bind();

		float u1, u2;
		if (hero.isLookingLeft()) {
			u1 = 1.0f;
			u2 = 0.0f;
		} else {
			u1 = 0.0f;
			u2 = 1.0f;
		}
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopMatrix();
		gl.glPopAttrib();

	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT
				| gl.GL_CURRENT_BIT);
		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	public void drawMobilePikeAnchor(MobilePikeAnchor anchor, ITexture texture) {
		gl.glPushMatrix();
		gl.glTranslatef(anchor.getPosition().getX(), anchor.getPosition()
				.getY(), 0.0f);
		final float x1 = -anchor.getRadius();
		final float x2 = anchor.getRadius();
		final float y1 = -anchor.getRadius();
		final float y2 = anchor.getRadius();

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT
				| gl.GL_CURRENT_BIT);
		gl.glEnable(gl.GL_BLEND);
		gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
		texture.bind();

		final float u1 = 0.18f, u2 = 0.8f;
		final float v1 = 0.2f, v2 = 0.8f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, v1);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, v1);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, v2);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, v2);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopMatrix();
		gl.glPopAttrib();
	}

	public void drawMobilePikes(MobilePikes pikes, ITexture texture) {
		Box box = (Box) pikes.getShape();
		Vector2f[] pts = box
				.getPoints(pikes.getPosition(), pikes.getRotation());

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(pts[0].x, pts[0].y);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(pts[1].x, pts[1].y);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(pts[2].x, pts[2].y);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(pts[3].x, pts[3].y);
		gl.glEnd();
		gl.glPopAttrib();
	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function

		texture.bind();

		float u1, u2;
		if (mummy.isLookingLeft()) {
			u1 = 1.0f;
			u2 = 0.0f;
		} else {
			u1 = 0.0f;
			u2 = 1.0f;
		}
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		texture.bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

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

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function

		bodyTexture.bind();

		if (isHurtBlinkState) {
			gl.glColor3f(1, 0, 0);
		}
		final float u1 = 1, u2 = 0;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		if (isHurtBlinkState) {
			gl.glColor3f(1, 1, 1);
		}
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	public void drawEgyptianBossHand(EgyptianBossHand hand, ITexture texture) {
		AABox bounds = hand.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(hand.getPosition().getX(), hand.getPosition().getY(),
				0.0f);
		float x1 = -bounds.getWidth() / 2.0f;
		float x2 = bounds.getWidth() / 2.0f;
		float y1 = -bounds.getHeight() / 2.0f;
		float y2 = bounds.getHeight() / 2.0f;

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function

		texture.bind();

		final float u1 = 1, u2 = 0;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glPopMatrix();

	}

	public void drawStoryBoardSequence(StoryboardSequence sequence,
			ITrueTypeFont font) {
		if (Display.isVisible() || Display.wasResized() || Display.isDirty()
				|| sequence.isDirty()) {
			sequence.setDirty(false);

			gl.glPushMatrix();
			GLU.gluOrtho2D(StoryboardSequence.ortho2DLeft,
					StoryboardSequence.ortho2DRight,
					StoryboardSequence.ortho2DBottom,
					StoryboardSequence.ortho2DTop);
			getTextureCache().getTexture(sequence.getTexture()).bind();
			final float x1 = StoryboardSequence.ortho2DLeft;
			final float x2 = StoryboardSequence.ortho2DRight;
			final float y1 = StoryboardSequence.ortho2DBottom;
			final float y2 = StoryboardSequence.ortho2DTop;
			final float u1 = 0.0f, u2 = 1.0f;
			gl.glBegin(gl.GL_QUADS);
			gl.glTexCoord2f(u1, 0.0f);
			gl.glVertex2f(x1, y2);
			gl.glTexCoord2f(u2, 0.0f);
			gl.glVertex2f(x2, y2);
			gl.glTexCoord2f(u2, 1.0f);
			gl.glVertex2f(x2, y1);
			gl.glTexCoord2f(u1, 1.0f);
			gl.glVertex2f(x1, y1);
			gl.glEnd();
			drawContinueText(font);
			gl.glPopMatrix();
		}
	}

	private void drawContinueText(ITrueTypeFont font) {
		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST);
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight(),
				"Press enter to continue ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
		gl.glPopAttrib();
	}

	private void drawGameOverText(ITrueTypeFont font) {
		drawContinueText(font);
		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST);
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight() * 2,
				"Press up to retry ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
		gl.glPopAttrib();
	}

	public void drawScoreSequence(ScoreSequence sequence, ITrueTypeFont font,
			QuestScore questScore, long scorePerCentToShow) {
		if (Display.isVisible() || Display.isDirty() || Display.wasResized()
				|| sequence.isDirty()) {
			sequence.setDirty(false);
			gl.glPushMatrix();
			GLU.gluOrtho2D(ScoreSequence.ortho2DLeft,
					ScoreSequence.ortho2DRight, ScoreSequence.ortho2DBottom,
					ScoreSequence.ortho2DTop);
			gl.glClear(gl.GL_COLOR_BUFFER_BIT);
			gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
			gl.glEnable(gl.GL_ALPHA_TEST);
			gl.glAlphaFunc(gl.GL_GREATER, 0.1f);
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
			gl.glPopMatrix();
			gl.glPopAttrib();
		}
	}

	public void drawGameOverSequence(GameOverSequence sequence,
			ITrueTypeFont font) {
		if (Display.isVisible() || Display.wasResized() || Display.isDirty()
				|| sequence.isDirty()) {
			sequence.setDirty(false);

			gl.glPushMatrix();
			GLU.gluOrtho2D(StoryboardSequence.ortho2DLeft,
					StoryboardSequence.ortho2DRight,
					StoryboardSequence.ortho2DBottom,
					StoryboardSequence.ortho2DTop);
			getTextureCache().getTexture(sequence.getTexture()).bind();
			final float x1 = StoryboardSequence.ortho2DLeft;
			final float x2 = StoryboardSequence.ortho2DRight;
			final float y1 = StoryboardSequence.ortho2DBottom;
			final float y2 = StoryboardSequence.ortho2DTop;
			final float u1 = 0.0f, u2 = 1.0f;
			gl.glBegin(gl.GL_QUADS);
			gl.glTexCoord2f(u1, 0.0f);
			gl.glVertex2f(x1, y2);
			gl.glTexCoord2f(u2, 0.0f);
			gl.glVertex2f(x2, y2);
			gl.glTexCoord2f(u2, 1.0f);
			gl.glVertex2f(x2, y1);
			gl.glTexCoord2f(u1, 1.0f);
			gl.glVertex2f(x1, y1);
			gl.glEnd();
			drawGameOverText(font);
			gl.glPopMatrix();
		}
	}

	public void drawSnowLayer(SnowLayer layer) {
		gl.glPushMatrix();

		float aspectRatio = (float) viewPortWidth / (float) viewPortHeight;
		GLU.gluOrtho2D(SnowLayer.ortho2DLeft * aspectRatio,
				SnowLayer.ortho2DRight * aspectRatio, SnowLayer.ortho2DBottom,
				SnowLayer.ortho2DTop);
		layer.setAspectRatio(aspectRatio);

		gl.glPushAttrib(gl.GL_ENABLE_BIT);
		gl.glDisable(gl.GL_TEXTURE_2D);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glBegin(gl.GL_POINTS);
		for (int i = 0; i < layer.getFlakes().length; ++i) {
			if (null != layer.getFlakes()[i]) {
				gl.glVertex2f(layer.getFlakes()[i].x, layer.getFlakes()[i].y);
			}
		}
		gl.glEnd();
		gl.glPopMatrix();
		gl.glPopAttrib();
	}

	private void drawAppleIndicatorIcon(World world, float x, float y, float w,
			float h) {
		final float x1 = x;
		final float x2 = x + w;
		final float y1 = y;
		final float y2 = y + h;

		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or transperancy
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f); // sets aplha function
		world.getAppleIconTexture().bind();

		final float u1 = 0.0f, u2 = 1.0f;
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(u1, 0.0f);
		gl.glVertex2f(x1, y2);
		gl.glTexCoord2f(u2, 0.0f);
		gl.glVertex2f(x2, y2);
		gl.glTexCoord2f(u2, 1.0f);
		gl.glVertex2f(x2, y1);
		gl.glTexCoord2f(u1, 1.0f);
		gl.glVertex2f(x1, y1);
		gl.glEnd();
		gl.glPopAttrib();
	}

	public void drawLevelIndicators(World world, ITrueTypeFont appleFont) {
		gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
		gl.glEnable(gl.GL_ALPHA_TEST);
		gl.glAlphaFunc(gl.GL_GREATER, 0.1f);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, viewPortWidth, 0, viewPortHeight, -1, 1);
		gl.glTranslatef(0, viewPortHeight - 64, 0);
		String nbAppleStr = "" + world.getHero().getNbApple();
		appleFont.drawString(nbAppleStr);
		int iconWidth = appleFont.getWidth("O");
		drawAppleIndicatorIcon(world, appleFont.getWidth(nbAppleStr),
				appleFont.getWidth(nbAppleStr), iconWidth, iconWidth);
		gl.glPopMatrix();
		gl.glPopAttrib();
	}

	public void drawMenuSequence(MenuSequence sequence) {
		if (Display.isVisible() || Display.isDirty() || Display.wasResized()
				|| sequence.isDirty()) {
			sequence.setDirty(false);
			gl.glPushMatrix();
			GLU.gluOrtho2D(MenuSequence.ortho2DLeft, MenuSequence.ortho2DRight,
					MenuSequence.ortho2DBottom, MenuSequence.ortho2DTop);

			if (sequence.getBackgroundImage() != null) {
				getTextureCache().getTexture(sequence.getBackgroundImage())
						.bind();
				final float x1 = MenuSequence.ortho2DLeft;
				final float x2 = MenuSequence.ortho2DRight;
				final float y1 = MenuSequence.ortho2DBottom;
				final float y2 = MenuSequence.ortho2DTop;
				final float u1 = 0.0F;
				final float u2 = 1.0F;
				gl.glBegin(gl.GL_QUADS);
				gl.glTexCoord2f(u1, 0.0F);
				gl.glVertex2f(x1, y2);
				gl.glTexCoord2f(u2, 0.0F);
				gl.glVertex2f(x2, y2);
				gl.glTexCoord2f(u2, 1.0F);
				gl.glVertex2f(x2, y1);
				gl.glTexCoord2f(u1, 1.0F);
				gl.glVertex2f(x1, y1);
				gl.glEnd();
			} else {
				gl.glClear(gl.GL_COLOR_BUFFER_BIT);
			}

			gl.glPushAttrib(gl.GL_COLOR_BUFFER_BIT | gl.GL_ENABLE_BIT);
			gl.glEnable(gl.GL_ALPHA_TEST); // allows alpha channels or
											// transperancy
			gl.glAlphaFunc(gl.GL_GREATER, 0.1F); // sets aplha function
			for (Button b : sequence.getButtons()) {
				b.draw();
			}
			gl.glPopAttrib();
			gl.glPopMatrix();
		}
	}

	public void drawButton(Button button) {
		if (button.currentTexture != null) {
			ITexture texture = getTextureCache().getTexture(
					button.currentTexture);
			texture.bind();
			final float x1 = button.x;
			final float x2 = button.x
					+ (button.w > 0 ? button.w : texture.getWidth());
			final float y1 = button.y
					+ (button.h > 0 ? button.h : texture.getHeight());
			final float y2 = button.y;
			final float u1 = 0.0f, u2 = 1.0f;
			gl.glBegin(gl.GL_QUADS);
			gl.glTexCoord2f(u1, 0.0f);
			gl.glVertex2f(x1, y2);
			gl.glTexCoord2f(u2, 0.0f);
			gl.glVertex2f(x2, y2);
			gl.glTexCoord2f(u2, 1.0f);
			gl.glVertex2f(x2, y1);
			gl.glTexCoord2f(u1, 1.0f);
			gl.glVertex2f(x1, y1);
			gl.glEnd();
		}
	}

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

	public void drawWorld(World world) {
		gl.glPushMatrix();

		float aspectRatio = (float) viewPortWidth / (float) viewPortHeight;
		GLU.gluOrtho2D(World.ortho2DLeft * aspectRatio, World.ortho2DRight
				* aspectRatio, World.ortho2DBottom, World.ortho2DTop);

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
		world.getBackgroundTexture().bind();

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

		gl.glEnable(gl.GL_TEXTURE_2D);
		gl.glBegin(gl.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(staticBounds.x1, staticBounds.y2);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(staticBounds.x2, staticBounds.y2);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(staticBounds.x2, staticBounds.y1);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(staticBounds.x1, staticBounds.y1);
		gl.glEnd();
		gl.glPopMatrix();
	}

	public ITrueTypeFont createStoryBoardSequenceFont() {
		return new TrueTypeFont(new Font("monospaced", Font.BOLD, 32), false);
	}

	public ITrueTypeFont createQuestNameFont() {
		return new TrueTypeFont();
	}

	public ITrueTypeFont createAppleFont() {
		return new TrueTypeFont();
	}

	public ITrueTypeFont createScoreSequenceFont() {
		return new TrueTypeFont(new Font("monospaced", Font.BOLD, 32), false);
	}

	public Animation loadFromGif(String name) throws FileNotFoundException {
		GifDecoder d = new GifDecoder();
		d.read(new FileInputStream(name));
		Animation animation = new Animation();
		int n = d.getFrameCount();
		for (int i = 0; i < n; i++) {
			BufferedImage frameImage = d.getFrame(i); // frame i
			int t = d.getDelay(i); // display duration of frame in milliseconds
			animation.addFrame(
					textureCache.createTexture(name + '#' + i, frameImage), t);
		}
		return animation;
	}

	public void setGl(GL10 gl2) {
		gl = gl2;
	}

	public void setViewPort(int width, int height) {
		gl.glViewport(0, 0, width, height);
		this.viewPortWidth = width;
		this.viewPortHeight = height;

	}

}
