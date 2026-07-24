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

            if (geoBox == null) {
                return null;
            }

            if (combinedBox == null) {
                combinedBox = geoBox;
            } else {
                combinedBox = combinedBox.union(geoBox);
            }
        }

        return combinedBox;
    }

    /**
     * Rebuilds the Bounding Volume Hierarchy (BVH) tree for the current geometries.
     * This method flattens any existing hierarchical structure into a single list
     * of basic shapes and then delegates the construction of an optimized SAH
     * (Surface Area Heuristic) tree to the {@code BvhBuilder}.
     */
    public void buildBvhTree() {
        // 1. Flatten the existing tree into a single list of basic geometries
        List<Intersectable> flatList = flattenTree(this);

        // 2. Build the optimized SAH tree (assumes BvhBuilder is in the same package)
        Geometries optimizedTree = BvhBuilder.buildSahTree(flatList);

        // 3. Clear the current internal list of geometries
        this._geometries.clear();

        // 4. Add the optimized branches back to this Geometries object
        for (Intersectable geo : optimizedTree._geometries) {
            this.add(geo);
        }
    }

    /**
     * Helper method to recursively flatten a hierarchical Geometries structure
     * into a single list of basic intersectable shapes (e.g., Sphere, Triangle, Plane).
     *
     * @param geometries the root of the hierarchy to flatten
     * @return a flat list containing only basic geometries
     */
    private List<Intersectable> flattenTree(Geometries geometries) {
        List<Intersectable> flatList = new java.util.ArrayList<>();

        // Iterate through all direct children of the current geometries branch
        for (Intersectable geo : geometries._geometries) {
            if (geo instanceof Geometries) {
                // If the child is also a Geometries collection, flatten it recursively
                flatList.addAll(flattenTree((Geometries) geo));
            } else {
                // If it is a basic shape, add it directly to the list
                flatList.add(geo);
            }
        }

        return flatList;
    }
}
