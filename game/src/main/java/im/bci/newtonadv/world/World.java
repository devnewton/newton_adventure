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

import im.bci.newtonadv.Game;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.game.Entity;
import im.bci.newtonadv.game.EntityList;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.Sequence.ResumeTransitionException;
import im.bci.newtonadv.game.Sequence.NormalTransitionException;
import im.bci.newtonadv.game.Sequence.ResumableTransitionException;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.util.AbsoluteAABox;
import im.bci.newtonadv.util.NewtonColor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import net.phys2d.math.Matrix2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import tiled.core.Tile;
import tiled.io.TMXMapReader;

/**
 * 
 * @author devnewton
 */
public strictfp class World extends net.phys2d.raw.World {

	private static final Logger LOGGER = Logger
			.getLogger(World.class.getName());
	static final int STATIC_BODY_COLLIDE_BIT = 1;
	boolean mustDrawContacts = false;
	boolean mustDrawNormals = false;
	boolean mustDrawJoints = false;
	static public final float distanceUnit = 1.0f;
	private Hero hero;
	private float gravityAngle = 0.0f;
	private boolean nonProgressiveGravityRotationActive = false;
	private float gravityAngleTarget;
	private static final float gravityForce = 2f;
	private Vector2f gravityVector = new Vector2f();
	private Game game;
	private ITexture backgroundTexture;
	private List<Updatable> updatableBodies = new LinkedList<Updatable>();
	protected EntityList topLevelEntities = new EntityList();
	private AnimationCollection appleIconTexture;
	private Animation.Play appleIconPlay;
	private AnimationCollection coinTexture;
	private AnimationCollection worldMapTexture;
	private AnimationCollection compassTexture;
	private AnimationCollection fireBallTexture;
	private AnimationCollection bombTexture;
	private AnimationCollection crateTexture;
	private boolean objectivesCompleted = false;
	private float nonProgressiveGravityRotationStep;
	private AnimationCollection explosionAnimation;
	private AnimationCollection mummyAnimation;
	private AnimationCollection batAnimation;
	private AnimationCollection keyTexture;
	private AnimationCollection openDoorTexture;
	private AnimationCollection closedDoorTexture;
	private AnimationCollection openDoorToBonusWorldTexture;
	private AnimationCollection closedDoorToBonusWorldTexture;
	private AnimationCollection mobilePikesTexture;
	private AnimationCollection axeTexture;
	private AnimationCollection activator1OnTexture;
	private AnimationCollection activator2OnTexture;
	private AnimationCollection activator3OnTexture;
	private AnimationCollection activator1OffTexture;
	private AnimationCollection activator2OffTexture;
	private AnimationCollection activator3OffTexture;
	private AnimationCollection memoryActivatorHiddenTexture;
	private AnimationCollection blocker1Texture;
	private AnimationCollection blocker2Texture;
	private AnimationCollection blocker3Texture;
	private final String questName;
	private final String levelName;
	private int nbCollectableApple;
	private ArrayList<Key> keys = new ArrayList<Key>();
	private ArrayList<Runnable> postStepActions = new ArrayList<Runnable>();
	private ArrayList<PostUpdateAction> postUpdateActions = new ArrayList<PostUpdateAction>();
	private boolean isRotateGravityPossible = true;
	private ITrueTypeFont scoreIndicatorFont;

	public static interface PostUpdateAction {

		public void run() throws Sequence.NormalTransitionException,
				ResumableTransitionException, ResumeTransitionException;

	}

	public boolean areObjectivesCompleted() {
		return objectivesCompleted;
	}

	public void setObjectivesCompleted(boolean objectivesCompleted) {
		this.objectivesCompleted = objectivesCompleted;
	}

	public AnimationFrame getAppleIconTexture() {
		return appleIconPlay.getCurrentFrame();
	}

	public AnimationCollection getFireBallTexture() {
		return fireBallTexture;
	}	

	AnimationCollection getExplosionAnimation() {
		return explosionAnimation;
	}
	
	AnimationCollection getBombTexture() {
		return bombTexture;
	}
	
	AnimationCollection getCrateTexture() {
		return crateTexture;
	}


	public Hero getHero() {
		return hero;
	}

	public World(Game game, String questName, String levelName,
			ITrueTypeFont scoreIndicatorFont) {
		super(new Vector2f(0.0f, -gravityForce), 2,
				new StaticQuadSpaceStrategy(20, 5));
		this.game = game;
		progressiveRotateGravity(0.0f);
		this.questName = questName;
		this.levelName = levelName;
		this.scoreIndicatorFont = scoreIndicatorFont;
	}

	public AbsoluteAABox getStaticBounds() {
		if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
			return ((StaticQuadSpaceStrategy) collisionStrategy)
					.getStaticBounds();
		} else {
			return null;// todo
		}
	}

	public BodyList getVisibleBodies(float camera_x1, float camera_y1,
			float camera_x2, float camera_y2) {
		if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
			return ((StaticQuadSpaceStrategy) collisionStrategy)
					.findVisibleBodies(camera_x1, camera_y1, camera_x2,
							camera_y2);
		} else {
			return bodies;
		}
	}

	@Override
	public strictfp void add(Body body) {
		if (body instanceof Updatable) {
			updatableBodies.add((Updatable) body);
		}
		super.add(body);
		if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
			((StaticQuadSpaceStrategy) collisionStrategy).addBody(body);
		}
	}

	void addTopLevelEntities(Entity e) {
		topLevelEntities.add(e);
	}

	@Override
	public void remove(Body body) {
		body.setEnabled(false);
		if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
			((StaticQuadSpaceStrategy) collisionStrategy).removeBody(body);
		}
		if (body instanceof Updatable) {
			updatableBodies.remove(body);
		}
		super.remove(body);
	}

	@Override
	public void step() {

		if (nonProgressiveGravityRotationActive) {
			if (Math.abs(gravityAngle - gravityAngleTarget) < Math
					.abs(nonProgressiveGravityRotationStep)) {
				gravityAngle = gravityAngleTarget;
				nonProgressiveGravityRotationStep = 0.0f;
				nonProgressiveGravityRotationActive = false;
			}

			progressiveRotateGravity(nonProgressiveGravityRotationStep);
		} else {
			hero.step();
			super.step();
			postStep();
		}
	}

	private void postStep() {
		for (Runnable runnable : postStepActions) {
			runnable.run();
		}
		postStepActions.clear();
	}

	private static final Properties defaultMapProperties = new Properties();

	static {
		defaultMapProperties.put("newton_adventure.mummy", "mummy.gif");
		defaultMapProperties.put("newton_adventure.bat", "bat.nanim");
		defaultMapProperties.put("newton_adventure.explosion", "explosion.gif");
		defaultMapProperties.put("newton_adventure.fireball", "fireball.png");
		defaultMapProperties.put("newton_adventure.bomb", "bomb.nanim");
		defaultMapProperties.put("newton_adventure.crate", "crate.nanim");
		defaultMapProperties.put("newton_adventure.axe", "axe.png");
		defaultMapProperties.put("newton_adventure.mobilePikes",
				"mobile_pikes.png");
		defaultMapProperties.put("newton_adventure.door_to_bonus_world",
				"door_to_bonus_world.png");
		defaultMapProperties.put("newton_adventure.door_to_bonus_world_open",
				"door_to_bonus_world_open.png");
		defaultMapProperties.put("newton_adventure.door", "door.png");
		defaultMapProperties.put("newton_adventure.door_open", "door_open.png");
		defaultMapProperties.put("newton_adventure.key", "key.png");
		defaultMapProperties.put("newton_adventure.hero", "hero.gif");
		defaultMapProperties.put("newton_adventure.apple", "apple.png");
		defaultMapProperties.put("newton_adventure.coin", "coin.png");
		defaultMapProperties.put("newton_adventure.world_map", "map.png");
		defaultMapProperties.put("newton_adventure.compass", "compass.png");
		defaultMapProperties.put("newton_adventure.activator1.on",
				"actived1.png");
		defaultMapProperties.put("newton_adventure.activator2.on",
				"actived2.png");
		defaultMapProperties.put("newton_adventure.activator3.on",
				"actived3.png");
		defaultMapProperties.put("newton_adventure.activator1.off",
				"activable1.png");
		defaultMapProperties.put("newton_adventure.activator2.off",
				"activable2.png");
		defaultMapProperties.put("newton_adventure.activator3.off",
				"activable3.png");
		defaultMapProperties.put("newton_adventure.memory_activator.hidden",
				"activable_hidden.png");
		defaultMapProperties.put("newton_adventure.blocker1", "blocker1.png");
		defaultMapProperties.put("newton_adventure.blocker2", "blocker2.png");
		defaultMapProperties.put("newton_adventure.blocker3", "blocker3.png");
		defaultMapProperties.put("newton_adventure.music", "hopnbop.ogg");
		defaultMapProperties.put("newton_adventure.rotate_gravity_possible",
				"true");
	}

	public String getFileFromMap(tiled.core.Map map, String filePropertyName) {
		String filename = getFileFromMapIfAvailable(map, filePropertyName);
		if (filename != null) {
			return filename;
		} else {
			throw new RuntimeException(
					"error in tmx map file, cannot find property "
							+ filePropertyName);
		}
	}

	public String getFileFromMapIfAvailable(tiled.core.Map map,
			String filePropertyName) {
		String filename = map.getProperties().getProperty(filePropertyName);
		if (filename == null) {
			filename = defaultMapProperties.getProperty(filePropertyName);
			if (filename == null) {
				return null;
			}
		}
		return game.getData().getLevelFilePath(questName, levelName, filename);
	}

	private String getMapProperty(tiled.core.Map map, String prop) {
		String value = map.getProperties().getProperty(prop);
		if (null == value) {
			return defaultMapProperties.getProperty(prop);
		}
		return value;
	}

	private boolean loadNalLevel() {
		NalLoader loader = null;
		try {
			InputStream input = game.getData().openLevelNal(questName,
					levelName);
			if(null == input) {
				return false;
			}
			loader = new NalLoader(game, this, questName,
					levelName, input);
		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.WARNING, "Cannot load nal file for quest " + questName + " and level " + levelName, e);
			return false;
		}
		loader.load();
		return true;
	}

	public boolean loadLevel() {
		return loadNalLevel() || loadTmxLevel();
	}

	private boolean loadTmxLevel() {
		try {
			TMXMapReader mapReader = new TMXMapReader();
			tiled.core.Map map;
			InputStream mapInputStream = game.getData().openLevelTmx(questName,
					levelName);
			if (null == mapInputStream) {
				return false;
			}
			try {
				map = mapReader.readMap(mapInputStream);
			} finally {
				mapInputStream.close();
			}

			final ITextureCache textureCache = game.getView().getTextureCache();
			explosionAnimation = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.explosion"));
			mummyAnimation = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.mummy"));
			batAnimation = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.bat"));
			appleIconTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.apple"));
			appleIconPlay = appleIconTexture.getFirst().start();
			coinTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.coin"));
			worldMapTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.world_map"));
			compassTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.compass"));
			fireBallTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.fireball"));
			bombTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.bomb"));
			crateTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.crate"));
			keyTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.key"));
			closedDoorTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.door"));
			openDoorTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.door_open"));
			closedDoorToBonusWorldTexture = game.getView()
					.loadFromAnimation(
							getFileFromMap(map,
									"newton_adventure.door_to_bonus_world"));
			openDoorToBonusWorldTexture = game.getView().loadFromAnimation(
					getFileFromMap(map,
							"newton_adventure.door_to_bonus_world_open"));
			mobilePikesTexture = game.getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.mobilePikes"));
			axeTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.axe"));
			activator1OnTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.activator1.on"));
			activator2OnTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.activator2.on"));
			activator3OnTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.activator3.on"));
			activator1OffTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.activator1.off"));
			activator2OffTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.activator2.off"));
			activator3OffTexture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.activator3.off"));
			memoryActivatorHiddenTexture = getView().loadFromAnimation(
					getFileFromMap(map,
							"newton_adventure.memory_activator.hidden"));
			blocker1Texture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.blocker1"));
			blocker2Texture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.blocker2"));
			blocker3Texture = getView().loadFromAnimation(
					getFileFromMap(map, "newton_adventure.blocker3"));

			int zorderBase = 0;
			for (tiled.core.MapLayer layer : map.getLayers()) {
				if (layer instanceof tiled.core.TileLayer) {
					tiled.core.TileLayer tileLayer = (tiled.core.TileLayer) layer;
					for (int x = 0; x < tileLayer.getWidth(); ++x) {
						for (int y = 0; y < tileLayer.getHeight(); ++y) {
							Tile tile = tileLayer.getTileAt(x, y);
							if (null != tile) {
								initFromTile(x - map.getWidth() / 2.0f, -y
										+ map.getHeight() / 2.0f, map, tile,
										zorderBase);
							}
						}
					}
				}
				zorderBase += 1000000;
			}

			String backgroundTextureFile = getFileFromMapIfAvailable(map,
					"newton_adventure.background");
			if (null != backgroundTextureFile) {
				backgroundTexture = textureCache
						.getTexture(backgroundTextureFile);
			}
			game.getSoundCache().playMusicIfEnabled(
					getFileFromMap(map, "newton_adventure.music"));

			isRotateGravityPossible = "true".equals(getMapProperty(map,
					"newton_adventure.rotate_gravity_possible"));
			return true;
		} catch (Exception e) {
			LOGGER.log(java.util.logging.Level.WARNING, "Cannot load level " + levelName + " in quest "
					+ questName, e);
			return false;
		}
	}

	public float getGravityAngle() {
		return gravityAngle;
	}

	public final void progressiveRotateGravity(float angle) {
		if (isRotateGravityPossible) {
			gravityAngle += angle;
			Matrix2f rot = new Matrix2f(gravityAngle);
			this.gravityVector = net.phys2d.math.MathUtil.mul(rot,
					new Vector2f(0, -gravityForce));
			setGravity(getGravityVector().x, getGravityVector().y);
		}
	}

	public void rotateGravity(float angle) {
		if (isRotateGravityPossible) {
			if (!nonProgressiveGravityRotationActive) {
				this.gravityAngleTarget = this.gravityAngle + angle;
				this.nonProgressiveGravityRotationStep = 2.0f * angle
						/ Game.FPSf;
				this.nonProgressiveGravityRotationActive = true;
			}
		}
	}

	float getGravityForce() {
		return gravityForce;
	}

	static final float defaultPickableObjectSize = 2.0f * World.distanceUnit;
	private static final Circle defaultPickableObjectShape = new Circle(
			defaultPickableObjectSize / 2.0f);

	private void initFromTile(float x, float y, tiled.core.Map map,
			tiled.core.Tile tile, int zOrderBase) throws IOException {
		ITextureCache textureCache = game.getView().getTextureCache();
		String c = tile.getProperties().getProperty("newton_adventure.type",
				"unknown");

		final float baseSize = 2.0f * World.distanceUnit;
		final float tileWidthScale = (float) tile.getWidth()
				/ (float) map.getTileWidth();
		final float tileHeightScale = (float) tile.getHeight()
				/ (float) map.getTileHeight();
		final float tileWidth = baseSize * tileWidthScale;
		final float tileHeight = baseSize * tileHeightScale;
		final float tileX = x * baseSize + tileWidth / 2.0f;
		final float tileY = y * baseSize + tileHeight / 2.0f;

		if (c.equals("platform")) {
			Platform platform = new Platform(this, tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("slash_platform")) {
			Platform platform = new Platform(this, tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setShape(new Line(-tileWidth/2.0f, -tileHeight/2.0f, tileWidth/2.0f, tileHeight/2.0f));
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("antislash_platform")) {
			Platform platform = new Platform(this, tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setShape(new Line(-tileWidth/2.0f, tileHeight/2.0f, tileWidth/2.0f, -tileHeight/2.0f));
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("up_right_half_platform")) {
			UpRightHalfPlatform platform = new UpRightHalfPlatform(this,
					tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("up_left_half_platform")) {
			UpLeftHalfPlatform platform = new UpLeftHalfPlatform(this,
					tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("down_left_half_platform")) {
			DownLeftHalfPlatform platform = new DownLeftHalfPlatform(this,
					tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("down_right_half_platform")) {
			DownRightHalfPlatform platform = new DownRightHalfPlatform(this,
					tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("hero")) {
			if (null == hero) {
				hero = new Hero(this);
				hero.setPosition(tileX, tileY);
				hero.setZOrder(getTileZOrder(tile, zOrderBase));
				hero.setAnimation(
						game.getView().loadFromAnimation(
								getFileFromMap(map, "newton_adventure.hero")));
				hero.setJumpSound(
						game.getSoundCache().getSoundIfEnabled(
								game.getData().getFile("jump.wav")));
				add(hero);
			} else {
				LOGGER.warning("One hero is enough for level " + levelName
						+ " in quest " + questName);
			}
		} else if (c.equals("mummy")) {
			Mummy mummy = new Mummy(this, new Circle(distanceUnit),
					mummyAnimation);
			mummy.setPosition(tileX, tileY);
			mummy.setZOrder(getTileZOrder(tile, zOrderBase));
			add(mummy);
		} else if (c.equals("bat")) {
			Bat bat = new Bat(this, new Box(distanceUnit * 1.0f,
					distanceUnit * 0.5f), batAnimation);
			bat.setPosition(tileX, tileY);
			bat.setZOrder(getTileZOrder(tile, zOrderBase));
			add(bat);
		} else if (c.equals("apple")) {
			Apple apple = new Apple(this, defaultPickableObjectShape);
			apple.setPosition(tileX, tileY);
			apple.setTexture(appleIconTexture);
			apple.setZOrder(getTileZOrder(tile, zOrderBase));
			addApple(apple);
		} else if (c.equals("coin")) {
			Coin coin = new Coin(this, defaultPickableObjectShape);
			coin.setPosition(tileX, tileY);
			coin.setTexture(coinTexture);
			coin.setZOrder(getTileZOrder(tile, zOrderBase));
			add(coin);
		} else if (c.equals("world_map")) {
			WorldMap worldMap = new WorldMap(this, defaultPickableObjectShape);
			worldMap.setPosition(tileX, tileY);
			worldMap.setTexture(worldMapTexture);
			worldMap.setZOrder(getTileZOrder(tile, zOrderBase));
			add(worldMap);
		} else if (c.equals("compass")) {
			Compass compass = new Compass(this, defaultPickableObjectShape);
			compass.setPosition(tileX, tileY);
			compass.setTexture(compassTexture);
			compass.setZOrder(getTileZOrder(tile, zOrderBase));
			add(compass);
		} else if (c.equals("key")) {
			Key key = new Key(this);
			key.setPosition(tileX, tileY);
			key.setTexture(keyTexture);
			key.setZOrder(getTileZOrder(tile, zOrderBase));
			add(key);
			keys.add(key);
		} else if (c.equals("door")) {
			Door door = new Door(this, tileWidth, tileHeight);
			door.setPosition(tileX, tileY);
			door.setClosedTexture(closedDoorTexture);
			door.setOpenTexture(openDoorTexture);
			door.setZOrder(getTileZOrder(tile, zOrderBase));
			add(door);
		} else if (c.equals("door_to_bonus_world")) {
			DoorToBonusWorld door = new DoorToBonusWorld(this, tileWidth,
					tileHeight);
			door.setPosition(tileX, tileY);
			door.setClosedTexture(closedDoorToBonusWorldTexture);
			door.setOpenTexture(openDoorToBonusWorldTexture);
			door.setZOrder(getTileZOrder(tile, zOrderBase));
			add(door);
		} else if (c.equals("cloud")) {
			Cloud cloud = new Cloud(this, tileWidth, tileHeight);
			cloud.setTexture(getAnimationForTile(map, tile, textureCache));
			cloud.setPosition(tileX, tileY);
			cloud.setZOrder(getTileZOrder(tile, zOrderBase));
			cloud.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
					"newton_adventure.color", "white")));
			add(cloud);
		} else if (c.equals("pikes_up")) {
			Pikes pikes = new Pikes(this, Pikes.DangerousSide.UP, tileWidth,
					tileHeight);
			pikes.setTexture(getAnimationForTile(map, tile, textureCache));
			pikes.setPosition(tileX, tileY);
			pikes.setZOrder(getTileZOrder(tile, zOrderBase));
			add(pikes);
		} else if (c.equals("pikes_down")) {
			Pikes pikes = new Pikes(this, Pikes.DangerousSide.DOWN, tileWidth,
					tileHeight);
			pikes.setTexture(getAnimationForTile(map, tile, textureCache));
			pikes.setPosition(tileX, tileY);
			pikes.setZOrder(getTileZOrder(tile, zOrderBase));
			add(pikes);
		} else if (c.equals("pikes_left")) {
			Pikes pikes = new Pikes(this, Pikes.DangerousSide.LEFT, tileWidth,
					tileHeight);
			pikes.setTexture(getAnimationForTile(map, tile, textureCache));
			pikes.setPosition(tileX, tileY);
			pikes.setZOrder(getTileZOrder(tile, zOrderBase));
			add(pikes);
		} else if (c.equals("pikes_right")) {
			Pikes pikes = new Pikes(this, Pikes.DangerousSide.RIGHT, tileWidth,
					tileHeight);
			pikes.setTexture(getAnimationForTile(map, tile, textureCache));
			pikes.setPosition(tileX, tileY);
			pikes.setZOrder(getTileZOrder(tile, zOrderBase));
			add(pikes);
		} else if (c.equals("cannon_up")) {
			Cannon cannon = new Cannon(this, Cannon.Orientation.UP, tileWidth,
					tileHeight);
			cannon.setTexture(getAnimationForTile(map, tile, textureCache));
			cannon.setPosition(tileX, tileY);
			cannon.setZOrder(getTileZOrder(tile, zOrderBase));
			add(cannon);
		} else if (c.equals("cannon_down")) {
			Cannon cannon = new Cannon(this, Cannon.Orientation.DOWN,
					tileWidth, tileHeight);
			cannon.setTexture(getAnimationForTile(map, tile, textureCache));
			cannon.setPosition(tileX, tileY);
			cannon.setZOrder(getTileZOrder(tile, zOrderBase));
			add(cannon);
		} else if (c.equals("cannon_right")) {
			Cannon cannon = new Cannon(this, Cannon.Orientation.RIGHT,
					tileWidth, tileHeight);
			cannon.setTexture(getAnimationForTile(map, tile, textureCache));
			cannon.setPosition(tileX, tileY);
			cannon.setZOrder(getTileZOrder(tile, zOrderBase));
			add(cannon);
		} else if (c.equals("cannon_left")) {
			Cannon cannon = new Cannon(this, Cannon.Orientation.LEFT,
					tileWidth, tileHeight);
			cannon.setTexture(getAnimationForTile(map, tile, textureCache));
			cannon.setPosition(tileX, tileY);
			cannon.setZOrder(getTileZOrder(tile, zOrderBase));
			add(cannon);
		} else if (c.equals("mobile_pike_anchor")) {
			MobilePikeAnchor anchor = new MobilePikeAnchor(this);
			anchor.setTexture(getAnimationForTile(map, tile, textureCache));
			anchor.setPosition(tileX, tileY);
			anchor.setZOrder(getTileZOrder(tile, zOrderBase));
			add(anchor);

			MobilePikes pikes = new MobilePikes(this);
			pikes.setTexture(mobilePikesTexture);
			pikes.setPosition(anchor.getPosition().getX(), anchor.getPosition()
					.getY()
					- MobilePikes.height
					/ 2.0f
					- anchor.getShape().getBounds().getHeight() / 2.0f);
			add(pikes);
			pikes.setZOrder(getTileZOrder(tile, zOrderBase));

			BasicJoint j = new BasicJoint(anchor, pikes, new Vector2f(
					anchor.getPosition()));
			j.setRelaxation(0);
			add(j);
		} else if (c.equals("axe_anchor")) {
			AxeAnchor anchor = new AxeAnchor(this);
			anchor.setTexture(getAnimationForTile(map, tile, textureCache));
			anchor.setPosition(tileX, tileY);
			anchor.setZOrder(getTileZOrder(tile, zOrderBase));
			add(anchor);

			Axe axe = new Axe(this);
			axe.setTexture(axeTexture);
			axe.setPosition(anchor.getPosition().getX(), anchor.getPosition()
					.getY()
					- MobilePikes.height
					/ 2.0f
					- anchor.getShape().getBounds().getHeight() / 2.0f);
			axe.setZOrder(getTileZOrder(tile, zOrderBase));
			add(axe);

			BasicJoint j = new BasicJoint(anchor, axe, new Vector2f(
					anchor.getPosition()));
			j.setRelaxation(0);
			add(j);
		} else if (c.equals("bounce_platform")) {
			BouncePlatform platform = new BouncePlatform(this, tileWidth,
					tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("activator1")) {
			Activator activator = new Activator(this, 1, activator1OnTexture,
					activator1OffTexture, tileWidth, tileHeight);
			activator.setPosition(tileX, tileY);
			activator.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activator);
		} else if (c.equals("activator2")) {
			Activator activator = new Activator(this, 2, activator2OnTexture,
					activator2OffTexture, tileWidth, tileHeight);
			activator.setPosition(tileX, tileY);
			activator.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activator);
		} else if (c.equals("activator3")) {
			Activator activator = new Activator(this, 3, activator3OnTexture,
					activator3OffTexture, tileWidth, tileHeight);
			activator.setPosition(tileX, tileY);
			activator.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activator);
		} else if (c.equals("memory_activator1")) {
			MemoryActivator activator = new MemoryActivator(this, 1,
					activator1OnTexture, activator1OffTexture,
					memoryActivatorHiddenTexture, tileWidth, tileHeight);
			activator.setPosition(tileX, tileY);
			activator.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activator);
		} else if (c.equals("memory_activator2")) {
			MemoryActivator activator = new MemoryActivator(this, 2,
					activator2OnTexture, activator2OffTexture,
					memoryActivatorHiddenTexture, tileWidth, tileHeight);
			activator.setPosition(tileX, tileY);
			activator.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activator);
		} else if (c.equals("memory_activator3")) {
			MemoryActivator activator = new MemoryActivator(this, 3,
					activator3OnTexture, activator3OffTexture,
					memoryActivatorHiddenTexture, tileWidth, tileHeight);
			activator.setPosition(tileX, tileY);
			activator.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activator);
		} else if (c.equals("blocker1")) {
			Blocker activable = new Blocker(this, 1, tileWidth, tileHeight);
			activable.setTexture(blocker1Texture);
			activable.setPosition(tileX, tileY);
			activable.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activable);
		} else if (c.equals("blocker2")) {
			Blocker activable = new Blocker(this, 2, tileWidth, tileHeight);
			activable.setTexture(blocker2Texture);
			activable.setPosition(tileX, tileY);
			activable.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activable);
		} else if (c.equals("blocker3")) {
			Blocker activable = new Blocker(this, 3, tileWidth, tileHeight);
			activable.setTexture(blocker3Texture);
			activable.setPosition(tileX, tileY);
			activable.setZOrder(getTileZOrder(tile, zOrderBase));
			add(activable);
		} else if (c.equals("moving_platform")) {
			MovingPlatform platform = new MovingPlatform(this,
					getAnimationForTile(map, tile, textureCache),
					getMovingPlatformPath(tile, x, y, baseSize),
					tileWidth, tileHeight);
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("dangerous_moving_platform")) {
			DangerousMovingPlatform platform = new DangerousMovingPlatform(this,
					getAnimationForTile(map, tile, textureCache),
					getMovingPlatformPath(tile, x, y, baseSize),
					tileWidth, tileHeight);
			if(tile.getProperties().containsKey("newton_adventure.color")) {
				platform.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
					"newton_adventure.color", "white")));
			}
			platform.setPosition(tileX, tileY);
			platform.setFriction(getTileFriction(tile));
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		} else if (c.equals("teleporter")) {
			Teleporter teleporter = new Teleporter(this, tileWidth, tileHeight);
			teleporter.setTexture(getAnimationForTile(map, tile, textureCache));
			teleporter.setPosition(tileX, tileY);
			teleporter.setZOrder(getTileZOrder(tile, zOrderBase, 1));
			teleporter.setColor(tile.getProperties().getProperty(
					"newton_adventure.teleporter.color"));
			add(teleporter);
		} else if (c.equals("colorizer")) {
			Colorizer colorizer = new Colorizer(this, tileWidth, tileHeight);
			colorizer.setTexture(getAnimationForTile(map, tile, textureCache));
			colorizer.setPosition(tileX, tileY);
			colorizer.setZOrder(getTileZOrder(tile, zOrderBase, 1));
			colorizer.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
					"newton_adventure.color", "white")));
			add(colorizer);
		} else if (c.equals("colored_platform")) {
			ColoredPlatform colored = new ColoredPlatform(this, tileWidth, tileHeight);
			colored.setTexture(getAnimationForTile(map, tile, textureCache));
			colored.setPosition(tileX, tileY);
			colored.setZOrder(getTileZOrder(tile, zOrderBase, 1));
			colored.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
					"newton_adventure.color")));
			add(colored);
		}else if (c.equals("keylock")) {
			KeyLock keylock = new KeyLock(this, tileWidth, tileHeight);
			keylock.setTexture(getAnimationForTile(map, tile, textureCache));
			keylock.setPosition(tileX, tileY);
			keylock.setZOrder(getTileZOrder(tile, zOrderBase));
			add(keylock);
		} else if (c.equals("help_sign")) {
			HelpSign helpSign = new HelpSign(this, tileWidth, tileHeight);
			helpSign.setTexture(getAnimationForTile(map, tile, textureCache));
			helpSign.setPosition(tileX, tileY);
			helpSign.setZOrder(getTileZOrder(tile, zOrderBase));
			add(helpSign);
		} else if (c.equals("bomb")) {
			Bomb bomb = new Bomb(this);
			bomb.setPosition(tileX, tileY);
			bomb.setZOrder(getTileZOrder(tile, zOrderBase));
			add(bomb);
		} else if (c.equals("bomb_hole")) {
			BombHole bombHole = new BombHole(this, tileWidth, tileHeight);
			bombHole.setTexture(getAnimationForTile(map, tile, textureCache));
			bombHole.setPosition(tileX, tileY);
			bombHole.setZOrder(getTileZOrder(tile, zOrderBase));
			add(bombHole);
		} else if (c.equals("crate")) {
			Crate crate = new Crate(this, tileWidth, tileHeight);
			crate.setPosition(tileX, tileY);
			crate.setZOrder(getTileZOrder(tile, zOrderBase));
			add(crate);
		} else if (c.equals("egyptian_boss")) {
			EgyptianBoss boss = new EgyptianBoss(this, tileX, tileY);
			boss.setBodyTexture(new AnimationCollection(textureCache.getTexture(game.getData().getFile(
					"egyptian_boss_body.png"))));
			boss.setHandTexture(new AnimationCollection(textureCache.getTexture(game.getData().getFile(
					"egyptian_boss_hand.png"))));
			boss.setZOrder(getTileZOrder(tile, zOrderBase));
			boss.getLeftHand().setZOrder(getTileZOrder(tile, zOrderBase + 1));
			boss.getRightHand().setZOrder(getTileZOrder(tile, zOrderBase + 1));
			add(boss);
			add(boss.getLeftHand());
			add(boss.getRightHand());
		} else {
			Platform platform = new Platform(this, tileWidth, tileHeight);
			platform.setTexture(getAnimationForTile(map, tile, textureCache));
			platform.setPosition(tileX, tileY);
			platform.setEnabled(false);
			platform.setZOrder(getTileZOrder(tile, zOrderBase));
			add(platform);
		}
	}

	private AnimationCollection getAnimationForTile(tiled.core.Map map,
			tiled.core.Tile tile, ITextureCache textureCache)
			throws FileNotFoundException, IOException {
		AnimationCollection animation;
		String gfx = tile.getProperties().getProperty("newton_adventure.gfx");
		if (null != gfx) {
			animation = game.getView().loadFromAnimation(
					game.getData().getLevelFilePath(questName, levelName, gfx));
		} else {
			animation = new AnimationCollection(textureCache.getTexture(
					questName, levelName, map, tile));
		}
		return animation;
	}

	public Vector2f getGravityVector() {
		return gravityVector;
	}

	public void cheatActivateAll() {
		final BodyList allBodies = getBodies();
		for (int i = 0; i < allBodies.size(); ++i) {
			Body b = allBodies.get(i);
			if (b instanceof Blocker) {
				Blocker a = (Blocker) b;
				a.activate();
			}
		}
	}

	public static final float ortho2DBaseSize = World.distanceUnit * 20.0f;
	public static final float ortho2DLeft = -ortho2DBaseSize;
	public static final float ortho2DBottom = -ortho2DBaseSize;
	public static final float ortho2DRight = ortho2DBaseSize;
	public static final float ortho2DTop = ortho2DBaseSize;
	float aspectRatio = 1.0f;

	public void draw() {
		getView().drawWorld(this);
	}

	public void update() throws GameOverException, NormalTransitionException,
			ResumableTransitionException, ResumeTransitionException {
		FrameTimeInfos frameTimeInfos = game.getFrameTimeInfos();
		appleIconPlay.update(frameTimeInfos.elapsedTime / 1000000);
		for (Updatable u : new ArrayList<Updatable>(updatableBodies)) {// copy
																		// to
																		// allow
																		// updatable
																		// body
																		// to be
																		// removed
																		// from
																		// list
			u.update(frameTimeInfos);
		}
		topLevelEntities.update(frameTimeInfos);

		try {
			for (PostUpdateAction action : postUpdateActions) {
				action.run();
			}
		} finally {
			postUpdateActions.clear();
		}
	}

	@Override
	public void resolve(BodyList bodyList, float dt) {
		super.resolve(bodyList, dt);

		for (int i = 0; i < bodyList.size(); ++i) {
			Body body = bodyList.get(i);
			if (body instanceof CollisionDetectionOnly) {
				clearArbiters(body);
			}
		}
	}

	private float getTileFriction(Tile tile) {
		return Float.parseFloat(tile.getProperties().getProperty(
				"newton_adventure.friction", "10"));
	}

	private int getTileZOrder(Tile tile, int zOrderBase, int defaultZ) {
		int z = zOrderBase;
		String zprop = tile.getProperties().getProperty(
				"newton_adventure.zorder");
		if (null == zprop) {
			z += defaultZ;
		} else {
			z += Integer.parseInt(zprop);
		}
		return z;
	}

	private int getTileZOrder(Tile tile, int zOrderBase) {
		return getTileZOrder(tile, zOrderBase, 0);
	}

	private Vector2f[] getMovingPlatformPath(Tile tile,
			float x, float y, float baseSize) {
		Vector2f[] dest = new Vector2f[2];
		float ax = Float.parseFloat(tile.getProperties().getProperty(
				"newton_adventure.moving_platform.a.x", "-1"));
		float ay = Float.parseFloat(tile.getProperties().getProperty(
				"newton_adventure.moving_platform.a.y", "-1"));
		float bx = Float.parseFloat(tile.getProperties().getProperty(
				"newton_adventure.moving_platform.b.x", "1"));
		float by = Float.parseFloat(tile.getProperties().getProperty(
				"newton_adventure.moving_platform.b.y", "1"));

		ax += x;
		bx += x;
		ay += y;
		by += y;

		ax *= baseSize;
		bx *= baseSize;
		ay *= baseSize;
		by *= baseSize;

		dest[0] = new Vector2f(ax, ay);
		dest[1] = new Vector2f(bx, by);
		return dest;
	}

	public LevelScore getLevelScore() {
		return hero.getLevelScore();
	}

	IGameView getView() {
		return game.getView();
	}

	public ITexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public EntityList getTopLevelEntities() {
		return topLevelEntities;
	}

	void goToBonusWorld() {
		postUpdateActions.add(new PostUpdateAction() {

			@Override
			public void run() throws ResumableTransitionException {
				game.goToRandomBonusLevel(questName);
			}
		});
	}

	void removeApple(Apple apple) {
		remove(apple);
		--nbCollectableApple;
		if (nbCollectableApple <= 0) {
			for (int i = 0; i < bodies.size(); ++i) {
				Body body = bodies.get(i);
				if (body instanceof DoorToBonusWorld) {
					((DoorToBonusWorld) body).open();
				}
			}
		}
	}

	public List<Key> getKeys() {
		return keys;
	}

	private Teleporter findNextTeleporter(Teleporter previousTeleporter) {
		Teleporter firstTeleporterWithSameColor = null;
		boolean previousTeleporterFound = false;

		for (int i = 0; i < bodies.size(); ++i) {
			Body body = bodies.get(i);
			if (body instanceof Teleporter) {
				Teleporter teleporter = (Teleporter) body;
				if (teleporter.getColor().equals(previousTeleporter.getColor())) {
					if (previousTeleporterFound)
						return teleporter;
					if (firstTeleporterWithSameColor == null)
						firstTeleporterWithSameColor = teleporter;
					if (teleporter == previousTeleporter)
						previousTeleporterFound = true;

				}
			}
		}
		return firstTeleporterWithSameColor;
	}

	public void teleportFrom(Teleporter previousTeleporter) {
		final Teleporter teleporter = findNextTeleporter(previousTeleporter);
		remove(hero);
		postStepActions.add(new Runnable() {
			public void run() {
				hero.setEnabled(true);
				hero.setPosition(teleporter.getPosition().getX(), teleporter
						.getPosition().getY() + 1.0f);
				add(hero);
			};
		});

	}

	public void gotoLevel(final String newQuestName, final String newLevelName) {
		postUpdateActions.add(new PostUpdateAction() {

			@Override
			public void run() throws NormalTransitionException {
				game.gotoLevel(newQuestName, newLevelName);
			}
		});
	}

	public void resume() {
		for (int i = 0; i < bodies.size(); ++i) {
			Body body = bodies.get(i);
			if (body instanceof DoorToBonusWorld) {
				((DoorToBonusWorld) body).close();
			}
		}
	}

	public void showHelp() {

		postUpdateActions.add(new PostUpdateAction() {

			@Override
			public void run() throws ResumableTransitionException {
				game.showHelp();
			}
		});
	}

	public ITrueTypeFont getScoreIndicatorFont() {
		return scoreIndicatorFont;
	}

	public void addApple(im.bci.newtonadv.world.Apple apple) {
		++nbCollectableApple;
		add(apple);
	}

	public void setHero(Hero hero) {
		this.hero = hero;
		add(hero);		
	}
}
