package im.bci.newtonadv.platform.playn.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import im.bci.newtonadv.platform.playn.core.PlaynNewtonAdventureGame;

public class PlaynNewtonAdventureGameActivity extends GameActivity {

  @Override
  public void main(){
    PlayN.run(new PlaynNewtonAdventureGame());
  }
}
