package primitives;

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


}
