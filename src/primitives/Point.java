package primitives;

/**
 * Represents a point in a 3D Cartesian coordinate system.
 * Coordinates are stored as an immutable {@link Double3} tuple.
 *
 * @author David &amp; Yehuda
 */

public class Point {
    /**
     * Coordinate tuple (x, y, z) of the point.
     */
    protected final Double3 _xyz;

    /**
     * Constant for the origin point (0,0,0).
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point from a {@link Double3} coordinate tuple.
     *
     * @param xyz the coordinate tuple
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    /**
     * Constructs a point from three coordinate values.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Subtracts another point from this point.
     *
     * @param other the point to subtract
     * @return the vector from {@code other} to this point
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Translates this point by a vector.
     *
     * @param vector the translation vector
     * @return a new translated point
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Computes the squared Euclidean distance to another point.
     *
     * @param other the other point
     * @return the squared distance between the points
     */
    public double distanceSquared(Point other) {
        double dx = _xyz._d1() - other._xyz._d1();
        double dy = _xyz._d2() - other._xyz._d2();
        double dz = _xyz._d3() - other._xyz._d3();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Computes the Euclidean distance to another point.
     *
     * @param other the other point
     * @return the distance between the points
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Returns the X coordinate of the point.
     *
     * @return the X coordinate
     */
    public double getX() {
        return _xyz._d1();
    }

    /**
     * Returns the Y coordinate of the point.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return _xyz._d2();
    }

    /**
     * Returns the Z coordinate of the point.
     *
     * @return the Z coordinate
     */
    public double getZ() {
        return _xyz._d3();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && _xyz.equals(((Point) obj)._xyz);
    }

    @Override
    public int hashCode() {
        return _xyz.hashCode();
    }

    @Override
    public String toString() {
        return "Point" + _xyz;
    }
}
