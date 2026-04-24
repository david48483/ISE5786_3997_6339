package geometries.api;

import java.util.List;

import primitives.Point;
import primitives.Ray;

/**
 * Abstract base class for all geometric bodies that can be intersected by rays.
 * Defines the interface for finding intersection points between a ray and a geometric shape.
 *
 * @author David &amp; Yehuda
 */
public abstract class Intersectable {
    /**
     * Finds all intersection points between a ray and this geometric shape.
     * Returns a list of intersection points in the order they are encountered along the ray direction.
     *
     * @param ray the ray to find intersections with
     * @return a list of intersection points, or null if no intersections exist
     */
    public abstract List<Point> findIntersections(Ray ray);
}
