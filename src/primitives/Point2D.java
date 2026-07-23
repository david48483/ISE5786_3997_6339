package primitives;

/**
 * A record representing a 2D point in space, used to define offsets for super-sampling.
 *
 * @param x the x-coordinate of the point
 * @param y the y-coordinate of the point
 * @author David &amp; Yehuda
 */
public record Point2D(double x, double y) {
    /**
     * Returns the x-coordinate of the point.
     * @return the x-coordinate
     */
    public double getX(){
        return this.x;
    }

    /**
     * Returns the y-coordinate of the point.
     * @return the y-coordinate
     */
    public double getY(){
        return this.y;
    }
}