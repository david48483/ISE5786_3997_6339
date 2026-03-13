package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometric bodies in the scene.
 * Every geometry must be able to return its normal vector at a given point.
 */
public abstract class Geometry {
    /**
     * Default constructor for base geometry type.
     */
    protected Geometry() {
    }

    /**
     * Returns the normal vector to the geometry at the given point.
     *
     * @param point a point on the surface of the geometry
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);
}
