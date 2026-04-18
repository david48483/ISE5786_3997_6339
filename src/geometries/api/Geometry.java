package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometric bodies in the scene.
 * Every geometry must be able to return its normal vector at a given point.
 *
 * @author David &amp; Yehuda
 */
public abstract class Geometry {

    /**
     * Constructs a geometry. This constructor is empty because the base class does not have any fields to initialize.
     */
    public Geometry() {
        // No initialization needed for the base class
    }

    /**
     * Returns the normal vector to the geometry at the given point.
     *
     * @param point a point on the surface of the geometry
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);
}
