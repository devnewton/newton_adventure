package im.bci.newtonadv.util;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.shapes.AABox;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Shape;

public class ShapeUtils {

    public static Vector2f[] getVertices(Shape shape, ROVector2f position, float rotation) {
        if (shape instanceof Box) {
            Box box = (Box) shape;
            return box.getPoints(position, rotation);
        } else if (shape instanceof ConvexPolygon) {
            ConvexPolygon polygon = (ConvexPolygon) shape;
            Vector2f[] vertices = polygon.getVertices(position, rotation);
            float minX = vertices[0].x, maxX = vertices[0].x, minY = vertices[0].y, maxY = vertices[0].y;
            for (int i = 1; i < vertices.length; ++i) {
                minX = Math.min(minX, vertices[i].x);
                maxX = Math.max(maxX, vertices[i].x);
                minY = Math.min(minY, vertices[i].y);
                maxY = Math.max(maxY, vertices[i].y);
            }
            float width = maxX - minX;
            float height = maxY - minY;
            return new Box(width, height).getPoints(position, rotation);
        } else if (shape instanceof Line) {
            Line line = (Line) shape;
            float width = Math.abs(line.getX2() - line.getX1());
            float height = Math.abs(line.getY2() - line.getY1());
            return new Box(width, height).getPoints(position, rotation);
        } else {
            AABox bounds = shape.getBounds();
            return new Box(bounds.getWidth(), bounds.getHeight()).getPoints(position, rotation);
        }
    }

    public static ROVector2f getSize(Shape shape) {
        if (shape instanceof Box) {
            Box box = (Box) shape;
            return box.getSize();
        } else if (shape instanceof ConvexPolygon) {
            ConvexPolygon polygon = (ConvexPolygon) shape;
            ROVector2f[] vertices = polygon.getVertices();
            float minX = vertices[0].getX(), maxX = vertices[0].getX(), minY = vertices[0].getY(), maxY = vertices[0].getY();
            for (int i = 1; i < vertices.length; ++i) {
                minX = Math.min(minX, vertices[i].getX());
                maxX = Math.max(maxX, vertices[i].getX());
                minY = Math.min(minY, vertices[i].getY());
                maxY = Math.max(maxY, vertices[i].getY());
            }
            return new Vector2f(maxX - minX, maxY - minY);
        } else if (shape instanceof Line) {
            Line line = (Line) shape;
            float width = Math.abs(line.getX2() - line.getX1());
            float height = Math.abs(line.getY2() - line.getY1());
            return new Vector2f(width, height);
        } else if(shape instanceof Circle) {
            Circle circle = (Circle) shape;
            float diameter = circle.getRadius() * 2.0f;
            return new Vector2f(diameter, diameter);
        } else {
            AABox bounds = shape.getBounds();
            return new Vector2f(bounds.getWidth(), bounds.getHeight());
        }
    }

}
