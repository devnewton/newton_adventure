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
package im.bci.newtonadv.world;

import im.bci.newtonadv.Game;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.util.MultidimensionnalIterator;
import im.bci.newtonadv.util.NewtonColor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import tiled.core.Map;
import tiled.core.Tile;
import tiled.io.TMXMapReader;

/**
 *
 * @author devnewton
 */
public class TmxLoader {

    private static final Logger LOGGER = Logger.getLogger(TmxLoader.class.getName());
    private AnimationCollection explosionAnimation;
    private AnimationCollection mummyAnimation;
    private AnimationCollection batAnimation;
    private AnimationCollection keyTexture;
    private AnimationCollection doorTexture;
    private AnimationCollection doorToBonusWorldTexture;
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
    private AnimationCollection appleIconTexture;
    private AnimationCollection coinTexture;
    private AnimationCollection worldMapTexture;
    private AnimationCollection compassTexture;
    private AnimationCollection fireBallTexture;
    private AnimationCollection bombTexture;
    private AnimationCollection crateTexture;
    private final World world;
    private final Game game;
    private final String questName;
    private final String levelName;
    private Map map;
    private Future<Map> futureMap;
    private Hero hero;
    private MultidimensionnalIterator iterator;
    static final float defaultPickableObjectSize = 2.0f * World.distanceUnit;
    private static final Circle defaultPickableObjectShape = new Circle(
            defaultPickableObjectSize / 2.0f);
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    

    public TmxLoader(Game game, World world, String questName, String levelName) throws Exception {

        this.world = world;
        this.game = game;
        this.questName = questName;
        this.levelName = levelName;
    }

    public void preloading() {
        futureMap = executor.submit(new Callable<Map>() {

            @Override
            public Map call() throws Exception {
                InputStream mapInputStream = game.getData().openLevelTmx(questName,
                        levelName);
                if (null == mapInputStream) {
                    throw new RuntimeException("Cannot find tmx file for quest " + questName + " and level " + levelName);
                }
                try {
                    TMXMapReader mapReader = new TMXMapReader();
                    return mapReader.readMap(mapInputStream);
                } finally {
                    mapInputStream.close();
                }
            }
        });
    }

    public void startLoading() throws FileNotFoundException, IOException, Exception {

        this.map = futureMap.get();

        final ITextureCache textureCache = game.getView().getTextureCache();
        explosionAnimation = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.explosion"));
        mummyAnimation = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.mummy"));
        batAnimation = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.bat"));
        appleIconTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.apple"));
        world.setAppleIcon(appleIconTexture);
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
        doorTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.door"));
        doorToBonusWorldTexture = game.getView().loadFromAnimation(
                getFileFromMap(map,
                "newton_adventure.door_to_bonus_world"));
        mobilePikesTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.mobilePikes"));
        axeTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.axe"));
        activator1OnTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.activator1.on"));
        activator2OnTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.activator2.on"));
        activator3OnTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.activator3.on"));
        activator1OffTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.activator1.off"));
        activator2OffTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.activator2.off"));
        activator3OffTexture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.activator3.off"));
        memoryActivatorHiddenTexture = game.getView().loadFromAnimation(
                getFileFromMap(map,
                "newton_adventure.memory_activator.hidden"));
        blocker1Texture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.blocker1"));
        blocker2Texture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.blocker2"));
        blocker3Texture = game.getView().loadFromAnimation(
                getFileFromMap(map, "newton_adventure.blocker3"));

        iterator = new MultidimensionnalIterator(new int[]{map.getLayerCount(), map.getWidth(), map.getHeight()});
    }

