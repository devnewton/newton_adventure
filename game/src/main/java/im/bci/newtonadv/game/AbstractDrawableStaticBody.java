package im.bci.newtonadv.game;

import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Shape;

public abstract class AbstractDrawableStaticBody extends StaticBody implements Drawable {
	
	private int zorder = 0;

	public AbstractDrawableStaticBody(Shape shape) {
		super(shape);
		// TODO Auto-generated constructor stub
	}

	public AbstractDrawableStaticBody(String name, Shape shape) {
		super(name, shape);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getZOrder() {
		return zorder;
	}
	
	public void setZOrder(int z) {
		zorder = z;
	}

}
