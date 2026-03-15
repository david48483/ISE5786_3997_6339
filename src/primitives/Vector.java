package primitives;

import static primitives.Util.isZero;

/**
 * Represents a 3D vector with direction and magnitude.
 * Inherits coordinate storage from {@link Point}.
 * A zero vector is forbidden and will cause an {@link IllegalArgumentException}.
 */
public class Vector extends Point {

    /**
     * Unit vector along the X axis.
     */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    /**
     * Unit vector along the Y axis.
     */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    /**
     * Unit vector along the Z axis.
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructs a vector from three double components.
     *
     * @param x the X component
     * @param y the Y component
     * @param z the Z component
     * @throws IllegalArgumentException if the resulting vector is zero
     */
    public Vector(double x, double y, double z) {
        super(validateNonZero(new Double3(x, y, z)));
    }

    /**
     * Constructs a vector from a {@link Double3} object.
     *
     * @param xyz the coordinate container
     * @throws IllegalArgumentException if the resulting vector is zero
     */
    public Vector(Double3 xyz) {
        super(validateNonZero(xyz));
    }

    /**
     * Validates that the given {@link Double3} is not a zero vector.
     *
     * @param xyz the coordinate container to validate
     * @return the same {@code xyz} if valid
     * @throws IllegalArgumentException if all components are effectively zero
     */
    private static Double3 validateNonZero(Double3 xyz) {
        if (isZero(xyz._d1()) && isZero(xyz._d2()) && isZero(xyz._d3()))
            throw new IllegalArgumentException("Zero vector is not allowed");
        return xyz;
    }

    /**
     * Adds another vector to this vector.
     *
     * @param other the vector to add
     * @return a new vector representing the sum
     */
    public Vector add(Vector other) {
        return new Vector(_xyz.add(other._xyz));
    }

    /**
     * Scales this vector by a scalar factor.
     *
     * @param scalar the scaling factor
     * @return a new scaled vector
     * @throws IllegalArgumentException if scaling creates a zero vector
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Computes the dot product of this vector with another.
     *
     * @param other the other vector
     * @return the scalar dot product
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1()
                + _xyz._d2() * other._xyz._d2()
                + _xyz._d3() * other._xyz._d3();
    }

    /**
     * Computes the cross product of this vector with another.
     *
     * @param other the other vector
     * @return a new vector perpendicular to both operands
     * @throws IllegalArgumentException if the result is a zero vector (parallel vectors)
     */
    public Vector crossProduct(Vector other) {
        double x = _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2();
        double y = _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3();
        double z = _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1();
        return new Vector(x, y, z);
    }

    /**
     * Returns the squared length (magnitude) of this vector.
     *
     * @return the squared length
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Returns the length (magnitude) of this vector.
     *
     * @return the length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Returns a normalized (unit length) version of this vector.
     *
     * @return a new unit vector in the same direction
     */
    public Vector normalize() {
        return new Vector(_xyz.divide(length()));
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && _xyz.equals(((Vector) obj)._xyz);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Vector" + _xyz;
    }
}
