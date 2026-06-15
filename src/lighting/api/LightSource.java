package lighting.api;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a light source in a 3D scene. A light source can be of various types, such as point light, directional light, or spotlight.
 * Each light source must be able to provide the direction of the light (as a vector) and the intensity of the light (as a color) at a given point in the scene.
 *
 * @author David &amp; Yehuda
 */
public interface LightSource {

    /**
     * Returns the direction vector from the light source to the given point in the scene.
     *
     * @param p the point in the scene for which to calculate the light direction
     * @return Vector representing the direction of the light from the light source to the given point
     */
    Vector getL(Point p);

    /**
     * Returns the intensity of the light at the given point in the scene as a color value.
     *
     * @param p the point in the scene for which to calculate the light intensity
     * @return Color representing the intensity of the light at the given point
     */

    Color getIntensity(Point p);

}
