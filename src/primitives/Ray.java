package primitives;

import geometries.api.Intersectable.Intersection;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a ray (half-line) in 3D space by an origin point and a direction vector.
 * The direction vector is stored in normalized form.
 *
 * @author David &amp; Yehuda
 */
public class Ray {

    /**
     * Small delta value used to offset rays to avoid self-intersection.
     */
    private static final double DELTA = 0.1;
    /**
     * Origin point of the ray.
     */
    private final Point _origin;

    /**
     * Normalized direction vector of the ray.
     */
    private final Vector _direction;

    /**
     * Creates a ray from an origin point and a direction vector.
     * The direction vector is normalized before storage.
     *
     * @param origin    starting point of the ray
     * @param direction direction of the ray (does not have to be normalized)
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Creates a ray from a head point, a direction vector, and a normal vector.
     *
     * @param head      starting point of the ray
     * @param direction direction of the ray (does not have to be normalized)
     * @param normal    normal vector used to offset the ray origin to avoid self-intersection
     */
    public Ray(Point head, Vector direction, Vector normal) {
        _direction = direction.normalize();

        // alignment/cleaning of floating point errors
        double nv = alignZero(normal.dotProduct(_direction));

        // if nv is not zero, shift along the normal
        if (!isZero(nv)) {
            Vector delta = normal.scale(nv > 0 ? DELTA : -DELTA);
            _origin = head.add(delta);
        } else {
            // if the ray is tangent to the surface (orthogonal to normal), do not shift
            _origin = head;
        }
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
     * Computes a point on the ray at parameter {@code t}.
     *
     * @param t signed distance along the ray direction
     * @return point at parameter {@code t}
     * @throws IllegalArgumentException if {@code t} yields a zero displacement vector
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("t results in a zero displacement vector", e);
        }

    }

    /**
     * Finds the closest point to the ray origin from a given list.
     *
     * @param points list of candidate points
     * @return closest point to the ray origin, or {@code null} if the list is empty
     */

    public Point findClosestPoint(List<Point> points) {
        return points == null ? null
                : findClosestIntersection(
                points.stream()
                        .map(point -> new Intersection(point, null))
                        .toList()
        ).point;
    }

    /**
     * Finds the closest intersection to the ray origin from a given list of intersections.
     *
     * @param intersections list of candidate intersections
     * @return closest intersection to the ray origin, or {@code null} if the list is empty
     */

    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }
        double minDistance = Double.POSITIVE_INFINITY;
        Intersection closestIntersection = null;
        for (Intersection i : intersections) {
            double currentDistance = i.point.distanceSquared(_origin);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestIntersection = i;

            }
        }
        return closestIntersection;
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
