package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space, defined by a center point and a radius.
 *
 * @author David &amp; Yehuda
 */
public class Sphere extends RadialGeometry {

    /**
     * Center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere from a center point and radius.
     *
     * @param center the center point
     * @param radius the sphere radius
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {

        return point.subtract(_center).normalize();
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Vector l;
        try {
            l = _center.subtract(ray.origin());
        } catch (IllegalArgumentException ignore) {
            // Ray origin is exactly the center of the sphere: only one intersection point ahead
            return List.of(ray.getPoint(_radius));
        }
        // tm: projection of l onto the ray direction (closest approach parameter)
        double tm = alignZero(l.dotProduct(ray.direction()));

        // dSquared: squared distance from the sphere center to the ray line
        double dSquared = alignZero(l.lengthSquared() - tm * tm);
        // No intersection when the ray misses the sphere entirely
        double thSquared = _radiusSquared - dSquared;
        if (alignZero(thSquared) <= 0) return null;

        // th: half-chord length from the projection point to each surface intersection
        double th = Math.sqrt(thSquared); // always positive

        // t1 < t2 (always!)
        double t2 = alignZero(tm + th);
        if (t2 <= 0) return null;

        double t1 = alignZero(tm - th);
        return t1 <= 0 ? List.of(ray.getPoint(t2)) : List.of(ray.getPoint(t1), ray.getPoint(t2));
    }

    @Override
    public String toString() {
        return "Sphere{center=" + _center + ", radius=" + _radius + "}";
    }
}
