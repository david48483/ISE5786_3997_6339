package primitives;

/**
 * Axis-Aligned Bounding Box (AABB).
 * <p>
 * A simple bounding volume aligned with the world axes, used primarily for
 * broad-phase intersection tests and BVH acceleration. The box is defined by
 * two corner points: {@code min} (smallest x/y/z) and {@code max} (largest x/y/z).
 */
public class AABB {
    /**
     * Minimum coordinates point (bottom-left-back)
     */
    private final Point min;

    /**
     * Maximum coordinates point (top-right-front)
     */
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

        double ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();
        double dx = dir.getX(), dy = dir.getY(), dz = dir.getZ();
        double minX = min.getX(), minY = min.getY(), minZ = min.getZ();
        double maxX = max.getX(), maxY = max.getY(), maxZ = max.getZ();

        double tMin = 0.0;
        double tMax = maxDistance;

        // ציר X
        if (dx != 0) {
            double invDx = 1.0 / dx;
            double t1 = (minX - ox) * invDx;
            double t2 = (maxX - ox) * invDx;
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
            if (tMin > tMax) return false;
        } else if (ox < minX || ox > maxX) {
            return false;
        }

        // ציר Y
        if (dy != 0) {
            double invDy = 1.0 / dy;
            double t1 = (minY - oy) * invDy;
            double t2 = (maxY - oy) * invDy;
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
            if (tMin > tMax) return false;
        } else if (oy < minY || oy > maxY) {
            return false;
        }

        // ציר Z
        if (dz != 0) {
            double invDz = 1.0 / dz;
            double t1 = (minZ - oz) * invDz;
            double t2 = (maxZ - oz) * invDz;
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
            if (tMin > tMax) return false;
        } else if (oz < minZ || oz > maxZ) {
            return false;
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
