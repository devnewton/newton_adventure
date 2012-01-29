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

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.game.GameOverSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.MenuSequence.Button;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.StoryboardSequence;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.world.Axe;
import im.bci.newtonadv.world.AxeAnchor;
import im.bci.newtonadv.world.Bat;
import im.bci.newtonadv.world.Blocker;
import im.bci.newtonadv.world.Cloud;
import im.bci.newtonadv.world.Door;
import im.bci.newtonadv.world.DownLeftHalfPlatform;
import im.bci.newtonadv.world.DownRightHalfPlatform;
import im.bci.newtonadv.world.EgyptianBoss;
import im.bci.newtonadv.world.EgyptianBossHand;
import im.bci.newtonadv.world.Explosion;
import im.bci.newtonadv.world.FireBall;
import im.bci.newtonadv.world.Hero;
import im.bci.newtonadv.world.Key;
import im.bci.newtonadv.world.LosedApple;
import im.bci.newtonadv.world.MobilePikeAnchor;
import im.bci.newtonadv.world.MobilePikes;
import im.bci.newtonadv.world.Mummy;
import im.bci.newtonadv.world.PickableObject;
import im.bci.newtonadv.world.PickedUpObject;
import im.bci.newtonadv.world.Platform;
import im.bci.newtonadv.world.UpLeftHalfPlatform;
import im.bci.newtonadv.world.UpRightHalfPlatform;
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

    void drawPickableObject(PickableObject pickable, ITexture texture, World world);

    void drawAxe(Axe axe, ITexture texture);

    void drawAxeAnchor(AxeAnchor axeAnchor, float radius, ITexture texture);

    void drawBat(Bat bat, float scale, ITexture texture, World world);

    void drawBlocker(Blocker blocker, ITexture texture, float alpha);

    void drawButton(Button button);

    void drawCloud(Cloud cloud, ITexture texture, float alpha);

    void drawDoor(Door door, ITexture texture);

    void drawDownLeftHalfPlatform(DownLeftHalfPlatform platform, ITexture texture);

    void drawDownRightHalfPlatform(DownRightHalfPlatform platform, ITexture texture);

    void drawEgyptianBoss(EgyptianBoss boss, ITexture bodyTexture, boolean isHurtBlinkState);

    void drawEgyptianBossHand(EgyptianBossHand hand, ITexture texture);

    void drawExplosion(Explosion explosion, ITexture texture, World world);

    void drawFPS(int nbFps);

    void drawFireBall(FireBall fireball, ITexture texture, World world);

    void drawGameOverSequence(GameOverSequence sequence, ITrueTypeFont font);

    void drawHero(Hero hero, ITexture texture, World world, float scale);

    void drawKey(Key key, ITexture texture, World world);

    void drawLevelIndicators(String indicators, ITrueTypeFont appleFont);

    void drawLosedApple(LosedApple apple, World world, ITexture texture, float alpha);

    void drawMenuSequence(MenuSequence sequence);

    void drawMobilePikeAnchor(MobilePikeAnchor anchor, ITexture texture);

    void drawMobilePikes(MobilePikes pikes, ITexture texture);

    void drawMummy(Mummy mummy, World world, ITexture texture, float scale);

    void drawPickedUpObject(PickedUpObject apple, World world, ITexture texture);

    void drawPlatform(Platform platform, ITexture texture);

    void drawQuestMenuButton(Button button, ITrueTypeFont questNameFont, String questName);

    void drawScoreSequence(ScoreSequence sequence, ITrueTypeFont font, QuestScore questScore, long scorePerCentToShow);

    void drawSnowLayer(SnowLayer layer);

    void drawStoryBoardSequence(StoryboardSequence sequence, ITrueTypeFont font);

    void drawUpLeftHalfPlatform(UpLeftHalfPlatform platform, ITexture texture);

    void drawUpRightHalfPlatform(UpRightHalfPlatform platform, ITexture texture);

    void drawUsedKey(UsedKey key, ITexture texture, World world);

    void drawWorld(World world);

    ITextureCache getTextureCache();

    void toggleFullscreen();

    public ITrueTypeFont createStoryBoardSequenceFont();

    public ITrueTypeFont createQuestNameFont();

    public ITrueTypeFont createAppleFont();

    public ITrueTypeFont createScoreSequenceFont();

    public Animation loadFromGif(String name) throws FileNotFoundException, IOException;

    public void drawFadeSequence(float r, float g, float b, float a);
}
