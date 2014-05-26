package im.bci.newtonadv.platform.playn.java;

import im.bci.jnuit.playn.java.PlaynNuitJavaDisplay;
import im.bci.jnuit.NuitDisplay;
import playn.core.PlayN;
import playn.java.JavaPlatform;

import im.bci.newtonadv.platform.playn.core.PlaynNewtonAdventureGame;

public class PlaynNewtonAdventureGameJava {

    public static void main(String[] args) {
        JavaPlatform.Config config = new JavaPlatform.Config();
        config.appName = "Newton Adventure";
        JavaPlatform.register(config);
        PlayN.run(new PlaynNewtonAdventureGame() {

            @Override
            protected NuitDisplay createNuitDisplay() {
                return new PlaynNuitJavaDisplay();
            }

            @Override
            protected void finish() {
                System.exit(0);
            }
        }.useVirtualPad(true));
    }
}
