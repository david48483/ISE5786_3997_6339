package geometries.impl;

import geometries.api.Intersectable;
import primitives.AABB;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite geometry that groups any number of {@link Intersectable} objects.
 * Implements the Composite design pattern so a collection of geometries
 * can be treated uniformly as a single intersectable entity.
 *
 * @author David &amp; Yehuda
 */
public class Geometries extends Intersectable {

    /**
     * The collection of intersect geometries managed by this composite.
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Constructs an empty composite with no geometries.
     */
    public Geometries() {
    }

    /**
     * Constructs a composite and immediately adds the given geometries.
     *
     * @param geometries one or more intersectable geometries to add
     */
    public Geometries(Intersectable... geometries) {

        add(geometries);
        resetBoundingBox();
    }

    /**
     * Adds one or more intersectable geometries to this composite.
     *
     * @param geometries one or more intersectable geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        for (Intersectable geometry : _geometries) {

            List<Intersection> intersections = geometry.calcIntersections(ray, maxDistance);
            if (intersections != null)
                if (result == null)
                    result = new ArrayList<>(intersections);
                else
                    result.addAll(intersections);

        }
        return result;

    }

    @Override
    protected AABB setBoundingBoxHelper() {
        if (_geometries.isEmpty()) {
            return null;
        }

        AABB combinedBox = null;

        for (Intersectable geo : _geometries) {
            AABB geoBox = geo.getBoundingBox();

            // Ignore infinite geometries that don't have a bounding box (like Plane)
            if (geoBox != null) {
                if (combinedBox == null) {
                    combinedBox = geoBox;
                } else {
                    combinedBox = combinedBox.union(geoBox);
                }
            }
        }

        return combinedBox;
    }
}
