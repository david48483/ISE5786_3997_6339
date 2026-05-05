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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sphere other = (Sphere) obj;
        return Double.compare(_radius, other._radius) == 0 && _center.equals(other._center);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // Ray origin is exactly the center of the sphere: only one intersection point ahead
            return List.of(ray.getPoint(_radius));
        }
        // tm: projection of l onto the ray direction (closest approach parameter)
        double tm = alignZero(l.dotProduct(v));

        // dSquared: squared distance from the sphere center to the ray line
        double dSquared = alignZero(l.lengthSquared() - tm * tm);
        double rSquared = _radius * _radius;

        // No intersection when the ray misses the sphere entirely
        if (alignZero(dSquared - rSquared) >= 0) return null;

        // th: half-chord length from the projection point to each surface intersection
        double th = alignZero(Math.sqrt(rSquared - dSquared));

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t2 <= 0) return null;

        if (t1 <= 0) return List.of(ray.getPoint(t2));

        return List.of(ray.getPoint(t1), ray.getPoint(t2));

    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(_radius) + _center.hashCode();
    }

    @Override
    public String toString() {
        return "Sphere{center=" + _center + ", radius=" + _radius + "}";
    }
}
