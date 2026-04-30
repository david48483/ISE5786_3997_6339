package geometries.impl;

import java.util.ArrayList;
import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space, defined by a center point and a radius.
 *
 * @author David &amp; Yehuda
 */
public class Sphere extends RadialGeometry {

    /**
     * Center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere from a center point and radius.
     *
     * @param center the center point
     * @param radius the sphere radius
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {

        return point.subtract(_center).normalize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sphere other = (Sphere) obj;
        return Double.compare(_radius, other._radius) == 0 && _center.equals(other._center);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector l;
        try {
            l = _center.subtract(p0);
        } catch (IllegalArgumentException ignore) {
            // אם ראשית הקרן היא בדיוק מרכז הכדור
            return List.of(ray.origin().add(v.scale(_radius)));
        }

        double tm = alignZero(l.dotProduct(v));

        // חישוב המרחק בריבוע ממרכז הכדור לקו הקרן (d^2)
        double dSquared = alignZero(l.lengthSquared() - tm * tm);
        double rSquared = _radius * _radius;

        // אם המרחק גדול מהרדיוס, אין חיתוך
        if (dSquared >= rSquared) return null;

        // th הוא המרחק מנקודת ההיטל לנקודות החיתוך על פני הכדור
        double th = alignZero(Math.sqrt(rSquared - dSquared));

        List<Point> intersections = new ArrayList<>(2);

        double t1 = alignZero(tm - th);
        double t2 = alignZero(tm + th);

        if (t1 > 0) intersections.add(ray.origin().add(v.scale(t1)));
        if (t2 > 0) intersections.add(ray.origin().add(v.scale(t2)));

        return intersections.isEmpty() ? null : intersections;

    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(_radius) + _center.hashCode();
    }

    @Override
    public String toString() {
        return "Sphere{center=" + _center + ", radius=" + _radius + "}";
    }
}
