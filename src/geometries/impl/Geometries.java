package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Arrays;
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
    }

    /**
     * Adds one or more intersectable geometries to this composite.
     *
     * @param geometries one or more intersectable geometries to add
     */
    public void add(Intersectable... geometries) {
        _geometries.addAll(Arrays.asList(geometries));
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;
        for (Intersectable geometry : _geometries) {
            List<Point> points = geometry.findIntersections(ray);
            if (points != null) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(points);
            }
        }
        return result;
    }
}
