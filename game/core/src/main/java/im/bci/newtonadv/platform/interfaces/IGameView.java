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
package im.bci.newtonadv.platform.interfaces;

import im.bci.newtonadv.anim.Animation.Play;
import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.anim.AnimationFrame;
import im.bci.newtonadv.game.MainMenuSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.MenuSequence.Button;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.world.Axe;
import im.bci.newtonadv.world.AxeAnchor;
import im.bci.newtonadv.world.Bat;
import im.bci.newtonadv.world.Blocker;
import im.bci.newtonadv.world.Bomb;
import im.bci.newtonadv.world.Cloud;
import im.bci.newtonadv.world.Door;
import im.bci.newtonadv.world.Boss;
import im.bci.newtonadv.world.BossHand;
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
import im.bci.newtonadv.world.AnimatedPlatform;
import im.bci.newtonadv.world.ScoreVisualIndicator;
import im.bci.newtonadv.world.StaticPlatformDrawable;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author devnewton
 */
public interface IGameView {

    void draw(Sequence sequence);

    void drawPickableObject(PickableObject pickable, AnimationFrame texture, World world);

    void drawAxe(Axe axe, AnimationFrame texture);

    void drawAxeAnchor(AxeAnchor axeAnchor, float radius, AnimationFrame texture);

    void drawBat(Bat bat, float scale, AnimationFrame frame, World world);

    void drawBlocker(Blocker blocker, float alpha);

    void drawButton(Button button);

    void drawCloud(Cloud cloud, float alpha);

    void drawDoor(Door door, AnimationFrame texture);

    void drawBoss(Boss boss, AnimationFrame bodyTexture);

    void drawBossHand(BossHand hand, AnimationFrame texture);

    void drawExplosion(Explosion explosion, AnimationFrame texture, World world);

    void drawFPS(int nbFps);

    void drawFireBall(FireBall fireball, AnimationFrame texture, World world);

    void drawHero(Hero hero, AnimationFrame texture, World world);

    void drawKey(Key key, AnimationFrame texture, World world);

    void drawLevelIndicators(String indicators);

    void drawLosedApple(LosedApple apple, World world, AnimationFrame texture, float alpha);

    void drawMenuSequence(MenuSequence sequence);

    void drawMobilePikeAnchor(MobilePikeAnchor anchor, AnimationFrame texture);

    void drawMobilePikes(MobilePikes pikes, AnimationFrame texture);

    void drawMummy(Mummy mummy, World world, AnimationFrame texture, float scale);

    void drawPickedUpObject(PickedUpObject apple, World world, AnimationFrame texture);

    void drawPlatform(AnimatedPlatform platform);

    void drawMenuButton(Button button, String leftLabel, String rightLabel);

    void drawScoreSequence(ScoreSequence sequence, QuestScore questScore, long scorePerCentToShow);

    void drawSnowLayer(SnowLayer layer);
    
    void drawUsedKey(UsedKey key, AnimationFrame texture, World world);

    void drawWorld(World world);

    ITextureCache getTextureCache();

    void toggleFullscreen();

    AnimationCollection loadFromAnimation(String name) throws IOException;
    
    void drawFadeSequence(ITexture backgroundTexture, Play loadingPlay, float r, float g, float b, float a);

    void drawMovingPlatform(MovingPlatform aThis, AnimationFrame texture);

    void drawMainMenuSequence(MainMenuSequence mainMenuSequence);

    float getWidth();

    float getHeight();

    void drawMinimap(World world, ITexture minimapTexture);

    void drawKeyLock(KeyLock keyLock, float alpha);

    void drawScoreVisualIndicator(World world, ScoreVisualIndicator scoreVisualIndicator);

    void drawBomb(Bomb bomb, AnimationFrame currentFrame, World world);

    void drawLoading(Play loadingPlay);

	void drawStaticPlatforms(StaticPlatformDrawable staticPlatformDrawable);
}
