package geometries.impl;

import geometries.api.Intersectable;
import primitives.AABB;
import primitives.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

    protected List<Intersection> calcIntersectionsHelper22(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        //for (Intersectable geometry : _geometries) {
        // שינוי קריטי מס' 1: שימוש בלולאת אינדקס כדי למנוע יצירת Iterator בזיכרון
        int size = _geometries.size();
        for (int i = 0; i < size; i++) {
            Intersectable geometry = _geometries.get(i);

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
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;

        int size = _geometries.size();
        for (int i = 0; i < size; i++) {
            Intersectable geometry = _geometries.get(i);
            List<Intersection> intersections = geometry.calcIntersections(ray, maxDistance);
            if (intersections != null) {
                if (result == null) {
                    result = intersections;
                } else {
                    if (!(result instanceof java.util.ArrayList)) {
                        result = new ArrayList<>(result);
                    }
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

            if (combinedBox == null) {
                combinedBox = geoBox;
            } else {
                combinedBox = combinedBox.union(geoBox);
            }
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
        List<Intersectable> flatList = flattenAsList();
        return new Geometries(flatList.toArray(new Intersectable[0]));
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
        if (current instanceof Geometries) {
            Geometries group = (Geometries) current;
            for (Intersectable child : group._geometries) {
                flattenHelper(child, flatList); // קריאה רקורסיבית פנימה
            }
        } else {
            flatList.add(current); // עצירה והוספה - הגענו לגוף בסיסי
        }
    }

    /**
     * Rebuilds the Bounding Volume Hierarchy (BVH) tree for the current geometries.
     * This method flattens any existing hierarchical structure into a single list
     * of basic shapes and then delegates the construction of an optimized SAH
     * (Surface Area Heuristic) tree to the {@code BvhBuilder}.
     */
    public void buildBvhTree() {
        // 1. קבלת רשימה שטוחה באמצעות המתודה החדשה
        List<Intersectable> flatList = flattenAsList();

        // 2. בניית עץ ה-SAH האופטימלי
        Geometries optimizedTree = BvhBuilder.buildSahTree(flatList);

        // 3. ניקוי המצב הפנימי הקיים
        this._geometries.clear();

        // 4. הוספת הענפים החדשים פנימה
        for (Intersectable geo : optimizedTree._geometries) {
            this.add(geo);
        }
    }

}
