package primitives;

/**
 * Axis-Aligned Bounding Box (AABB).
 * <p>
 * A simple bounding volume aligned with the world axes, used primarily for
 * broad-phase intersection tests and BVH acceleration. The box is defined by
 * two corner points: {@code min} (smallest x/y/z) and {@code max} (largest x/y/z).
 */
public class AABB {
    /** Minimum coordinates point (bottom-left-back) */
    private final Point min;

    /** Maximum coordinates point (top-right-front) */
    private final Point max;

    /**
     * Constructs an AABB with the given minimum and maximum points.
     *
     * @param min minimum coordinates point
     * @param max maximum coordinates point
     */
    public AABB(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Returns the minimum point of the bounding box.
     *
     * @return min point
     */
    public Point getMin() {
        return min;
    }

    /**
     * Returns the maximum point of the bounding box.
     *
     * @return max point
     */
    public Point getMax() {
        return max;
    }

    /**
     * Performs a fast boolean ray-AABB intersection check up to maxDistance.
     *
     * @param ray         the ray to test against the box
     * @param maxDistance maximum distance along the ray to consider
     * @return true if the ray intersects the box within [0, maxDistance], false otherwise
     */
    public boolean intersects(Ray ray, double maxDistance) {
        Point origin = ray.origin();
        Vector dir = ray.direction();

        double[] o = { origin._xyz._d1(), origin._xyz._d2(), origin._xyz._d3() };
        double[] d = { dir._xyz._d1(), dir._xyz._d2(), dir._xyz._d3() };
        double[] bMin = { min._xyz._d1(), min._xyz._d2(), min._xyz._d3() };
        double[] bMax = { max._xyz._d1(), max._xyz._d2(), max._xyz._d3() };

        double tMin = 0.0;
        double tMax = maxDistance;

        // מעבר על שלושת הצירים: X=0, Y=1, Z=2
        for (int i = 0; i < 3; i++) {
            if (d[i] != 0) {
                double invDir = 1.0 / d[i];
                double t1 = (bMin[i] - o[i]) * invDir;
                double t2 = (bMax[i] - o[i]) * invDir;

                tMin = Math.max(tMin, Math.min(t1, t2));
                tMax = Math.min(tMax, Math.max(t1, t2));

                // Early exit - פספוס באחד הצירים פוסל את החיתוך מידית
                if (tMin > tMax) return false;
            } else if (o[i] < bMin[i] || o[i] > bMax[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Unites this bounding box with another bounding box to create a new enclosing AABB.
     * It compares the minimum and maximum coordinates of both boxes and creates a new box
     * that encompasses both.
     *
     * @param other the other AABB to merge with
     * @return a new AABB enclosing both bounding boxes, or this box if other is null
     */
    public AABB union(AABB other) {
        if (other == null) {
            return this;
        }

        // Find the absolute minimums between both boxes
        double newMinX = Math.min(this.min.getX(), other.min.getX());
        double newMinY = Math.min(this.min.getY(), other.min.getY());
        double newMinZ = Math.min(this.min.getZ(), other.min.getZ());

        // Find the absolute maximums between both boxes
        double newMaxX = Math.max(this.max.getX(), other.max.getX());
        double newMaxY = Math.max(this.max.getY(), other.max.getY());
        double newMaxZ = Math.max(this.max.getZ(), other.max.getZ());

        return new AABB(
                new Point(newMinX, newMinY, newMinZ),
                new Point(newMaxX, newMaxY, newMaxZ)
        );
    }


}
