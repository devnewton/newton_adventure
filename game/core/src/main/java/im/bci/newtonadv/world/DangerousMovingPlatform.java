package im.bci.newtonadv.world;

import im.bci.newtonadv.anim.AnimationCollection;
import im.bci.newtonadv.util.NewtonColor;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;

public strictfp class DangerousMovingPlatform extends MovingPlatform {
	
	private NewtonColor color = NewtonColor.white;

	public DangerousMovingPlatform(World world,
			AnimationCollection texture, Vector2f[] destinations, float w,
			float h) {
		super(world, texture, destinations, w, h);
	}
	
	@Override
	public void collided(Body other) {
		if (other instanceof Hero) {
			Hero hero = (Hero) other;
			if (hero.isInvincible() || (NewtonColor.white != color && color == hero.getColor())) {
				return;
			}
			CollisionEvent[] events = world.getContacts(this);

			for (CollisionEvent event : events) {
				if (event.getBodyB() == hero) {
					Vector2f normal = new Vector2f(event.getNormal());
					hero.hurtByPike(normal/* .negate() */);
					return;
				} else if (event.getBodyA() == hero) {
					Vector2f normal = new Vector2f(event.getNormal());
					hero.hurtByPike(normal.negate());
					return;
				}
			}
		}
	}

	public void setColor(NewtonColor color) {
		this.color = color;
	}
	

}
