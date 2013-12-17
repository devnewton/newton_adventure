package im.bci.newtonadv.game;

import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.DynamicShape;
import net.phys2d.raw.shapes.Shape;

public abstract class AbstractDrawableBody extends Body implements
		Drawable {

	public AbstractDrawableBody(DynamicShape shape, float m) {
		super(shape, m);
	}

	public AbstractDrawableBody(Shape shape, float m) {
		super(shape, m);
	}

	public AbstractDrawableBody(String name, DynamicShape shape, float m) {
		super(name, shape, m);
	}

	public AbstractDrawableBody(String name, Shape shape, float m) {
		super(name, shape, m);
	}

	private int zorder = 0;

	@Override
	public int getZOrder() {
		return zorder;
	}

	public void setZOrder(int z) {
		zorder = z;
	}

}
