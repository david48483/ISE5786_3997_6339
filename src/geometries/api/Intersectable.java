package geometries.api;

import primitives.Material;
import primitives.Point;
import primitives.Ray;

import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all geometric bodies that can be intersected by rays.
 * Defines the interface for finding intersection points between a ray and a geometric shape.
 *
 * @author David &amp; Yehuda
 */
public abstract class Intersectable {

    /**
     * Constructs a geometry. This constructor is empty because the base class does not have any fields to initialize.
     */
    protected Intersectable() {
    }

    /**
     * Finds all intersection points between a ray and this geometric shape.
     * Returns a list of intersection points in the order they are encountered along the ray direction.
     *
     * @param ray the ray to find intersections with
     * @return a list of intersection points, or null if no intersections exist
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Finds all intersections between a ray and this geometric shape, returning detailed information about each intersection.
     *
     * @param ray the ray to find intersections with
     * @return a list of Intersection objects, or null if no intersections exist
     */

    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);

    /**
     * Finds all intersections between a ray and this geometric shape, returning detailed information about each intersection.
     *
     * @param ray the ray to find intersections with
     * @return a list of Intersection objects, or null if no intersections exist
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Represents a single intersection between a ray and a geometry,
     * pairing the intersection point with the intersected geometry.
     */
    public static final class Intersection {

        /**
         * The intersection point in 3D space.
         */
        public final Point point;

        /**
         * The geometry that was intersected.
         */
        public final Geometry geometry;

        /**
         * The material of the intersected geometry, which can be used for shading calculations.
         */

        public final Material material;

        /**
         * Creates an intersection record for the given point and geometry.
         *
         * @param point    the intersection point
         * @param geometry the intersected geometry
         */
        public Intersection(Point point, Geometry geometry) {
            this.point = point;
            this.geometry = geometry;
            this.material = geometry != null ? geometry.getMaterial() : new Material();
        }

        @Override
        public String toString() {
            return "Intersection{point=" + point + ", geometry=" + geometry + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Intersection other)) return false;
            return this.point.equals(other.point) && this.geometry == other.geometry;
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, System.identityHashCode(geometry));
        }
    }
}
