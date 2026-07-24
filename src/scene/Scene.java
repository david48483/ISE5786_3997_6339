package scene;

import geometries.api.Intersectable;
import geometries.impl.Geometries;
import lighting.api.LightSource;
import lighting.impl.AmbientLight;
import primitives.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3D scene configuration used by the renderer.
 * Contains scene-level properties such as background color,
 * ambient light, and scene geometries.
 *
 * @author David &amp; Yehuda
 */
public class Scene {

    /**
     * Scene name.
     */
    public String name;

    /**
     * Scene background color.
     */
    public Color background = Color.BLACK;

    /**
     * Ambient light applied to the entire scene.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * Collection of geometries in the scene.
     */
    public Geometries geometries = new Geometries();

    /**
     * Collection of light sources in the scene.
     */
    public List<LightSource> lights = new ArrayList<>();

    /**
     * Creates a scene with the given name.
     *
     * @param name scene name
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Flag indicating whether flat AABB (axis-aligned bounding box) filtering
     * is enabled for intersection tests (without BVH tree construction).
     */
    private boolean _AABBenable = false;

    /**
     * Flag indicating whether BVH (Bounding Volume Hierarchy) acceleration
     * structure is enabled for this scene.
     */
    private boolean _bvhTreeEnable = false;




    /**
     * Sets the scene background color.
     *
     * @param background background color
     * @return this scene instance
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight ambient light value
     * @return this scene instance
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the scene geometries collection.
     *
     * @param geometries geometries collection
     * @return this scene instance
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }


    /**
     * Enables or disables flat AABB filtering for intersection tests.
     *
     * @param isEnable true to enable AABB, false to disable
     * @return this scene instance for chaining
     */
    public Scene setAABB(boolean isEnable){
        this._AABBenable = isEnable;
        return this;
    }

    /**
     * Enables or disables BVH tree construction for accelerating ray tracing.
     *
     * @param isEnable true to build and use BVH, false to disable
     * @return this scene instance for chaining
     */
    public Scene setBvhTree(boolean isEnable){
        this._bvhTreeEnable = isEnable;
        return this;
    }

    /**
     * Checks if the BVH tree optimization is enabled for this scene.
     *
     * @return true if BVH is enabled, false otherwise
     */
    public boolean BvhEnabled() {
        return this._bvhTreeEnable;
    }

    /**
     * Checks if the flat AABB bounding box filtering is enabled for this scene.
     *
     * @return true if AABB is enabled, false otherwise
     */
    public boolean AAABBEnabled() {
        return this._AABBenable;
    }

}
