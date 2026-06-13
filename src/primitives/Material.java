package primitives;

/**
 * Represents the material properties of a geometry, including its ambient reflection coefficient (kA).
 *
 * @author David &amp; Yehuda
 */

public class Material {

    /**
     * Default constructor for Material. Initializes the ambient reflection coefficient (kA) to 1, meaning that the material fully reflects ambient light by default.
     */
    public Material() {
    }

    /**
     * The ambient reflection coefficient (kA) of the material, which represents how much ambient light the material reflects.
     * By default, it is set to 1 for all color channels, meaning that the material fully reflects ambient light.
     */

    public Double3 kA = Double3.ONE;

    /**
     * Returns the ambient reflection coefficient (kA) of the material, which represents how much ambient light the material reflects.
     *
     * @param obj the new ambient reflection coefficient to set for the material
     * @return the material with the updated ambient reflection coefficient
     */
    public Material setKA(Double3 obj) {
        kA = obj;

        return this;
    }

    /**
     * Returns the ambient reflection coefficient (kA) of the material, which represents how much ambient light the material reflects.
     *
     * @param obj the new ambient reflection coefficient to set for the material, given as a single double value that will be applied to all color channels
     * @return the material with the updated ambient reflection coefficient
     */
    public Material setKA(double obj) {

        kA = new Double3(obj);
        return this;
    }

}
