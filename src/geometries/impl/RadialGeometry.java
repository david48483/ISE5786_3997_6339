package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract base class for all radial geometric bodies (bodies defined by a radius).
 * @author David & Yheuda
 */
public abstract class RadialGeometry extends Geometry {

    /**
     * The radius of the geometry.
     */
    protected final double _radius;

    /**
     * The squared radius of the geometry.
     */
    protected final double _radiusSquared;

    /**
     * Constructs a radial geometry with the given radius.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        _radius = radius;
        _radiusSquared = radius * radius;
    }
}
