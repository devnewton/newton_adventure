package im.bci.newtonadv.world;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.NewtonAdventureLevelParser;
import im.bci.newtonadv.NewtonAdventureLevelParser.Activator;
import im.bci.newtonadv.NewtonAdventureLevelParser.AnimationReference;
import im.bci.newtonadv.NewtonAdventureLevelParser.Apple;
import im.bci.newtonadv.NewtonAdventureLevelParser.AxeAnchor;
import im.bci.newtonadv.NewtonAdventureLevelParser.Bat;
import im.bci.newtonadv.NewtonAdventureLevelParser.Cannon.Orientation;
import im.bci.newtonadv.NewtonAdventureLevelParser.Entity;
import im.bci.newtonadv.NewtonAdventureLevelParser.EntityType;
import im.bci.newtonadv.NewtonAdventureLevelParser.Level;
import im.bci.newtonadv.NewtonAdventureLevelParser.Path;
import im.bci.newtonadv.NewtonAdventureLevelParser.Pikes.DangerousSide;
import im.bci.newtonadv.NewtonAdventureLevelParser.Position;
import im.bci.newtonadv.anim.AnimationCollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Shape;

strictfp class NalLoader {
	private final World world;
	private final String questName, levelName;
	private final Level level;
	private final Game game;
	private im.bci.newtonadv.world.Hero hero;
	private static final Logger LOGGER = Logger.getLogger(NalLoader.class.getName());

	NalLoader(Game game, World world, String questName, String levelName, InputStream input) throws IOException {
		this.game = game;
		this.world = world;
		this.level = NewtonAdventureLevelParser.Level.parseFrom(input);
		this.questName = questName;
		this.levelName = levelName;
	}

	public void load() {
		for (NewtonAdventureLevelParser.Entity entity : level.getEntitiesList()) {
			EntityType type = findEntityType(entity.getType());
			if (null != type) {
				try {
					loadEntity(entity, type);
				} catch (CannotLoadAnimation e) {
					LOGGER.log(java.util.logging.Level.WARNING, "Cannot load entity animation " + e.animation, e);
				}
			}
		}
	}

	private void loadEntity(NewtonAdventureLevelParser.Entity entity,
			NewtonAdventureLevelParser.EntityType type) throws CannotLoadAnimation {
		if (type.hasActivator()) {
			loadActivator(entity, type, type.getActivator());
		} else if (type.hasApple()) {
			loadApple(entity, type, type.getApple());
		} else if (type.hasAxeAnchor()) {
			loadAxeAnchor(entity, type, type.getAxeAnchor());
		} else if (type.hasBat()) {
			loadBat(entity, type, type.getBat());
		} else if (type.hasBouncePlatform()) {
			loadBouncePlatform(entity, type, type.getBouncePlatform());
		} else if (type.hasCannon()) {
			loadCannon(entity, type, type.getCannon());
		} else if (type.hasCloud()) {
			loadCloud(entity, type, type.getCloud());
		} else if (type.hasCoin()) {
			loadCoin(entity, type, type.getCoin());
		} else if (type.hasCompass()) {
			loadCompass(entity, type, type.getCompass());
		} else if (type.hasDoor()) {
			loadDoor(entity, type, type.getDoor());
		} else if (type.hasDoorToBonusWorld()) {
			loadDoorToBonusWorld(entity, type, type.getDoorToBonusWorld());
		} else if (type.hasEgyptianBoss()) {
			loadEgyptianBoss(entity, type, type.getEgyptianBoss());
		} else if (type.hasHelpSign()) {
			loadHelpSign(entity, type, type.getHelpSign());
		} else if (type.hasHero()) {
			loadHero(entity, type, type.getHero());
		} else if (type.hasKey()) {
			loadKey(entity, type, type.getKey());
		} else if (type.hasKeyLock()) {
			loadKeyLock(entity, type, type.getKeyLock());
		} else if (type.hasMemoryActivator()) {
			loadMemoryActivator(entity, type, type.getMemoryActivator());
		} else if (type.hasMobilePikeAnchor()) {
			loadMobilePikeAnchor(entity, type, type.getMobilePikeAnchor());
		} else if (type.hasMovingPlatform()) {
			loadMovingPlatform(entity, type, type.getMovingPlatform());
		} else if (type.hasMummy()) {
			loadMummy(entity, type, type.getMummy());
		} else if (type.hasPikes()) {
			loadPikes(entity, type, type.getPikes());
		} else if (type.hasPlatform()) {
			loadPlatform(entity, type, type.getPlatform());
		} else {
			LOGGER.warning(
					"no entity type record found, entity type name: "
							+ type.getName());
		}
	}

	private Vector2f getPos(Entity entity) {
		Position pos = entity.getPosition();
		return new Vector2f(pos.getX(), pos.getY());
	}

	private void loadBat(Entity entity, EntityType type, Bat batType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Bat bat = new im.bci.newtonadv.world.Bat(
					world, shape, getOrLoadAnimation(batType.getAnimation()));
			Vector2f pos = getPos(entity);
			bat.setPosition(pos.getX(), pos.getY());
			bat.setZOrder(entity.getZorder());
			world.add(bat);
		}
	}

	private void loadAxeAnchor(Entity entity, EntityType type,
			AxeAnchor axeAnchorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.AxeAnchor anchor = new im.bci.newtonadv.world.AxeAnchor(
					world, shape);
			anchor.setTexture(getOrLoadAnimation(axeAnchorType.getAnimation()));
			Vector2f pos = getPos(entity);
			anchor.setPosition(pos.getX(), pos.getY());
			anchor.setZOrder(entity.getZorder());
			world.add(anchor);

			Axe axe = new Axe(world);
			axe.setTexture(getOrLoadAnimation(axeAnchorType.getAxeAnimation()));
			axe.setPosition(anchor.getPosition().getX(), anchor.getPosition()
					.getY()
					- MobilePikes.height
					/ 2.0f
					- anchor.getShape().getBounds().getHeight() / 2.0f);
			axe.setZOrder(entity.getZorder());
			world.add(axe);

			BasicJoint j = new BasicJoint(anchor, axe, new Vector2f(
					anchor.getPosition()));
			j.setRelaxation(0);
			world.add(j);
		}
	}

	private void loadApple(Entity entity, EntityType type, Apple appleType)  throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Apple apple = new im.bci.newtonadv.world.Apple(
					world, shape);
			Vector2f pos = getPos(entity);
			apple.setPosition(pos.getX(), pos.getY());
			apple.setTexture(getOrLoadAnimation(appleType.getAnimation()));
			apple.setZOrder(entity.getZorder());
			world.addApple(apple);
		}
	}

	private void loadActivator(Entity entity, EntityType type,
			Activator activatorType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Activator activator = new im.bci.newtonadv.world.Activator(
					world, activatorType.getActivableId(),
					getOrLoadAnimation(activatorType.getOnAnimation()),
					getOrLoadAnimation(activatorType.getOffAnimation()), shape);
			Vector2f pos = getPos(entity);
			activator.setPosition(pos.getX(), pos.getY());
			activator.setZOrder(entity.getZorder());
			world.add(activator);
		}
	}

	private void loadPikes(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Pikes pikesType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Pikes pikes = new im.bci.newtonadv.world.Pikes(
					world, convertDangerousSide(pikesType.getDangerousSide()),
					shape);
			pikes.setTexture(getOrLoadAnimation(pikesType.getAnimation()));
			Vector2f pos = getPos(entity);
			pikes.setPosition(pos.getX(), pos.getY());
			pikes.setZOrder(entity.getZorder());
			world.add(pikes);
		}
	}

	private im.bci.newtonadv.world.Pikes.DangerousSide convertDangerousSide(
			DangerousSide dangerousSide) {
		switch (dangerousSide) {
		case DOWN:
			return im.bci.newtonadv.world.Pikes.DangerousSide.DOWN;
		case LEFT:
			return im.bci.newtonadv.world.Pikes.DangerousSide.LEFT;
		case RIGHT:
			return im.bci.newtonadv.world.Pikes.DangerousSide.RIGHT;
		case UP:
			return im.bci.newtonadv.world.Pikes.DangerousSide.UP;
		}
		return null;
	}

	private void loadMummy(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Mummy mummyType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Mummy mummy = new im.bci.newtonadv.world.Mummy(
					world, shape, getOrLoadAnimation(mummyType.getAnimation()));
			Vector2f pos = getPos(entity);
			mummy.setPosition(pos.getX(), pos.getY());
			mummy.setZOrder(entity.getZorder());
			world.add(mummy);
		}
	}

	private void loadMovingPlatform(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.MovingPlatform movingPlatformType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.MovingPlatform movingPlatform = new im.bci.newtonadv.world.MovingPlatform(world, getOrLoadAnimation(movingPlatformType.getAnimation()), convertPath(movingPlatformType.getPath()), shape);
			movingPlatform.setPosition(entity.getPosition().getX(), entity
					.getPosition().getY());
			movingPlatform.setZOrder(entity.getZorder());
			world.add(movingPlatform);			
		}

	}

	private Vector2f[] convertPath(Path path) {
		Vector2f[] result = new Vector2f[path.getPositionsCount()];
		for(int i=0; i<result.length; ++i) {
			final Position pos = path.getPositions(i);
			result[i] = new Vector2f(pos.getX(), pos.getY());
		}
		return null;
	}

	private void loadMobilePikeAnchor(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.MobilePikeAnchor mobilePikeAnchorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.MobilePikeAnchor anchor = new im.bci.newtonadv.world.MobilePikeAnchor(
					world, shape);
			anchor.setTexture(getOrLoadAnimation(mobilePikeAnchorType.getAnimation()));
			Vector2f pos = getPos(entity);
			anchor.setPosition(pos.getX(), pos.getY());
			anchor.setZOrder(entity.getZorder());
			world.add(anchor);

			MobilePikes mobilePikes = new MobilePikes(world);
			mobilePikes.setTexture(getOrLoadAnimation(mobilePikeAnchorType.getMobilePikesAnimation()));
			mobilePikes.setPosition(anchor.getPosition().getX(), anchor.getPosition()
					.getY()
					- MobilePikes.height
					/ 2.0f
					- anchor.getShape().getBounds().getHeight() / 2.0f);
			mobilePikes.setZOrder(entity.getZorder());
			world.add(mobilePikes);

			BasicJoint j = new BasicJoint(anchor, mobilePikes, new Vector2f(
					anchor.getPosition()));
			j.setRelaxation(0);
			world.add(j);
		}
	}

	private void loadMemoryActivator(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.MemoryActivator memoryActivatorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.MemoryActivator activator = new im.bci.newtonadv.world.MemoryActivator(
					world, memoryActivatorType.getActivableId(),
					getOrLoadAnimation(memoryActivatorType.getOnAnimation()),
					getOrLoadAnimation(memoryActivatorType.getOffAnimation()),
					getOrLoadAnimation(memoryActivatorType.getHideAnimation()),
					shape);
			Vector2f pos = getPos(entity);
			activator.setPosition(pos.getX(), pos.getY());
			activator.setZOrder(entity.getZorder());
			world.add(activator);
		}
	}

	private void loadKeyLock(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.KeyLock keyLockType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.KeyLock keyLock = new im.bci.newtonadv.world.KeyLock(
					world, shape);
			Vector2f pos = getPos(entity);
			keyLock.setPosition(pos.getX(), pos.getY());
			keyLock.setZOrder(entity.getZorder());
			keyLock.setTexture(getOrLoadAnimation(keyLockType.getAnimation()));
			world.add(keyLock);
		}
	}

	private void loadKey(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Key keyType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Key key = new im.bci.newtonadv.world.Key(
					world, shape);
			Vector2f pos = getPos(entity);
			key.setPosition(pos.getX(), pos.getY());
			key.setZOrder(entity.getZorder());
			key.setTexture(getOrLoadAnimation(keyType.getAnimation()));
			world.add(key);
		}
	}

	private void loadHero(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Hero heroType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			if (null == hero) {
				hero = new im.bci.newtonadv.world.Hero(world,shape);
				hero.setAnimation(getOrLoadAnimation(heroType.getAnimation()));
				Vector2f pos = getPos(entity);
				hero.setPosition(pos.getX(), pos.getY());
				hero.setZOrder(entity.getZorder());
				world.setHero(hero);
			} else {
				LOGGER.warning("One hero is enough for level " + levelName
						+ " in quest " + questName);
			}
		}

	}

	private void loadHelpSign(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.HelpSign helpSignType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.HelpSign helpSign = new im.bci.newtonadv.world.HelpSign(
					world, shape);
			Vector2f pos = getPos(entity);
			helpSign.setPosition(pos.getX(), pos.getY());
			helpSign.setZOrder(entity.getZorder());
			helpSign.setTexture(getOrLoadAnimation(helpSignType.getAnimation()));
			world.add(helpSign);
		}
	}

	private void loadEgyptianBoss(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.EgyptianBoss egyptianBossType)  throws CannotLoadAnimation{
		Vector2f pos = getPos(entity);
		im.bci.newtonadv.world.EgyptianBoss boss = new EgyptianBoss(world, pos.getX(), pos.getY());
		boss.setBodyTexture(getOrLoadAnimation(egyptianBossType.getBodyAnimation()));
		boss.setHandTexture(getOrLoadAnimation(egyptianBossType.getHandAnimation()));
		boss.setZOrder(entity.getZorder());
		boss.getLeftHand().setZOrder(entity.getZorder()+1);
		boss.getRightHand().setZOrder(entity.getZorder()+1);
		world.add(boss);
		world.add(boss.getLeftHand());
		world.add(boss.getRightHand());

	}

	private void loadDoorToBonusWorld(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.DoorToBonusWorld doorToBonusWorldType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.DoorToBonusWorld door = new im.bci.newtonadv.world.DoorToBonusWorld(
					world, shape);
			Vector2f pos = getPos(entity);
			door.setPosition(pos.getX(), pos.getY());
			door.setZOrder(entity.getZorder());
			door.setOpenTexture(getOrLoadAnimation(doorToBonusWorldType
					.getOpenAnimation()));
			door.setClosedTexture(getOrLoadAnimation(doorToBonusWorldType
					.getClosedAnimation()));
			world.add(door);
		}

	}

	private void loadDoor(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Door doorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Door door = new im.bci.newtonadv.world.Door(
					world, shape);
			Vector2f pos = getPos(entity);
			door.setPosition(pos.getX(), pos.getY());
			door.setZOrder(entity.getZorder());
			door.setOpenTexture(getOrLoadAnimation(doorType.getOpenAnimation()));
			door.setClosedTexture(getOrLoadAnimation(doorType
					.getClosedAnimation()));
			world.add(door);
		}
	}

	private void loadCompass(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Compass compassType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Compass compass = new im.bci.newtonadv.world.Compass(
					world, shape);
			Vector2f pos = getPos(entity);
			compass.setPosition(pos.getX(), pos.getY());
			compass.setZOrder(entity.getZorder());
			compass.setTexture(getOrLoadAnimation(compassType.getAnimation()));
			world.add(compass);
		}
	}

	private void loadCoin(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Coin coinType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Coin coin = new im.bci.newtonadv.world.Coin(
					world, shape);
			Vector2f pos = getPos(entity);
			coin.setPosition(pos.getX(), pos.getY());
			coin.setZOrder(entity.getZorder());
			coin.setTexture(getOrLoadAnimation(coinType.getAnimation()));
			world.add(coin);
		}
	}

	private void loadCloud(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Cloud cloudType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Cloud cloud = new im.bci.newtonadv.world.Cloud(
					world, shape);
			Vector2f pos = getPos(entity);
			cloud.setPosition(pos.getX(), pos.getY());
			cloud.setZOrder(entity.getZorder());
			cloud.setTexture(getOrLoadAnimation(cloudType.getAnimation()));
			world.add(cloud);
		}
	}

	private void loadCannon(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Cannon cannonType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Cannon cannon = new im.bci.newtonadv.world.Cannon(
					world, convertOrientation(cannonType.getOrientation()), shape);
			cannon.setTexture(getOrLoadAnimation(cannonType.getAnimation()));
			Vector2f pos = getPos(entity);
			cannon.setPosition(pos.getX(), pos.getY());
			cannon.setZOrder(entity.getZorder());
			world.add(cannon);
		}

	}

	private im.bci.newtonadv.world.Cannon.Orientation convertOrientation(
			Orientation orientation) {
		switch(orientation) {
		case DOWN:
			return im.bci.newtonadv.world.Cannon.Orientation.DOWN;
		case LEFT:
			return im.bci.newtonadv.world.Cannon.Orientation.LEFT;
		case RIGHT:
			return im.bci.newtonadv.world.Cannon.Orientation.RIGHT;
		case UP:
			return im.bci.newtonadv.world.Cannon.Orientation.RIGHT;
		}
		return null;
	}

	private void loadBouncePlatform(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.BouncePlatform bouncePlatformType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.BouncePlatform platform = new im.bci.newtonadv.world.BouncePlatform(
					world, shape);
			Vector2f pos = getPos(entity);
			platform.setPosition(pos.getX(), pos.getY());
			platform.setZOrder(entity.getZorder());
			platform.setTexture(getOrLoadAnimation(bouncePlatformType
					.getAnimation()));
			world.add(platform);
		}
	}

	private void loadPlatform(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Platform platformField) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			Platform platform = new Platform(this.world, shape);
			platform.setTexture(getOrLoadAnimation(platformField.getAnimation()));
			Vector2f pos = getPos(entity);
			platform.setPosition(pos.getX(), pos.getY());
			platform.setFriction(platformField.getFriction());
			platform.setZOrder(entity.getZorder());
			this.world.add(platform);
		}
	}

	private net.phys2d.raw.shapes.Shape loadShape(
			im.bci.newtonadv.NewtonAdventureLevelParser.Shape shape) throws CannotLoadAnimation {
		if (shape.hasCircle()) {
			return new Circle(shape.getCircle().getSize());
		} else if (shape.hasRectangle()) {
			return new Box(shape.getRectangle().getWidth(), shape
					.getRectangle().getHeight());
		} else if (shape.hasPolygon()) {
			ROVector2f[] vertices = new ROVector2f[shape.getPolygon()
					.getVerticesCount()];
			for (int i = 0; i < vertices.length; ++i) {
				vertices[i] = new Vector2f(shape.getPolygon().getVertices(i)
						.getX(), shape.getPolygon().getVertices(i).getY());
			}
			return new ConvexPolygon(vertices);
		}
		LOGGER.warning("unknow shape type");
		return null;
	}
	
	private static class CannotLoadAnimation extends Exception {

		private static final long serialVersionUID = 8865132972554999129L;
		private AnimationReference animation;

		public CannotLoadAnimation(AnimationReference animation, Exception e) {
			super(e);
			this.animation = animation;
		}
		
	}

	private AnimationCollection getOrLoadAnimation(AnimationReference animation) throws CannotLoadAnimation {
		try {
		return game.getView().loadSomeAnimations(
				game.getData().getLevelFilePath(questName, levelName, animation.getFile()), animation.getName());
		} catch(Exception e) {
			throw new CannotLoadAnimation(animation, e);
		}
	}

	private EntityType findEntityType(String type) {
		for (EntityType entityType : level.getEntityTypesList()) {
			if (entityType.getName().equals(type)) {
				return entityType;
			}
		}
		LOGGER.warning("cannot find entity type "+ type);
		return null;
	}
}