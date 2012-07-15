package im.bci.newtonadv.world;

import im.bci.newtonadv.NewtonAdventureLevelParser;
import im.bci.newtonadv.NewtonAdventureLevelParser.Activator;
import im.bci.newtonadv.NewtonAdventureLevelParser.AnimationReference;
import im.bci.newtonadv.NewtonAdventureLevelParser.Apple;
import im.bci.newtonadv.NewtonAdventureLevelParser.AxeAnchor;
import im.bci.newtonadv.NewtonAdventureLevelParser.Bat;
import im.bci.newtonadv.NewtonAdventureLevelParser.Entity;
import im.bci.newtonadv.NewtonAdventureLevelParser.EntityType;
import im.bci.newtonadv.NewtonAdventureLevelParser.Level;
import im.bci.newtonadv.anim.AnimationCollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Shape;

class NalLoader {
	/**
	 * 
	 */
	private final World world;
	Level level;
	final Logger logger = Logger.getLogger(NalLoader.class.getName());
	
	NalLoader(World world, InputStream input) throws IOException {
		this.world = world;
		this.level = NewtonAdventureLevelParser.Level.parseFrom(input);
	}
	public void load() {
		for(NewtonAdventureLevelParser.Entity entity : level.getEntitiesList()) {
			EntityType type = findEntityType(entity.getType());
			if(null != type) {
				loadEntity(entity, type);
			}
		}
	}
	private void loadEntity(
			NewtonAdventureLevelParser.Entity entity,
			NewtonAdventureLevelParser.EntityType type) {
		if(type.hasActivator()) {
			loadActivator(entity, type, type.getActivator());
		} else if(type.hasApple()) {
			loadApple(entity, type, type.getApple());
		}else if(type.hasAxeAnchor()) {
			loadAxeAnchor(entity, type, type.getAxeAnchor());
		}else if(type.hasBat()) {
			loadBat(entity, type, type.getBat());
		}else if(type.hasBouncePlatform()) {
			loadBouncePlatform(entity, type, type.getBouncePlatform());
		}else if(type.hasCannon()) {
			loadCannon(entity, type, type.getCannon());
		}else if(type.hasCloud()) {
			loadCloud(entity, type, type.getCloud());
		}else if(type.hasCoin()) {
			loadCoin(entity, type, type.getCoin());
		}else if(type.hasCompass()) {
			loadCompass(entity, type, type.getCompass());
		}else if(type.hasDoor()) {
			loadDoor(entity, type, type.getDoor());
		}else if(type.hasDoorToBonusWorld()) {
			loadDoorToBonusWorld(entity, type, type.getDoorToBonusWorld());
		}else if(type.hasEgyptianBoss()) {
			loadEgyptianBoss(entity, type, type.getEgyptianBoss());
		}else if(type.hasHelpSign()) {
			loadHelpSign(entity, type, type.getHelpSign());
		}else if(type.hasHero()) {
			loadHero(entity, type, type.getHero());
		}else if(type.hasKey()) {
			loadKey(entity, type, type.getKey());
		}else if(type.hasKeyLock()) {
			loadKeyLock(entity, type, type.getKeyLock());
		}else if(type.hasMemoryActivator()) {
			loadMemoryActivator(entity, type, type.getMemoryActivator());
		}else if(type.hasMobilePikeAnchor()) {
			loadMobilePikeAnchor(entity, type, type.getMobilePikeAnchor());
		}else if(type.hasMovingPlatform()) {
			loadMovingPlatform(entity, type, type.getMovingPlatform());
		}else if(type.hasMummy()) {
			loadMummy(entity, type, type.getMummy());
		}else if(type.hasPikes()) {
			loadPikes(entity, type, type.getPikes());
		}else if(type.hasPlatform()) {
			loadPlatform(entity, type, type.getPlatform());
		} else {
			logger.log(java.util.logging.Level.WARNING, "no entity type record found, entity type name: " + type.getName());
		}
	}
	private void loadBat(Entity entity, EntityType type, Bat bat) {
		// TODO Auto-generated method stub
		
	}
	private void loadAxeAnchor(Entity entity, EntityType type,
			AxeAnchor axeAnchor) {
		// TODO Auto-generated method stub
		
	}
	private void loadApple(Entity entity, EntityType type, Apple apple) {
		// TODO Auto-generated method stub
		
	}
	private void loadActivator(Entity entity, EntityType type,
			Activator activator) {
		// TODO Auto-generated method stub
		
	}
	private void loadPikes(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Pikes pikes) {
		// TODO Auto-generated method stub
		
	}
	private void loadMummy(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Mummy mummy) {
		// TODO Auto-generated method stub
		
	}
	private void loadMovingPlatform(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.MovingPlatform movingPlatform) {
		// TODO Auto-generated method stub
		
	}
	private void loadMobilePikeAnchor(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.MobilePikeAnchor mobilePikeAnchor) {
		// TODO Auto-generated method stub
		
	}
	private void loadMemoryActivator(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.MemoryActivator memoryActivator) {
		// TODO Auto-generated method stub
		
	}
	private void loadKeyLock(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.KeyLock keyLock) {
		// TODO Auto-generated method stub
		
	}
	private void loadKey(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Key key) {
		// TODO Auto-generated method stub
		
	}
	private void loadHero(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Hero hero) {
		// TODO Auto-generated method stub
		
	}
	private void loadHelpSign(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.HelpSign helpSign) {
		// TODO Auto-generated method stub
		
	}
	private void loadEgyptianBoss(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.EgyptianBoss egyptianBoss) {
		// TODO Auto-generated method stub
		
	}
	private void loadDoorToBonusWorld(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.DoorToBonusWorld doorToBonusWorld) {
		// TODO Auto-generated method stub
		
	}
	private void loadDoor(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Door door) {
		// TODO Auto-generated method stub
		
	}
	private void loadCompass(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Compass compass) {
		// TODO Auto-generated method stub
		
	}
	private void loadCoin(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Coin coin) {
		// TODO Auto-generated method stub
		
	}
	private void loadCloud(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Cloud cloud) {
		// TODO Auto-generated method stub
		
	}
	private void loadCannon(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Cannon cannon) {
		// TODO Auto-generated method stub
		
	}
	private void loadBouncePlatform(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.BouncePlatform bouncePlatform) {
		// TODO Auto-generated method stub
		
	}
	private void loadPlatform(
			im.bci.newtonadv.NewtonAdventureLevelParser.Entity entity,
			EntityType type,
			im.bci.newtonadv.NewtonAdventureLevelParser.Platform platformField) {
		Shape shape = loadShape(type.getShape());
		if(null != shape) {
		Platform platform = new Platform(this.world, shape);
		platform.setTexture(getOrLoadAnimation(platformField.getAnimation()));
		platform.setPosition(entity.getPosition().getX(), entity.getPosition().getY());
		platform.setFriction(platformField.getFriction());
		platform.setZOrder(entity.getZorder());
		this.world.add(platform);
		}
	}
	private net.phys2d.raw.shapes.Shape loadShape(
			im.bci.newtonadv.NewtonAdventureLevelParser.Shape shape) {
		if(shape.hasCircle()) {
			return new Circle(shape.getCircle().getSize());
		} else if(shape.hasRectangle()) {
			return new Box(shape.getRectangle().getWidth(), shape.getRectangle().getHeight());
		} else if(shape.hasPolygon()) {
			ROVector2f[] vertices = new ROVector2f[shape.getPolygon().getVerticesCount()];
			for(int i = 0; i<vertices.length; ++i) {
				vertices[i] = new Vector2f(shape.getPolygon().getVertices(i).getX(), shape.getPolygon().getVertices(i).getY());
			}
			return new ConvexPolygon(vertices );
		}
		logger.log(java.util.logging.Level.WARNING, "unknow shape type");
		return null;
	}
	private AnimationCollection getOrLoadAnimation(
			AnimationReference animation) {
		// TODO Auto-generated method stub
		return null;
	}
	private EntityType findEntityType(String type) {
		for(EntityType entityType : level.getEntityTypesList()) {
			if(entityType.getName().equals(type)) {
				return entityType;
			}
		}
		logger.log(java.util.logging.Level.WARNING, "cannot find entity type " + type);
		return null;
	}		
}