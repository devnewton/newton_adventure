package im.bci.newtonadv.platform.android;

import java.io.FileNotFoundException;
import java.util.Properties;

import im.bci.newtonadv.anim.Animation;
import im.bci.newtonadv.game.GameOverSequence;
import im.bci.newtonadv.game.MenuSequence;
import im.bci.newtonadv.game.ScoreSequence;
import im.bci.newtonadv.game.Sequence;
import im.bci.newtonadv.game.StoryboardSequence;
import im.bci.newtonadv.game.MenuSequence.Button;
import im.bci.newtonadv.game.special.occasion.SnowLayer;
import im.bci.newtonadv.platform.interfaces.IGameView;
import im.bci.newtonadv.platform.interfaces.ITexture;
import im.bci.newtonadv.platform.interfaces.ITextureCache;
import im.bci.newtonadv.platform.interfaces.ITrueTypeFont;
import im.bci.newtonadv.score.QuestScore;
import im.bci.newtonadv.world.Apple;
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
import im.bci.newtonadv.world.PickedUpApple;
import im.bci.newtonadv.world.Platform;
import im.bci.newtonadv.world.UpLeftHalfPlatform;
import im.bci.newtonadv.world.UpRightHalfPlatform;
import im.bci.newtonadv.world.UsedKey;
import im.bci.newtonadv.world.World;

public class AndroidGameView implements IGameView {

	public AndroidGameView(Properties config) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Sequence sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawApple(Apple apple, ITexture texture, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawAxe(Axe axe, ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawAxeAnchor(AxeAnchor axeAnchor, float radius,
			ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawBat(Bat bat, float scale, ITexture texture, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawBlocker(Blocker blocker, ITexture texture, float alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawButton(Button button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCloud(Cloud cloud, ITexture texture, float alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawDoor(Door door, ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawDownLeftHalfPlatform(DownLeftHalfPlatform platform,
			ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawDownRightHalfPlatform(DownRightHalfPlatform platform,
			ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawEgyptianBoss(EgyptianBoss boss, ITexture bodyTexture,
			boolean isHurtBlinkState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawEgyptianBossHand(EgyptianBossHand hand, ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawExplosion(Explosion explosion, ITexture texture, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawFPS(int nbFps) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawFireBall(FireBall fireball, ITexture texture, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawGameOverSequence(GameOverSequence sequence,
			ITrueTypeFont font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHero(Hero hero, ITexture texture, World world, float scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawKey(Key key, ITexture texture, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLevelIndicators(World world, ITrueTypeFont appleFont) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLosedApple(LosedApple apple, World world, ITexture texture,
			float alpha) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawMenuSequence(MenuSequence sequence) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawMobilePikeAnchor(MobilePikeAnchor anchor, ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawMobilePikes(MobilePikes pikes, ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawMummy(Mummy mummy, World world, ITexture texture,
			float scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPickedUpApple(PickedUpApple apple, World world,
			ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPlatform(Platform platform, ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawQuestMenuButton(Button button, ITrueTypeFont questNameFont,
			String questName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawScoreSequence(ScoreSequence sequence, ITrueTypeFont font,
			QuestScore questScore, long scorePerCentToShow) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawSnowLayer(SnowLayer layer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawStoryBoardSequence(StoryboardSequence sequence,
			ITrueTypeFont font) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawUpLeftHalfPlatform(UpLeftHalfPlatform platform,
			ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawUpRightHalfPlatform(UpRightHalfPlatform platform,
			ITexture texture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawUsedKey(UsedKey key, ITexture texture, World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawWorld(World world) {
		// TODO Auto-generated method stub

	}

	@Override
	public ITextureCache getTextureCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void toggleFullscreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public ITrueTypeFont createStoryBoardSequenceFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITrueTypeFont createQuestNameFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITrueTypeFont createAppleFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITrueTypeFont createScoreSequenceFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Animation loadFromGif(String name) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
