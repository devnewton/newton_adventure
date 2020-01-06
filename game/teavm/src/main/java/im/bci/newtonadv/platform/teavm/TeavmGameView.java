/*
 The MIT License (MIT)

 Copyright (c) 2019 devnewton <devnewton@bci.im>

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
package im.bci.newtonadv.platform.teavm;

import im.bci.jnuit.animation.IAnimationFrame;
import im.bci.jnuit.animation.IPlay;
import im.bci.jnuit.teavm.TeavmSync;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.jnuit.animation.ITexture;
import im.bci.jnuit.teavm.TeavmTexture;
import im.bci.jnuit.teavm.assets.animation.TeavmAnimationImage;
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
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;
import java.util.Collection;
import java.util.Collections;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;

/**
 *
 * @author devnewton
 */
class TeavmGameView implements IGameView {

    private TeavmSync sync = new TeavmSync();
    private CanvasRenderingContext2D ctx;
    private HTMLCanvasElement canvas;

    public TeavmGameView(HTMLCanvasElement canvas, CanvasRenderingContext2D ctx) {
        this.canvas = canvas;
        this.ctx = ctx;
    }

    @Override
    public void draw(Sequence sequence) {
        if (sync.shouldDraw()) {
            ctx.setTransform(1, 0, 0, 1, 0, 0);
            sequence.draw();
        }
    }

    @Override
    public void drawPickableObject(PickableObject pickableObject, IAnimationFrame texture, World world) {
        //TODO
        /*AABox bounds = pickableObject.getShape().getBounds();

        GL11.glPushMatrix();
        GL11.glTranslatef(pickableObject.getPosition().getX(), pickableObject.getPosition().getY(), 0.0f);
        GL11.glRotatef((float) Math.toDegrees(world.getGravityAngle()), 0, 0, 1.0f);
        final float x1 = -bounds.getWidth() / 2.0f;
        final float x2 = bounds.getWidth() / 2.0f;
        final float y1 = -bounds.getHeight() / 2.0f;
        final float y2 = bounds.getHeight() / 2.0f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getImage().getId());

        final float u1 = texture.getU1(), u2 = texture.getU2();
        final float v1 = texture.getV1(), v2 = texture.getV2();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);*/
    }

    @Override
    public void drawAxe(Axe axe, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawAxeAnchor(AxeAnchor axeAnchor, float radius, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawBat(Bat bat, float scale, IAnimationFrame frame, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawBlocker(Blocker blocker, float alpha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawButton(MenuSequence.Button button) {
        TeavmTexture texture = (TeavmTexture) button.getTexture();
        if (texture != null) {
            double dx = button.x;
            double dy = button.y;
            double dw = button.w > 0 ? button.w : texture.getWidth();
            double dh = button.h > 0 ? button.h : texture.getHeight();
            ctx.drawImage(texture.getImage(), dx, dy, dw, dh);
        }
    }

    @Override
    public void drawCloud(Cloud cloud, float alpha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawDoor(Door door, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawBoss(Boss boss, IAnimationFrame bodyTexture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawBossHand(BossHand hand, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawExplosion(Explosion explosion, IAnimationFrame texture, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawFPS(int nbFps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawFireBall(FireBall fireball, IAnimationFrame texture, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawHero(Hero hero, IAnimationFrame texture, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawKey(Key key, IAnimationFrame texture, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawLevelIndicators(String indicators) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawLosedApple(LosedApple apple, World world, IAnimationFrame texture, float alpha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMenuSequence(MenuSequence sequence) {
        // TODO if (Display.isVisible() || Display.isDirty() || LwjglHelper.wasResized()
        // || sequence.isDirty()) {
        sequence.setDirty(false);
        doDrawMenuSequence(sequence);
        // TODO }    
    }

    private void doDrawMenuSequence(MenuSequence sequence) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        TeavmAnimationImage background = (TeavmAnimationImage) sequence.getBackgroundImage();
        if (background != null) {
            final float x1 = sequence.getBackgroundX1();
            final float x2 = sequence.getBackgroundX2();
            final float y1 = sequence.getBackgroundY1();
            final float y2 = sequence.getBackgroundY2();
            ctx.drawImage(canvas, x1, y1, x2 - x1, y2 - y1);
        }
        for (MenuSequence.Button b : sequence.getButtons()) {
            b.draw();
        }
    }

    @Override
    public void drawMobilePikeAnchor(MobilePikeAnchor anchor, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMobilePikes(MobilePikes pikes, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMummy(Mummy mummy, World world, IAnimationFrame texture, float scale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawPickedUpObject(PickedUpObject apple, World world, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawPlatform(AnimatedPlatform platform) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMenuButton(MenuSequence.Button button, String leftLabel, String rightLabel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawScoreSequence(ScoreSequence sequence, QuestScore questScore, long scorePerCentToShow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawSnowLayer(SnowLayer layer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawUsedKey(UsedKey key, IAnimationFrame texture, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawWorld(World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawFadeSequence(ITexture backgroundTexture, IPlay loadingPlay, float r, float g, float b, float a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMovingPlatform(MovingPlatform aThis, IAnimationFrame texture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMainMenuSequence(MainMenuSequence mainMenuSequence) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawMinimap(World world, ITexture minimapTexture) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawKeyLock(KeyLock keyLock, float alpha) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawScoreVisualIndicator(World world, ScoreVisualIndicator scoreVisualIndicator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawBomb(Bomb bomb, IAnimationFrame currentFrame, World world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawStaticPlatforms(IStaticPlatformDrawable staticPlatformDrawable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IStaticPlatformDrawer createStaticPlatformDrawer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<String> listShaders() {
        return Collections.emptyList();
    }

}
