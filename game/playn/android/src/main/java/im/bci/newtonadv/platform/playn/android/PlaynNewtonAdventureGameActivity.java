package im.bci.newtonadv.platform.playn.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import im.bci.newtonadv.platform.playn.core.PlaynNewtonAdventureGame;

public class PlaynNewtonAdventureGameActivity extends GameActivity {

    private PlaynNewtonAdventureGame game;

    @Override
    public void main() {
        game = new PlaynNewtonAdventureGame().useVirtualPad(true);
        PlayN.run(game);
    }

    @Override
    public void onBackPressed() {
        if(null != game) {
            game.onBackPressed();
        }
    }

}
