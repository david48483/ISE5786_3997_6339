package primitives;

import java.util.List;

/**
 * Represents a ray (half-line) in 3D space, defined by an origin point and a direction vector.
 * The direction vector is always stored normalized.
 *
 * @author David &amp; Yehuda
 */
public class Ray {

    /**
     * The origin point of the ray.
     */
    private final Point _origin;

    /**
     * The normalized direction vector of the ray.
     */
    private final Vector _direction;

    /**
     * Constructs a ray from an origin point and a direction vector.
     * The direction vector is normalized before being stored.
     *
     * @param origin    the starting point of the ray
     * @param direction the direction of the ray (need not be normalized)
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Returns the origin point of the ray.
     *
     * @return the origin point
     */
    public Point origin() {
        return _origin;
    }

    /**
     * Returns the normalized direction vector of the ray.
     *
     * @return the direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Calculates the point along the ray at a given parameter t from the origin.
     *
     * @param t the signed distance along the ray direction
     * @return the point at parameter t along the ray
     * @throws IllegalArgumentException if t produces a zero displacement vector
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("t results in a zero displacement vector", e);
        }

    }

    /**
     * Finds the point in the given list that is closest to the ray's origin.
     *
     * @param points the list of points to search
     * @return the point closest to the ray's origin, or null if the list is empty
     */
    public Point findClosestPoint(List<Point> points) {

        Double minDistance = Double.POSITIVE_INFINITY;
        Point closestPoint = null;
        for (Point p : points) {
            Double currentDistance = p.distanceSquared(_origin);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestPoint = p;

            }
        }
        return closestPoint;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ray other = (Ray) obj;
        return _origin.equals(other._origin) && _direction.equals(other._direction);
    }

    @Override
    public int hashCode() {
        return 31 * _origin.hashCode() + _direction.hashCode();
    }

    @Override
    public String toString() {
        return "Ray{origin=" + _origin + ", direction=" + _direction + "}";
    }
}
