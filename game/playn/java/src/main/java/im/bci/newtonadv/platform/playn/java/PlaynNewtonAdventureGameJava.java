package im.bci.newtonadv.platform.playn.java;

import playn.core.PlayN;
import playn.java.JavaPlatform;

import im.bci.newtonadv.platform.playn.core.PlaynNewtonAdventureGame;

public class PlaynNewtonAdventureGameJava {

  public static void main(String[] args) {
    JavaPlatform.Config config = new JavaPlatform.Config();
    // use config to customize the Java platform, if needed
    JavaPlatform.register(config);
    PlayN.run(new PlaynNewtonAdventureGame());
  }
}
