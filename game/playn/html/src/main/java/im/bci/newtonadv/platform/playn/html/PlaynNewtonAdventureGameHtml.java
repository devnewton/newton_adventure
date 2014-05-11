package im.bci.newtonadv.platform.playn.html;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import im.bci.newtonadv.platform.playn.core.PlaynNewtonAdventureGame;

public class PlaynNewtonAdventureGameHtml extends HtmlGame {

  @Override
  public void start() {
    HtmlPlatform.Config config = new HtmlPlatform.Config();
    config.mode = HtmlPlatform.Mode.CANVAS;
    HtmlPlatform platform = HtmlPlatform.register(config);
    platform.assets().setPathPrefix("playn/");
    PlayN.run(new PlaynNewtonAdventureGame() {

        @Override
        protected void finish() {
        }
    });
  }
}
