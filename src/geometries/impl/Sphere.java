package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
        return null;
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
