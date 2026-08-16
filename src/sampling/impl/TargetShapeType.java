package sampling.impl;

/**
 * Types of geometric target shapes used to limit or filter generated sample points.
 *
 * @author David &amp; Yehuda
 */
public enum TargetShapeType {

    /**
     * Circular target area with the given size treated as the diameter.
     */
    CIRCLE,

    /**
     * Square target area with the given size treated as the side length.
     */
    SQUARE
}
