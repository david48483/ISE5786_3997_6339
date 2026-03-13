package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents an infinite tube in 3D space.
 * The tube is defined by a central axis ray and a radius.
 */
public class Tube extends RadialGeometry {

    /**
     * Axis ray of the tube.
     */
    private final Ray _axis;

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

    /**
     * Returns the tube axis ray.
     *
     * @return axis ray
     */
    protected Ray axis() {
        return _axis;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tube other = (Tube) obj;
        return Double.compare(radius(), other.radius()) == 0 && axis().equals(other.axis());
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(radius()) + axis().hashCode();
    }

    @Override
    public String toString() {
        return "Tube{radius=" + radius() + ", axis=" + axis() + "}";
    }
}
