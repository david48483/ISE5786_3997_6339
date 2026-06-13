package geometries.api;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract base class for all geometric bodies in the scene.
 * Every geometry must be able to return its normal vector at a given point.
 *
 * @author David &amp; Yehuda
 */
public abstract class Geometry extends Intersectable {

    /**
     * Constructs a geometry. This constructor is empty because the base class does not have any fields to initialize.
     */
    protected Geometry() {
        // No initialization needed for the base class
    }

    /**
     * The emission color of the geometry, which represents the color that the geometry emits as light.
     * By default, it is set to black, meaning that the geometry does not emit any light.
     */
    private Color _emission = Color.BLACK;

    /**
     * The material of the geometry, which contains properties that affect how the geometry interacts with light, such as its ambient reflection coefficient (kA).
     * By default, it is initialized to a new Material instance with default properties.
     */
    private Material _material = new Material();

    /**
     * Returns the emission color of the geometry.
     *
     * @return the emission color of the geometry
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Returns the material of the geometry, which contains properties that affect how the geometry interacts with light, such as its ambient reflection coefficient (kA).
     *
     * @return the material of the geometry
     */

    public Material getMaterial() {
        return _material;
    }

    /**
     * Sets the emission color of the geometry and returns the geometry itself for method chaining.
     *
     * @param color the new emission color to set for the geometry
     * @return the geometry with the updated emission color
     */
    public Geometry setEmission(Color color) {
        _emission = color;
        return this;
    }

    /**
     * Sets the material of the geometry and returns the geometry itself for method chaining.
     *
     * @param material the new material to set for the geometry
     * @return the geometry with the updated material
     */

    public Geometry setMaterial(Material material) {
        _material = material;
        return this;
    }

    /**
     * Returns the normal vector to the geometry at the given point.
     *
     * @param point a point on the surface of the geometry
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);

}
