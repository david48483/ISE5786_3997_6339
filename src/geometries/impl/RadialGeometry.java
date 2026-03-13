package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract base class for all radial geometric bodies (bodies defined by a radius).
 */
public abstract class RadialGeometry extends Geometry {

    /**
     * The radius of the geometry.
     */
    private final double _radius;

    /**
     * Constructs a radial geometry with the given radius.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        _radius = radius;
    }

    /**
     * Returns the radius value.
     *
     * @return geometry radius
     */
    protected double radius() {
        return _radius;
    }
}
