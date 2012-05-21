package im.bci.newtonadv.platform.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
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

import im.bci.nanim.NanimParser;
import im.bci.nanim.NanimParser.Nanim;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.GameOverSequence;
import im.bci.newtonadv.game.MainMenuSequence;
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
import im.bci.newtonadv.world.PickableObject;
import im.bci.newtonadv.world.PickedUpObject;
import im.bci.newtonadv.world.Platform;
import im.bci.newtonadv.world.UpLeftHalfPlatform;
import im.bci.newtonadv.world.UpRightHalfPlatform;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;

public class AndroidGameView implements IGameView {

	private AndroidTextureCache textureCache;
	private ITrueTypeFont fpsFont;
	private GL10 gl;
	private int viewPortWidth;
	private int viewPortHeight;
	private AssetManager assets;
	private Properties config;
	private FloatBuffer vertexBuffer;
	private FloatBuffer texCoordBuffer;

	public AndroidGameView(AssetManager assets, Properties config) {
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

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		vertexBuffer = ByteBuffer.allocateDirect(8 * (Float.SIZE / Byte.SIZE))
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);

		texCoordBuffer = ByteBuffer
				.allocateDirect(8 * (Float.SIZE / Byte.SIZE))
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);
	}

	@Override
	public void draw(Sequence sequence) {
		sequence.draw();
	}

	@Override
	public void drawFPS(int nbFps) {
		String fps = nbFps + " FPS";
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthox(0, viewPortWidth, 0, viewPortHeight, -1, 1);
		gl.glTranslatef(viewPortWidth - fpsFont.getWidth(fps),
				viewPortHeight - 64, 0);
		fpsFont.drawString(fps);
		gl.glPopMatrix();
	}

	@Override
	public ITextureCache getTextureCache() {
		return textureCache;
	}

	public void drawTexturedTriangleFans(ITexture texture, float vert[],
			float tex[]) {
		if(texture.hasAlpha()) {
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getId());

		vertexBuffer.position(0);
		vertexBuffer.put(vert);
		vertexBuffer.position(0);

		texCoordBuffer.position(0);
		texCoordBuffer.put(tex);
		texCoordBuffer.position(0);

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vert.length / 2);
		
		if(texture.hasAlpha()) {
			gl.glDisable(GL10.GL_BLEND);
		}
	}

	public void drawNonTexturedTriangleFans(float vert[]) {

		vertexBuffer.position(0);
		vertexBuffer.put(vert);
		vertexBuffer.position(0);

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, vert.length / 2);
	}

	public void drawTexturedTriangleFans(ITexture texture, FloatBuffer vert,
			FloatBuffer tex, int count) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getId());

		vert.position(0);
		vertexBuffer.position(0);
		vertexBuffer.put(vert);
		vertexBuffer.position(0);

		tex.position(0);
		texCoordBuffer.position(0);
		texCoordBuffer.put(tex);
		texCoordBuffer.position(0);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, count);
	}

	@Override
	public void drawPickableObject(PickableObject pickableObject,
			AnimationFrame frame, World world) {
		AABox bounds = pickableObject.getShape().getBounds();

		gl.glPushMatrix();
		gl.glTranslatef(pickableObject.getPosition().getX(), pickableObject
				.getPosition().getY(), 0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -bounds.getWidth() / 2.0f;
		final float x2 = bounds.getWidth() / 2.0f;
		final float y1 = -bounds.getHeight() / 2.0f;
		final float y2 = bounds.getHeight() / 2.0f;

		float vert[] = { x1, y1, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();
	}

	@Override
	public void drawAxe(Axe axe, AnimationFrame frame) {
		Box box = (Box) axe.getShape();
		Vector2f[] pts = box.getPoints(axe.getPosition(), axe.getRotation());

		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawAnimationFrame(frame, vert);

	}

	private void drawAnimationFrame(AnimationFrame frame, float[] vert) {
		float tex[] = { frame.getU1(), frame.getV2(), frame.getU2(),
				frame.getV2(), frame.getU2(), frame.getV1(), frame.getU1(),
				frame.getV1() };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
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
		drawTexturedTriangleFans(texture, vert, tex);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);
	}

	@Override
	public void drawBat(Bat bat, float scale, AnimationFrame frame, World world) {
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

		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();
	}

	@Override
	public void drawBlocker(Blocker blocker, float alpha) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(blocker);
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawPlatform(Platform platform) {
		drawTexturedTriangleFans(platform.frame.getImage(), platform.vertices,
				platform.texCoords, 4);
	}

	@Override
	public void drawCloud(Cloud cloud, float alpha) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(cloud);
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawDoor(Door door, AnimationFrame frame) {
		Box box = (Box) door.getShape();
		Vector2f[] pts = box.getPoints(door.getPosition(), door.getRotation());
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawAnimationFrame(frame, vert);
	}

	@Override
	public void drawDownLeftHalfPlatform(DownLeftHalfPlatform platform,
			AnimationFrame frame) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		float tex[] = { frame.getU1(), frame.getV1(), frame.getU1(),
				frame.getV2(), frame.getU2(), frame.getV2() };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
	}

	@Override
	public void drawDownRightHalfPlatform(DownRightHalfPlatform platform,
			AnimationFrame frame) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		float tex[] = { frame.getU1(), frame.getV2(), frame.getU2(),
				frame.getV2(), frame.getU2(), frame.getV1() };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
	}

	@Override
	public void drawUpLeftHalfPlatform(UpLeftHalfPlatform platform,
			AnimationFrame frame) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		float tex[] = { frame.getU1(), frame.getV1(), frame.getU1(),
				frame.getV2(), frame.getU2(), frame.getV1() };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
	}

	@Override
	public void drawUpRightHalfPlatform(UpRightHalfPlatform platform,
			AnimationFrame frame) {
		ConvexPolygon polygon = (ConvexPolygon) platform.getShape();
		Vector2f[] pts = polygon.getVertices(platform.getPosition(),
				platform.getRotation());

		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y };
		float tex[] = { frame.getU1(), frame.getV1(), frame.getU2(),
				frame.getV2(), frame.getU2(), frame.getV1() };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
	}

	@Override
	public void drawExplosion(Explosion explosion, AnimationFrame frame,
			World world) {
		gl.glPushMatrix();
		gl.glTranslatef(explosion.getPosition().getX(), explosion.getPosition()
				.getY(), 0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -explosion.getSize() / 2.0f;
		final float x2 = explosion.getSize() / 2.0f;
		final float y1 = -explosion.getSize() / 2.0f;
		final float y2 = explosion.getSize() / 2.0f;
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();
	}

	@Override
	public void drawFireBall(FireBall fireball, AnimationFrame frame,
			World world) {
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

		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glDisable(GL10.GL_BLEND);
		gl.glPopMatrix();

	}

	@Override
	public void drawHero(Hero hero, AnimationFrame frame, World world,
			float scale) {
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
		float u1, u2;
		if (hero.isLookingLeft()) {
			u1 = frame.getU2();
			u2 = frame.getU1();
		} else {
			u1 = frame.getU1();
			u2 = frame.getU2();
		}
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		float tex[] = { u1, frame.getV1(), u2, frame.getV1(), u2,
				frame.getV2(), u1, frame.getV2() };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
		gl.glPopMatrix();
	}

	@Override
	public void drawKey(Key key, AnimationFrame frame, World world) {
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

		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();

	}

	@Override
	public void drawLosedApple(LosedApple apple, World world,
			AnimationFrame frame, float alpha) {
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

		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
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
		drawTexturedTriangleFans(texture, vert, tex);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);
		// gl.glPopAttrib();
	}

	@Override
	public void drawMobilePikes(MobilePikes pikes, AnimationFrame frame) {
		Box box = (Box) pikes.getShape();
		Vector2f[] pts = box
				.getPoints(pikes.getPosition(), pikes.getRotation());
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();
	}

	@Override
	public void drawMummy(Mummy mummy, World world, AnimationFrame frame,
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

		float u1, u2;
		if (mummy.isLookingLeft()) {
			u1 = frame.getU2();
			u2 = frame.getU1();
		} else {
			u1 = frame.getU1();
			u2 = frame.getU2();
		}
		float vert[] = { u1, frame.getV1(), u2, frame.getV1(), u2,
				frame.getV2(), u1, frame.getV2() };
		float tex[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedTriangleFans(frame.getImage(), vert, tex);
		gl.glPopMatrix();
	}

	@Override
	public void drawPickedUpObject(PickedUpObject pickedUpObject, World world,
			AnimationFrame frame) {
		gl.glPushMatrix();
		gl.glTranslatef(pickedUpObject.getPosition().getX(), pickedUpObject
				.getPosition().getY(), 0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -pickedUpObject.getSize() / 2.0f;
		final float x2 = pickedUpObject.getSize() / 2.0f;
		final float y1 = -pickedUpObject.getSize() / 2.0f;
		final float y2 = pickedUpObject.getSize() / 2.0f;

		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();
	}

	@Override
	public void drawUsedKey(UsedKey key, AnimationFrame frame, World world) {
		gl.glPushMatrix();
		gl.glTranslatef(key.getPosition().getX(), key.getPosition().getY(),
				0.0f);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		float x1 = -key.getSize() / 2.0f;
		final float x2 = key.getSize() / 2.0f;
		final float y1 = -key.getSize() / 2.0f;
		final float y2 = key.getSize() / 2.0f;

		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
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

		if (isHurtBlinkState) {
			gl.glColor4f(1, 0, 0, 1);
		}
		final float u1 = 1, u2 = 0;
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedTriangleFans(bodyTexture, vert, tex);
		if (isHurtBlinkState) {
			gl.glColor4f(1, 1, 1, 1);
		}
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

		final float u1 = 1, u2 = 0;
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedTriangleFans(texture, vert, tex);
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
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedTriangleFans(
				getTextureCache().getTexture(sequence.getTexture()), vert, tex);
		drawContinueText(font);
		gl.glPopMatrix();
	}

	private void drawContinueText(ITrueTypeFont font) {
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight(),
				"Press enter to continue ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
	}

	private void drawGameOverText(ITrueTypeFont font) {
		drawContinueText(font);
		font.drawString(StoryboardSequence.ortho2DRight,
				StoryboardSequence.ortho2DBottom - font.getHeight() * 2,
				"Press up to retry ", 1, -1, ITrueTypeFont.ALIGN_RIGHT);
	}

	@Override
	public void drawScoreSequence(ScoreSequence sequence, ITrueTypeFont font,
			QuestScore questScore, long scorePerCentToShow) {
		sequence.setDirty(false);
		gl.glPushMatrix();
		gl.glOrthof(ScoreSequence.ortho2DLeft, ScoreSequence.ortho2DRight,
				ScoreSequence.ortho2DBottom, ScoreSequence.ortho2DTop, -1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawTexturedTriangleFans(
				getTextureCache().getTexture(sequence.getTexture()), vert, tex);
		drawGameOverText(font);
		gl.glPopMatrix();
	}

	@Override
	public void drawSnowLayer(SnowLayer layer) {
		// todo
	}

	@Override
	public void drawLevelIndicators(String indicators,
			ITrueTypeFont indicatorsFont) {
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(0, viewPortWidth, 0, viewPortHeight, -1, 1);
		gl.glTranslatef(0, viewPortHeight - indicatorsFont.getHeight(), 0);
		indicatorsFont.drawString(indicators);
		gl.glPopMatrix();
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
			float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
			float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
			drawTexturedTriangleFans(
					getTextureCache().getTexture(sequence.getBackgroundImage()),
					vert, tex);
		} else {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}

		for (Button b : sequence.getButtons()) {
			b.draw();
		}
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
			float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
			float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
			drawTexturedTriangleFans(texture, vert, tex);
		}
	}

	@Override
	public void drawMenuButton(Button button, ITrueTypeFont questNameFont,
			String label) {
		drawButton(button);
		gl.glPushMatrix();
		gl.glTranslatef(button.x,
				button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT
						+ questNameFont.getHeight(), 0);
		gl.glScalef(1, -1, 1);
		questNameFont.drawString(label);
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

		if (null == world.getBackgroundTexture()) {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		} else {
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
			float vert[] = { staticBounds.x1, staticBounds.y2, staticBounds.x2,
					staticBounds.y2, staticBounds.x2, staticBounds.y1,
					staticBounds.x1, staticBounds.y1 };
			float tex[] = { 0, 0, 1, 0, 1, 1, 0, 1 };
			drawTexturedTriangleFans(world.getBackgroundTexture(), vert, tex);
			gl.glPopMatrix();
		}
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
	public ITrueTypeFont createAppleFont(String questName, String levelName) {
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
	public AnimationCollection loadFromAnimation(String name)
			throws FileNotFoundException, IOException {
		if (name.endsWith(".gif")) {
			AndroidGifDecoder d = new AndroidGifDecoder();
			d.read(assets.open(name));
			Animation animation = new Animation();
			int n = d.getFrameCount();
			for (int i = 0; i < n; i++) {
				Bitmap frameImage = d.getFrame(i); // frame i
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
			InputStream is = assets.open(name);
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

	@Override
	public void drawFadeSequence(float r, float g, float b, float a) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(0, 1, 0, 1, -1, 1);
		gl.glColor4f(r, g, b, a);
		final float x1 = 0, x2 = 1, y1 = 0, y2 = 1;
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawNonTexturedTriangleFans(vert);
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}

	@Override
	public void drawMovingPlatform(MovingPlatform platform, AnimationFrame frame) {
		Box box = (Box) platform.getShape();
		Vector2f[] pts = box.getPoints(platform.getPosition(),
				platform.getRotation());
		float vert[] = { pts[0].x, pts[0].y, pts[1].x, pts[1].y, pts[2].x,
				pts[2].y, pts[3].x, pts[3].y };
		drawAnimationFrame(frame, vert);
	}

	@Override
	public void drawMainMenuSequence(MainMenuSequence mainMenuSequence) {
		drawMenuSequence(mainMenuSequence);
	}

	@Override
	public float getWidth() {
		return viewPortWidth;
	}

	@Override
	public float getHeight() {
		return viewPortHeight;
	}

	static final float minimapSize = 32;

	@Override
	public void drawMinimap(World world, ITexture minimapTexture) {
		if (!world.getHero().hasMap() && !world.getHero().hasCompass()) {
			return;
		}
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(0, 100, 0, 100, -1, 1);
		gl.glTranslatef(100 - minimapSize / 1.5f, minimapSize / 1.5f, 0);

		gl.glPushMatrix();
		gl.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
				1.0f);
		final float x1 = -minimapSize / 2.0f;
		final float x2 = minimapSize / 2.0f;
		final float y1 = -minimapSize / 2.0f;
		final float y2 = minimapSize / 2.0f;
		final float u1 = 0.0f, u2 = 1.0f;
		float tex[] = { u1, 0, u2, 0, u2, 1, u1, 1 };
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };

		if (world.getHero().hasMap()) {
			drawTexturedTriangleFans(minimapTexture, vert, tex);
		} else {
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
			drawNonTexturedTriangleFans(vert);
			gl.glEnable(GL10.GL_TEXTURE_2D);
		}

		gl.glPopMatrix();

		if (world.getHero().hasCompass()) {
			drawMinimapIcon(world, world.getHero().getPosition(), world
					.getHero().getAnimation().getCurrentFrame());
			for (Key key : world.getKeys())
				drawMinimapIcon(world, key.getPosition(), key.getTexture()
						.getFirst().getCurrentFrame());
		}
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_BLEND);

	}

	private void drawMinimapIcon(World world, ROVector2f worldPos,
			AnimationFrame frame) {
		float iconW = World.distanceUnit * 8.0f;
		float iconH = World.distanceUnit * 8.0f;

		final float x1 = -iconW / 2.0f;
		final float x2 = iconW / 2.0f;
		final float y1 = -iconH / 2.0f;
		final float y2 = iconH / 2.0f;
		gl.glPushMatrix();
		gl.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
				1.0f);
		final float miniMapPlatformSize = minimapSize * 4.0f / 256.0f;// harcoded,
																		// that's
																		// bad!
		gl.glScalef(miniMapPlatformSize / (World.distanceUnit * 2.0f),
				miniMapPlatformSize / (World.distanceUnit * 2.0f), 1);
		gl.glTranslatef(worldPos.getX(), worldPos.getY(), 0);
		gl.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0,
				1.0f);
		float vert[] = { x1, y2, x2, y2, x2, y1, x1, y1 };
		drawAnimationFrame(frame, vert);
		gl.glPopMatrix();
	}

	@Override
	public void drawKeyLock(KeyLock keyLock, float alpha) {
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
		drawPlatform(keyLock);
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

}
