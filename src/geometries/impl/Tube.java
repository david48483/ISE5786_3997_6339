package geometries.impl;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

/**
 * Represents an infinite tube in 3D space.
 * The tube is defined by a central axis ray and a radius.
 *
 * @author David &amp; Yehuda
 */
public class Tube extends RadialGeometry {

    /**
     * Axis ray of the tube (central axis with normalized direction).
     */
    protected final Ray _axis;

    /**
     * Constructs a tube with a radius and an axis ray.
     *
     * @param radius the tube radius
     * @param axis   the axis ray of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {

        Vector u = point.subtract(_axis.origin());
        double t = u.dotProduct(_axis.direction());
        if (Util.isZero(t)) {
            return point.subtract(_axis.origin()).normalize();
        }
        Point o = _axis.origin().add(_axis.direction().scale(t));
        return point.subtract(o).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return null;
    }



    @Override
    public String toString() {
        return "Tube{radius=" + _radius + ", axis=" + _axis + "}";
    }
}
