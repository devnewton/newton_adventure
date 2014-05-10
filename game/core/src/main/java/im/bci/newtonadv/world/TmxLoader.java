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
import im.bci.tmxloader.TmxFrame;
import im.bci.tmxloader.TmxImage;
import im.bci.tmxloader.TmxLayer;
import im.bci.tmxloader.TmxMap;
import im.bci.tmxloader.TmxTileInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Line;

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
    private AnimationCollection clueTexture;
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
    private World world;
    private final Game game;
    private final String questName;
    private final String levelName;
    private TmxMap map;
    private Hero hero;
    private MultidimensionnalIterator iterator;
    static final float defaultPickableObjectSize = 2.0f * World.distanceUnit;
    private static final Circle defaultPickableObjectShape = new Circle(
            defaultPickableObjectSize / 2.0f);

    public TmxLoader(Game game, String questName, String levelName) throws Exception {
        this.game = game;
        this.questName = questName;
        this.levelName = levelName;
    }

    public void preloading() {
        map = game.getData().openLevelTmx(questName, levelName);
    }

    public void startLoading(World world) throws IOException, Exception {
        this.world = world;
        world.setAppleIcon(getAppleIconTexture());
        iterator = new MultidimensionnalIterator(new int[]{map.getLayers().size(), map.getWidth(), map.getHeight()});
    }

    public boolean hasMoreToLoad() {
        return iterator.hasNext();
    }

    public void loadSome() throws IOException {
        int[] indexes = iterator.next();
        final int layerIndex = indexes[0];
        TmxLayer tileLayer = map.getLayers().get(layerIndex);
        int zorderBase = layerIndex * 1000000;
        int x = indexes[1], y = indexes[2];
        TmxTileInstance tile = tileLayer.getTileAt(x, y);
        if (null != tile) {
            initFromTile(x - map.getWidth() / 2.0f, -y
                    + map.getHeight() / 2.0f, map, tile,
                    zorderBase);
        }
    }

    public void finishLoading() {

        String backgroundTextureFile = getFileFromMapIfAvailable(map,
                "newton_adventure.background");
        if (null != backgroundTextureFile) {
            world.setBackgroundTexture(game.getView().getTextureCache().getTexture(backgroundTextureFile));
        }
        game.getNuitToolkit().getAudio().playMusic(
                getFileFromMap(map, "newton_adventure.music"), true);

        world.setIsRotateGravityPossible("true".equals(getMapProperty(map,
                "newton_adventure.rotate_gravity_possible")));

        world.staticPlatformDrawer.postConstruct(world.getView());
    }

    private void initFromTile(float x, float y, TmxMap map,
            TmxTileInstance tile, int zOrderBase) throws IOException {
        ITextureCache textureCache = game.getView().getTextureCache();
        String c = tile.getProperty("newton_adventure.type",
                "unknown");

        final float baseSize = 2.0f * World.distanceUnit;
        final float tileWidthScale = (float) tile.getWidth()
                / (float) map.getTilewidth();
        final float tileHeightScale = (float) tile.getHeight()
                / (float) map.getTileheight();
        final float tileWidth = baseSize * tileWidthScale;
        final float tileHeight = baseSize * tileHeightScale;
        final float tileX = x * baseSize + tileWidth / 2.0f;
        final float tileY = y * baseSize + tileHeight / 2.0f;

        if (c.equals("platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimationCollection animation = game.getView().loadFromAnimation(
                        game.getData().getLevelFilePath(questName, levelName, gfx));
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(animation);
                platform.setPosition(tileX, tileY);
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("slash_platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(getAnimationForTile(map, tile, textureCache));
                platform.setPosition(tileX, tileY);
                platform.setShape(new Line(-tileWidth / 2.0f, -tileHeight / 2.0f, tileWidth / 2.0f, tileHeight / 2.0f));
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setShape(new Line(-tileWidth / 2.0f, -tileHeight / 2.0f, tileWidth / 2.0f, tileHeight / 2.0f));
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("antislash_platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(getAnimationForTile(map, tile, textureCache));
                platform.setPosition(tileX, tileY);
                platform.setShape(new Line(-tileWidth / 2.0f, tileHeight / 2.0f, tileWidth / 2.0f, -tileHeight / 2.0f));
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setShape(new Line(-tileWidth / 2.0f, tileHeight / 2.0f, tileWidth / 2.0f, -tileHeight / 2.0f));
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("up_right_half_platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(getAnimationForTile(map, tile, textureCache));
                platform.setPosition(tileX, tileY);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, tileHeight / 2.0f)}));
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, tileHeight / 2.0f)}));
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("up_left_half_platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(getAnimationForTile(map, tile, textureCache));
                platform.setPosition(tileX, tileY);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, tileHeight / 2.0f),
                    new Vector2f(-tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, tileHeight / 2.0f)}));
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, tileHeight / 2.0f),
                    new Vector2f(-tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, tileHeight / 2.0f)}));
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("down_left_half_platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(getAnimationForTile(map, tile, textureCache));
                platform.setPosition(tileX, tileY);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, tileHeight / 2.0f), new Vector2f(-tileWidth / 2.0f, -tileHeight / 2.0f), new Vector2f(tileWidth / 2.0f, -tileHeight / 2.0f)}));
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, tileHeight / 2.0f), new Vector2f(-tileWidth / 2.0f, -tileHeight / 2.0f), new Vector2f(tileWidth / 2.0f, -tileHeight / 2.0f)}));
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("down_right_half_platform")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(getAnimationForTile(map, tile, textureCache));
                platform.setPosition(tileX, tileY);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, tileHeight / 2.0f)}));
                platform.setFriction(getTileFriction(tile));
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setShape(new ConvexPolygon(new ROVector2f[]{new Vector2f(-tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, -tileHeight / 2.0f),
                    new Vector2f(tileWidth / 2.0f, tileHeight / 2.0f)}));
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setFriction(getTileFriction(tile));
                world.addStaticPlatform(platform);
            }
        } else if (c.equals("hero")) {
            if (null == hero) {
                hero = new Hero(world);
                hero.setPosition(tileX, tileY);
                hero.setZOrder(getTileZOrder(tile, zOrderBase));
                hero.setAnimation(
                        game.getView().loadFromAnimation(
                                getFileFromMap(map, "newton_adventure.hero")));
                hero.setJumpSound(
                        game.getNuitToolkit().getAudio().getSound(
                                game.getData().getFile("jump.wav")));
                hero.setPickupSound(game.getNuitToolkit().getAudio().getSound(
                        game.getData().getFile("pickup.wav")));
                hero.setHurtSound(game.getNuitToolkit().getAudio().getSound(
                        game.getData().getFile("hurt.wav")));
                Long deadClock = getMapDeadClock(map);
                if (null != deadClock) {
                    hero.setDeadClock(deadClock);
                }
                world.setHero(hero);
            } else {
                LOGGER.warning("One hero is enough for level " + levelName + " in quest " + questName);
            }
        } else if (c.equals("mummy")) {
            Mummy mummy = new Mummy(world, new Circle(World.distanceUnit), getMummyAnimation());
            mummy.setPosition(tileX, tileY);
            mummy.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(mummy);
        } else if (c.equals("bat")) {
            Bat bat = new Bat(world, new Box(World.distanceUnit * 1.0f,
                    World.distanceUnit * 0.5f), getBatAnimation());
            bat.setPosition(tileX, tileY);
            bat.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(bat);
        } else if (c.equals("apple")) {
            Apple apple = new Apple(world, defaultPickableObjectShape);
            apple.setPosition(tileX, tileY);
            apple.setTexture(getAppleIconTexture());
            apple.setZOrder(getTileZOrder(tile, zOrderBase));
            world.addApple(apple);
        } else if (c.equals("coin")) {
            Coin coin = new Coin(world, defaultPickableObjectShape);
            coin.setPosition(tileX, tileY);
            coin.setTexture(getCoinTexture());
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
            worldMap.setTexture(getWorldMapTexture());
            worldMap.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(worldMap);
        } else if (c.equals("compass")) {
            Compass compass = new Compass(world, defaultPickableObjectShape);
            compass.setPosition(tileX, tileY);
            compass.setTexture(getCompassTexture());
            compass.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(compass);
        } else if (c.equals("key")) {
            Key key = new Key(world);
            key.setPosition(tileX, tileY);
            key.setTexture(getKeyTexture());
            key.setZOrder(getTileZOrder(tile, zOrderBase));
            key.setColor(NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white")));
            world.addKey(key);

        } else if (c.equals("door")) {
            Door door = new Door(world, tileWidth, tileHeight);
            door.setPosition(tileX, tileY);
            door.setTexture(getDoorTexture());
            door.setZOrder(getTileZOrder(tile, zOrderBase));
            door.setColor(NewtonColor.valueOf(tile.getProperty("newton_adventure.color", "white")));
            world.add(door);
        } else if (c.equals("door_to_bonus_world")) {
            DoorToBonusWorld door = new DoorToBonusWorld(world, tileWidth,
                    tileHeight);
            door.setPosition(tileX, tileY);
            door.setTexture(getDoorToBonusWorldTexture());
            door.setZOrder(getTileZOrder(tile, zOrderBase));
            door.setColor(NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white")));
            world.add(door);
        } else if (c.equals("cloud")) {
            Cloud cloud = new Cloud(world, tileWidth, tileHeight);
            cloud.setTexture(getAnimationForTile(map, tile, textureCache));
            cloud.setPosition(tileX, tileY);
            cloud.setZOrder(getTileZOrder(tile, zOrderBase));
            cloud.setColor(NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white")));
            world.add(cloud);
        } else if (c.startsWith("pikes_")) {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            PikesComponent.DangerousSide side = PikesComponent.DangerousSide.valueOf(c.replaceFirst("pikes_", "").toUpperCase());
            if (null != gfx) {
                Pikes pikes = new Pikes(world, side, tileWidth, tileHeight);
                pikes.setTexture(getAnimationForTile(map, tile, textureCache));
                pikes.setPosition(tileX, tileY);
                pikes.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(pikes);
            } else {
                StaticPikes pikes = new StaticPikes(world, side, tileWidth, tileHeight);
                setTextureFromTile(tile, pikes, textureCache);
                pikes.setPosition(tileX, tileY);
                pikes.setZOrder(getTileZOrder(tile, zOrderBase));
                world.addStaticPlatform(pikes);
            }
        } else if (c.equals("cannon_up")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.UP, tileWidth,
                    tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(getFireBallTexture());
            cannon.setExplosionTexture(getExplosionAnimation());
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("cannon_down")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.DOWN,
                    tileWidth, tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(getFireBallTexture());
            cannon.setExplosionTexture(getExplosionAnimation());
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("cannon_right")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.RIGHT,
                    tileWidth, tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(getFireBallTexture());
            cannon.setExplosionTexture(getExplosionAnimation());
            cannon.setPosition(tileX, tileY);
            cannon.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(cannon);
        } else if (c.equals("cannon_left")) {
            Cannon cannon = new Cannon(world, Cannon.Orientation.LEFT,
                    tileWidth, tileHeight);
            cannon.setTexture(getAnimationForTile(map, tile, textureCache));
            cannon.setFireBallTexture(getFireBallTexture());
            cannon.setExplosionTexture(getExplosionAnimation());
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
            pikes.setTexture(getMobilePikesTexture());
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
            axe.setTexture(getAxeTexture());
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
            Activator activator = new Activator(world, 1, getActivator1OnTexture(), getActivator1OffTexture(), tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("activator2")) {
            Activator activator = new Activator(world, 2, getActivator2OnTexture(), getActivator2OffTexture(), tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("activator3")) {
            Activator activator = new Activator(world, 3, getActivator3OnTexture(), getActivator3OffTexture(), tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("memory_activator1")) {
            MemoryActivator activator = new MemoryActivator(world, 1, getActivator1OnTexture(), getActivator1OffTexture(), getMemoryActivatorHiddenTexture(), tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("memory_activator2")) {
            MemoryActivator activator = new MemoryActivator(world, 2, getActivator2OnTexture(), getActivator2OffTexture(), getMemoryActivatorHiddenTexture(), tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("memory_activator3")) {
            MemoryActivator activator = new MemoryActivator(world, 3, getActivator3OnTexture(), getActivator3OffTexture(), getMemoryActivatorHiddenTexture(), tileWidth, tileHeight);
            activator.setPosition(tileX, tileY);
            activator.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activator);
        } else if (c.equals("blocker1")) {
            Blocker activable = new Blocker(world, 1, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, getBlocker1Texture()));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("blocker2")) {
            Blocker activable = new Blocker(world, 2, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, getBlocker2Texture()));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("blocker3")) {
            Blocker activable = new Blocker(world, 3, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, getBlocker3Texture()));
            activable.setPosition(tileX, tileY);
            activable.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(activable);
        } else if (c.equals("laser_blocker")) {
            LaserBlocker activable = new LaserBlocker(world, 1, tileWidth, tileHeight);
            activable.setTexture(getAnimationForTile(map, tile, getBlocker1Texture()));
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
            platform.setColor(NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white")));
            platform.setPosition(tileX, tileY);
            platform.setFriction(getTileFriction(tile));
            platform.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(platform);
        } else if (c.equals("teleporter")) {
            Teleporter teleporter = new Teleporter(world, tileWidth, tileHeight);
            teleporter.setTexture(getAnimationForTile(map, tile, textureCache));
            teleporter.setPosition(tileX, tileY);
            teleporter.setZOrder(getTileZOrder(tile, zOrderBase, 1));
            teleporter.setColor(tile.getProperty(
                    "newton_adventure.teleporter.color", "white"));
            world.add(teleporter);
        } else if (c.equals("colorizer")) {
            Colorizer colorizer = new Colorizer(world, tileWidth, tileHeight);
            colorizer.setTexture(getAnimationForTile(map, tile, textureCache));
            colorizer.setPosition(tileX, tileY);
            colorizer.setZOrder(getTileZOrder(tile, zOrderBase, 1));
            colorizer.setColor(NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white")));
            world.add(colorizer);
        } else if (c.equals("colored_platform")) {
            ColoredPlatform colored = new ColoredPlatform(world, tileWidth, tileHeight);
            colored.setTexture(getAnimationForTile(map, tile, textureCache));
            colored.setPosition(tileX, tileY);
            colored.setZOrder(getTileZOrder(tile, zOrderBase, 1));
            NewtonColor color = NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white"));
            world.addColoredPlatform(color, colored);

        } else if (c.equals("keylock")) {
            KeyLock keylock = new KeyLock(world, tileWidth, tileHeight);
            keylock.setTexture(getAnimationForTile(map, tile, textureCache));
            keylock.setPosition(tileX, tileY);
            keylock.setZOrder(getTileZOrder(tile, zOrderBase));
            keylock.setColor(NewtonColor.valueOf(tile.getProperty(
                    "newton_adventure.color", "white")));
            world.add(keylock);
        } else if (c.equals("help_sign")) {
            HelpSign helpSign = new HelpSign(world, tileWidth, tileHeight);
            helpSign.setTexture(getAnimationForTile(map, tile, textureCache));
            helpSign.setPosition(tileX, tileY);
            helpSign.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(helpSign);

            Clue clue = new Clue(world, tileWidth / 2.0f, tileHeight / 2.0f);
            clue.setTexture(getClueTexture());
            clue.setAnchor(tileX, tileY + tileHeight);
            clue.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(clue);

        } else if (c.equals("bomb")) {
            Bomb bomb = new Bomb(world);
            bomb.setTexture(getBombTexture());
            bomb.setFireBallTexture(getFireBallTexture());
            bomb.setExplosionTexture(getExplosionAnimation());
            bomb.setPosition(tileX, tileY);
            bomb.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(bomb);
        } else if (c.equals("bomb_hole")) {
            BombHole bombHole = new BombHole(world, tileWidth, tileHeight);
            bombHole.setBombTexture(getBombTexture());
            bombHole.setFireBallTexture(getFireBallTexture());
            bombHole.setExplosionTexture(getExplosionAnimation());
            bombHole.setTexture(getAnimationForTile(map, tile, textureCache));
            bombHole.setPosition(tileX, tileY);
            bombHole.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(bombHole);
        } else if (c.equals("crate")) {
            Crate crate = new Crate(world, tileWidth, tileHeight);
            crate.setTexture(getCrateTexture());
            crate.setPosition(tileX, tileY);
            crate.setZOrder(getTileZOrder(tile, zOrderBase));
            world.add(crate);
        } else if (c.equals("boss")) {
            Boss boss = new Boss(world, tileX, tileY);
            boss.setTexture(getAnimationForTile(map, tile, textureCache));
            boss.setExplosionTexture(getExplosionAnimation());
            boss.setZOrder(getTileZOrder(tile, zOrderBase));
            boss.getLeftHand().setZOrder(getTileZOrder(tile, zOrderBase + 1));
            boss.getRightHand().setZOrder(getTileZOrder(tile, zOrderBase + 1));
            world.add(boss);
            world.add(boss.getLeftHand());
            world.add(boss.getRightHand());
        } else {
            String gfx = tile.getProperty("newton_adventure.gfx", null);
            if (null != gfx) {
                AnimationCollection animation = game.getView().loadFromAnimation(
                        game.getData().getLevelFilePath(questName, levelName, gfx));
                AnimatedPlatform platform = new AnimatedPlatform(world, tileWidth, tileHeight);
                platform.setTexture(animation);
                platform.setPosition(tileX, tileY);
                platform.setEnabled(false);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                world.add(platform);
            } else {
                StaticPlatform platform = new StaticPlatform(tileWidth, tileHeight);
                setTextureFromTile(tile, platform, textureCache);
                platform.setPosition(tileX, tileY);
                platform.setZOrder(getTileZOrder(tile, zOrderBase));
                platform.setEnabled(false);
                world.addStaticPlatform(platform);
            }

        }
    }

    private void setTextureFromTile(TmxTileInstance tile, StaticPlatform platform, ITextureCache textureCache) {
        final TmxFrame frame = tile.getFrame();
        final TmxImage image = frame.getImage();
        final float imageWidth = (float) image.getWidth();
        final float imageHeight = (float) image.getHeight();
        platform.setTexture(textureCache.getTexture(image.getSource()));
        platform.setU1((float) frame.getX1() / imageWidth);
        platform.setV1((float) frame.getY1() / imageHeight);
        platform.setU2((float) frame.getX2() / imageWidth);
        platform.setV2((float) frame.getY2() / imageHeight);
    }

    private AnimationCollection getAnimationForTile(TmxMap map,
            TmxTileInstance tile, ITextureCache textureCache)
            throws IOException {
        AnimationCollection animation;
        String gfx = tile.getProperty("newton_adventure.gfx", null);
        if (null != gfx) {
            animation = game.getView().loadFromAnimation(
                    game.getData().getLevelFilePath(questName, levelName, gfx));
        } else {
            final TmxFrame frame = tile.getFrame();
            final TmxImage image = frame.getImage();
            final float imageWidth = (float) image.getWidth();
            final float imageHeight = (float) image.getHeight();
            animation = new AnimationCollection(textureCache.getTexture(image.getSource()), (float) frame.getX1() / imageWidth, (float) frame.getY1() / imageHeight, (float) frame.getX2() / imageWidth, (float) frame.getY2() / imageHeight);
        }
        return animation;
    }

    private AnimationCollection getAnimationForTile(TmxMap map,
            TmxTileInstance tile, AnimationCollection defaultAnimation)
            throws IOException {
        AnimationCollection animation;
        String gfx = tile.getProperty("newton_adventure.gfx", null);
        if (null != gfx) {
            animation = game.getView().loadFromAnimation(
                    game.getData().getLevelFilePath(questName, levelName, gfx));
        } else {
            animation = defaultAnimation;
        }
        return animation;
    }

    private float getTileFriction(TmxTileInstance tile) {
        return Float.parseFloat(tile.getProperty(
                "newton_adventure.friction", "10"));
    }

    private int getTileZOrder(TmxTileInstance tile, int zOrderBase, int defaultZ) {
        int z = zOrderBase;
        String zprop = tile.getProperty("newton_adventure.zorder", null);
        if (null == zprop) {
            z += defaultZ;
        } else {
            z += Integer.parseInt(zprop);
        }
        return z;
    }

    private int getTileZOrder(TmxTileInstance tile, int zOrderBase) {
        return getTileZOrder(tile, zOrderBase, 0);
    }

    private Vector2f[] getMovingPlatformPath(TmxTileInstance tile,
            float x, float y, float baseSize) {
        Vector2f[] dest = new Vector2f[2];
        float ax = Float.parseFloat(tile.getProperty(
                "newton_adventure.moving_platform.a.x", "-1"));
        float ay = Float.parseFloat(tile.getProperty(
                "newton_adventure.moving_platform.a.y", "-1"));
        float bx = Float.parseFloat(tile.getProperty(
                "newton_adventure.moving_platform.b.x", "1"));
        float by = Float.parseFloat(tile.getProperty(
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

    private Vector2f getAcceleratorForce(TmxTileInstance tile) {
        float ax = Float.parseFloat(tile.getProperty("newton_adventure.accelerator.ax", "0.0"));
        float ay = Float.parseFloat(tile.getProperty("newton_adventure.accelerator.ay", "0.0"));
        return new Vector2f(ax, ay);
    }

    public final String getFileFromMap(TmxMap map, String filePropertyName) {
        String filename = getFileFromMapIfAvailable(map, filePropertyName);
        if (filename != null) {
            return filename;
        } else {
            throw new RuntimeException(
                    "error in tmx map file, cannot find property "
                    + filePropertyName);
        }
    }

    public final String getFileFromMapIfAvailable(TmxMap map,
            String filePropertyName) {
        String filename = map.getProperty(filePropertyName, null);
        if (filename == null) {
            filename = defaultMapProperties.get(filePropertyName);
            if (filename == null) {
                return null;
            }
        }
        return game.getData().getLevelFilePath(questName, levelName, filename);
    }
    private static final Map<String,String> defaultMapProperties = new HashMap<String, String>();

    static {
        defaultMapProperties.put("newton_adventure.mummy", "mummy.json");
        defaultMapProperties.put("newton_adventure.bat", "bat.json");
        defaultMapProperties.put("newton_adventure.explosion", "explosion.json");
        defaultMapProperties.put("newton_adventure.fireball", "fireball.png");
        defaultMapProperties.put("newton_adventure.bomb", "bomb.json");
        defaultMapProperties.put("newton_adventure.crate", "crate.json");
        defaultMapProperties.put("newton_adventure.axe", "axe.png");
        defaultMapProperties.put("newton_adventure.mobilePikes",
                "mobile_pikes.png");
        defaultMapProperties.put("newton_adventure.door_to_bonus_world",
                "door_to_bonus_world.json");
        defaultMapProperties.put("newton_adventure.door", "door.json");
        defaultMapProperties.put("newton_adventure.key", "key.json");
        defaultMapProperties.put("newton_adventure.clue", "clue.json");
        defaultMapProperties.put("newton_adventure.hero", "hero.json");
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

    private String getMapProperty(TmxMap map, String prop) {
        String value = map.getProperty(prop, null);
        if (null == value) {
            return defaultMapProperties.get(prop);
        }
        return value;
    }

    private AnimationCollection getExplosionAnimation() throws IOException {
        if (null == explosionAnimation) {
            explosionAnimation = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.explosion"));
        }
        return explosionAnimation;
    }

    private AnimationCollection getMummyAnimation() throws IOException {
        if (null == mummyAnimation) {
            mummyAnimation = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.mummy"));
        }
        return mummyAnimation;
    }

    private AnimationCollection getBatAnimation() throws IOException {
        if (null == batAnimation) {
            batAnimation = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.bat"));
        }
        return batAnimation;
    }

    private AnimationCollection getKeyTexture() throws IOException {
        if (null == keyTexture) {
            keyTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.key"));
        }
        return keyTexture;
    }

    private AnimationCollection getClueTexture() throws IOException {
        if (null == clueTexture) {
            clueTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.clue"));
        }
        return clueTexture;
    }

    private AnimationCollection getDoorTexture() throws IOException {
        if (null == doorTexture) {
            doorTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.door"));
        }
        return doorTexture;
    }

    private AnimationCollection getDoorToBonusWorldTexture() throws IOException {
        if (null == doorToBonusWorldTexture) {
            doorToBonusWorldTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map,
                            "newton_adventure.door_to_bonus_world"));
        }
        return doorToBonusWorldTexture;
    }

    private AnimationCollection getMobilePikesTexture() throws IOException {
        if (null == mobilePikesTexture) {
            mobilePikesTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.mobilePikes"));
        }
        return mobilePikesTexture;
    }

    private AnimationCollection getAxeTexture() throws IOException {

        if (null == axeTexture) {
            axeTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.axe"));
        }
        return axeTexture;
    }

    private AnimationCollection getActivator1OnTexture() throws IOException {
        if (null == activator1OnTexture) {
            activator1OnTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.activator1.on"));
        }
        return activator1OnTexture;
    }

    private AnimationCollection getActivator2OnTexture() throws IOException {
        if (null == activator2OnTexture) {
            activator2OnTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.activator2.on"));
        }
        return activator2OnTexture;
    }

    private AnimationCollection getActivator3OnTexture() throws IOException {
        if (null == activator3OnTexture) {
            activator3OnTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.activator3.on"));
        }
        return activator3OnTexture;
    }

    private AnimationCollection getActivator1OffTexture() throws IOException {
        if (null == activator1OffTexture) {
            activator1OffTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.activator1.off"));
        }
        return activator1OffTexture;
    }

    private AnimationCollection getActivator2OffTexture() throws IOException {
        if (null == activator2OffTexture) {
            activator2OffTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.activator2.off"));
        }
        return activator2OffTexture;
    }

    private AnimationCollection getActivator3OffTexture() throws IOException {
        if (null == activator3OffTexture) {
            activator3OffTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.activator3.off"));
        }
        return activator3OffTexture;
    }

    private AnimationCollection getMemoryActivatorHiddenTexture() throws IOException {
        if (null == memoryActivatorHiddenTexture) {
            memoryActivatorHiddenTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map,
                            "newton_adventure.memory_activator.hidden"));
        }
        return memoryActivatorHiddenTexture;
    }

    private AnimationCollection getBlocker1Texture() throws IOException {
        if (null == blocker1Texture) {
            blocker1Texture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.blocker1"));
        }
        return blocker1Texture;
    }

    private AnimationCollection getBlocker2Texture() throws IOException {
        if (null == blocker2Texture) {
            blocker2Texture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.blocker2"));
        }
        return blocker2Texture;
    }

    private AnimationCollection getBlocker3Texture() throws IOException {
        if (null == blocker3Texture) {
            blocker3Texture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.blocker3"));
        }
        return blocker3Texture;
    }

    private AnimationCollection getAppleIconTexture() throws IOException {
        if (null == appleIconTexture) {
            appleIconTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.apple"));
        }
        return appleIconTexture;
    }

    private AnimationCollection getCoinTexture() throws IOException {
        if (null == coinTexture) {
            coinTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.coin"));
        }
        return coinTexture;

    }

    private AnimationCollection getWorldMapTexture() throws IOException {
        if (null == worldMapTexture) {
            worldMapTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.world_map"));
        }
        return worldMapTexture;
    }

    private AnimationCollection getCompassTexture() throws IOException {
        if (null == compassTexture) {
            compassTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.compass"));

        }
        return compassTexture;
    }

    private AnimationCollection getFireBallTexture() throws IOException {
        if (null == fireBallTexture) {
            fireBallTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.fireball"));
        }
        return fireBallTexture;
    }

    private AnimationCollection getBombTexture() throws IOException {
        if (null == bombTexture) {
            bombTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.bomb"));
        }
        return bombTexture;
    }

    private AnimationCollection getCrateTexture() throws IOException {
        if (null == crateTexture) {
            crateTexture = game.getView().loadFromAnimation(
                    getFileFromMap(map, "newton_adventure.crate"));
        }
        return crateTexture;
    }

    public boolean isReadyToLoad() {
        return map.isReady();
    }

    private Long getMapDeadClock(TmxMap map) {
        String deadclock = map.getProperty("newton_adventure.deadclock", null);
        if (null != deadclock) {
            return Long.valueOf(deadclock) * 1000000000L;
        } else {
            return null;
        }
    }
}
