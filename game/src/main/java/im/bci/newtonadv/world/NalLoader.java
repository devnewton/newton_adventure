package im.bci.newtonadv.world;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Activator;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.AnimationReference;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Apple;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.AxeAnchor;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Bat;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Blocker;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.EntityType;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Level;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Path;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Phys2dBodyParameters;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Position;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Cannon.Orientation;
import im.bci.newtonadv.nal.NewtonAdventureLevelParser.Pikes.DangerousSide;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
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
		} else if(type.hasBlocker()) {
			loadBlocker(entity, type, type.getBlocker());
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
			if(batType.hasPhys2D()) {
				loadBody(bat, batType.getPhys2D());
			}
			world.add(bat);
		}
	}

	private void loadAxeAnchor(Entity entity, EntityType type,
			AxeAnchor axeAnchorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.AxeAnchor anchor = new im.bci.newtonadv.world.AxeAnchor(
					world, shape);
			anchor.setTexture(getOrLoadAnimation(axeAnchorType.getAnchor().getAnimation()));
			Vector2f pos = getPos(entity);
			anchor.setPosition(pos.getX(), pos.getY());
			anchor.setZOrder(entity.getZorder());
			if(axeAnchorType.getAnchor().hasPhys2D()) {
				loadBody(anchor, axeAnchorType.getAnchor().getPhys2D());
			}
			world.add(anchor);

			Axe axe = new Axe(world);
			axe.setTexture(getOrLoadAnimation(axeAnchorType.getMobile().getAnimation()));
			axe.setPosition(anchor.getPosition().getX(), anchor.getPosition()
					.getY()
					- MobilePikes.height
					/ 2.0f
					- anchor.getShape().getBounds().getHeight() / 2.0f);
			axe.setZOrder(entity.getZorder());
			if(axeAnchorType.getMobile().hasPhys2D()) {
				loadBody(axe, axeAnchorType.getMobile().getPhys2D());
			}
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
			if(activatorType.hasPhys2D()) {
				loadBody(activator, activatorType.getPhys2D());
			}
			world.add(activator);
		}
	}
	
	private void loadBlocker(Entity entity, EntityType type,
			Blocker blockerType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Blocker blocker = new im.bci.newtonadv.world.Blocker(
					world, blockerType.getActivableId(), shape);
			if(blockerType.hasAnimation()) {
				blocker.setTexture(getOrLoadAnimation(blockerType.getAnimation()));
			}
			Vector2f pos = getPos(entity);
			blocker.setPosition(pos.getX(), pos.getY());
			blocker.setZOrder(entity.getZorder());
			if(blockerType.hasPhys2D()) {
				loadBody(blocker, blockerType.getPhys2D());
			}
			world.add(blocker);
		}
	}

	private void loadBody(Body body,
			Phys2dBodyParameters p) {
			if(p.hasCanRest()) {
				body.setCanRest(p.getCanRest());
			}
			if(p.hasDamping()) {
				body.setDamping(p.getDamping());
			}
			if(p.hasEnabled()) {
				body.setEnabled(p.getEnabled());
			}
			if(p.hasFriction()) {
				body.setFriction(p.getFriction());
			}
			if(p.hasGravityEffected()) {
				body.setGravityEffected(p.getGravityEffected());
			}
			if(p.hasMoveable()) {
				body.setMoveable(p.getMoveable());
			}
			if(p.hasRestitution()) {
				body.setRestitution(p.getRestitution());
			}
			if(p.hasRotatable()) {
				body.setRotatable(p.getRotatable());
			}
			if(p.hasRotDamping()) {
				body.setRotDamping(p.getRotDamping());
			}
	}

	private void loadPikes(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Pikes pikesType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Pikes pikes = new im.bci.newtonadv.world.Pikes(
					world, convertDangerousSide(pikesType.getDangerousSide()),
					shape);
			if(pikesType.hasAnimation()) {
				pikes.setTexture(getOrLoadAnimation(pikesType.getAnimation()));
			}
			Vector2f pos = getPos(entity);
			pikes.setPosition(pos.getX(), pos.getY());
			pikes.setZOrder(entity.getZorder());
			if(pikesType.hasPhys2D()) {
				loadBody(pikes, pikesType.getPhys2D());
			}
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
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Mummy mummyType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Mummy mummy = new im.bci.newtonadv.world.Mummy(
					world, shape, getOrLoadAnimation(mummyType.getAnimation()));
			Vector2f pos = getPos(entity);
			mummy.setPosition(pos.getX(), pos.getY());
			mummy.setZOrder(entity.getZorder());
			if(mummyType.hasPhys2D()) {
				loadBody(mummy, mummyType.getPhys2D());
			}
			world.add(mummy);
		}
	}

	private void loadMovingPlatform(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.MovingPlatform movingPlatformType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.MovingPlatform movingPlatform = new im.bci.newtonadv.world.MovingPlatform(world, getOrLoadAnimation(movingPlatformType.getAnimation()), convertPath(movingPlatformType.getPath()), shape);
			movingPlatform.setPosition(entity.getPosition().getX(), entity
					.getPosition().getY());
			movingPlatform.setZOrder(entity.getZorder());
			if(movingPlatformType.hasPhys2D()) {
				loadBody(movingPlatform, movingPlatformType.getPhys2D());
			}
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
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.MobilePikeAnchor mobilePikeAnchorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.MobilePikeAnchor anchor = new im.bci.newtonadv.world.MobilePikeAnchor(
					world, shape);
			anchor.setTexture(getOrLoadAnimation(mobilePikeAnchorType.getAnchor().getAnimation()));
			Vector2f pos = getPos(entity);
			anchor.setPosition(pos.getX(), pos.getY());
			anchor.setZOrder(entity.getZorder());
			if(mobilePikeAnchorType.getAnchor().hasPhys2D()) {
				loadBody(anchor, mobilePikeAnchorType.getAnchor().getPhys2D());
			}
			world.add(anchor);

			MobilePikes mobilePikes = new MobilePikes(world);
			mobilePikes.setTexture(getOrLoadAnimation(mobilePikeAnchorType.getMobile().getAnimation()));
			mobilePikes.setPosition(anchor.getPosition().getX(), anchor.getPosition()
					.getY()
					- MobilePikes.height
					/ 2.0f
					- anchor.getShape().getBounds().getHeight() / 2.0f);
			mobilePikes.setZOrder(entity.getZorder());
			if(mobilePikeAnchorType.getMobile().hasPhys2D()) {
				loadBody(mobilePikes, mobilePikeAnchorType.getMobile().getPhys2D());
			}
			world.add(mobilePikes);

			BasicJoint j = new BasicJoint(anchor, mobilePikes, new Vector2f(
					anchor.getPosition()));
			j.setRelaxation(0);
			world.add(j);
		}
	}

	private void loadMemoryActivator(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.MemoryActivator memoryActivatorType) throws CannotLoadAnimation {
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
			if(memoryActivatorType.hasPhys2D()) {
				loadBody(activator, memoryActivatorType.getPhys2D());
			}
			world.add(activator);
		}
	}

	private void loadKeyLock(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.KeyLock keyLockType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.KeyLock keyLock = new im.bci.newtonadv.world.KeyLock(
					world, shape);
			Vector2f pos = getPos(entity);
			keyLock.setPosition(pos.getX(), pos.getY());
			keyLock.setZOrder(entity.getZorder());
			keyLock.setTexture(getOrLoadAnimation(keyLockType.getAnimation()));
			if(keyLockType.hasPhys2D()) {
				loadBody(keyLock, keyLockType.getPhys2D());
			}
			world.add(keyLock);
		}
	}

	private void loadKey(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Key keyType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Key key = new im.bci.newtonadv.world.Key(
					world, shape);
			Vector2f pos = getPos(entity);
			key.setPosition(pos.getX(), pos.getY());
			key.setZOrder(entity.getZorder());
			key.setTexture(getOrLoadAnimation(keyType.getAnimation()));
			if(keyType.hasPhys2D()) {
				loadBody(key, keyType.getPhys2D());
			}
			world.add(key);
		}
	}

	private void loadHero(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Hero heroType)  throws CannotLoadAnimation{
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			if (null == hero) {
				hero = new im.bci.newtonadv.world.Hero(world,shape);
				hero.setAnimation(getOrLoadAnimation(heroType.getAnimation()));
				Vector2f pos = getPos(entity);
				hero.setPosition(pos.getX(), pos.getY());
				hero.setZOrder(entity.getZorder());
				if(heroType.hasPhys2D()) {
					loadBody(hero, heroType.getPhys2D());
				}
				world.setHero(hero);
			} else {
				LOGGER.warning("One hero is enough for level " + levelName
						+ " in quest " + questName);
			}
		}

	}

	private void loadHelpSign(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.HelpSign helpSignType)  throws CannotLoadAnimation{
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
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.EgyptianBoss egyptianBossType)  throws CannotLoadAnimation{
		Vector2f pos = getPos(entity);
		im.bci.newtonadv.world.Boss boss = new Boss(world, pos.getX(), pos.getY());
		boss.setTexture(getOrLoadAnimation(egyptianBossType.getBodyAnimation()));
		boss.setZOrder(entity.getZorder());
		boss.getLeftHand().setZOrder(entity.getZorder()+1);
		boss.getRightHand().setZOrder(entity.getZorder()+1);
		world.add(boss);
		world.add(boss.getLeftHand());
		world.add(boss.getRightHand());
	}

	private void loadDoorToBonusWorld(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.DoorToBonusWorld doorToBonusWorldType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.DoorToBonusWorld door = new im.bci.newtonadv.world.DoorToBonusWorld(
					world, shape);
			Vector2f pos = getPos(entity);
			door.setPosition(pos.getX(), pos.getY());
			door.setZOrder(entity.getZorder());
			door.setTexture(getOrLoadAnimation(doorToBonusWorldType
					.getOpenAnimation()));
			world.add(door);
		}

	}

	private void loadDoor(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Door doorType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Door door = new im.bci.newtonadv.world.Door(
					world, shape);
			Vector2f pos = getPos(entity);
			door.setPosition(pos.getX(), pos.getY());
			door.setZOrder(entity.getZorder());
			door.setTexture(getOrLoadAnimation(doorType.getOpenAnimation()));
			world.add(door);
		}
	}

	private void loadCompass(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Compass compassType) throws CannotLoadAnimation {
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
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Coin coinType) throws CannotLoadAnimation {
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
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Cloud cloudType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Cloud cloud = new im.bci.newtonadv.world.Cloud(
					world, shape);
			Vector2f pos = getPos(entity);
			cloud.setPosition(pos.getX(), pos.getY());
			cloud.setZOrder(entity.getZorder());
			cloud.setTexture(getOrLoadAnimation(cloudType.getAnimation()));
			if(cloudType.hasPhys2D()) {
				loadBody(cloud, cloudType.getPhys2D());
			}
			world.add(cloud);
		}
	}

	private void loadCannon(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Cannon cannonType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.Cannon cannon = new im.bci.newtonadv.world.Cannon(
					world, convertOrientation(cannonType.getOrientation()), shape);
			if(cannonType.hasAnimation()) {
				cannon.setTexture(getOrLoadAnimation(cannonType.getAnimation()));
			}
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
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.BouncePlatform bouncePlatformType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			im.bci.newtonadv.world.BouncePlatform platform = new im.bci.newtonadv.world.BouncePlatform(
					world, shape);
			Vector2f pos = getPos(entity);
			platform.setPosition(pos.getX(), pos.getY());
			platform.setZOrder(entity.getZorder());
			if(bouncePlatformType.hasAnimation()) {
				platform.setTexture(getOrLoadAnimation(bouncePlatformType
					.getAnimation()));
			}
			if(bouncePlatformType.hasPhys2D()) {
				loadBody(platform, bouncePlatformType.getPhys2D());
			}
			world.add(platform);
		}
	}

	private void loadPlatform(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Platform platformType) throws CannotLoadAnimation {
		Shape shape = loadShape(type.getShape());
		if (null != shape) {
			Platform platform = new Platform(this.world, shape);
			if(platformType.hasAnimation()) {
				platform.setTexture(getOrLoadAnimation(platformType.getAnimation()));
			}
			Vector2f pos = getPos(entity);
			platform.setPosition(pos.getX(), pos.getY());
			platform.setZOrder(entity.getZorder());
			if(platformType.hasPhys2D()) {
				loadBody(platform, platformType.getPhys2D());
			}
			this.world.add(platform);
		}
	}

	private net.phys2d.raw.shapes.Shape loadShape(
			im.bci.newtonadv.nal.NewtonAdventureLevelParser.Shape shape) {
		if (shape.hasCircle()) {
			return new Circle(shape.getCircle().getSize());
		} else if (shape.hasRectangle()) {
			return new Box(shape.getRectangle().getWidth(), shape
					.getRectangle().getHeight());
		} else if (shape.hasPolygon()) {
			ROVector2f[] vertices = new ROVector2f[shape.getPolygon()
					.getVerticesCount()];
			if(vertices.length < 3) {
				LOGGER.warning("Invalid polygon (less than 3 vertices)");
				return null;
			}
			if(vertices.length > 4) {
				LOGGER.warning("Newton Adventure does not handle polygon with more than 4 vertices");
				return null;
			}
			int nbVertices = Math.min(4, vertices.length);
			for (int i = 0; i < nbVertices; ++i) {
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