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

    public Double3 kS = Double3.ZERO;
    public Double3 kD = Double3.ZERO;
    public int nShininess;

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

    public Material setKS(Double3 obj) {
        kS = obj;

        return this;
    }

    public Material setKS(double obj) {
        return setKS(new Double3(obj));
    }

    public Material setKD(Double3 obj) {
        kD = obj;

        return this;
    }

    public Material setKD(double obj) {
        return setKD(new Double3(obj));
    }

    public Material setShininess(int n) {
        nShininess = n;
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
