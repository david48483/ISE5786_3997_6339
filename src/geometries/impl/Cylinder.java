package geometries.impl;

import primitives.Ray;

/**
 * Represents a finite cylinder in 3D space.
 * A cylinder extends a tube and adds a finite height.
 *
 * @author David &amp; Yehuda
 */
public class Cylinder extends Tube {

    /**
     * Height of the cylinder (distance along the axis from base to top).
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
    public String toString() {
        return "Cylinder{height=" + _height + ", base=" + super.toString() + "}";
    }
}
