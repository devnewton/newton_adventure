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

import im.bci.jnuit.audio.Sound;
import im.bci.newtonadv.Game;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.anim.Play;
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

import net.phys2d.math.Matrix2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;

/**
 *
 * @author devnewton
 */
public strictfp class World extends net.phys2d.raw.World {

    public static final int STATIC_BODY_COLLIDE_BIT = 1;
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
    private Play appleIconPlay;
    private boolean objectivesCompleted = false;
    private float nonProgressiveGravityRotationStep;
    private final String questName;
    private int nbCollectableApple;
    private int nbCollectableCoin;
    private ArrayList<Key> keys = new ArrayList<Key>();
    private ArrayList<Runnable> postStepActions = new ArrayList<Runnable>();
    private ArrayList<PostUpdateAction> postUpdateActions = new ArrayList<PostUpdateAction>();
    private boolean isRotateGravityPossible = true;
    private EnumMap<NewtonColor, BodyList> coloredStaticBodies;
    private Sound explodeSound;
    public IStaticPlatformDrawer staticPlatformDrawer;

    public void setAppleIcon(AnimationCollection appleIcon) {
        this.appleIconPlay = appleIcon.getFirst().start();
    }

    void removeKey(Key key) {
        keys.remove(key);
        remove(key);
    }

    Sound getExplodeSound() {
        return explodeSound;
    }

    void addKey(Key key) {
        add(key);
        keys.add(key);
    }

    void addColoredPlatform(NewtonColor color, ColoredPlatform colored) {
        add(colored);
        coloredStaticBodies.get(color).add(colored);
    }

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

    public Hero getHero() {
        return hero;
    }

    public World(Game game, String questName, String levelName) {
        super(new Vector2f(0.0f, -gravityForce), 2,
                new StaticQuadSpaceStrategy(20, 5));
        this.game = game;
        progressiveRotateGravity(0.0f);
        this.questName = questName;
        coloredStaticBodies = new EnumMap<NewtonColor, BodyList>(NewtonColor.class);
        for (NewtonColor color : NewtonColor.values()) {
            coloredStaticBodies.put(color, new BodyList());
        }
        explodeSound = game.getNuitToolkit().getAudio().getSound(game.getData().getFile("explode.wav"));
        staticPlatformDrawer = game.getView().createStaticPlatformDrawer();
    }

    public AbsoluteAABox getStaticBounds() {
        if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
            return ((StaticQuadSpaceStrategy) collisionStrategy).getStaticBounds();
        } else {
            return null;// todo
        }
    }

    public BodyList getVisibleBodies(float camera_x1, float camera_y1,
            float camera_x2, float camera_y2) {
        if (collisionStrategy instanceof StaticQuadSpaceStrategy) {
            return ((StaticQuadSpaceStrategy) collisionStrategy).findVisibleBodies(camera_x1, camera_y1, camera_x2,
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
            postStep();
        }
    }

    private void postStep() {
        for (Runnable runnable : postStepActions) {
            runnable.run();
        }
        postStepActions.clear();
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

    void setBackgroundTexture(ITexture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    void setIsRotateGravityPossible(boolean isRotateGravityPossible) {
        this.isRotateGravityPossible = isRotateGravityPossible;
    }

    public BodyList getColoredStaticBodyList(NewtonColor color) {
        return coloredStaticBodies.get(color);
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
                game.getNuitToolkit().getAudio().getSound(game.getData().getFile("go_to_bonus_world.wav")).play();
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
                    game.getNuitToolkit().getAudio().getSound(game.getData().getFile("door_to_bonus_world_unlocked.wav")).play();
                    ((DoorToBonusWorld) body).open();
                }
            }
        }
    }

    void removeCoin(Coin coin) {
        remove(coin);
        --nbCollectableCoin;
        if (nbCollectableCoin <= 0) {
            boolean levelWithDoor = false;
            for (int i = 0; i < bodies.size(); ++i) {
                Body body = bodies.get(i);
                if (body instanceof Door) {
                    ((Door) body).open();
                    levelWithDoor = true;
                }
            }
            if (!levelWithDoor) {
                setObjectivesCompleted(true);
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
                    if (previousTeleporterFound) {
                        return teleporter;
                    }
                    if (firstTeleporterWithSameColor == null) {
                        firstTeleporterWithSameColor = teleporter;
                    }
                    if (teleporter == previousTeleporter) {
                        previousTeleporterFound = true;
                    }

                }
            }
        }
        return firstTeleporterWithSameColor;
    }

    public void teleportFrom(Teleporter previousTeleporter) {
        final Teleporter teleporter = findNextTeleporter(previousTeleporter);
        remove(hero);
        postStepActions.add(new Runnable() {
            @Override
            public void run() {
                hero.setEnabled(true);
                hero.setPosition(teleporter.getPosition().getX(), teleporter.getPosition().getY() + 1.0f);
                add(hero);
            }
        ;
    }

    );

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
    
    void addStaticPlatform(StaticPlatform platform) {
    	staticPlatformDrawer.add(platform);
    	add(platform);
    }

    void addApple(im.bci.newtonadv.world.Apple apple) {
        ++nbCollectableApple;
        add(apple);
    }

    void addCoin(Coin coin) {
        ++nbCollectableCoin;
        add(coin);
    }

    void setHero(Hero hero) {
        this.hero = hero;
        add(hero);
    }
}
