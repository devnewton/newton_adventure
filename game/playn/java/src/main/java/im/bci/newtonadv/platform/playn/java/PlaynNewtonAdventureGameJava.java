package im.bci.newtonadv.platform.playn.java;

import im.bci.jnuit.NuitDisplay;
import im.bci.jnuit.playn.java.PlaynNuitJavaDisplay;
import im.bci.newtonadv.platform.playn.core.PlaynNewtonAdventureGame;
import org.lwjgl.opengl.Display;
import playn.core.PlayN;
import playn.java.JavaPlatform;

public class PlaynNewtonAdventureGameJava {

    public static void main(String[] args) {
        JavaPlatform.Config config = new JavaPlatform.Config();
        config.appName = "Newton Adventure";
        JavaPlatform.register(config);
        PlayN.run(new PlaynNewtonAdventureGame() {

            int width, height;

            @Override
            protected NuitDisplay createNuitDisplay() {
                return new PlaynNuitJavaDisplay();
            }

            @Override
            public void init() {
                Display.setResizable(true);
                super.init();
            }

            @Override
            public void paint(float alpha) {
                if (width != Display.getWidth() || height != Display.getHeight()) {
                    width = Display.getWidth();
                    height = Display.getHeight();
                    PlayN.graphics().ctx().setSize(Display.getWidth(), Display.getHeight());
                }
                super.paint(alpha);
            }

            @Override
            protected void finish() {
                System.exit(0);
            }
        }.useVirtualPad(true));
    }
}
