package im.bci.newtonadv.util;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Shape;

public class ShapeUtils {

	public static Vector2f[] getVertices(Shape shape, ROVector2f position, float rotation) {
		if(shape instanceof Box) {
			Box box = (Box) shape;
			return box.getPoints(position, rotation);
		} else if(shape instanceof ConvexPolygon){
			ConvexPolygon polygon = (ConvexPolygon) shape;
			return polygon.getVertices(position, rotation);
		} else {
			AABox bounds = shape.getBounds();
			final float x1 = position.getX() + -bounds.getWidth() / 2.0f;
			final float x2 = position.getX() + bounds.getWidth() / 2.0f;
			final float y1 = position.getY() + -bounds.getHeight() / 2.0f;
			final float y2 = position.getY() + bounds.getHeight() / 2.0f;		
			return new Vector2f[] { new Vector2f( x1, y2), new Vector2f(x2, y2), new Vector2f(x2, y1), new Vector2f(x1, y1) };
		}
	}

}
