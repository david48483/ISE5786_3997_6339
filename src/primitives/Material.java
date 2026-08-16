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
     * Specular reflection coefficient.
     */
    public Double3 kS = Double3.ZERO;
    /**
     * Diffuse reflection coefficient.
     */
    public Double3 kD = Double3.ZERO;

    /**
     * Transmission coefficient
     */
    public Double3 kT = Double3.ZERO;

    /**
     * Reflection coefficient
     */
    public Double3 kR = Double3.ZERO;
    /**
     * Shininess exponent for specular highlights.
     */
    public int nShininess;

    /**
     * Glossy radius for reflection blur (0 means perfect mirror).
     */
    public double kG = 0.0;

    /**
     * Blurry radius for refraction blur (0 means perfect glass).
     */
    public double kB = 0.0;

    /**
     * Sets the glossy reflection radius.
     *
     * @param kG the radius of the beam for glossy reflection
     * @return this material instance
     */
    public Material setKG(double kG) {
        this.kG = kG;
        return this;
    }

    /**
     * Sets the diffusive glass (refraction) radius.
     *
     * @param kB the radius of the beam for diffusive refraction
     * @return this material instance
     */
    public Material setKB(double kB) {
        this.kB = kB;
        return this;
    }

    /**
     * Sets the ambient reflection coefficient.
     *
     * @param obj the new ambient reflection coefficient to set for the material
     * @return the material with the updated ambient reflection coefficient
     */
    public Material setKA(Double3 obj) {
        kA = obj;
        return this;
    }

    /**
     * Sets the specular reflection coefficient.
     *
     * @param obj specular reflection coefficient
     * @return this material instance
     */
    public Material setKS(Double3 obj) {
        kS = obj;

        return this;
    }

    /**
     * Sets the specular reflection coefficient uniformly for all channels.
     *
     * @param obj uniform specular reflection coefficient
     * @return this material instance
     */
    public Material setKS(double obj) {
        return setKS(new Double3(obj));
    }

    /**
     * Sets the diffuse reflection coefficient.
     *
     * @param obj diffuse reflection coefficient
     * @return this material instance
     */
    public Material setKD(Double3 obj) {
        kD = obj;

        return this;
    }

    /**
     * Sets the diffuse reflection coefficient uniformly for all channels.
     *
     * @param obj uniform diffuse reflection coefficient
     * @return this material instance
     */
    public Material setKD(double obj) {
        return setKD(new Double3(obj));
    }

    /**
     * Sets the transmission coefficient.
     *
     * @param obj transmission coefficient
     * @return this material instance
     */
    public Material setKT(Double3 obj) {
        kT = obj;
        return this;
    }

    /**
     * Sets the transmission coefficient uniformly for all channels.
     *
     * @param obj transmission coefficient
     * @return this material instance
     */
    public Material setKT(double obj) {
        return setKT(new Double3(obj));
    }

    /**
     * Sets the reflection coefficient uniformly for all channels.
     *
     * @param obj reflection coefficient
     * @return this material instance
     */
    public Material setKR(double obj) {
        return setKR(new Double3(obj));
    }

    /**
     * Sets the reflection coefficient.
     *
     * @param obj reflection coefficient
     * @return this material instance
     */

    public Material setKR(Double3 obj) {
        kR = obj;
        return this;
    }

    /**
     * Sets the shininess exponent.
     *
     * @param n shininess exponent
     * @return this material instance
     */
    public Material setShininess(int n) {
        nShininess = n;
        return this;
    }

    /**
     * Sets the ambient reflection coefficient uniformly for all channels.
     *
     * @param obj the new ambient reflection coefficient to set for the material, given as a single double value that will be applied to all color channels
     * @return the material with the updated ambient reflection coefficient
     */
    public Material setKA(double obj) {
        kA = new Double3(obj);
        return this;
    }

}
