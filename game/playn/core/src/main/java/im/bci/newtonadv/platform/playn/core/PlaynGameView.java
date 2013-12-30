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
package im.bci.newtonadv.platform.playn.core;

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.world.AnimatedPlatform;
import im.bci.newtonadv.world.Axe;
import im.bci.newtonadv.world.AxeAnchor;
import im.bci.newtonadv.world.Bat;
import im.bci.newtonadv.world.Blocker;
import im.bci.newtonadv.world.Bomb;
import im.bci.newtonadv.world.Boss;
import im.bci.newtonadv.world.BossHand;
import im.bci.newtonadv.world.Cloud;
import im.bci.newtonadv.world.Door;
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
import im.bci.newtonadv.world.ScoreVisualIndicator;
import im.bci.newtonadv.world.StaticPlatformDrawable;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import playn.core.ImageLayer;
import playn.core.Json;
import playn.core.PlayN;
import playn.core.util.Callback;
import pythagoras.f.Transform;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynGameView implements IGameView {

    private final PlaynTextureCache textureCache;
    private static final Logger LOGGER = Logger.getLogger(PlaynGameView.class.getName());

    PlaynGameView() {
        float screenW = (float) PlayN.graphics().width();
        float screenH = (float) PlayN.graphics().height();
        float gameW = World.ortho2DRight - World.ortho2DLeft;
        float gameH = World.ortho2DTop - World.ortho2DBottom;
        float aspectRatio = screenW / screenH;
        float scaleX =  screenW / (aspectRatio *gameW);
        float scaleY = screenH / gameH;
        PlayN.graphics().rootLayer().setScale(scaleX, scaleY);
        PlayN.graphics().rootLayer().setTranslation(screenW/2.0f, screenH/2.0f);
        final ImageLayer apple = PlayN.graphics().createImageLayer(PlayN.assets().getImage("default_level_data/apple.png"));
        apple.setTranslation(-World.distanceUnit, -World.distanceUnit);
        apple.setSize(2 * World.distanceUnit, 2 * World.distanceUnit);
        PlayN.graphics().rootLayer().add(apple);
        textureCache = new PlaynTextureCache();
    }

    private static void setToOrtho(Transform transform, float left, float right, float bottom, float top) {
        float xOrth = 2.0f / (right - left);
        float yOrth = 2.0f / (top - bottom);
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        transform.setTransform(xOrth, 0, 0, yOrth,
                tx, ty);
    }

    @Override
    public void draw(Sequence sequence) {
        //TODO
    }

    @Override
    public void drawPickableObject(PickableObject pickable, AnimationFrame texture, World world) {
        //TODO
    }

    @Override
    public void drawAxe(Axe axe, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawAxeAnchor(AxeAnchor axeAnchor, float radius, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawBat(Bat bat, float scale, AnimationFrame frame, World world) {
        //TODO
    }

    @Override
    public void drawBlocker(Blocker blocker, float alpha) {
        //TODO
    }

    @Override
    public void drawButton(MenuSequence.Button button) {
        //TODO
    }

    @Override
    public void drawCloud(Cloud cloud, float alpha) {
        //TODO
    }

    @Override
    public void drawDoor(Door door, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawBoss(Boss boss, AnimationFrame bodyTexture) {
        //TODO
    }

    @Override
    public void drawBossHand(BossHand hand, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawExplosion(Explosion explosion, AnimationFrame texture, World world) {
        //TODO
    }

    @Override
    public void drawFPS(int nbFps) {
        //TODO
    }

    @Override
    public void drawFireBall(FireBall fireball, AnimationFrame texture, World world) {
        //TODO
    }

    @Override
    public void drawHero(Hero hero, AnimationFrame texture, World world) {
        //TODO
    }

    @Override
    public void drawKey(Key key, AnimationFrame texture, World world) {
        //TODO
    }

    @Override
    public void drawLevelIndicators(String indicators) {
        //TODO
    }

    @Override
    public void drawLosedApple(LosedApple apple, World world, AnimationFrame texture, float alpha) {
        //TODO
    }

    @Override
    public void drawMenuSequence(MenuSequence sequence) {
        //TODO
    }

    @Override
    public void drawMobilePikeAnchor(MobilePikeAnchor anchor, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawMobilePikes(MobilePikes pikes, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawMummy(Mummy mummy, World world, AnimationFrame texture, float scale) {
        //TODO
    }

    @Override
    public void drawPickedUpObject(PickedUpObject apple, World world, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawPlatform(AnimatedPlatform platform) {
        //TODO
    }

    @Override
    public void drawMenuButton(MenuSequence.Button button, String leftLabel, String rightLabel) {
        //TODO
    }

    @Override
    public void drawScoreSequence(ScoreSequence sequence, QuestScore questScore, long scorePerCentToShow) {
        //TODO
    }

    @Override
    public void drawSnowLayer(SnowLayer layer) {
        //TODO
    }

    @Override
    public void drawUsedKey(UsedKey key, AnimationFrame texture, World world) {
        //TODO
    }

    @Override
    public void drawWorld(World world) {
        //TODO
    }

    @Override
    public ITextureCache getTextureCache() {
        return textureCache;
    }

    @Override
    public void toggleFullscreen() {
        //TODO
    }

    @Override
    public AnimationCollection loadFromAnimation(final String filename) throws IOException {
        final AnimationCollection nanim = new AnimationCollection();
        PlayN.assets().getText(filename, new Callback<String>() {

            @Override
            public void onSuccess(String result) {
                String path;
                final int lastIndexOfSlash = filename.lastIndexOf("/");
                if (lastIndexOfSlash < 0) {
                    path = "";
                } else {
                    path = filename.substring(0, filename.lastIndexOf("/") + 1);
                }
                Json.Object json = PlayN.json().parse(result);
                Json.Array jsonAnimations = json.getArray("animations");
                for (int a = 0, na = jsonAnimations.length(); a < na; ++a) {
                    Json.Object jsonAnimation = jsonAnimations.getObject(a);
                    Animation animation = new Animation(jsonAnimation.getString("name"));
                    Json.Array jsonFrames = jsonAnimation.getArray("frames");
                    for (int f = 0, nf = jsonFrames.length(); f < nf; ++f) {
                        Json.Object jsonFrame = jsonFrames.getObject(f);
                        final String imageFilename = jsonFrame.getString("image");
                        ITexture texture = textureCache.getTexture(path + imageFilename);
                        animation.addFrame(texture, jsonFrame.getInt("duration"), jsonFrame.getNumber("u1"), jsonFrame.getNumber("v1"), jsonFrame.getNumber("u2"), jsonFrame.getNumber("v2"));
                    }
                    nanim.addAnimation(animation);
                }
                nanim.setReady(true);
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load " + filename, cause);
            }
        });
        return nanim;
    }

    @Override
    public void drawFadeSequence(ITexture backgroundTexture, Animation.Play loadingPlay, float r, float g, float b, float a) {
        //TODO
    }

    @Override
    public void drawMovingPlatform(MovingPlatform aThis, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawMainMenuSequence(MainMenuSequence mainMenuSequence) {
        //TODO
    }

    @Override
    public float getWidth() {
        return PlayN.graphics().width();
    }

    @Override
    public float getHeight() {
        return PlayN.graphics().height();
    }

    @Override
    public void drawMinimap(World world, ITexture minimapTexture) {
        //TODO
    }

    @Override
    public void drawKeyLock(KeyLock keyLock, float alpha) {
        //TODO
    }

    @Override
    public void drawScoreVisualIndicator(World world, ScoreVisualIndicator scoreVisualIndicator) {
        //TODO
    }

    @Override
    public void drawBomb(Bomb bomb, AnimationFrame currentFrame, World world) {
        //TODO
    }

    @Override
    public void drawLoading(Animation.Play loadingPlay) {
        //TODO
    }

    @Override
    public void drawStaticPlatforms(StaticPlatformDrawable staticPlatformDrawable) {
        //TODO
    }

}