    public boolean loadSome() throws IOException {
        if (iterator.hasNext()) {
            int[] indexes = iterator.next();
            final int layerIndex = indexes[0];
            tiled.core.MapLayer layer = map.getLayer(layerIndex);
            if (layer instanceof tiled.core.TileLayer) {
                tiled.core.TileLayer tileLayer = (tiled.core.TileLayer) layer;
                int zorderBase = layerIndex * 1000000;
                int x = indexes[1], y = indexes[2];
                Tile tile = tileLayer.getTileAt(x, y);
                if (null != tile) {
                    initFromTile(x - map.getWidth() / 2.0f, -y
                            + map.getHeight() / 2.0f, map, tile,
                            zorderBase);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void finishLoading() {

        String backgroundTextureFile = getFileFromMapIfAvailable(map,
                "newton_adventure.background");
        if (null != backgroundTextureFile) {
            world.setBackgroundTexture(game.getView().getTextureCache().getTexture(backgroundTextureFile));
        }
        game.getSoundCache().playMusicIfEnabled(
                getFileFromMap(map, "newton_adventure.music"));

        world.setIsRotateGravityPossible("true".equals(getMapProperty(map,
                "newton_adventure.rotate_gravity_possible")));
    }

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
            Platform platform = new Platform(world, tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("slash_platform")) {
            Platform platform = new Platform(world, tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setShape(new Line(-tileWidth / 2.0f, -tileHeight / 2.0f, tileWidth / 2.0f, tileHeight / 2.0f));
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("antislash_platform")) {
            Platform platform = new Platform(world, tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setShape(new Line(-tileWidth / 2.0f, tileHeight / 2.0f, tileWidth / 2.0f, -tileHeight / 2.0f));
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("up_right_half_platform")) {
            UpRightHalfPlatform platform = new UpRightHalfPlatform(world,
                    tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("up_left_half_platform")) {
            UpLeftHalfPlatform platform = new UpLeftHalfPlatform(world,
                    tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("down_left_half_platform")) {
            DownLeftHalfPlatform platform = new DownLeftHalfPlatform(world,
                    tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("down_right_half_platform")) {
            DownRightHalfPlatform platform = new DownRightHalfPlatform(world,
                    tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("hero")) {
            if (null == hero) {
                hero = new Hero(world);
                hero.setPosition(tileX, tileY);
                hero.setZOrder(getTileZOrder(tile, zOrderBase));
                hero.setAnimation(
                        game.getView().loadFromAnimation(
                        getFileFromMap(map, "newton_adventure.hero")));
                hero.setJumpSound(
                        game.getSoundCache().getSound(
                        game.getData().getFile("jump.wav")));
                hero.setPickupSound(game.getSoundCache().getSound(
                        game.getData().getFile("pickup.wav")));
                hero.setHurtSound(game.getSoundCache().getSound(
                        game.getData().getFile("hurt.wav")));
                world.setHero(hero);
            } else {
                LOGGER.log(Level.WARNING, "One hero is enough for level {0} in quest {1}", new Object[]{levelName, questName});
            }
        } else if (c.equals("mummy")) {
            Mummy mummy = new Mummy(world, new Circle(World.distanceUnit),
                    mummyAnimation);
            mummy.setPosition(tileX, tileY);
            mummy.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(mummy);
        } else if (c.equals("bat")) {
            Bat bat = new Bat(world, new Box(World.distanceUnit * 1.0f,
                    World.distanceUnit * 0.5f), batAnimation);
            bat.setPosition(tileX, tileY);
            bat.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(bat);
        } else if (c.equals("apple")) {
            Apple apple = new Apple(world, defaultPickableObjectShape);
            apple.setPosition(tileX, tileY);
            apple.setTexture(appleIconTexture);
            apple.setZOrder(getTileZOrder(tile, zOrderBase));
            world.addApple(apple);
        } else if (c.equals("coin")) {
            Coin coin = new Coin(world, defaultPickableObjectShape);
            coin.setPosition(tileX, tileY);
            coin.setTexture(coinTexture);
            coin.setZOrder(getTileZOrder(tile, zOrderBase));
            world.addCoin(coin);
        } else if (c.equals("letter")) {
            Letter letter = new Letter(world, defaultPickableObjectShape);
            letter.setPosition(tileX, tileY);
            letter.setTexture(getAnimationForTile(map, tile, textureCache));
            letter.setZOrder(getTileZOrder(tile, zOrderBase + 10));
            world.addCoin(letter);
        } else if (c.equals("world_map")) {
            WorldMap worldMap = new WorldMap(world, defaultPickableObjectShape);
            worldMap.setPosition(tileX, tileY);
            worldMap.setTexture(worldMapTexture);
            worldMap.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(worldMap);
        } else if (c.equals("compass")) {
            Compass compass = new Compass(world, defaultPickableObjectShape);
            compass.setPosition(tileX, tileY);
            compass.setTexture(compassTexture);
            compass.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(compass);
        } else if (c.equals("key")) {
            Key key = new Key(world);
            key.setPosition(tileX, tileY);
            key.setTexture(keyTexture);
            key.setZOrder(getTileZOrder(tile, zOrderBase));
            key.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                    "newton_adventure.color", "white")));
            world.addKey(key);

        } else if (c.equals("door")) {
            Door door = new Door(world, tileWidth, tileHeight);
            door.setPosition(tileX, tileY);
            door.setTexture(doorTexture);
            door.setZOrder(getTileZOrder(tile, zOrderBase));
            if (tile.getProperties().containsKey("newton_adventure.color")) {
                door.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                        "newton_adventure.color", "white")));
            }
            world.add(door);
        } else if (c.equals("door_to_bonus_world")) {
            DoorToBonusWorld door = new DoorToBonusWorld(world, tileWidth,
                    tileHeight);
            door.setPosition(tileX, tileY);
            door.setTexture(doorToBonusWorldTexture);
            door.setZOrder(getTileZOrder(tile, zOrderBase));
            door.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                    "newton_adventure.color", "white")));
            world.add(door);
        } else if (c.equals("cloud")) {
            Cloud cloud = new Cloud(world, tileWidth, tileHeight);
            cloud.setTexture(getAnimationForTile(map, tile, textureCache));
            cloud.setPosition(tileX, tileY);
            cloud.setZOrder(getTileZOrder(tile, zOrderBase));
            cloud.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                    "newton_adventure.color", "white")));
            world.add(cloud);
        } else if (c.equals("pikes_up")) {
            Pikes pikes = new Pikes(world, Pikes.DangerousSide.UP, tileWidth,
                    tileHeight);
            pikes.setTexture(getAnimationForTile(map, tile, textureCache));
            pikes.setPosition(tileX, tileY);
            pikes.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(pikes);
        } else if (c.equals("pikes_down")) {
            Pikes pikes = new Pikes(world, Pikes.DangerousSide.DOWN, tileWidth,
                    tileHeight);
            pikes.setTexture(getAnimationForTile(map, tile, textureCache));
            pikes.setPosition(tileX, tileY);
            pikes.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(pikes);
        } else if (c.equals("pikes_left")) {
            Pikes pikes = new Pikes(world, Pikes.DangerousSide.LEFT, tileWidth,
                    tileHeight);
            pikes.setTexture(getAnimationForTile(map, tile, textureCache));
            pikes.setPosition(tileX, tileY);
            pikes.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(pikes);
        } else if (c.equals("pikes_right")) {
            Pikes pikes = new Pikes(world, Pikes.DangerousSide.RIGHT, tileWidth,
                    tileHeight);
            pikes.setTexture(getAnimationForTile(map, tile, textureCache));
            pikes.setPosition(tileX, tileY);
            pikes.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(pikes);
        } else if (c.equals("cannon_up")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.UP, tileWidth,
                    tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(fireBallTexture);
            cannon.setExplosionTexture(explosionAnimation);
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("cannon_down")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.DOWN,
                    tileWidth, tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(fireBallTexture);
            cannon.setExplosionTexture(explosionAnimation);
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("cannon_right")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.RIGHT,
                    tileWidth, tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(fireBallTexture);
            cannon.setExplosionTexture(explosionAnimation);
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("cannon_left")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.LEFT,
                    tileWidth, tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(fireBallTexture);
            cannon.setExplosionTexture(explosionAnimation);
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("mobile_pike_anchor")) {
            MobilePikeAnchor anchor = new MobilePikeAnchor(world);
            anchor.setTexture(getAnimationForTile(map, tile, textureCache));
            anchor.setPosition(tileX, tileY);
            anchor.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(anchor);

            MobilePikes pikes = new MobilePikes(world);
            pikes.setTexture(mobilePikesTexture);
            pikes.setPosition(anchor.getPosition().getX(), anchor.getPosition().getY()
                    - MobilePikes.height
                    / 2.0f
                    - anchor.getShape().getBounds().getHeight() / 2.0f);
            world.add(pikes);
            pikes.setZOrder(getTileZOrder(tile, zOrderBase));

            BasicJoint j = new BasicJoint(anchor, pikes, new Vector2f(
                    anchor.getPosition()));
            j.setRelaxation(0);
            world.add(j);
        } else if (c.equals("axe_anchor")) {
            AxeAnchor anchor = new AxeAnchor(world);
            anchor.setTexture(getAnimationForTile(map, tile, textureCache));
            anchor.setPosition(tileX, tileY);
            anchor.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(anchor);

            Axe axe = new Axe(world);
            axe.setTexture(axeTexture);
            axe.setPosition(anchor.getPosition().getX(), anchor.getPosition().getY()
                    - MobilePikes.height
                    / 2.0f
                    - anchor.getShape().getBounds().getHeight() / 2.0f);
            axe.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(axe);

            BasicJoint j = new BasicJoint(anchor, axe, new Vector2f(
                    anchor.getPosition()));
            j.setRelaxation(0);
            world.add(j);
        } else if (c.equals("bounce_platform")) {
            BouncePlatform platform = new BouncePlatform(world, tileWidth,
                    tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("activator1")) {
            Activator activator = new Activator(world, 1, activator1OnTexture,
                    activator1OffTexture, tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("activator2")) {
            Activator activator = new Activator(world, 2, activator2OnTexture,
                    activator2OffTexture, tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("activator3")) {
            Activator activator = new Activator(world, 3, activator3OnTexture,
                    activator3OffTexture, tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("memory_activator1")) {
            MemoryActivator activator = new MemoryActivator(world, 1,
                    activator1OnTexture, activator1OffTexture,
                    memoryActivatorHiddenTexture, tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("memory_activator2")) {
            MemoryActivator activator = new MemoryActivator(world, 2,
                    activator2OnTexture, activator2OffTexture,
                    memoryActivatorHiddenTexture, tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("memory_activator3")) {
            MemoryActivator activator = new MemoryActivator(world, 3,
                    activator3OnTexture, activator3OffTexture,
                    memoryActivatorHiddenTexture, tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("blocker1")) {
            Blocker activable = new Blocker(world, 1, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, textureCache, blocker1Texture));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("blocker2")) {
            Blocker activable = new Blocker(world, 2, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, textureCache, blocker2Texture));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("blocker3")) {
            Blocker activable = new Blocker(world, 3, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, textureCache, blocker3Texture));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("laser_blocker")) {
            LaserBlocker activable = new LaserBlocker(world, 1, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, textureCache, blocker1Texture));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("accelerator")) {
            Accelerator accelerator = new Accelerator(world, tileWidth, tileHeight, getAcceleratorForce(tile));
            accelerator.setTexture(getAnimationForTile(map, tile, textureCache));
            accelerator.setPosition(tileX, tileY);
            accelerator.setFriction(getTileFriction(tile));
            accelerator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(accelerator);
        } else if (c.equals("moving_platform")) {
            MovingPlatform platform = new MovingPlatform(world,
                    getAnimationForTile(map, tile, textureCache),
                    getMovingPlatformPath(tile, x, y, baseSize),
                    tileWidth, tileHeight);
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("dangerous_moving_platform")) {
            DangerousMovingPlatform platform = new DangerousMovingPlatform(world,
                    getAnimationForTile(map, tile, textureCache),
                    getMovingPlatformPath(tile, x, y, baseSize),
                    tileWidth, tileHeight);
            if (tile.getProperties().containsKey("newton_adventure.color")) {
                platform.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                        "newton_adventure.color", "white")));
            }
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("teleporter")) {
            Teleporter teleporter = new Teleporter(world, tileWidth, tileHeight);
            teleporter.setTexture(getAnimationForTile(map, tile, textureCache));
            teleporter.setPosition(tileX, tileY);
            teleporter.setZOrder(getTileZOrder(tile, zOrderBase, 1));
            teleporter.setColor(tile.getProperties().getProperty(
                    "newton_adventure.teleporter.color"));
            world.add(teleporter);
        } else if (c.equals("colorizer")) {
            Colorizer colorizer = new Colorizer(world, tileWidth, tileHeight);
            colorizer.setTexture(getAnimationForTile(map, tile, textureCache));
            colorizer.setPosition(tileX, tileY);
            colorizer.setZOrder(getTileZOrder(tile, zOrderBase, 1));
            colorizer.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                    "newton_adventure.color", "white")));
            world.add(colorizer);
        } else if (c.equals("colored_platform")) {
            ColoredPlatform colored = new ColoredPlatform(world, tileWidth, tileHeight);
            colored.setTexture(getAnimationForTile(map, tile, textureCache));
            colored.setPosition(tileX, tileY);
            colored.setZOrder(getTileZOrder(tile, zOrderBase, 1));
            NewtonColor color = NewtonColor.valueOf(tile.getProperties().getProperty(
                    "newton_adventure.color"));
            world.addColoredPlatform(color, colored);

        } else if (c.equals("keylock")) {
            KeyLock keylock = new KeyLock(world, tileWidth, tileHeight);
            keylock.setTexture(getAnimationForTile(map, tile, textureCache));
            keylock.setPosition(tileX, tileY);
            keylock.setZOrder(getTileZOrder(tile, zOrderBase));
            keylock.setColor(NewtonColor.valueOf(tile.getProperties().getProperty(
                    "newton_adventure.color", "white")));
            world.add(keylock);
        } else if (c.equals("help_sign")) {
            HelpSign helpSign = new HelpSign(world, tileWidth, tileHeight);
            helpSign.setTexture(getAnimationForTile(map, tile, textureCache));
            helpSign.setPosition(tileX, tileY);
            helpSign.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(helpSign);
        } else if (c.equals("bomb")) {
            Bomb bomb = new Bomb(world);
            bomb.setTexture(bombTexture);
            bomb.setFireBallTexture(fireBallTexture);
            bomb.setExplosionTexture(explosionAnimation);
            bomb.setPosition(tileX, tileY);
            bomb.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(bomb);
        } else if (c.equals("bomb_hole")) {
            BombHole bombHole = new BombHole(world, tileWidth, tileHeight);
            bombHole.setBombTexture(bombTexture);
            bombHole.setFireBallTexture(fireBallTexture);
            bombHole.setExplosionTexture(explosionAnimation);
            bombHole.setTexture(getAnimationForTile(map, tile, textureCache));
            bombHole.setPosition(tileX, tileY);
            bombHole.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(bombHole);
        } else if (c.equals("crate")) {
            Crate crate = new Crate(world, tileWidth, tileHeight);
            crate.setTexture(crateTexture);
            crate.setPosition(tileX, tileY);
            crate.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(crate);
        } else if (c.equals("boss")) {
            Boss boss = new Boss(world, tileX, tileY);
            boss.setTexture(getAnimationForTile(map, tile, textureCache));
            boss.setExplosionTexture(explosionAnimation);
            boss.setZOrder(getTileZOrder(tile, zOrderBase));
            boss.getLeftHand().setZOrder(getTileZOrder(tile, zOrderBase + 1));
            boss.getRightHand().setZOrder(getTileZOrder(tile, zOrderBase + 1));
            world.add(boss);
            world.add(boss.getLeftHand());
            world.add(boss.getRightHand());
        } else {
            Platform platform = new Platform(world, tileWidth, tileHeight);
            platform.setTexture(getAnimationForTile(map, tile, textureCache));
            platform.setPosition(tileX, tileY);
            platform.setEnabled(false);
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
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

    private AnimationCollection getAnimationForTile(tiled.core.Map map,
            tiled.core.Tile tile, ITextureCache textureCache, AnimationCollection defaultAnimation)
            throws FileNotFoundException, IOException {
        AnimationCollection animation;
        String gfx = tile.getProperties().getProperty("newton_adventure.gfx");
        if (null != gfx) {
            animation = game.getView().loadFromAnimation(
                    game.getData().getLevelFilePath(questName, levelName, gfx));
        } else {
            animation = defaultAnimation;
        }
        return animation;
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

    private Vector2f getAcceleratorForce(Tile tile) {
        float ax = Float.parseFloat(tile.getProperties().getProperty("newton_adventure.accelerator.ax"));
        float ay = Float.parseFloat(tile.getProperties().getProperty("newton_adventure.accelerator.ay"));
        return new Vector2f(ax, ay);
    }

    public final String getFileFromMap(tiled.core.Map map, String filePropertyName) {
        String filename = getFileFromMapIfAvailable(map, filePropertyName);
        if (filename != null) {
            return filename;
        } else {
            throw new RuntimeException(
                    "error in tmx map file, cannot find property "
                    + filePropertyName);
        }
    }

    public final String getFileFromMapIfAvailable(tiled.core.Map map,
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
                "door_to_bonus_world.nanim");
        defaultMapProperties.put("newton_adventure.door", "door.nanim");
        defaultMapProperties.put("newton_adventure.key", "key.nanim");
        defaultMapProperties.put("newton_adventure.hero", "hero.nanim");
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

    private String getMapProperty(tiled.core.Map map, String prop) {
        String value = map.getProperties().getProperty(prop);
        if (null == value) {
            return defaultMapProperties.getProperty(prop);
        }
        return value;
    }
}
