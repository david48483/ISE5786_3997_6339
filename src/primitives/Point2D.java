package primitives;

/**
 * Represents a point in 2D space, used to define offsets for super-sampling.
 *
 * @author David  &amp; Yehuda
 */
public class Point2D {
    /**
     * The x-coordinate of the point in 2D space.
     */
    private final double x;

    /**
     * The y-coordinate of the point in 2D space.
     */
    private final double y;

    /**
     * Creates a new Point2D instance with the specified x and y coordinates.
     *
     * @param x argument representing the x-coordinate of the point
     * @param y argument representing the y-coordinate of the point
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the point.
     *
     * @return the x-coordinate of the point
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the point.
     *
     * @return the y-coordinate of the point
     */
    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}