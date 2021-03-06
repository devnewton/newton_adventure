package im.bci.newtonadv.game;

import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Shape;

public strictfp abstract class AbstractDrawableStaticBody extends StaticBody implements Drawable {
	
	private int zorder = 0;

	public AbstractDrawableStaticBody(Shape shape) {
		super(shape);
	}

	public AbstractDrawableStaticBody(String name, Shape shape) {
		super(name, shape);
	}

	@Override
	public int getZOrder() {
		return zorder;
	}
	
	public void setZOrder(int z) {
		zorder = z;
	}

}
