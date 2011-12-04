/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package im.bci.newtonadv.world;

import net.phys2d.math.Vector2f;
import im.bci.newtonadv.Texture;
import im.bci.newtonadv.game.FrameTimeInfos;
import im.bci.newtonadv.game.Updatable;

/**
 *
 * @author bci
 */
public strictfp class Cannon extends Platform implements Updatable {

    private static final long durationBetweenFireballGeneration = 2000000000;
    private long nextFireballTime = 0;
    private World world;
    private static final float shotForce = 10000.0f;

    public enum Orientation {

        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private Orientation orientation;

    public Cannon(World world, Orientation orientation) {
        this.world = world;
        this.orientation = orientation;
        setEnabled(false);
    }

    public void update(FrameTimeInfos frameTimeInfos) throws GameOverException {
        if( frameTimeInfos.currentTime > nextFireballTime) {
            throwFireball();
            nextFireballTime = frameTimeInfos.currentTime + durationBetweenFireballGeneration;
        }
    }
    
    private void throwFireball() {
        Vector2f pos = new Vector2f(getPosition());
        //pos.add( new Vector2f( size, size));
        FireBall fireBall = new FireBall(world);
        fireBall.setPosition(pos.x, pos.y);
        fireBall.setTexture(world.getFireBallTexture());
        world.add(fireBall);
        switch(orientation) {
            case UP:
                fireBall.addForce(new Vector2f(0, shotForce));
                break;
            case DOWN:
                fireBall.addForce(new Vector2f(0, -shotForce));
                break;
            case LEFT:
                fireBall.addForce(new Vector2f(-shotForce, 0));
                break;
            case RIGHT:
                fireBall.addForce(new Vector2f(shotForce, 0));
                break;
        }
        
    }
}
