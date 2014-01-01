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
import im.bci.newtonadv.anim.Play;
import im.bci.newtonadv.game.Drawable;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.QuestMenuSequence;
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
import im.bci.newtonadv.world.IStaticPlatformDrawable;
import im.bci.newtonadv.world.IStaticPlatformDrawer;
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
import im.bci.newtonadv.world.StaticPlatform;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.BodyList;
import net.phys2d.raw.shapes.Box;
import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Json;
import playn.core.PlayN;
import playn.core.Surface;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.core.util.Callback;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public class PlaynGameView implements IGameView {

    private final PlaynTextureCache textureCache;
    private static final Logger LOGGER = Logger.getLogger(PlaynGameView.class.getName());
    private Surface surface;
    private final TextFormat textFormat;

    public void setCurrentSurface(Surface currentSurface) {
        this.surface = currentSurface;
    }

    PlaynGameView() {
        textFormat = new TextFormat(PlayN.graphics().createFont("monospaced", Font.Style.BOLD, 24), true);
        //setOrtho2D(World.ortho2DLeft, World.ortho2DRight, World.ortho2DBottom, World.ortho2DTop);
        final ImageLayer apple = PlayN.graphics().createImageLayer(PlayN.assets().getImage("default_level_data/apple.png"));
        apple.setTranslation(-World.distanceUnit, -World.distanceUnit);
        apple.setSize(2 * World.distanceUnit, 2 * World.distanceUnit);
        PlayN.graphics().rootLayer().add(apple);
        textureCache = new PlaynTextureCache();
    }

    private void setOrtho2D(Surface surface, float left, float right, float bottom, float top) {
        float screenW = surface.width();
        float screenH = (float) surface.height();
        float gameW = right - left;
        float gameH = top - bottom;
        float aspectRatio = screenW / screenH;
        float scaleX = screenW / (aspectRatio * gameW);
        float scaleY = screenH / gameH;
        surface.translate(screenW / 2.0f, screenH / 2.0f);
        surface.scale(scaleX, scaleY);

//        
    }

    @Override
    public void draw(Sequence sequence) {
        sequence.draw();
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
        PlaynTexture texture = (PlaynTexture) button.getTexture();
        if (texture != null) {
            surface.drawImage(texture.getImage(), button.x, button.y, button.w > 0 ? button.w : texture.getWidth(), button.h > 0 ? button.h : texture.getHeight());
        }
    }

    @Override
    public void drawCloud(Cloud cloud, float alpha) {
        //TODO
    }

    @Override
    public void drawDoor(Door door, AnimationFrame texture) {
        Box box = (Box) door.getShape();
        final Image image = ((PlaynTexture) texture.getImage()).getImage();
        surface.drawImage(image, door.getPosition().getX(), door.getPosition().getY(), box.getSize().getX(), box.getSize().getY(), texture.getU1() * image.width(), texture.getV1() * image.height(), (texture.getU2() - texture.getU1())  * image.width(), (texture.getV2() - texture.getV1())* image.height());
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
        doDrawMenuSequence(sequence);
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
        drawButton(button);
        drawText(leftLabel, button.x, button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT);
        drawRighAlignedText(rightLabel, button.x, button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT);
    }

    private void drawText(String text, float x, float y) {
        if (!text.isEmpty()) {
            TextLayout textLayout = PlayN.graphics().layoutText(text, textFormat);
            drawText(textLayout, x, y);
        }
    }

    private void drawRighAlignedText(String text, float x, float y) {
        if (!text.isEmpty()) {
            TextLayout textLayout = PlayN.graphics().layoutText(text, textFormat);
            drawText(textLayout, x - textLayout.width(), y);
        }
    }

    private void drawText(TextLayout textLayout, float x, float y) {
        CanvasImage textImage = PlayN.graphics().createImage(textLayout.width(), textLayout.height());
        final Canvas canvas = textImage.canvas();
        canvas.setFillColor(Color.rgb(255, 255, 255));
        canvas.fillText(textLayout, 0, 0);
        surface.drawImage(textImage, x, y);
        /*       GL11.glEnable(GL11.GL_BLEND);
         GL11.glPushMatrix();
         GL11.glTranslatef(button.x,
         button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT
         + font.getHeight(), 0);
         GL11.glScalef(1, -1, 1);
         font.drawString(leftLabel, TrueTypeFont.Align.LEFT);
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslatef(button.x + QuestMenuSequence.QUEST_MINIATURE_WIDTH,
         button.y + QuestMenuSequence.QUEST_MINIATURE_HEIGHT
         + font.getHeight(), 0);
         GL11.glScalef(1, -1, 1);
         font.drawString(rightLabel, TrueTypeFont.Align.RIGHT);
         GL11.glPopMatrix();
         GL11.glDisable(GL11.GL_BLEND);*/
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
        surface.save();
        setOrtho2D(surface, World.ortho2DLeft, World.ortho2DRight, World.ortho2DBottom, World.ortho2DTop);

        /*TODO
         if (rotateViewWithGravity) {
         GL11.glRotatef((float) Math.toDegrees(-world.getGravityAngle()), 0, 0,
         1.0f);
         }*/
        //TODO drawWorldBackground(world, 1.0f);
        ROVector2f heroPos = world.getHero().getPosition();
        surface.translate(-heroPos.getX(), -heroPos.getY());

        float cameraW = (World.ortho2DRight - World.ortho2DLeft)/* * aspectRatio */;
        float cameraH = World.ortho2DTop - World.ortho2DBottom;
        final float cameraSize = (float) Math.sqrt(cameraW * cameraW + cameraH * cameraH);
        final BodyList visibleBodies = world.getVisibleBodies(heroPos.getX()
                - cameraSize, heroPos.getY() - cameraSize, heroPos.getX()
                + cameraSize, heroPos.getY() + cameraSize);

        ArrayList<Drawable> drawableBodies = new ArrayList<Drawable>();
        world.staticPlatformDrawer.resetVisibles();
        for (int i = 0; i < visibleBodies.size(); i++) {
            Body body = visibleBodies.get(i);
            if (body instanceof Drawable) {
                drawableBodies.add(((Drawable) body));
            }
            if (body instanceof StaticPlatform) {
                world.staticPlatformDrawer.addVisible((StaticPlatform) body);
            }
        }
        world.staticPlatformDrawer.getVisibleDrawables(drawableBodies);
        java.util.Collections.sort(drawableBodies, Drawable.comparator);
        for (Drawable drawableBody : drawableBodies) {
            drawableBody.draw();
        }
        world.getTopLevelEntities().draw();
        surface.restore();
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
        if(filename.endsWith(".json")) {
        return loadNanim(filename);
        } else if(filename.endsWith(".png")) {
            return new AnimationCollection(textureCache.getTexture(filename));
        } else {
            throw new RuntimeException("Unknow animation format of " + filename);
        }
    }

    private AnimationCollection loadNanim(final String filename) {
        final AnimationCollection nanim = new AnimationCollection();
        PlayN.assets().getText(filename, new Callback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
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
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error loading " + filename, ex);
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                LOGGER.log(Level.SEVERE, "Cannot load " + filename, cause);
            }
        });
        return nanim;
    }

    @Override
    public void drawFadeSequence(ITexture backgroundTexture, Play loadingPlay, float r, float g, float b, float a) {
        //TODO
    }

    @Override
    public void drawMovingPlatform(MovingPlatform aThis, AnimationFrame texture) {
        //TODO
    }

    @Override
    public void drawMainMenuSequence(MainMenuSequence mainMenuSequence) {
        drawMenuSequence(mainMenuSequence);
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
    public void drawLoading(Play loadingPlay) {
        //TODO
    }

    @Override
    public void drawStaticPlatforms(IStaticPlatformDrawable platforms) {
        //TODO
    }

    private void doDrawMenuSequence(final MenuSequence sequence) {
        surface.save();
        final float screenW = surface.width();
        final float screenH = surface.height();
        final float gameW = MenuSequence.ortho2DRight;
        final float gameH = MenuSequence.ortho2DBottom;
        final float scaleX = screenW / gameW;
        final float scaleY = screenH / gameH;
        surface.scale(scaleX, scaleY);
        PlaynTexture background = (PlaynTexture) sequence.getBackgroundImage();
        if (null != background) {
            surface.drawImage(background.getImage(), 0, 0, gameW, gameH);
        } else {
            surface.clear();
        }
        for (MenuSequence.Button b : sequence.getButtons()) {
            b.draw();
        }
        surface.restore();
    }

    @Override
    public IStaticPlatformDrawer createStaticPlatformDrawer() {
        return new PlaynStaticPlatformDrawer();
    }

}
