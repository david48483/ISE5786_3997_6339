package geometries.impl;

import primitives.Ray;

/**
 * Represents a finite cylinder in 3D space.
 * A cylinder extends a tube and adds a finite height.
 */
public class Cylinder extends Tube {

    /**
     * Height of the cylinder.
     */
    private final double _height;

    /**
     * Constructs a cylinder from radius, axis ray and height.
     *
     * @param radius the cylinder radius
     * @param axis   the cylinder axis ray
     * @param height the cylinder height
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        _height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Cylinder other = (Cylinder) obj;
        return Double.compare(_height, other._height) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Double.hashCode(_height);
    }

    @Override
    public String toString() {
        return "Cylinder{height=" + _height + ", base=" + super.toString() + "}";
    }
}
