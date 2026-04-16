package geometries.impl;

import geometries.api.Geometry;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a plane in 3D space, defined by a point and a normal vector.
 *
 * @author David & Yheuda
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
     * At this stage, one point is stored as a reference and the normal
     * is intentionally initialized to {@code null}.
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Plane other = (Plane) obj;
        return _point.equals(other._point) && _normal.equals(other._normal);
    }

    @Override
    public int hashCode() {
        return 31 * _point.hashCode() + _normal.hashCode();
    }

    @Override
    public String toString() {
        return "Plane{point=" + _point + ", normal=" + _normal + "}";
    }
}
