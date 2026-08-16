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

    }

    /**
     * Adds one or more intersectable geometries to this composite.
     *
     * @param geometries one or more intersectable geometries to add
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(_geometries, geometries);
        resetBoundingBox();//if you add obj to list you must update the box
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        int size = _geometries.size();
        for (Intersectable geometry : _geometries) {
            List<Intersection> intersections = geometry.calcIntersections(ray, maxDistance);
            if (intersections != null) {
                if (result == null) {
                    result = new ArrayList<>(intersections);
                } else {
                    result.addAll(intersections);
                }
            }
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

            combinedBox = (combinedBox == null) ? geoBox : combinedBox.union(geoBox);
        }

        return combinedBox;
    }

    /**
     * Flattens the hierarchical geometries structure into a single-level flat Geometries object.
     * This is required for testing CBR on a flat scene.
     *
     * @return a new Geometries object containing all basic intersectables in a flat list
     */
    public Geometries flatten() {
        Geometries flatContainer = new Geometries();
        flattenHelper(this, flatContainer._geometries);
        return flatContainer;
    }

    /**
     * Flattens the hierarchy and returns it as a List.
     * Useful for feeding the SAH BVH builder.
     *
     * @return a list of all basic geometric shapes
     */
    public List<Intersectable> flattenAsList() {
        List<Intersectable> flatList = new ArrayList<>();
        flattenHelper(this, flatList);
        return flatList;
    }

    /**
     * A recursive helper method to extract all basic geometries from the hierarchy.
     *
     * @param current  the current intersectable being checked
     * @param flatList the list accumulating the basic geometries
     */
    private void flattenHelper(Intersectable current, List<Intersectable> flatList) {
        if (current instanceof Geometries group) {
            for (Intersectable child : group._geometries) {
                flattenHelper(child, flatList);
            }
        } else {
            flatList.add(current);
        }
    }

    /**
     * Rebuilds the Bounding Volume Hierarchy (BVH) tree for the current geometries.
     * This method flattens any existing hierarchical structure into a single list
     * of basic shapes and then delegates the construction of an optimized SAH
     * (Surface Area Heuristic) tree to the {@code BvhBuilder}.
     */
    public void buildBvhTree() {
        List<Intersectable> flatList = flattenAsList();

        Geometries optimizedTree = BvhBuilder.buildSahTree(flatList);

        _geometries.clear();
        _geometries.addAll(optimizedTree._geometries);
    }

}
