package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a triangle in 3D space.
 * A triangle is a polygon with exactly three vertices.
 *
 * @author David &amp; Yehuda
 */
public class Triangle extends Polygon {

    /**
     * Constructs a triangle from three vertices.
     *
     * @param p1 first vertex
     * @param p2 second vertex
     * @param p3 third vertex
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {

        List<Point> planeIntersections = _plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p0 = planeIntersections.get(0);

        Vector v = ray.direction();
        Point head = ray.origin();

        Vector v1 = _vertices.get(0).subtract(head);
        Vector v2 = _vertices.get(1).subtract(head);
        Vector v3 = _vertices.get(2).subtract(head);

        double n1 = alignZero(v.dotProduct(v1.crossProduct(v2)));
        if (n1 == 0) return null;

        double n2 = alignZero(v.dotProduct(v2.crossProduct(v3)));
        if (n1 * n2 <= 0) return null;

        double n3 = alignZero(v.dotProduct(v3.crossProduct(v1)));
        if (n1 * n3 <= 0) return null;

        return List.of(p0);

    }
}
