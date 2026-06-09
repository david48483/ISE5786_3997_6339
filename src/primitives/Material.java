package primitives;

public class Material {
    public Material() {
    }

    public Double3 kA = Double3.ONE;

    public Material setKA(Double3 obj) {
        kA = obj;

        return this;
    }

    public Material setKA(double obj) {

        kA = new Double3(obj);
        return this;
    }

}
