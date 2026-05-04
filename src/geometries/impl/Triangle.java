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

    /**
     * Finds the intersection points of a ray with this triangle.
     *
     * @param ray the ray to find intersections with
     * @return
     */
    @Override
    public List<Point> findIntersections(Ray ray) {

        List<Point> planeIntersections = _plane.findIntersections(ray);

        if (planeIntersections == null) return null;

        Point p0 = planeIntersections.get(0);

        Point p1 = _vertices.get(0);
        Point p2 = _vertices.get(1);
        Point p3 = _vertices.get(2);

        Vector v1 = p1.subtract(ray.origin());
        Vector v2 = p2.subtract(ray.origin());
        Vector v3 = p3.subtract(ray.origin());

        Vector n1 = v1.crossProduct(v2);
        Vector n2 = v2.crossProduct(v3);
        Vector n3 = v3.crossProduct(v1);

        double s1 = alignZero(ray.direction().dotProduct(n1));
        double s2 = alignZero(ray.direction().dotProduct(n2));
        double s3 = alignZero(ray.direction().dotProduct(n3));

        if (s1 == 0 || s2 == 0 || s3 == 0) return null;

        if (s1 > 0 && s2 > 0 && s3 > 0)
            return List.of(p0);

        if (s1 < 0 && s2 < 0 && s3 < 0)
            return List.of(p0);

        return null;

    }
}
