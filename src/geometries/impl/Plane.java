package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a plane in 3D space, defined by a point and a normal vector.
 *
 * @author David &amp; Yehuda
 */
public class Plane extends Geometry {

    /**
     * A point on the plane.
     */
    private final Point _point;

    /**
     * The normalized normal vector of the plane.
     */
    private final Vector _normal;

    /**
     * Constructs a plane from three points.
     * The plane stores a reference point and computes the normalized normal vector
     * from the cross product of two edge vectors formed by the three points.
     *
     * @param p1 first point on the plane
     * @param p2 second point on the plane
     * @param p3 third point on the plane
     */
    public Plane(Point p1, Point p2, Point p3) {
        _point = p1;

        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        _normal = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructs a plane from a point and a normal vector.
     * The normal vector is normalized before being stored.
     *
     * @param point  a point on the plane
     * @param normal the normal vector of the plane (need not be normalized)
     */
    public Plane(Point point, Vector normal) {
        _point = point;
        _normal = normal.normalize();
    }

    @Override
    public Vector getNormal(Point point) {
        return _normal;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {

        double nv = _normal.dotProduct(ray.direction());
        // no intersection – the ray is parallel to the plane
        if (isZero(nv)) return null;

        Vector u;
        try {
            u = _point.subtract(ray.origin());
        } catch (IllegalArgumentException _) {
            return null;
        }

        double t = alignZero(u.dotProduct(_normal) / nv); // find t by the formula t = (Q-P)·N / v·N

        // The intersection point is behind the ray's origin (t <= 0)
        // OR it is beyond the maximum distance (t > maxDistance)
        if (alignZero(t) <= 0 || alignZero(t - maxDistance) > 0) {
            return null;
        }

        // If we reached here, the intersection is valid and within the light's range
        return List.of(new Intersection(ray.getPoint(t), this));
    }

    @Override
    public String toString() {
        return "Plane{point=" + _point + ", normal=" + _normal + "}";
    }
}
