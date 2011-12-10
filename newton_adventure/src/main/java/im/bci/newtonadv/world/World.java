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
import im.bci.newtonadv.Texture;
import im.bci.newtonadv.TextureCache;
import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationLoaders;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.Entity;
import im.bci.newtonadv.game.EntityList;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;
import im.bci.newtonadv.util.AbsoluteAABox;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import net.phys2d.math.Matrix2f;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.lwjgl.util.glu.GLU;

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
    private Texture backgroundTexture;
    private List<Updatable> updatableBodies = new LinkedList();
    protected EntityList topLevelEntities = new EntityList();
    private Texture appleIconTexture;
    private Texture fireBallTexture;
    private boolean objectivesCompleted = false;
    private float nonProgressiveGravityRotationStep;
    private Animation explosionAnimation;
    private Animation mummyAnimation;
    private Animation batAnimation;

    public boolean areObjectivesCompleted() {
        return objectivesCompleted;
    }

    public void setObjectivesCompleted(boolean objectivesCompleted) {
        this.objectivesCompleted = objectivesCompleted;
    }

    public Texture getAppleIconTexture() {
        return appleIconTexture;
    }

    public Texture getFireBallTexture() {
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

    public World(Game game) {
        super(new Vector2f(0.0f, -gravityForce), 2, new StaticQuadSpaceStrategy(20, 5));
        this.game = game;
        progressiveRotateGravity(0.0f);
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

    public void loadLevel(String levelPath) throws IOException {
        World.LevelInfo levelInfo = readLevelInfo(levelPath);
        initLevelFromTextMap(levelInfo);
        backgroundTexture = game.getView().getTextureCache().getTexture(levelInfo.getBackgroundTexture());
        this.getHero().setAnimation(AnimationLoaders.loadFromGif(game.getView().getTextureCache(), levelInfo.getHeroAnimation()));
        this.getHero().setJumpSound(game.getSoundCache().getSoundIfEnabled("data/jump.wav"));
        if (levelInfo.getMusicFile() != null) {
            game.getSoundCache().playMusicIfEnabled(levelInfo.getMusicFile());
        }
    }

    public float getGravityAngle() {
        return gravityAngle;
    }

    public void progressiveRotateGravity(float angle) {
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

    private void initLevelFromTextMap(LevelInfo levelInfo) throws IOException {
        final TextureCache textureCache = game.getView().getTextureCache();
        explosionAnimation = AnimationLoaders.loadFromGif(textureCache, levelInfo.getExplosionAnimation());
        mummyAnimation = AnimationLoaders.loadFromGif(textureCache, levelInfo.getMummyAnimation());
        batAnimation = AnimationLoaders.loadFromGif(textureCache, levelInfo.getBatAnimation());
        appleIconTexture = textureCache.getTexture(levelInfo.getAppleTexture());
        fireBallTexture = textureCache.getTexture(levelInfo.getFireBallTexture());
        LevelTextMap levelTextMap = readLevelMap(levelInfo.getLevelMap());
        for (Map.Entry<Point, Character> entry : levelTextMap.map.entrySet()) {
            char c = entry.getValue();
            Point pos = entry.getKey();
            float x = pos.getX() - levelTextMap.width / 2.0f;
            float y = -pos.getY() + levelTextMap.height / 2.0f;
            if (c == 'H') {
                hero.setPosition(x * Platform.size, y * Platform.size);
                add(hero);
            } else if (c == 'm') {
                Mummy mummy = new Mummy(this);
                mummy.setPosition(x * Platform.size, y * Platform.size);
                add(mummy);
            } else if (c == 'c') {
                Bat bat = new Bat(this);
                bat.setPosition(x * Platform.size, y * Platform.size);
                add(bat);
            } else if (c == 'A') {
                Apple apple = new Apple(this);
                apple.setPosition(x * Platform.size, y * Platform.size);
                apple.setTexture(appleIconTexture);
                add(apple);
            } else if (c == 'K') {
                Key key = new Key(this);
                key.setPosition(x * Platform.size, y * Platform.size);
                key.setTexture(textureCache.getTexture(levelInfo.getKeyTexture()));
                add(key);
            } else if (c == 'D') {
                Door door = new Door(this);
                door.setPosition(x * Platform.size/* + Door.width / 2.0f*/, y * Platform.size + Door.height / 2.0f - Platform.size / 2.0f);
                door.setClosedTexture(textureCache.getTexture(levelInfo.getClosedDoorTexture()));
                door.setOpenTexture(textureCache.getTexture(levelInfo.getOpenDoorTexture()));
                add(door);
            } else if (c == 'C') {
                Cloud cloud = new Cloud(this);
                cloud.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                cloud.setPosition(x * Platform.size, y * Platform.size);
                add(cloud);
            } else if (c == 'W') {
                Pikes pikes = new Pikes(this, Pikes.DangerousSide.UP);
                pikes.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                pikes.setPosition(x * Platform.size, y * Platform.size);
                add(pikes);
            } else if (c == 'M') {
                Pikes pikes = new Pikes(this, Pikes.DangerousSide.DOWN);
                pikes.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                pikes.setPosition(x * Platform.size, y * Platform.size);
                add(pikes);
            } else if (c == 'Q') {
                Pikes pikes = new Pikes(this, Pikes.DangerousSide.LEFT);
                pikes.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                pikes.setPosition(x * Platform.size, y * Platform.size);
                add(pikes);
            } else if (c == 'P') {
                Pikes pikes = new Pikes(this, Pikes.DangerousSide.RIGHT);
                pikes.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                pikes.setPosition(x * Platform.size, y * Platform.size);
                add(pikes);
            } else if (c == 'B') {
                Cannon cannon = new Cannon(this, Cannon.Orientation.UP);
                cannon.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                cannon.setPosition(x * Platform.size, y * Platform.size);
                add(cannon);
            } else if (c == 'b') {
                Cannon cannon = new Cannon(this, Cannon.Orientation.DOWN);
                cannon.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                cannon.setPosition(x * Platform.size, y * Platform.size);
                add(cannon);
            } else if (c == 'L') {
                Cannon cannon = new Cannon(this, Cannon.Orientation.RIGHT);
                cannon.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                cannon.setPosition(x * Platform.size, y * Platform.size);
                add(cannon);
            } else if (c == 'J') {
                Cannon cannon = new Cannon(this, Cannon.Orientation.LEFT);
                cannon.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                cannon.setPosition(x * Platform.size, y * Platform.size);
                add(cannon);
            } else if (c == 'o') {
                MobilePikeAnchor anchor = new MobilePikeAnchor();
                anchor.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                anchor.setPosition(x * Platform.size, y * Platform.size);
                add(anchor);

                MobilePikes pikes = new MobilePikes(this);
                pikes.setTexture(textureCache.getTexture(levelInfo.getMobilePikesTexture()));
                pikes.setPosition(anchor.getPosition().getX(), anchor.getPosition().getY() - MobilePikes.height / 2.0f - MobilePikeAnchor.radius);
                add(pikes);

                BasicJoint j = new BasicJoint(anchor, pikes, new Vector2f(anchor.getPosition()));
                j.setRelaxation(0);
                add(j);
            } else if (c == 'x') {
                AxeAnchor anchor = new AxeAnchor();
                anchor.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                anchor.setPosition(x * Platform.size, y * Platform.size);
                add(anchor);

                Axe axe = new Axe(this);
                axe.setTexture(textureCache.getTexture(levelInfo.getAxeTexture()));
                axe.setPosition(anchor.getPosition().getX(), anchor.getPosition().getY() - MobilePikes.height / 2.0f - MobilePikeAnchor.radius);
                add(axe);

                BasicJoint j = new BasicJoint(anchor, axe, new Vector2f(anchor.getPosition()));
                j.setRelaxation(0);
                add(j);
            } else if (c == '(') {
                UpRightHalfPlatform platform = new UpRightHalfPlatform();
                platform.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                platform.setPosition(x * Platform.size, y * Platform.size);
                platform.setFriction(levelInfo.getPlatformFriction(c));
                add(platform);
            } else if (c == ')') {
                UpLeftHalfPlatform platform = new UpLeftHalfPlatform();
                platform.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                platform.setPosition(x * Platform.size, y * Platform.size);
                platform.setFriction(levelInfo.getPlatformFriction(c));
                add(platform);
            } else if (c == '\\') {
                DownLeftHalfPlatform platform = new DownLeftHalfPlatform();
                platform.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                platform.setPosition(x * Platform.size, y * Platform.size);
                platform.setFriction(levelInfo.getPlatformFriction(c));
                add(platform);
            } else if (c == '/') {
                DownRightHalfPlatform platform = new DownRightHalfPlatform();
                platform.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                platform.setPosition(x * Platform.size, y * Platform.size);
                platform.setFriction(levelInfo.getPlatformFriction(c));
                add(platform);
            } else if (c == 'S') {
                EgyptianBoss boss = new EgyptianBoss(this, x * Platform.size, y * Platform.size);
                boss.setBodyTexture(textureCache.getTexture("data/egyptian_boss_body.png"));
                boss.setHandTexture(textureCache.getTexture("data/egyptian_boss_hand.png"));
            } else if (c == 'R') {
                BouncePlatform platform = new BouncePlatform(this);
                platform.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                platform.setPosition(x * Platform.size, y * Platform.size);
                add(platform);
            } else {
                Platform platform = new Platform();
                platform.setTexture(textureCache.getTexture(levelInfo.getTextureByChar("" + c)));
                platform.setPosition(x * Platform.size, y * Platform.size);
                platform.setFriction(levelInfo.getPlatformFriction(c));
                add(platform);
            }
        }
    }

    public Vector2f getGravityVector() {
        return gravityVector;
    }

    private class LevelInfo {

        private String mummyAnimation;
        private String batAnimation;
        private String explosionAnimation;
        private String fireBallTexture;
        private String axeTexture;
        private String mobilePikesTexture;
        private String doorOpenTexture;
        private String doorClosedTexture;
        private File levelDir;
        private Map<String, String> textureByChar = new HashMap();
        private Map<String, Float> frictionByChar = new HashMap();
        private Properties properties = new Properties();
        private String heroAnimation;
        private String appleTexture;
        private String keyTexture;
        private String musicFile = null;

        public String getMusicFile() {
            return musicFile;
        }

        String getTextureByChar(String c) {
            return textureByChar.get(c);
        }

        String getMummyAnimation() {
            return mummyAnimation;
        }

        String getBatAnimation() {
            return batAnimation;
        }

        String getExplosionAnimation() {
            return explosionAnimation;
        }

        String getFireBallTexture() {
            return fireBallTexture;
        }

        String getMobilePikesTexture() {
            return mobilePikesTexture;
        }

        String getAxeTexture() {
            return axeTexture;
        }

        String getClosedDoorTexture() {
            return doorClosedTexture;
        }

        String getOpenDoorTexture() {
            return doorOpenTexture;
        }

        String getKeyTexture() {
            return keyTexture;
        }

        String getHeroAnimation() {
            return heroAnimation;
        }

        String getAppleTexture() {
            return appleTexture;
        }

        String getBackgroundTexture() {
            return buildTextureName(levelDir.getAbsolutePath() + File.separator, "newton_adventure.background");
        }

        String getLevelMap() {
            return levelDir.getAbsolutePath() + File.separator + properties.getProperty("newton_adventure.map");
        }

        private String buildTextureName(String levelPathBase, String propertyName) {
            String imageFilename = properties.getProperty(propertyName);
            String path = levelPathBase + imageFilename;
            if ((new File(path)).exists()) {
                return path;
            } else {
                return "data" + File.separator + "default_level_data" + File.separator + imageFilename;
            }
        }

        private void buildTextureNames() {
            final String pathBase = levelDir.getAbsolutePath() + File.separator;
            mummyAnimation = buildTextureName(pathBase, "newton_adventure.mummy");
            batAnimation = buildTextureName(pathBase, "newton_adventure.bat");
            explosionAnimation = buildTextureName(pathBase, "newton_adventure.explosion");
            fireBallTexture = buildTextureName(pathBase, "newton_adventure.fireball");
            axeTexture = buildTextureName(pathBase, "newton_adventure.axe");
            mobilePikesTexture = buildTextureName(pathBase, "newton_adventure.mobilePikes");
            doorClosedTexture = buildTextureName(pathBase, "newton_adventure.door");
            doorOpenTexture = buildTextureName(pathBase, "newton_adventure.door_open");
            keyTexture = buildTextureName(pathBase, "newton_adventure.key");
            heroAnimation = buildTextureName(pathBase, "newton_adventure.hero");
            appleTexture = buildTextureName(pathBase, "newton_adventure.apple");
            if (properties.getProperty("newton_adventure.music") != null) {
                musicFile = buildTextureName(pathBase, "newton_adventure.music");
            } else {
                musicFile = "data/hopnbop.mid";
            }

            for (Entry entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                if (key.endsWith(".char")) {
                    String c = properties.getProperty(key);
                    String textureFilename = properties.getProperty(key.replace(".char", ".texture.filename"));
                    textureByChar.put(c, pathBase + textureFilename);
                }
            }
        }

        private void buildFrictionByChar() {
            for (Entry entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                if (key.endsWith(".char")) {
                    String c = properties.getProperty(key);
                    String friction = properties.getProperty(key.replace(".char", ".friction"));
                    if(null != friction) {
                        frictionByChar.put(c, Float.parseFloat(friction));
                    }

                }
            }
        }

        private float getPlatformFriction(String key) {
            Float f = frictionByChar.get(key);
            if( f==null)
                return 10.0f;
            else
                return f;
        }

        private float getPlatformFriction(char key) {
            return getPlatformFriction( "" + key );
        }
    }

    private LevelInfo readLevelInfo(String levelPath) throws FileNotFoundException, IOException {

        File levelDir = new File(levelPath);
        File[] propertyFiles = levelDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".properties");
            }
        });
        if (propertyFiles.length == 0) {
            throw new FileNotFoundException("cannot find *.properties file in level path: " + levelPath);
        }

        LevelInfo level = new LevelInfo();

        FileInputStream levelFileStream = new FileInputStream(propertyFiles[0]);
        try {
            level.properties.load(levelFileStream);
        } finally {
            levelFileStream.close();
        }
        level.levelDir = levelDir;
        level.buildTextureNames();
        level.buildFrictionByChar();
        return level;
    }

    class LevelTextMap {

        Map<Point, Character> map = new HashMap();
        int width = 0, height = 0;

        private void put(Point point, char c) {
            map.put(point, c);
            width = Math.max(width, point.getX());
            height = Math.max(height, point.getY());
        }
    }

    private LevelTextMap readLevelMap(String levelMap) throws IOException {
        FileInputStream inputStream = new FileInputStream(levelMap);
        LevelTextMap levelTextMap = new LevelTextMap();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = null;
            int y = 0;
            while ((line = in.readLine()) != null) {
                for (int x = 0; x < line.length(); ++x) {
                    char c = line.charAt(x);
                    if (c != ' ') {
                        levelTextMap.put(new Point(x, y), c);
                    }
                }
                ++y;
            }
        } finally {
            in.close();
        }
        return levelTextMap;
    }
    static final float ortho2DBaseSize = World.distanceUnit * 20.0f;
    static final float ortho2DLeft = -ortho2DBaseSize;
    static final float ortho2DBottom = -ortho2DBaseSize;
    static final float ortho2DRight = ortho2DBaseSize;
    static final float ortho2DTop = ortho2DBaseSize;
    float aspectRatio = 1.0f;

    public void draw() {
        GL11.glPushMatrix();

        aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        GLU.gluOrtho2D(ortho2DLeft * aspectRatio, ortho2DRight * aspectRatio, ortho2DBottom, ortho2DTop);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glRotatef((float) Math.toDegrees(-getGravityAngle()), 0, 0, 1.0f);
        drawBackground();

        ROVector2f heroPos = hero.getPosition();
        GL11.glTranslatef(-heroPos.getX(), -heroPos.getY(), 0.0f);

        final float cameraSize = ortho2DBaseSize * 1.5f;
        final BodyList visibleBodies = getVisibleBodies(heroPos.getX() - cameraSize, heroPos.getY() - cameraSize, heroPos.getX() + cameraSize, heroPos.getY() + cameraSize);
        //  final BodyList bodies = world.getBodies();

        for (int i = 0; i < visibleBodies.size(); i++) {
            Body body = visibleBodies.get(i);
            if (body instanceof Drawable) {
                ((Drawable) body).draw();
            }
        }

        topLevelEntities.draw();
        GL11.glPopMatrix();

    }

    public void update() throws GameOverException {
        FrameTimeInfos frameTimeInfos = game.getFrameTimeInfos();
        for (Updatable u : new ArrayList<Updatable>(updatableBodies)) {//copy to allow updatable body to be removed from list
            u.update(frameTimeInfos);
        }

        topLevelEntities.update(frameTimeInfos);
    }

    private void drawBackground() {
        GL11.glPushMatrix();
        backgroundTexture.bind();

        ROVector2f heroPos = hero.getPosition();

        AbsoluteAABox worldStaticBounds = getStaticBounds();
        AbsoluteAABox staticBounds = new AbsoluteAABox();
        staticBounds.x1 = ortho2DLeft * aspectRatio * 2.0f;
        staticBounds.x2 = ortho2DRight * aspectRatio * 2.0f;
        staticBounds.y1 = ortho2DBottom * aspectRatio * 2.0f;
        staticBounds.y2 = ortho2DTop * aspectRatio * 2.0f;
        float xt = -heroPos.getX() * (staticBounds.getWidth() / worldStaticBounds.getWidth());
        float yt = -heroPos.getY() * (staticBounds.getHeight() / worldStaticBounds.getHeight());

        xt = Math.max(xt, -ortho2DBaseSize / 2.0f);
        xt = Math.min(xt, ortho2DBaseSize / 2.0f);
        yt = Math.max(yt, -ortho2DBaseSize / 2.0f);
        yt = Math.min(yt, ortho2DBaseSize / 2.0f);

        staticBounds.x1 += xt;
        staticBounds.x2 += xt;
        staticBounds.y1 += yt;
        staticBounds.y2 += yt;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(staticBounds.x1, staticBounds.y2);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(staticBounds.x2, staticBounds.y2);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(staticBounds.x2, staticBounds.y1);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(staticBounds.x1, staticBounds.y1);
        GL11.glEnd();
        GL11.glPopMatrix();
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
}
