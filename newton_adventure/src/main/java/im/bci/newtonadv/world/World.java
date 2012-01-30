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
import im.bci.newtonadv.game.Sequence.TransitionException;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.game.Entity;
import im.bci.newtonadv.game.EntityList;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.score.LevelScore;
import im.bci.newtonadv.util.AbsoluteAABox;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import tiled.core.Tile;
import tiled.io.TMXMapReader;

/**
 *
 * @author devnewton
 */
public strictfp class World extends net.phys2d.raw.World {

    static final int STATIC_BODY_COLLIDE_BIT = 1;
    boolean mustDrawContacts = false;
    boolean mustDrawNormals = false;
    boolean mustDrawJoints = false;
    static public final float distanceUnit = 1.0f;
    Hero hero = new Hero(this);
    private float gravityAngle = 0.0f;
    private boolean nonProgressiveGravityRotationActive = false;
    private float gravityAngleTarget;
    private static final float gravityForce = 2f;
    private Vector2f gravityVector = new Vector2f();
    private Game game;
    private ITexture backgroundTexture;
    private List<Updatable> updatableBodies = new LinkedList<Updatable>();
    protected EntityList topLevelEntities = new EntityList();
    private ITexture appleIconTexture;
    private ITexture coinTexture;
    private ITexture fireBallTexture;
    private boolean objectivesCompleted = false;
    private float nonProgressiveGravityRotationStep;
    private Animation explosionAnimation;
    private Animation mummyAnimation;
    private Animation batAnimation;
    private ITexture keyTexture;
    private ITexture openDoorTexture;
    private ITexture closedDoorTexture;
    private ITexture openDoorToBonusWorldTexture;
    private ITexture closedDoorToBonusWorldTexture;
    private ITexture mobilePikesTexture;
    private ITexture axeTexture;
    private ITexture activator1OnTexture;
    private ITexture activator2OnTexture;
    private ITexture activator3OnTexture;
    private ITexture activator1OffTexture;
    private ITexture activator2OffTexture;
    private ITexture activator3OffTexture;
    private ITexture blocker1Texture;
    private ITexture blocker2Texture;
    private ITexture blocker3Texture;
    private final String questName;
    private final String levelName;
    private int nbCollectableApple;
    private boolean gotoBonusWorld;

    public boolean areObjectivesCompleted() {
        return objectivesCompleted;
    }

    public void setObjectivesCompleted(boolean objectivesCompleted) {
        this.objectivesCompleted = objectivesCompleted;
    }

    public ITexture getAppleIconTexture() {
        return appleIconTexture;
    }

    public ITexture getFireBallTexture() {
        return fireBallTexture;
    }

    Animation getMummyAnimation() {
        return mummyAnimation;
    }

    Animation getBatAnimation() {
        return batAnimation;
    }

    Animation getExplosionAnimation() {
        return explosionAnimation;
    }

    public Hero getHero() {
        return hero;
    }

    public World(Game game, String questName, String levelName) {
        super(new Vector2f(0.0f, -gravityForce), 2, new StaticQuadSpaceStrategy(20, 5));
        this.game = game;
        progressiveRotateGravity(0.0f);
        this.questName = questName;
        this.levelName = levelName;
    }

    public AbsoluteAABox getStaticBounds() {
        if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
            return ((StaticQuadSpaceStrategy) collisionStrategy).getStaticBounds();
        } else {
            return null;//todo
        }
    }

    public BodyList getVisibleBodies(float camera_x1, float camera_y1, float camera_x2, float camera_y2) {
        if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
            return ((StaticQuadSpaceStrategy) collisionStrategy).findVisibleBodies(camera_x1, camera_y1, camera_x2, camera_y2);
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
            updatableBodies.remove((Updatable) body);
        }
        super.remove(body);
    }

    @Override
    public void step() {

        if (nonProgressiveGravityRotationActive) {
            if (Math.abs(gravityAngle - gravityAngleTarget) < Math.abs(nonProgressiveGravityRotationStep)) {
                gravityAngle = gravityAngleTarget;
                nonProgressiveGravityRotationStep = 0.0f;
                nonProgressiveGravityRotationActive = false;
            }

            progressiveRotateGravity(nonProgressiveGravityRotationStep);
        } else {
            hero.step();
            super.step();
        }
    }
    private static final Properties defaultMapProperties = new Properties();

    static {
        defaultMapProperties.put("newton_adventure.mummy", "mummy.gif");
        defaultMapProperties.put("newton_adventure.bat", "bat.gif");
        defaultMapProperties.put("newton_adventure.explosion", "explosion.gif");
        defaultMapProperties.put("newton_adventure.fireball", "fireball.png");
        defaultMapProperties.put("newton_adventure.axe", "axe.png");
        defaultMapProperties.put("newton_adventure.mobilePikes", "mobile_pikes.png");
        defaultMapProperties.put("newton_adventure.door_to_bonus_world", "door_to_bonus_world.png");
        defaultMapProperties.put("newton_adventure.door_to_bonus_world_open", "door_to_bonus_world_open.png");
        defaultMapProperties.put("newton_adventure.door", "door.png");
        defaultMapProperties.put("newton_adventure.door_open", "door_open.png");
        defaultMapProperties.put("newton_adventure.key", "key.png");
        defaultMapProperties.put("newton_adventure.hero", "hero.gif");
        defaultMapProperties.put("newton_adventure.apple", "apple.png");
        defaultMapProperties.put("newton_adventure.coin", "coin.png");
        defaultMapProperties.put("newton_adventure.activator1.on", "actived1.png");
        defaultMapProperties.put("newton_adventure.activator2.on", "actived2.png");
        defaultMapProperties.put("newton_adventure.activator3.on", "actived3.png");
        defaultMapProperties.put("newton_adventure.activator1.off", "activable1.png");
        defaultMapProperties.put("newton_adventure.activator2.off", "activable2.png");
        defaultMapProperties.put("newton_adventure.activator3.off", "activable3.png");
        defaultMapProperties.put("newton_adventure.blocker1", "blocker1.png");
        defaultMapProperties.put("newton_adventure.blocker2", "blocker2.png");
        defaultMapProperties.put("newton_adventure.blocker3", "blocker3.png");
        defaultMapProperties.put("newton_adventure.music", "hopnbop.ogg");
    }

    public String getFileFromMap(tiled.core.Map map, String filePropertyName) {
        String filename = map.getProperties().getProperty(filePropertyName);
        if (filename == null) {
            filename = defaultMapProperties.getProperty(filePropertyName);
            if (filename == null) {
                throw new RuntimeException("error in tmx map file, cannot find property " + filePropertyName);
            }
        }
        return game.getData().getLevelFile(questName, levelName, filename);
    }

    public void loadLevel() throws IOException, Exception {

        TMXMapReader mapReader = new TMXMapReader();
        tiled.core.Map map;
        InputStream mapInputStream = game.getData().openLevelTmx(questName, levelName);
        try {
            map = mapReader.readMap(mapInputStream);
        } finally {
            mapInputStream.close();
        }


        final ITextureCache textureCache = game.getView().getTextureCache();
        explosionAnimation = game.getView().loadFromGif(getFileFromMap(map, "newton_adventure.explosion"));
        mummyAnimation = game.getView().loadFromGif(getFileFromMap(map, "newton_adventure.mummy"));
        batAnimation = game.getView().loadFromGif(getFileFromMap(map, "newton_adventure.bat"));
        appleIconTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.apple"));
        coinTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.coin"));
        fireBallTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.fireball"));
        keyTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.key"));
        closedDoorTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.door"));
        openDoorTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.door_open"));
        closedDoorToBonusWorldTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.door_to_bonus_world"));
        openDoorToBonusWorldTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.door_to_bonus_world_open"));
        mobilePikesTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.mobilePikes"));
        axeTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.axe"));
        activator1OnTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.activator1.on"));
        activator2OnTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.activator2.on"));
        activator3OnTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.activator3.on"));
        activator1OffTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.activator1.off"));
        activator2OffTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.activator2.off"));
        activator3OffTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.activator3.off"));
        blocker1Texture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.blocker1"));
        blocker2Texture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.blocker2"));
        blocker3Texture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.blocker3"));

        for (tiled.core.MapLayer layer : map.getLayers()) {
            if (layer instanceof tiled.core.TileLayer) {
                tiled.core.TileLayer tileLayer = (tiled.core.TileLayer) layer;
                for (int x = 0; x < tileLayer.getWidth(); ++x) {
                    for (int y = 0; y < tileLayer.getHeight(); ++y) {
                        Tile tile = tileLayer.getTileAt(x, y);
                        if (null != tile) {
                            initFromTile(x - map.getWidth() / 2.0f, -y + map.getHeight() / 2.0f, map, tile);
                        }
                    }
                }
            }
        }
        backgroundTexture = textureCache.getTexture(getFileFromMap(map, "newton_adventure.background"));
        this.getHero().setAnimation(game.getView().loadFromGif(getFileFromMap(map, "newton_adventure.hero")));
        this.getHero().setJumpSound(game.getSoundCache().getSoundIfEnabled(game.getData().getFile("jump.wav")));
        game.getSoundCache().playMusicIfEnabled(getFileFromMap(map, "newton_adventure.music"));

    }

    public float getGravityAngle() {
        return gravityAngle;
    }

    public final void progressiveRotateGravity(float angle) {
        gravityAngle += angle;
        Matrix2f rot = new Matrix2f(gravityAngle);
        this.gravityVector = net.phys2d.math.MathUtil.mul(rot, new Vector2f(0, -gravityForce));
        setGravity(getGravityVector().x, getGravityVector().y);
    }

    public void rotateGravity(float angle) {
        if (!nonProgressiveGravityRotationActive) {
            this.gravityAngleTarget = this.gravityAngle + angle;
            this.nonProgressiveGravityRotationStep = 2.0f * angle / Game.FPSf;
            this.nonProgressiveGravityRotationActive = true;
        }
    }

    float getGravityForce() {
        return gravityForce;
    }

    private void initFromTile(float x, float y, tiled.core.Map map, tiled.core.Tile tile) throws IOException {
        ITextureCache textureCache = game.getView().getTextureCache();
        String c = tile.getProperties().getProperty("newton_adventure.type", "unknown");
        if (c.equals("platform")) {
            Platform platform = new Platform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setFriction(getTileFriction(tile));
            add(platform);
        } else if (c.equals("up_right_half_platform")) {
            UpRightHalfPlatform platform = new UpRightHalfPlatform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setFriction(getTileFriction(tile));
            add(platform);
        } else if (c.equals("up_left_half_platform")) {
            UpLeftHalfPlatform platform = new UpLeftHalfPlatform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setFriction(getTileFriction(tile));
            add(platform);
        } else if (c.equals("down_left_half_platform")) {
            DownLeftHalfPlatform platform = new DownLeftHalfPlatform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setFriction(getTileFriction(tile));
            add(platform);
        } else if (c.equals("down_right_half_platform")) {
            DownRightHalfPlatform platform = new DownRightHalfPlatform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setFriction(getTileFriction(tile));
            add(platform);
        } else if (c.equals("hero")) {
            hero.setPosition(x * Platform.size, y * Platform.size);
            add(hero);
        } else if (c.equals("mummy")) {
            Mummy mummy = new Mummy(this);
            mummy.setPosition(x * Platform.size, y * Platform.size);
            add(mummy);
        } else if (c.equals("bat")) {
            Bat bat = new Bat(this);
            bat.setPosition(x * Platform.size, y * Platform.size);
            add(bat);
        } else if (c.equals("apple")) {
            Apple apple = new Apple(this);
            ++nbCollectableApple;
            apple.setPosition(x * Platform.size, y * Platform.size);
            apple.setTexture(appleIconTexture);
            add(apple);
        } else if (c.equals("coin")) {
            Coin apple = new Coin(this);
            apple.setPosition(x * Platform.size, y * Platform.size);
            apple.setTexture(coinTexture);
            add(apple);
        } else if (c.equals("key")) {
            Key key = new Key(this);
            key.setPosition(x * Platform.size, y * Platform.size);
            key.setTexture(keyTexture);
            add(key);
        } else if (c.equals("door")) {
            Door door = new Door(this);
            door.setPosition(x * Platform.size/* + Door.width / 2.0f*/, y * Platform.size + Door.height / 2.0f - Platform.size / 2.0f);
            door.setClosedTexture(closedDoorTexture);
            door.setOpenTexture(openDoorTexture);
            add(door);
        } else if (c.equals("door_to_bonus_world")) {
            DoorToBonusWorld door = new DoorToBonusWorld(this);
            door.setPosition(x * Platform.size/* + Door.width / 2.0f*/, y * Platform.size + Door.height / 2.0f - Platform.size / 2.0f);
            door.setClosedTexture(closedDoorToBonusWorldTexture);
            door.setOpenTexture(openDoorToBonusWorldTexture);
            add(door);
        } else if (c.equals("cloud")) {
            Cloud cloud = new Cloud(this);
            cloud.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            cloud.setPosition(x * Platform.size, y * Platform.size);
            add(cloud);
        } else if (c.equals("pikes_up")) {
            Pikes pikes = new Pikes(this, Pikes.DangerousSide.UP);
            pikes.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            pikes.setPosition(x * Platform.size, y * Platform.size);
            add(pikes);
        } else if (c.equals("pikes_down")) {
            Pikes pikes = new Pikes(this, Pikes.DangerousSide.DOWN);
            pikes.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            pikes.setPosition(x * Platform.size, y * Platform.size);
            add(pikes);
        } else if (c.equals("pikes_left")) {
            Pikes pikes = new Pikes(this, Pikes.DangerousSide.LEFT);
            pikes.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            pikes.setPosition(x * Platform.size, y * Platform.size);
            add(pikes);
        } else if (c.equals("pikes_right")) {
            Pikes pikes = new Pikes(this, Pikes.DangerousSide.RIGHT);
            pikes.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            pikes.setPosition(x * Platform.size, y * Platform.size);
            add(pikes);
        } else if (c.equals("cannon_up")) {
            Cannon cannon = new Cannon(this, Cannon.Orientation.UP);
            cannon.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            cannon.setPosition(x * Platform.size, y * Platform.size);
            add(cannon);
        } else if (c.equals("cannon_down")) {
            Cannon cannon = new Cannon(this, Cannon.Orientation.DOWN);
            cannon.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            cannon.setPosition(x * Platform.size, y * Platform.size);
            add(cannon);
        } else if (c.equals("cannon_right")) {
            Cannon cannon = new Cannon(this, Cannon.Orientation.RIGHT);
            cannon.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            cannon.setPosition(x * Platform.size, y * Platform.size);
            add(cannon);
        } else if (c.equals("cannon_left")) {
            Cannon cannon = new Cannon(this, Cannon.Orientation.LEFT);
            cannon.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            cannon.setPosition(x * Platform.size, y * Platform.size);
            add(cannon);
        } else if (c.equals("mobile_pike_anchor")) {
            MobilePikeAnchor anchor = new MobilePikeAnchor(this);
            anchor.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            anchor.setPosition(x * Platform.size, y * Platform.size);
            add(anchor);

            MobilePikes pikes = new MobilePikes(this);
            pikes.setTexture(mobilePikesTexture);
            pikes.setPosition(anchor.getPosition().getX(), anchor.getPosition().getY() - MobilePikes.height / 2.0f - MobilePikeAnchor.radius);
            add(pikes);

            BasicJoint j = new BasicJoint(anchor, pikes, new Vector2f(anchor.getPosition()));
            j.setRelaxation(0);
            add(j);
        } else if (c.equals("axe_anchor")) {
            AxeAnchor anchor = new AxeAnchor(this);
            anchor.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            anchor.setPosition(x * Platform.size, y * Platform.size);
            add(anchor);

            Axe axe = new Axe(this);
            axe.setTexture(axeTexture);
            axe.setPosition(anchor.getPosition().getX(), anchor.getPosition().getY() - MobilePikes.height / 2.0f - MobilePikeAnchor.radius);
            add(axe);

            BasicJoint j = new BasicJoint(anchor, axe, new Vector2f(anchor.getPosition()));
            j.setRelaxation(0);
            add(j);
        } else if (c.equals("bounce_platform")) {
            BouncePlatform platform = new BouncePlatform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            add(platform);
        } else if (c.equals("activator1")) {
            Activator activator = new Activator(this, 1, activator1OnTexture, activator1OffTexture);
            activator.setPosition(x * Platform.size, y * Platform.size);
            add(activator);
        } else if (c.equals("activator2")) {
            Activator activator = new Activator(this, 2, activator2OnTexture, activator2OffTexture);
            activator.setPosition(x * Platform.size, y * Platform.size);
            add(activator);
        } else if (c.equals("activator3")) {
            Activator activator = new Activator(this, 3, activator3OnTexture, activator3OffTexture);
            activator.setPosition(x * Platform.size, y * Platform.size);
            add(activator);
        } else if (c.equals("blocker1")) {
            Blocker activable = new Blocker(this, 1);
            activable.setTexture(blocker1Texture);
            activable.setPosition(x * Platform.size, y * Platform.size);
            add(activable);
        } else if (c.equals("blocker2")) {
            Blocker activable = new Blocker(this, 2);
            activable.setTexture(blocker2Texture);
            activable.setPosition(x * Platform.size, y * Platform.size);
            add(activable);
        } else if (c.equals("blocker3")) {
            Blocker activable = new Blocker(this, 3);
            activable.setTexture(blocker3Texture);
            activable.setPosition(x * Platform.size, y * Platform.size);
            add(activable);
        } else if (c.equals("moving_platform")) {
            MovingPlatform platform = new MovingPlatform(this, textureCache.getTexture(questName, levelName, map, tile), getMovingPlatformDestination(tile, x, y));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setFriction(getTileFriction(tile));
            add(platform);
        } else if (c.equals("egyptian_boss")) {
            EgyptianBoss boss = new EgyptianBoss(this, x * Platform.size, y * Platform.size);
            boss.setBodyTexture(textureCache.getTexture(game.getData().getFile("egyptian_boss_body.png")));
            boss.setHandTexture(textureCache.getTexture(game.getData().getFile("egyptian_boss_hand.png")));
            add(boss);
            add(boss.getLeftHand());
            add(boss.getRightHand());
        } else {
            Platform platform = new Platform(this);
            platform.setTexture(textureCache.getTexture(questName, levelName, map, tile));
            platform.setPosition(x * Platform.size, y * Platform.size);
            platform.setEnabled(false);
            add(platform);
        }
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

    public void update() throws GameOverException, TransitionException {
        if (gotoBonusWorld) {
            game.goToRandomBonusLevel(questName);
        }
        FrameTimeInfos frameTimeInfos = game.getFrameTimeInfos();
        for (Updatable u : new ArrayList<Updatable>(updatableBodies)) {//copy to allow updatable body to be removed from list
            u.update(frameTimeInfos);
        }
        topLevelEntities.update(frameTimeInfos);
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
        return Float.parseFloat(tile.getProperties().getProperty("newton_adventure.friction", "10"));
    }

    private MovingPlatform.Destinations getMovingPlatformDestination(Tile tile, float x, float y) {
        MovingPlatform.Destinations dest = new MovingPlatform.Destinations();
        /*        dest.xMin = Platform.size * (x + Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.xmin", "-1")));
        dest.xMax = Platform.size * (x + Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.xmax", "1")));
        dest.yMin = Platform.size * (y + Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.ymin", "-1")));
        dest.yMax = Platform.size * (y + Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.ymax", "1")));*/
        dest.xMin = Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.xmin", "-1"));
        dest.xMax = Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.xmax", "1"));
        dest.yMin = Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.ymin", "-1"));
        dest.yMax = Float.parseFloat(tile.getProperties().getProperty("newton_adventure.moving_platform.ymax", "1"));

        dest.xMin += x;
        dest.xMax += x;
        dest.yMin += y;
        dest.yMax += y;

        dest.xMin *= Platform.size;
        dest.xMax *= Platform.size;
        dest.yMin *= Platform.size;
        dest.yMax *= Platform.size;

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
        gotoBonusWorld = true;
        objectivesCompleted = true;
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
}
